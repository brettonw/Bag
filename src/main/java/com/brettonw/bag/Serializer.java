package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.ReflectionFactory;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * A tool to convert data types to and from BagObjects for serialization. It is designed to support
 * primitives, Plain Old Java Object (POJO) classes, object classes with getters and setters,
 * arrays, and array or map-based containers of one of the previously mentioned types. It explicitly
 * supports BagObject and BagArray as well.
 */
public final class Serializer<WorkingType> {
    private static final Logger log = LogManager.getLogger (Serializer.class);

    // the non-static interface
    public Serializer () {}

    /**
     * Deserialize the given BagObject representation back to the &lt;WorkingType&gt; object it
     * represents. This is a type-safe helper function
     *
     * @param bagObject the target BagObject to deserialize. It must be a valid representation of
     *                  the encoded type(i.e. created by the toBagObject method).
     * @return the reconstituted object, or null if the deserialization failed.
     */
    public WorkingType from (BagObject bagObject) {
        try {
            return (WorkingType) fromBagObject (bagObject);
        } catch (Exception exception) {
            log.error (exception);
        }
        return null;
    }

    // the static interface
    private static final String TYPE_KEY = "type";
    private static final String VERSION_KEY = "v";
    private static final String KEY_KEY = "key";
    private static final String VALUE_KEY = "value";

    // future changes might require the serializer to know a different type of encoding is expected.
    // we use a two step version, where changes in the ".x" region don't require a new deserializer
    // but for which we want old version of serialization to fail. changes in the "1." region
    // indicate a completely new deserializer is needed. we will not ever support serializing to
    // older formats (link against the old version of this package if you want that). we will decide
    // whether or not to support multiple deserializer formats when the time comes.
    private static final String SERIALIZER_VERSION_1 = "1.0";
    private static final String SERIALIZER_VERSION = SERIALIZER_VERSION_1;

    // a type extension registry so that we can instantiate types that don't adhere to the
    // 'Serializable' interface requirements.
    private static final Map<Class, Supplier> typeExtensions = new HashMap<> ();

    /**
     * Add a handler function to be called when a type without a default constructor is
     * deserialized.
     *
     * Some serialized types say they are Serializable (or act that way by default), but don't
     * actually have a default constructor. In that case, we have to have a way fo link in functions
     * that can be used to construct a default instance of that type for deserialization.
     *
     * @param c the Class to use this "constructor" for
     * @param s the function to call when constructing the unknown type
     */
    public static void forType (Class c, Supplier s) {
        typeExtensions.put (c, s);
    }

    private static boolean isPrimitive (Class type) {
        // an obvious check to do here is type.isPrimitive (), but that is never true because Java
        // has boxed the primitives before they get here. So, we have to check for boxed primitives
        // and strings as well
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
    private static SerializationType serializationType (Class type) {
        if (isPrimitive (type)) return SerializationType.PRIMITIVE;
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
        if (typeString.charAt (0) == '[') {
            return SerializationType.ARRAY;
        }

        ClassLoader classLoader = ClassLoader.getSystemClassLoader ();
        Class type = classLoader.loadClass (typeString);
        return serializationType (type);
    }

    private static BagObject serializePrimitiveType (BagObject bagObject, Object object) {
        return bagObject.put (VALUE_KEY, object);
    }

    private static BagObject serializeJavaEnumType (BagObject bagObject, Object object, Class type) {
        return bagObject.put (VALUE_KEY, object.toString ());
    }

    private static BagObject serializeJavaObjectType (BagObject bagObject, Object object, Class type) {
        try {
            // get the type's associated empty constructor just to see if we can
            Constructor constructor = type.getDeclaredConstructor ();
        } catch (NoSuchMethodException exception) {
            // well... we are looking at an object that has no default constructor, private or
            // otherwise.  want to warn the user if this type is not Serializable or not a known
            // type in the typeExtensions, so they know the type can't be deserialized
            if (!(Serializable.class.isAssignableFrom (type) || typeExtensions.containsKey (type))) {
                log.warn ("Type (" + type.getCanonicalName () + ") cannot be deserialized. Consider adding a 'forType' extension to mimic a default constructor.");
            }
        }

        // this bag object will hold the value(s) of the fields
        BagObject value = new BagObject ();

        // gather all of the fields declared, public, private, static, etc., then loop over them all
        Set<Field> fieldSet = new HashSet<> (Arrays.asList (type.getFields ()));
        fieldSet.addAll (Arrays.asList (type.getDeclaredFields ()));
        for (Field field : fieldSet) {
            // check if the field is static, we don't want to serialize any static values, as this
            // leads to recursion
            if (! Modifier.isStatic (field.getModifiers ())) {
                // force accessibility for serialization - this is an issue with the reflection API
                // that we want to step around because serialization is assumed to be the primary
                // goal, as opposed to viewing a way to workaround an API that needs to be over-
                // ridden. This should prevent the IllegalAccessException from ever happening.
                boolean accessible = field.isAccessible ();
                field.setAccessible (true);

                // get the name and type, and get the value to encode
                try {
                    value.put (field.getName (), toBagObject (field.get (object)));
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
        return bagObject.put (VALUE_KEY, value);
    }

    private static BagObject serializeArrayType (BagObject bagObject, Object object) {
        int length = Array.getLength (object);
        BagArray value = new BagArray (length);
        for (int i = 0; i < length; ++i) {
            // at runtime, we don't know what the array type is, and frankly we don't care
            value.add (toBagObject (Array.get (object, i)));
        }
        return bagObject.put (VALUE_KEY, value);
    }

    private static BagObject serializeMapType (BagObject bagObject, Map object) {
        Object[] keys = object.keySet ().toArray ();
        BagArray value = new BagArray (keys.length);
        for (Object key : keys) {
            Object item = object.get (key);
            BagObject pair = new BagObject (2)
                    .put (KEY_KEY, toBagObject (key))
                    .put (VALUE_KEY, toBagObject (item));
            value.add (pair);
        }
        return bagObject.put (VALUE_KEY, value);
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
        // fill out the header of the encapsulating bag
        Class type = object.getClass ();
        BagObject bagObject = new BagObject (3)
                .put (TYPE_KEY, type.getName ())
                .put (VERSION_KEY, SERIALIZER_VERSION);

        // the next step depends on the actual type of what's being serialized
        switch (serializationType (type)) {
            case PRIMITIVE:
                bagObject = serializePrimitiveType (bagObject, object);
                break;
            case ENUM:
                bagObject = serializeJavaEnumType (bagObject, object, type);
                break;
            case BAG_OBJECT:
                bagObject = serializePrimitiveType (bagObject, object);
                break;
            case BAG_ARRAY:
                bagObject = serializePrimitiveType (bagObject, object);
                break;
            case JAVA_OBJECT:
                bagObject = serializeJavaObjectType (bagObject, object, type);
                break;
            case COLLECTION:
                bagObject = serializeArrayType (bagObject, ((Collection) object).toArray ());
                break;
            case MAP:
                bagObject = serializeMapType (bagObject, (Map) object);
                break;
            case ARRAY:
                bagObject = serializeArrayType (bagObject, object);
                break;
        }
        return bagObject;
    }

    @SuppressWarnings (value="unchecked")
    private static Object deserializePrimitiveType (BagObject bagObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String valueString = bagObject.getString (VALUE_KEY);
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));

        // Character types don't have a constructor from a String, so we have to handle that as a
        // special case. Fingers crossed we don't find any others
        return (type.isAssignableFrom (Character.class)) ?
                type.getConstructor (char.class).newInstance (valueString.charAt (0)) :
                type.getConstructor (String.class).newInstance (valueString);
    }

    private static Object deserializeJavaEnumType (BagObject bagObject) throws ClassNotFoundException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        return Enum.valueOf (type, bagObject.getString (VALUE_KEY));
    }

    private static <T> T create(Class<T> type) {
        if (Serializable.class.isAssignableFrom (type)) {
            try {
                ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory ();
                Constructor declaredConstructor = Object.class.getDeclaredConstructor ();
                Constructor constructor = reflectionFactory.newConstructorForSerialization (type, declaredConstructor);
                return type.cast (constructor.newInstance ());
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new IllegalStateException ("Cannot create object", exception);
            }
        }
        return null;
    }

    private static Object deserializeJavaObjectType (BagObject bagObject) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object target = null;

        // get the type and try to construct from a default constructor
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        try {
            // get the type's associated empty constructor. Even if the constructor is private, we
            // force accessibility for serialization - this is an issue with the reflection API that
            // we want to step around because serialization is assumed to be the primary goal, as
            // opposed to viewing it as a way to workaround an API that needs to be over-ridden.
            //if (Serializable.class.isAssignableFrom (type)) {
            Constructor constructor = type.getDeclaredConstructor ();
            boolean accessible = constructor.isAccessible ();
            constructor.setAccessible (true);
            target = constructor.newInstance ();
            constructor.setAccessible (accessible);
            //}
        } catch (NoSuchMethodException exception) {
            // well... we are looking at an object that is technically not Serializable, as it has
            // no default constructor, private or otherwise. I'v encountered this issue with a type
            // that declares it implements Serializable (OffsetDateTime), so I know class authors
            // lie about their classes, even in the java.lang.* class hierarchy.
            log.debug (exception);
        }

        // if we didn't get the object at this point, try to recreate it as a Serializable object
        if (target == null) {
            // based on article at http://www.javaspecialists.eu/archive/Issue175.html
            if (Serializable.class.isAssignableFrom (type)) {
                try {
                    ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory ();
                    Constructor declaredConstructor = Object.class.getDeclaredConstructor ();
                    Constructor constructor = reflectionFactory.newConstructorForSerialization (type, declaredConstructor);
                    target = constructor.newInstance ();
                } catch (RuntimeException exception) {
                    throw exception;
                } catch (Exception exception) {
                    throw new IllegalStateException ("Cannot create object", exception);
                }
            }
        }

        // so... if we didn't get an object constructed at this point, we need to jump out to our
        // type extensions to allows us to say, for type X, use this method to make a default one...
        if (target == null) {
            // check to see if the type is in our registry
            if (typeExtensions.containsKey (type)) {
                target = typeExtensions.get (type).get ();
            } else {
                log.error ("Don't know how to construct: " + type.getCanonicalName ());
            }
        }

        // Wendy, is the water warm enough? Yes, Lisa. (Prince, RIP)
        if (target != null) {
            // traverse the fields via reflection to set the values, only the public values
            BagObject value = bagObject.getBagObject (VALUE_KEY);
            Set<Field> fieldSet = new HashSet<> (Arrays.asList (type.getFields ()));
            fieldSet.addAll (Arrays.asList (type.getDeclaredFields ()));
            for (Field field : fieldSet) {
                // check if the field is static, we don't want to serialize any static values, as this
                // leads to recursion
                if (! Modifier.isStatic (field.getModifiers ())) {
                    // force accessibility for serialization, as above... this should prevent the
                    // IllegalAccessException from ever happening.
                    boolean accessible = field.isAccessible ();
                    field.setAccessible (true);

                    // get the name and type, and set the value from the encode value
                    //log.trace ("Add " + field.getName () + " as " + field.getType ().getName ());
                    field.set (target, fromBagObject (value.getBagObject (field.getName ())));

                    // restore the accessibility - not 100% sure this is necessary, better be safe
                    // than sorry, right?
                    field.setAccessible (accessible);
                }
            }
        }
        return target;
    }

    @SuppressWarnings (value="unchecked")
    private static Object deserializeCollectionType (BagObject bagObject) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        Collection target = (Collection) type.newInstance ();
        BagArray value = bagObject.getBagArray (VALUE_KEY);
        for (int i = 0, end = value.getCount (); i < end; ++i) {
            target.add (fromBagObject (value.getBagObject (i)));
        }
        return target;
    }

    @SuppressWarnings (value="unchecked")
    private static Object deserializeMapType (BagObject bagObject) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class type = ClassLoader.getSystemClassLoader ().loadClass (bagObject.getString (TYPE_KEY));
        Map target = (Map) type.newInstance ();
        BagArray value = bagObject.getBagArray (VALUE_KEY);
        for (int i = 0, end = value.getCount (); i < end; ++i) {
            BagObject entry = value.getBagObject (i);
            target.put (fromBagObject (entry.getBagObject (KEY_KEY)), fromBagObject (entry.getBagObject (VALUE_KEY)));
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

        // this will only happen if we are deserializing from modified source
        throw new ClassNotFoundException(typeName);
    }

    private static int[] getArraySizes (BagObject bagObject) {
        // figure the array dimension
        String typeString = bagObject.getString (TYPE_KEY);
        int dimension = 0;
        while (typeString.charAt (dimension) == '[') { ++dimension; }

        // create and populate the sizes array
        int sizes[] = new int[dimension];
        for (int i = 0; i < dimension; ++i) {
            BagArray value = bagObject.getBagArray (VALUE_KEY);
            sizes[i] = value.getCount ();
            bagObject = value.getBagObject (0);
        }

        // return the result
        return sizes;
    }

    private static void populateArray(Object target, BagObject bagObject) {
        String classString = bagObject.getString (TYPE_KEY);
        BagArray values = bagObject.getBagArray (VALUE_KEY);
        for (int i = 0, end = values.getCount (); i < end; ++i) {
            if (classString.charAt (1) == '[') {
                // we should recur for each value
                Object newTarget = Array.get (target, i);
                BagObject newBagObject = values.getBagObject (i);
                populateArray (newTarget, newBagObject);
            } else {
                Array.set (target, i, fromBagObject (values.getBagObject (i)));
            }
        }
    }

    private static Object deserializeArrayType (BagObject bagObject) throws ClassNotFoundException {
        int[] arraySizes = getArraySizes (bagObject);
        Class type = getArrayType (bagObject.getString (TYPE_KEY));
        Object target = Array.newInstance (type, arraySizes);
        populateArray (target, bagObject);
        return target;
    }

    private static void checkVersion (String got) throws BadVersionException {
        if (! got.equals (SERIALIZER_VERSION)) {
            throw new BadVersionException (got, SERIALIZER_VERSION);
        }
    }

    /**
     * Reconstitute the given BagObject representation back to the object it represents.
     *
     * @param bagObject the target BagObject to deserialize. It must be a valid representation of
     *                  the encoded type(i.e. created by the toBagObject method).
     * @return the reconstituted object (user must typecast it), or null if the reconstitution
     * failed.
     */
    public static Object fromBagObject (BagObject bagObject) {
        Object  result = null;
        try {
            // we expect a future change might use a different approach to deserialization, so we
            // check to be sure this is the version we are working to
            checkVersion (bagObject.getString (VERSION_KEY));
            switch (serializationType (bagObject.getString (TYPE_KEY))) {
                case PRIMITIVE:
                    result = deserializePrimitiveType (bagObject);
                    break;
                case ENUM:
                    result = deserializeJavaEnumType (bagObject);
                    break;
                case BAG_OBJECT:
                    result = bagObject.getBagObject (VALUE_KEY);
                    break;
                case BAG_ARRAY:
                    result = bagObject.getBagArray (VALUE_KEY);
                    break;
                case JAVA_OBJECT:
                    result = deserializeJavaObjectType (bagObject);
                    break;
                case COLLECTION:
                    result = deserializeCollectionType (bagObject);
                    break;
                case MAP:
                    result = deserializeMapType (bagObject);
                    break;
                case ARRAY:
                    result = deserializeArrayType (bagObject);
                    break;
            }
        }
        catch (Exception exception) {
            log.error (exception);
        }
        return result;
    }
}
