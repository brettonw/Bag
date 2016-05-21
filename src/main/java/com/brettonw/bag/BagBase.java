package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

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

    /**
     * Retrieve a mapped element and return it as a String.
     *
     * @param key A string value used to index the element.
     * @return The element as a string, or null if the element is not found (or not a String).
     */
    public String getString (String key) {
        Object object = getObject (key);
        return (object instanceof String) ? (String) object : null;
        /*
        try {
            return (String) object;
        } catch (ClassCastException exception) {
            log.warn ("Cannot cast value type (" + object.getClass ().getName () + ") to String for key (" + key + ")");
        }
        return null;
        */
    }

    /**
     * Retrieve a mapped element and return it as a String.
     *
     * @param key A string value used to index the element.
     * @param notFound A String value to return if the key was not found
     * @return The element as a string, or notFound if the element is not found.
     */
    public String getString (String key, String notFound) {
        String value = getString (key);
        return (value != null) ? value : notFound;
    }

    /**
     * Retrieve a mapped element and return it as a Boolean.
     *
     * @param key A string value used to index the element.
     * @return The element as a Boolean, or null if the element is not found.
     */
    public Boolean getBoolean (String key) {
        String string = getString (key);
        return (string != null) ? Boolean.parseBoolean (string) : null;
    }

    /**
     * Retrieve a mapped element and return it as a Boolean.
     *
     * @param key A string value used to index the element.
     * @param notFound A Boolean value to return if the key was not found
     * @return The element as a Boolean, or notFound if the element is not found.
     */
    public Boolean getBoolean (String key, Boolean notFound) {
        Boolean value = getBoolean (key);
        return (value != null) ? value : notFound;
    }

    /**
     * Retrieve a mapped element and return it as a Long.
     *
     * @param key A string value used to index the element.
     * @return The element as a Long, or null if the element is not found.
     */
    public Long getLong (String key) {
        String string = getString (key);
        return (string != null) ? Long.parseLong (string) : null;
    }

    /**
     * Retrieve a mapped element and return it as a Long.
     *
     * @param key A string value used to index the element.
     * @param notFound A Long value to return if the key was not found
     * @return The element as a Long, or notFound if the element is not found.
     */
    public Long getLong (String key, Long notFound) {
        Long value = getLong (key);
        return (value != null) ? value : notFound;
    }

    /**
     * Retrieve a mapped element and return it as an Integer.
     *
     * @param key A string value used to index the element.
     * @return The element as an Integer, or null if the element is not found.
     */
    public Integer getInteger (String key) {
        Long value = getLong (key);
        return (value != null) ? value.intValue () : null;
    }

    /**
     * Retrieve a mapped element and return it as an Integer.
     *
     * @param key A string value used to index the element.
     * @param notFound An Integer value to return if the key was not found
     * @return The element as an Integer, or notFound if the element is not found.
     */
    public Integer getInteger (String key, Integer notFound) {
        Long value = getLong (key);
        return (value != null) ? value.intValue () : notFound;
    }

    /**
     * Retrieve a mapped element and return it as a Double.
     *
     * @param key A string value used to index the element.
     * @return The element as a Double, or null if the element is not found.
     */
    public Double getDouble (String key) {
        String string = getString (key);
        return (string != null) ? Double.parseDouble (string) : null;
    }

    /**
     * Retrieve a mapped element and return it as a Double.
     *
     * @param key A string value used to index the element.
     * @param notFound A Double value to return if the key was not found
     * @return The element as a Double, or notFound if the element is not found.
     */
    public Double getDouble (String key, Double notFound) {
        Double value = getDouble (key);
        return (value != null) ? value : notFound;
    }

    /**
     * Retrieve a mapped element and return it as a Float.
     *
     * @param key A string value used to index the element.
     * @return The element as a Float, or null if the element is not found.
     */
    public Float getFloat (String key) {
        Double value = getDouble (key);
        return (value != null) ? value.floatValue () : null;
    }

    /**
     * Retrieve a mapped element and return it as a Float.
     *
     * @param key A string value used to index the element.
     * @param notFound A Float value to return if the key was not found
     * @return The element as a Float, or notFound if the element is not found.
     */
    public Float getFloat (String key, Float notFound) {
        Double value = getDouble (key);
        return (value != null) ? value.floatValue () : notFound;
    }

    /**
     * Retrieve a mapped element and return it as a BagObject.
     *
     * @param key A string value used to index the element.
     * @return The element as a BagObject, or null if the element is not found.
     */
    public BagObject getBagObject (String key) {
        Object object = getObject (key);
        return (object instanceof BagObject) ? (BagObject) object : null;
        /*
        try {
            return (BagObject) object;
        } catch (ClassCastException exception) {
            log.warn ("Cannot cast value type (" + object.getClass ().getName () + ") to BagObject for key (" + key + ")");
        }
        return null;
        */
    }

    /**
     * Retrieve a mapped element and return it as a BagObject.
     *
     * @param key A string value used to index the element.
     * @param notFound A BagObject to return if the key was not found
     * @return The element as a BagObject, or notFound if the element is not found.
     */
    public BagObject getBagObject (String key, BagObject notFound) {
        BagObject value = getBagObject (key);
        return (value != null) ? value : notFound;
    }

    /**
     * Retrieve a mapped element and return it as a BagObject.
     *
     * @param key A string value used to index the element.
     * @param supplier A function to create a new object if the requested key was not found
     * @return The element as a BagObject, or supplier.get () if the element is not found.
     */
    public BagObject getBagObject (String key, Supplier<BagObject> supplier) {
        BagObject value = getBagObject (key);
        return (value != null) ? value : supplier.get ();
    }

    /**
     * Retrieve a mapped element and return it as a BagArray.
     *
     * @param key A string value used to index the element.
     * @return The element as a BagArray, or null if the element is not found.
     */
    public BagArray getBagArray (String key) {
        Object object = getObject (key);
        return (object instanceof BagArray) ? (BagArray) object : null;
        /*
        try {
            return (BagArray) object;
        } catch (ClassCastException exception) {
            log.warn ("Cannot cast value type (" + object.getClass ().getName () + ") to BagArray for key (" + key + ")");
        }
        return null;
        */
    }

    /**
     * Retrieve a mapped element and return it as a BagArray.
     *
     * @param key A string value used to index the element.
     * @param notFound A BagArray to return if the key was not found
     * @return The element as a BagArray, or notFound if the element is not found.
     */
    public BagArray getBagArray (String key, BagArray notFound) {
        BagArray value = getBagArray (key);
        return (value != null) ? value : notFound;
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
