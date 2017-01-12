package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.SourceAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.classindex.IndexSubclasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@IndexSubclasses
public class FormatReader {
    private static final Logger log = LogManager.getLogger (FormatReader.class);

    protected final String input;

    protected FormatReader () {
        this (null);
    }

    /**
     *
     * @param input
     */
    public FormatReader (String input) {
        this.input = input;
    }

    // static type registration by name
    private static final Map<String, Function<String, FormatReader>> formatReaders = new HashMap<> ();

    /**
     *
     * @param format
     * @param replace
     * @param factory
     */
    public static void registerFormatReader (String format, boolean replace, Function<String, FormatReader> factory) {
        format = format.toLowerCase ();
        if ((! replace) || (! formatReaders.containsKey(format))) {
            formatReaders.put(format, factory);
        }
    }

    private static FormatReader getFormatReader (String stringData, String mimeType, Class iType) {
        // deduce the format, and create the format reader
        String foundMimeType = MimeType.getFromMimeType (mimeType);
        if (foundMimeType != null) {
            FormatReader formatReader = formatReaders.get(foundMimeType).apply (stringData);
            if (formatReader != null) {
                if (iType.isInstance (formatReader)) {
                    return formatReader;
                } else {
                    log.error ("Reader for format (" + mimeType + ") doesn't implement " + iType.getName ());
                }
            } else {
                log.error ("No reader for format (" + mimeType + ")");
            }
        } else {
            log.error ("Unknown format (" + mimeType + ")");
        }
        return null;
    }

    /**
     *
     * @param sourceAdapter
     * @return
     */
    public static BagArray readBagArray (SourceAdapter sourceAdapter) {
        FormatReader formatReader = getFormatReader(sourceAdapter.getStringData(), sourceAdapter.getMimeType(), ArrayFormatReader.class);
        return (formatReader != null) ? ((ArrayFormatReader)formatReader).readBagArray () : null;
    }

    /**
     *
     * @param sourceAdapter
     * @return
     */
    public static BagObject readBagObject (SourceAdapter sourceAdapter) {
        FormatReader formatReader = getFormatReader(sourceAdapter.getStringData(), sourceAdapter.getMimeType(), ObjectFormatReader.class);
        return (formatReader != null) ? ((ObjectFormatReader)formatReader).readBagObject () : null;
    }
}
