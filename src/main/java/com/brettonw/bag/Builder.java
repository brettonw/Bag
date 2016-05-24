package com.brettonw.bag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

abstract class Builder {
    public static final String DEFAULT_FORMAT = "default";

    protected static final String[] QUOTES = { "\"" };

    String enclose (String input, String[] bracket) {
        String bracket0 = bracket[0];
        String bracket1 = (bracket.length > 1) ? bracket[1] : bracket0;
        return bracket0 + input + bracket1;
    }

    String quote (String input) {
        return enclose (input, QUOTES);
    }

    abstract public String from (BagObject bagObject);
    abstract public String from (BagArray bagArray);

    // static type registration by name
    private static Map<String, Builder> builders = new HashMap<>();
    public static void registerBuilder (String format, boolean replace, Supplier<Builder> supplier) {
        if ((! replace) || (! builders.containsKey(format))) {
            builders.put(format, supplier.get());
        }
    }

    public static String from (BagObject bagObject, String format) {
        if (builders.containsKey(format)) {
            return builders.get(format).from (bagObject);
        }
        return null;
    }

    public static String from (BagArray bagArray, String format) {
        if (builders.containsKey(format)) {
            return builders.get(format).from (bagArray);
        }
        return null;
    }

    // JSON is the default format
    static {
        Builder.registerBuilder(DEFAULT_FORMAT, false, () -> new BuilderJson());
    }
}
