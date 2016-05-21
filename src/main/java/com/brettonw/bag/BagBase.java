package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

import java.util.function.Function;

abstract class BagBase {
    private static final Logger log = LogManager.getLogger (BagBase.class);

    // data and functions for exporting as strings
    static final String SQUARE_BRACKETS[] = { "[", "]" };
    static final String CURLY_BRACKETS[] = { "{", "}" };

    private static final String QUOTES[] = { "\"" };

    String enclose (String input, String bracket[]) {
        String bracket0 = bracket[0];
        String bracket1 = (bracket.length > 1) ? bracket[1] : bracket0;
        return bracket0 + input + bracket1;
    }

    String quote (String input) {
        return enclose (input, QUOTES);
    }

    String getJsonString (Object object) {
        if (object != null) {
            switch (object.getClass ().getName ()) {
                case "java.lang.String":
                    return quote ((String) object);

                case "com.brettonw.bag.BagObject":
                case "com.brettonw.bag.BagArray":
                    return ((BagBase) object).toJsonString ();

                // we omit the default case, because there should not be any other types stored in
                // the Bag class - as in, they would not make it into the container, as the
                // "objectify" method will gate that
            }
        }
        // if we stored a null, we need to emit it as a value. This will only happen in the
        // array types, and is handled on the parsing side with a special case for reading
        // the bare value 'null' (not quoted)
        return "null";
    }

    Object objectify (Object value) {
        if (value != null) {
            Class type = value.getClass ();
            String typeName = type.getName ();
            switch (typeName) {
                case "java.lang.String":
                    // is this the right place to do a transformation that converts quotes to some
                    // escape character?
                    return value;

                case "java.lang.Long": case "java.lang.Integer": case "java.lang.Short": case "java.lang.Byte":
                case "java.lang.Character":
                case "java.lang.Boolean":
                case "java.lang.Double": case "java.lang.Float":
                    return value.toString ();

                case "com.brettonw.bag.BagObject":
                case "com.brettonw.bag.BagArray":
                    return value;

                default:
                    // no other type should be stored in the bag classes
                    //log.error ("Unhandled type: " + typeName);
                    throw new UnsupportedTypeException (type);
            }
        }
        return null;
    }

    abstract public Object getObject (String key);

    // XXX experimental thoughts... there are two types of gets here - parsed gets, or core gets
    // (String, BagArray, BagObject)
    /*
    private <T extends BagBase> T get (String key) {
        Object object = getObject (key);
        return (object instanceof BagBase) ? (T) object : null;
    }
    */

    /*
    private <T> T get (String key, Supplier<T> notFound) {
        T value = getObject (key);
        return (value != null) ? value : notFound.get ();
    }
    */

    private <T> T getParsed (String key, Function<String, T> parser, Supplier<T> notFound) {
        Object object = getObject (key);
        return (object instanceof String) ? parser.apply ((String) object) : notFound.get ();
    }

    /**
     * Retrieve a mapped element and return it as a String.
     *
     * @param key A string value used to index the element.
     * @return The element as a string, or null if the element is not found (or not a String).
     */
    public String getString (String key) {
        return getString (key, () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a String.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new String if the requested key was not found
     * @return The element as a string, or notFound if the element is not found.
     */
    public String getString (String key, Supplier<String> notFound) {
        Object object = getObject (key);
        return (object instanceof String) ? (String) object : notFound.get ();
    }

    /**
     * Retrieve a mapped element and return it as a Boolean.
     *
     * @param key A string value used to index the element.
     * @return The element as a Boolean, or null if the element is not found.
     */
    public Boolean getBoolean (String key) {
        return getParsed (key, (input) -> new Boolean (input), () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a Boolean.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new Boolean if the requested key was not found
     * @return The element as a Boolean, or notFound if the element is not found.
     */
    public Boolean getBoolean (String key, Supplier<Boolean> notFound) {
        return getParsed (key, (input) -> new Boolean (input), notFound);
    }

    /**
     * Retrieve a mapped element and return it as a Long.
     *
     * @param key A string value used to index the element.
     * @return The element as a Long, or null if the element is not found.
     */
    public Long getLong (String key) {
        return getParsed (key, (input) -> new Long (input), () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a Long.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new Long if the requested key was not found
     * @return The element as a Long, or notFound if the element is not found.
     */
    public Long getLong (String key, Supplier<Long> notFound) {
        return getParsed (key, (input) -> new Long (input), notFound);
    }

    /**
     * Retrieve a mapped element and return it as an Integer.
     *
     * @param key A string value used to index the element.
     * @return The element as an Integer, or null if the element is not found.
     */
    public Integer getInteger (String key) {
        return getParsed (key, (input) -> new Integer (input), () -> null);
    }

    /**
     * Retrieve a mapped element and return it as an Integer.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new Integer if the requested key was not found
     * @return The element as an Integer, or notFound if the element is not found.
     */
    public Integer getInteger (String key, Supplier<Integer> notFound) {
        return getParsed (key, (input) -> new Integer (input), notFound);
    }

    /**
     * Retrieve a mapped element and return it as a Double.
     *
     * @param key A string value used to index the element.
     * @return The element as a Double, or null if the element is not found.
     */
    public Double getDouble (String key) {
        return getParsed (key, (input) -> new Double (input), () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a Double.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new Double if the requested key was not found
     * @return The element as a Double, or notFound if the element is not found.
     */
    public Double getDouble (String key, Supplier<Double> notFound) {
        return getParsed (key, (input) -> new Double (input), notFound);
    }

    /**
     * Retrieve a mapped element and return it as a Float.
     *
     * @param key A string value used to index the element.
     * @return The element as a Float, or null if the element is not found.
     */
    public Float getFloat (String key) {
        return getParsed (key, (input) -> new Float (input), () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a Float.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new Float if the requested key was not found
     * @return The element as a Float, or notFound if the element is not found.
     */
    public Float getFloat (String key, Supplier<Float> notFound) {
        return getParsed (key, (input) -> new Float (input), notFound);
    }

    /**
     * Retrieve a mapped element and return it as a BagObject.
     *
     * @param key A string value used to index the element.
     * @return The element as a BagObject, or null if the element is not found.
     */
    public BagObject getBagObject (String key) {
        return getBagObject (key, () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a BagObject.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new BagObject if the requested key was not found
     * @return The element as a BagObject, or notFound if the element is not found.
     */
    public BagObject getBagObject (String key, Supplier<BagObject> notFound) {
        Object object = getObject (key);
        return (object instanceof BagObject) ? (BagObject) object : notFound.get ();
    }

    /**
     * Retrieve a mapped element and return it as a BagArray.
     *
     * @param key A string value used to index the element.
     * @return The element as a BagArray, or null if the element is not found.
     */
    public BagArray getBagArray (String key) {
        return getBagArray (key, () -> null);
    }

    /**
     * Retrieve a mapped element and return it as a BagArray.
     *
     * @param key A string value used to index the element.
     * @param notFound A function to create a new BagArray if the requested key was not found
     * @return The element as a BagArray, or notFound if the element is not found.
     */
    public BagArray getBagArray (String key, Supplier<BagArray> notFound) {
        Object object = getObject (key);
        return (object instanceof BagArray) ? (BagArray) object : notFound.get ();
    }


    abstract public String toJsonString ();

    @Override
    public String toString () {
        return toJsonString ();
    }

    @Override
    public boolean equals (Object object) {
        return toString ().equals (object.toString ());
    }

    @Override
    public int hashCode () {
        return toString ().hashCode ();
    }
}
