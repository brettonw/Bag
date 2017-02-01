package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.SourceAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;

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
     * @param mimeType
     * @param replace
     * @param factory
     */
    public static void registerFormatReader (String mimeType, boolean replace, Function<String, FormatReader> factory) {
        // try to find the mime type first, and if it's not there, add it
        String foundMimeType = MimeType.getFromMimeType (mimeType, () -> MimeType.addMimeTypeMapping (mimeType));
        if ((! replace) || (! formatReaders.containsKey(foundMimeType))) {
            formatReaders.put(foundMimeType, factory);
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

    static {
        // autoload all the reader subclasses to force their static initializers to get
        // called (but only if the reader constructor is visible, i.e. it's an actual
        // reader endpoint in the class hierarchy and not just a helper or base class.)
        for (Class<?> type : ClassIndex.getSubclasses (FormatReader.class)) {
            try {
                Class.forName (type.getName ()).newInstance ();
            } catch (IllegalAccessException exception) {
                // do nothing
            } catch (ClassNotFoundException | InstantiationException exception) {
                log.error (exception);
            }
        }

        // autoload all the writers, same as above
        for (Class<?> type : ClassIndex.getSubclasses (FormatWriter.class)) {
            try {
                Class.forName (type.getName ()).newInstance ();
            } catch (IllegalAccessException exception) {
                // do nothing
            } catch (ClassNotFoundException | InstantiationException exception) {
                log.error (exception);
            }
        }
    }
}
