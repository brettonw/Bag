package com.brettonw.bag;

import java.util.HashMap;
import java.util.Map;

public class MimeType {
    public static final String JSON = "application/json";
    public static final String XML = "application/xml";
    public static final String CSV = "text/csv";

    public static final String DEFAULT = JSON;

    private static final Map<String, String> extensions;
    static  {
        extensions = new HashMap<> ();
        extensions.put ("json", JSON);
        extensions.put ("xml", XML);
        extensions.put ("csv", CSV);
    }

    public static String getFromExtension (String extension) {
        return extensions.get(extension.toLowerCase ());
    }
}
