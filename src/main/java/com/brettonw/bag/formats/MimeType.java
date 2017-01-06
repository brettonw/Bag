package com.brettonw.bag.formats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MimeType {
    private static final Logger log = LogManager.getLogger (MimeType.class);

    public static final String JSON = "application/json";
    //public static final String XML = "application/xml";
    //public static final String CSV = "application/csv";
    public static final String URL = "application/x-www-form-urlencoded";
    public static final String TEXT = "application/text";
    public static final String PROP = "text/x-java-properties";

    public static final String DEFAULT = JSON;

    private static final Map<String, String> extensionMappings = new HashMap<> ();

    public static void addExtensionMapping (String mimeType, String... extensions) {
        for (String extension : extensions) {
            if (extensionMappings.containsKey (extension)) {
                log.error ("Duplicate file extension mapping (" + extension + ") for MIME type (" + mimeType +")");
            } else {
                extensionMappings.put (extension, mimeType);
            }
        }
    }

    /**
     * Returns the known format reader mime type by its registered file name extension
     * @param extension
     * @return
     */
    public static String getFromExtension (String extension) {
        return extensionMappings.get(extension.toLowerCase ());
    }

    private static final Map<String, String> mimeTypeRemappings = new HashMap<> ();

    public static void addMimeTypeMapping (String mimeType, String... synonyms) {
        mimeTypeRemappings.put (mimeType, mimeType);
        for (String synonym : synonyms) {
            mimeTypeRemappings.put (synonym, mimeType);
        }
    }

    /**
     * Returns a mime type with a known format reader from the given mime type. Some MIME types are
     * application or vendor specific examples that use a standard underlying format, like XML.
     * There are also examples of synonym types, like "text/csv" and "application/csv" that we want
     * to support.
     */
    public static String getFromMimeType (String mimeType, Supplier<String> notFound) {
        return mimeTypeRemappings.containsKey (mimeType) ? mimeTypeRemappings.get (mimeType) : notFound.get ();
    }

    /**
     * Returns a mime type with a known format reader from the given mime type. Unknown types are
     * treated as the default. @see #getFromMimeType(String,Supplier)
     */
    public static String getFromMimeType (String mimeType) {
        return getFromMimeType (mimeType, () -> DEFAULT);
    }
}
