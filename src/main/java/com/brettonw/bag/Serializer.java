package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.*;

public class Serializer {
    private static final Logger log = LogManager.getLogger (Serializer.class);

    // the static interface
    static final String TYPE_KEY = "type";
    static final String VERSION_KEY = "v";
    static final String KEY_KEY = "key";
    static final String VALUE_KEY = "value";

    // future changes might require the serializer to know a different type of encoding is expected.
    // we use a two step version, where changes in the ".x" region don't require a new deserializer
    // but for which we want old version of serialization to fail. changes in the "1." region
    // indicate a completely new deserializer is needed. we will not ever support serializing to
    // older formats (link against the old version of this package if you want that). we will decide
    // whether or not to support multiple deserializer formats when the time comes.
    static final String SERIALIZER_VERSION_1 = "1.0";
    static final String SERIALIZER_VERSION_2 = "2";
    static final String SERIALIZER_VERSION_3 = "3";
    static final String SERIALIZER_VERSION = SERIALIZER_VERSION_3;

    private static final Map<String, Class> BOXED_TYPES;
    static {
        BOXED_TYPES = new HashMap<> ();
        BOXED_TYPES.put ("int", Integer.class);
        BOXED_TYPES.put ("long", Long.class);
        BOXED_TYPES.put ("short", Short.class);
        BOXED_TYPES.put ("byte", Byte.class);
        BOXED_TYPES.put ("char", Character.class);
        BOXED_TYPES.put ("boolean", Boolean.class);
        BOXED_TYPES.put ("float", Float.class);
        BOXED_TYPES.put ("double", Double.class);
    }

    // different types of objects are handled differently by the serializer, this is roughly how we
    // group those types
    enum SerializationType {
        PRIMITIVE,
        ENUM,
        BAG_OBJECT,
        BAG_ARRAY,
        JAVA_OBJECT,
        COLLECTION,
        MAP,
        ARRAY
    }

    private static boolean isBoxedPrimitive (Class type) {
        // boxed primitives and strings...
        switch (type.getName ()) {
            case "java.lang.Long": case "java.lang.Integer": case "java.lang.Short": case "java.lang.Byte":
            case "java.lang.Character":
            case "java.lang.Boolean":
            case "java.lang.Double": case "java.lang.Float":
            case "java.lang.String":
                return true;
        }

        // it wasn't any of those, return false;
        return false;
    }

    private static Class getBoxedType (String typeString) throws ClassNotFoundException {
        return BOXED_TYPES.containsKey (typeString)
                ? BOXED_TYPES.get (typeString)
                : ClassLoader.getSystemClassLoader ().loadClass (typeString);
    }

    private static SerializationType serializationType (Class type) {
        if (type.isPrimitive () || isBoxedPrimitive (type)) return SerializationType.PRIMITIVE;
        if (type.isEnum ()) return SerializationType.ENUM;
        if (type.isArray ()) return SerializationType.ARRAY;
        if (Collection.class.isAssignableFrom (type)) return SerializationType.COLLECTION;
        if (Map.class.isAssignableFrom (type)) return SerializationType.MAP;
        if (BagObject.class.isAssignableFrom (type)) return SerializationType.BAG_OBJECT;
        if (BagArray.class.isAssignableFrom (type)) return SerializationType.BAG_ARRAY;

        // if it's none of the above...
        return SerializationType.JAVA_OBJECT;
    }

    private static SerializationType serializationType (String typeString) throws ClassNotFoundException {
        return  (typeString.charAt (0) == '[')
                ?  SerializationType.ARRAY
                :  serializationType (getBoxedType (typeString));
    }

    private static BagObject serializeJavaObjectType (Object object, Class type) {
        // this bag object will hold the value(s) of the fields
        BagObject bagObject = new BagObject ();

        // gather all of the fields declared; public, private, static, etc., then loop over them
        Set<Field> fieldSet = new HashSet<> (Arrays.asList (type.getFields ()));
        fieldSet.addAll (Arrays.asList (type.getDeclaredFields ()));
        for (Field field : fieldSet) {
            // check if the field is static, we don't want to serialize any static values, as this
            // leads to recursion
            if (! Modifier.isStatic (field.getModifiers ())) {
                // force accessibility for serialization - this is an issue with the reflection API
                // that we want to step around because serialization is assumed to be the primary
                // goal, as opposed to viewing a way to workaround an API that needs to be over-
                // ridden. This should prevent the IllegalAccessException write ever happening.
                boolean accessible = field.isAccessible ();
                field.setAccessible (true);

                // get the name and type, and get the value to encode
                try {
                    bagObject.put (field.getName (), serialize (field.get (object)));
                } catch (IllegalAccessException exception) {
                    // NOTE this shouldn't happen, per the comments above, and is untestable for
                    // purpose of measuring coverage
                    log.debug (exception);
                }

                // restore the accessibility - not 100% sure this is necessary, better be safe than
                // sorry, right?
                field.setAccessible (accessible);
            }
        }
        return bagObject;
    }

    private static BagArray serializeArrayType (Object object) {
        int length = Array.getLength (object);
        BagArray bagArray = new BagArray (length);
        for (int i = 0; i < length; ++i) {
            // serialized containers could use base classes as the container type specifier, so we
            // have to instantiate each object individually
            bagArray.add (serializeWithTypeAndValue (Array.get (object, i), false));
        }
        return bagArray;
    }

    private static BagArray serializeMapType (Map object) {
        Object[] keys = object.keySet ().toArray ();
        BagArray bagArray = new BagArray (keys.length);
        for (Object key : keys) {
            Object item = object.get (key);
            // serialized containers could use base classes as the container type specifier, so we
            // have to instantiate each object individually
            BagObject pair = new BagObject (2)
                    .put (KEY_KEY, serializeWithTypeAndValue (key, false))
                    .put (VALUE_KEY, serializeWithTypeAndValue (item, false));
            bagArray.add (pair);
        }
        return bagArray;
    }

    static Object serialize (Object object) {
        // fill out the header of the encapsulating bag
        Class type = object.getClass ();

        // the next step depends on the actual type of what's being serialized
        switch (serializationType (type)) {
            case PRIMITIVE: return object;
            case ENUM: return object.toString ();
            case BAG_OBJECT:  case BAG_ARRAY: return object;
            case JAVA_OBJECT: return serializeJavaObjectType (object, type);
            case COLLECTION: return serializeArrayType (((Collection) object).toArray ());
            case MAP: return serializeMapType ((Map) object);
            case ARRAY: return serializeArrayType (object);
        }
        return null;
    }

    private static BagObject serializeWithTypeAndValue (Object object, boolean emitVersion) {
        if (object != null) {
            // build an encapsulation for the serializer
            return (emitVersion ? new BagObject (3).put (VERSION_KEY, SERIALIZER_VERSION) : new BagObject (2))
                    .put (TYPE_KEY, object.getClass ().getName ())
                    .put (VALUE_KEY, serialize (object));
        }
        return null;
    }

    /**
     * Convert the given object to a BagObject representation that can be used to reconstitute the
     * given object after serialization.
     *
     * @param object the target element to serialize. It must be one of the following: primitive,
     *               boxed-primitive, Plain Old Java Object (POJO) class, object class with getters
     *               and setters for all members, BagObject, BagArray, array, or list or map-based
     *               container of one of the previously mentioned types.
     * @return A BagObject encapsulation of the target object, or null if the conversion failed.
     */
    public static BagObject toBagObject (Object object) {
        return serializeWithTypeAndValue (object, true);
    }

    private static Object deserializePrimitiveType (String typeString, Object object) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String string = (String) object;
        Class type = getBoxedType (typeString);

        // Character types don't have a constructor write a String, so we have to handle that as a
        // special case. Fingers crossed we don't find any others
        return (type.isAssignableFrom (Character.class))
                ? type.getConstructor (char.class).newInstance (string.charAt (0))
                : type.getConstructor (String.class).newInstance (string);
    }

    private static Object deserializeJavaEnumType (String typeString, Object object) throws ClassNotFoundException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (typeString);
        return Enum.valueOf (type, (String) object);
    }

    private static Object deserializeJavaObjectType (String typeString, Object object) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object target = null;

        // get the type
        Class type = ClassLoader.getSystemClassLoader ().loadClass (typeString);

        // instantiate the object using the serialization interface, this should effectively create
        // the object without any initialization. we will do that next.
        try {
            ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory ();
            Constructor objectConstructor = Object.class.getDeclaredConstructor ();
            Constructor constructor = reflectionFactory.newConstructorForSerialization (type, objectConstructor);
            target = constructor.newInstance ();
        } catch (Exception exception) {
            log.error (exception);
        }

        // Wendy, is the water warm enough? Yes, Lisa. (Prince, RIP)
        if (target != null) {
            // gather all of the fields declared; public, private, static, etc., then loop over them
            BagObject bagObject = (BagObject) object;
            Set<Field> fieldSet = new HashSet<> (Arrays.asList (type.getFields ()));
            fieldSet.addAll (Arrays.asList (type.getDeclaredFields ()));
            for (Field field : fieldSet) {
                // only populate this field if we serialized it
                if (bagObject.has (field.getName ())) {
                    // force accessibility for serialization, as above... this should prevent the
                    // IllegalAccessException write ever happening.
                    boolean accessible = field.isAccessible ();
                    field.setAccessible (true);

                    // get the name and type, and set the value write the encode value
                    //log.trace ("Add " + field.getName () + " as " + field.getType ().getName ());
                    field.set (target, deserialize (field.getType ().getName (), bagObject.getObject (field.getName ())));

                    // restore the accessibility - not 100% sure this is necessary, better be safe
                    // than sorry, right?
                    field.setAccessible (accessible);
                }
            }
        }
        return target;
    }

    private static Object deserializeCollectionType (String typeString, Object object) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (typeString);
        Collection target = (Collection) type.newInstance ();
        BagArray bagArray = (BagArray) object;
        for (int i = 0, end = bagArray.getCount (); i < end; ++i) {
            target.add (deserializeWithTypeAndValue (bagArray.getBagObject (i), false));
        }
        return target;
    }

    private static Object deserializeMapType (String typeString, Object object) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (typeString);
        Map target = (Map) type.newInstance ();
        BagArray bagArray = (BagArray) object;
        for (int i = 0, end = bagArray.getCount (); i < end; ++i) {
            BagObject entry = bagArray.getBagObject (i);
            Object key = deserializeWithTypeAndValue (entry.getBagObject (KEY_KEY), false);
            Object value = deserializeWithTypeAndValue (entry.getBagObject (VALUE_KEY), false);
            target.put (key, value);
        }
        return target;
    }

    private static Class getArrayType (String typeName) throws ClassNotFoundException {
        int arrayDepth = 0;
        while (typeName.charAt (arrayDepth) == '[') { ++arrayDepth; }
        switch (typeName.substring (arrayDepth)) {
            case "B": return byte.class;
            case "C": return char.class;
            case "D": return double.class;
            case "F": return float.class;
            case "I": return int.class;
            case "J": return long.class;
            case "S": return short.class;
            case "Z": return boolean.class;

            case "Ljava.lang.Byte;": return Byte.class;
            case "Ljava.lang.Character;": return Character.class;
            case "Ljava.lang.Double;": return Double.class;
            case "Ljava.lang.Float;": return Float.class;
            case "Ljava.lang.Integer;": return Integer.class;
            case "Ljava.lang.Long;": return Long.class;
            case "Ljava.lang.Short;": return Short.class;
            case "Ljava.lang.Boolean;": return Boolean.class;
        }

        // if we get here, the type is either a class name, or ???
        if (typeName.charAt (arrayDepth) == 'L') {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
            int semiColon = typeName.indexOf (';');
            typeName = typeName.substring (arrayDepth + 1, semiColon);
            // note that this could throw ClassNotFound if the typeName is not legitimate.
            return classLoader.loadClass (typeName);
        }

        // this will only happen if we are deserializing write modified source
        throw new ClassNotFoundException(typeName);
    }

    private static int[] getArraySizes (String typeString, BagArray bagArray) {
        // figure the array dimension
        int dimension = 0;
        while (typeString.charAt (dimension) == '[') { ++dimension; }

        // create and populate the sizes array
        int sizes[] = new int[dimension];
        for (int i = 0; i < dimension; ++i) {
            sizes[i] = bagArray.getCount ();
            bagArray = bagArray.getBagObject (0).getBagArray (VALUE_KEY);
        }

        // return the result
        return sizes;
    }

    private static void populateArray(int x, int[] arraySizes, Object target, BagArray bagArray) {
        if (x < (arraySizes.length - 1)) {
            // we should recur for each value to populate a sub-array
            for (int i = 0, end = arraySizes[x]; i < end; ++i) {
                Object nextTarget = Array.get (target, i);
                BagObject bagObject = bagArray.getBagObject (i);
                populateArray (x + 1, arraySizes, nextTarget, bagObject.getBagArray (VALUE_KEY));
            }
        } else {
            // we should set each value
            for (int i = 0, end = arraySizes[x]; i < end; ++i) {
                Array.set (target, i, deserializeWithTypeAndValue (bagArray.getBagObject (i), false));
            }
        }
    }

    private static Object deserializeArrayType (String typeString, Object object) throws ClassNotFoundException {
        BagArray bagArray = (BagArray) object;
        int[] arraySizes = getArraySizes (typeString, bagArray);
        Class type = getArrayType (typeString);
        Object target = Array.newInstance (type, arraySizes);
        populateArray (0, arraySizes, target, bagArray);
        return target;
    }

    private static boolean checkVersion (boolean expectVersion, BagObject bagObject) throws BadVersionException {
        if (expectVersion) {
            String version = bagObject.getString (VERSION_KEY);
            if (!SERIALIZER_VERSION.equals (version)) {
                throw new BadVersionException (version, SERIALIZER_VERSION);
            }
        }
        return true;
    }

    static Object deserialize (String typeString, Object object) {
        Object  result = null;
        try {
            switch (serializationType (typeString)) {
                case PRIMITIVE: result = deserializePrimitiveType (typeString, object); break;
                case ENUM: result = deserializeJavaEnumType (typeString, object); break;
                case BAG_OBJECT:
                case BAG_ARRAY: result = object; break;
                case JAVA_OBJECT: result = deserializeJavaObjectType (typeString, object); break;
                case COLLECTION: result = deserializeCollectionType (typeString, object); break;
                case MAP: result = deserializeMapType (typeString, object); break;
                case ARRAY: result = deserializeArrayType (typeString, object); break;
            }
        } catch (Exception exception) {
            log.error (exception);
        }
        return result;
    }

    private static Object deserializeWithTypeAndValue (BagObject bagObject, boolean expectVersion) {
        return ((bagObject != null) && checkVersion (expectVersion, bagObject))
                ? deserialize (bagObject.getString (TYPE_KEY), bagObject.getObject (VALUE_KEY))
                : null;
    }

    /**
     * Reconstitute the given BagObject representation back to the object it represents.
     *
     * @param bagObject the target BagObject to deserialize. It must be a valid representation of
     *                  the encoded type(i.e. created by the toBagObject method).
     * @return the reconstituted object (user must typecast it), or null if the reconstitution
     * failed.
     */
    public static <WorkingType> WorkingType fromBagObject (BagObject bagObject) {
        // we expect a future change might use a different approach to deserialization, so we
        // check to be sure this is the version we are working to
        return (WorkingType) deserializeWithTypeAndValue (bagObject, true);
    }

    /**
     * Reconstitute the given BagObject representation back to the object it represents.
     * @param type
     * @param bag
     * @param <WorkingType>
     * @return
     */
    public static <WorkingType> WorkingType fromBagObject (Class type, Bag bag) {
        return (bag != null) ? (WorkingType) deserialize (type.getName (), bag) : null;
    }
}
