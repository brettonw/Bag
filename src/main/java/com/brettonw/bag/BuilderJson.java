package com.brettonw.bag;

public class BuilderJson extends Builder {
    public static final String JSON_FORMAT = "json";

    static final String[] CURLY_BRACKETS = { "{", "}" };
    static final String[] SQUARE_BRACKETS = { "[", "]" };

    BuilderJson () {}

    String getJsonString (Object object) {
        if (object != null) {
            switch (object.getClass ().getName ()) {
                case "java.lang.String": return quote ((String) object);
                case "com.brettonw.bag.BagObject": return from ((BagObject) object);
                case "com.brettonw.bag.BagArray": return from ((BagArray) object);

                // we omit the default case, because there should not be any other types stored in
                // the Bag classes - as in, they would not make it into the container, as the
                // "objectify" method will gate that
            }
        }
        // if we stored a null, we need to emit it as a value. This will only happen in the
        // array types, and is handled on the parsing side with a special case for reading
        // the bare value 'null' (not quoted)
        return "null";
    }

    @Override
    public String from (BagObject bagObject) {
        StringBuilder stringBuilder = new StringBuilder ();
        String separator = "";
        String keys[] = bagObject.keys();
        for (int i = 0, end = keys.length; i < end; ++i) {
            stringBuilder
                    .append(separator)
                    .append(quote(keys[i]))
                    .append(":")
                    .append(getJsonString(bagObject.getObject(keys[i])));
            Object object = bagObject.getObject(keys[i]);
            separator = ",";
        }
        return enclose(stringBuilder.toString(), CURLY_BRACKETS);
    }

    @Override
    public String from (BagArray bagArray) {
        StringBuilder stringBuilder = new StringBuilder ();
        String separator = "";
        for (int i = 0, end = bagArray.getCount(); i < end; ++i) {
            stringBuilder
                    .append(separator)
                    .append(getJsonString(bagArray.getObject(i)));
            separator = ",";
        }
        return enclose(stringBuilder.toString(), SQUARE_BRACKETS);
    }

    // install me as the default JSON format builder
    static {
        Builder.registerBuilder(JSON_FORMAT, false, () -> new BuilderJson());
    }
}
