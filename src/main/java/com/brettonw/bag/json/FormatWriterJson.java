package com.brettonw.bag.json;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.FormatWriter;

public class FormatWriterJson extends FormatWriter {
    public static final String JSON_FORMAT = "json";

    static final String[] CURLY_BRACKETS = { "{", "}" };
    static final String[] SQUARE_BRACKETS = { "[", "]" };

    private String getJsonString (Object object) {
        if (object != null) {
            switch (object.getClass ().getName ()) {
                case "java.lang.String": return quote ((String) object);
                case "com.brettonw.bag.BagObject": return write ((BagObject) object);
                case "com.brettonw.bag.BagArray": return write ((BagArray) object);

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
    public String write (BagObject bagObject) {
        StringBuilder stringBuilder = new StringBuilder ();
        String separator = "";
        String keys[] = bagObject.keys();
        for (String key : keys) {
            stringBuilder
                    .append (separator)
                    .append (quote (key))
                    .append (":")
                    .append (getJsonString (bagObject.getObject (key)));
            separator = ",";
        }
        return enclose(stringBuilder.toString(), CURLY_BRACKETS);
    }

    @Override
    public String write (BagArray bagArray) {
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

    // install me as the default JSON format write
    static {
        registerFormatWriter (JSON_FORMAT, false, FormatWriterJson::new);
    }
}
