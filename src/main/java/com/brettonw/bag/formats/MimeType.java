package com.brettonw.bag.formats;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MimeType {
    public static final String JSON = "application/json";
    public static final String XML = "application/xml";
    public static final String CSV = "application/csv";
    public static final String URL = "application/url";

    public static final String DEFAULT = JSON;

    private static final Map<String, String> extensions;
    static  {
        extensions = new HashMap<> ();
        extensions.put ("json", JSON);
        extensions.put ("xml", XML);
        extensions.put ("csv", CSV);
        extensions.put ("url", URL);
    }

    /**
     * Returns the known format reader mime type by its registered file name extension
     * @param extension
     * @return
     */
    public static String getFromExtension (String extension) {
        return extensions.get(extension.toLowerCase ());
    }

    private static final Map<String, String> mimeTypes;

    private static void addMimeType (String mimeType, String... synonyms) {
        mimeTypes.put (mimeType, mimeType);
        for (String synonym : synonyms) {
            mimeTypes.put (synonym, mimeType);
        }
    }

    static {
        mimeTypes = new HashMap<> ();
        addMimeType (JSON, "text/json");
        addMimeType (XML, "text/xml");
        addMimeType (CSV, "text/csv");
        addMimeType (URL);
    }

    /**
     * Returns a mime type with a known format reader from the given mime type. Some MIME types are
     * application or vendor specific examples that use a standard underlying format, like XML.
     * There are also examples of synonym types, like "text/csv" and "application/csv" that we want
     * to support.
     */
    public static String getFromMimeType (String mimeType, Supplier<String> notFound) {
        return mimeTypes.containsKey (mimeType) ? mimeTypes.get (mimeType) : notFound.get ();
    }

    /**
     * Returns a mime type with a known format reader from the given mime type. Unknown types are
     * treated as the default. @see #getFromMimeType(String,Supplier)
     */
    public static String getFromMimeType (String mimeType) {
        return getFromMimeType (mimeType, () -> DEFAULT);
    }
}
