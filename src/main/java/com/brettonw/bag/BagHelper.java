package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class BagHelper {
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

                default:
                    // no other type should be stored in the bag classes
                    log.debug ("Unhandled type: " + value.getClass ().getName ());
                    break;
            }
        }
        return null;
    }

    public static Object objectify (Object value) {
        if (value != null) {
            String className = value.getClass ().getName ();
            switch (className) {
                case "java.lang.String":
                    return value;

                case "java.lang.Long": case "java.lang.Integer": case "java.lang.Short": case "java.lang.Byte":
                case "java.lang.Char":
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

    public static boolean isPrimitive (Class cls) {
        // obviously, if Java thinks it's a primitive, it is
        if (cls.isPrimitive ()) {
            return true;
        }

        // but we want this for boxed primitives and strings as well
        switch (cls.getName ()) {
            case "java.lang.Long": case "java.lang.Integer": case "java.lang.Short": case "java.lang.Byte":
            case "java.lang.Char":
            case "java.lang.Boolean":
            case "java.lang.Double": case "java.lang.Float":
            case "java.lang.String":
                return true;
        }

        // it wasn't any of those, return false;
        return false;
    }
}
