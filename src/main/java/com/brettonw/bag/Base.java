package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

abstract class Base {
    private static final Logger log = LogManager.getLogger (Base.class);

    // data and functions for exporting as strings
    static final String SQUARE_BRACKETS[] = { "[", "]" };
    static final String CURLY_BRACKETS[] = { "{", "}" };

    private static final String QUOTES[] = { "\"" };
    private static final String ANGLE_BRACKETS[] = { "<", ">" };
    private static final String ANGLE_CLOSE_BRACKETS[] = { "</", ">" };


    String enclose (String input, String bracket[]) {
        String bracket0 = bracket[0];
        String bracket1 = (bracket.length > 1) ? bracket[1] : bracket0;
        return bracket0 + input + bracket1;
    }

    String quote (String input) {
        return enclose (input, QUOTES);
    }

    String encloseXml (String name, String input) {
        String brackets[] = { enclose (name, ANGLE_BRACKETS), enclose (name, ANGLE_CLOSE_BRACKETS) };
        return enclose (input, brackets);
    }

    String getJsonString (Object object) {
        if (object != null) {
            switch (object.getClass ().getName ()) {
                case "java.lang.String":
                    return quote ((String) object);

                case "com.brettonw.bag.BagObject":
                case "com.brettonw.bag.BagArray":
                    return ((Base) object).toJsonString ();

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

    String getXmlString (String name, Object object) {
        if (object != null) {
            switch (object.getClass ().getName ()) {
                case "java.lang.String":
                    return encloseXml (name, (String) object);

                case "com.brettonw.bag.BagObject":
                case "com.brettonw.bag.BagArray":
                    return ((Base) object).toXmlString (name);

                // we omit the default case, because there should not be any other types stored in
                // the Bag class - as in, they would not make it into the container, as the
                // "objectify" method will gate that
            }
        }
        // if we stored a null, we need to emit it as a value. This will only happen in the
        // array types
        return encloseXml (name, "");
    }



    Object objectify (Object value) {
        if (value != null) {
            String className = value.getClass ().getName ();
            switch (className) {
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
                    log.error ("Unhandled type: " + className);
                    break;
            }
        }
        return null;
    }

    abstract public String toJsonString ();
    abstract public String toXmlString (String name);

    @Override
    public String toString () {
        return toJsonString ();
    }
}
