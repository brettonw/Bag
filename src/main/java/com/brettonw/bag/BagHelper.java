package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

enum BagHelper { ;
    private static final Logger log = LogManager.getLogger (BagHelper.class);

    public static String enclose (String input, String bracket) {
        char bracket0 = bracket.charAt (0);
        char bracket1 = bracket.length () > 1 ? bracket.charAt (1) : bracket0;
        return new StringBuilder ().append (bracket0).append (input).append (bracket1).toString ();
    }

    public static String quote (String input) {
        // XXX should this escape quote marks in the string?
        return enclose (input, "\"");
    }

    public static String stringify (Object value) {
        if (value != null) {
            switch (value.getClass ().getName ()) {
                case "java.lang.String":
                    return quote ((String) value);

                case "com.brettonw.bag.BagObject":
                case "com.brettonw.bag.BagArray":
                    return value.toString ();

                // we omit the default case, because there should not be any other types stored in
                // the Bag class - as in, they would not make it into the container, as the
                // "objectify" method will gate that
            }
        }
        return null;
    }

    public static Object objectify (Object value) {
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
                    // XXX throw an exception?
                    log.error ("Unhandled type: " + className);
                    break;
            }
        }
        return null;
    }
}
