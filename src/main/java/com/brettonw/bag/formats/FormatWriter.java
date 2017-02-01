package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@IndexSubclasses
abstract public class FormatWriter {
    private static final Logger log = LogManager.getLogger (FormatReader.class);

    protected static final String[] QUOTES = { "\"" };

    protected String enclose (String input, String[] bracket) {
        String bracket0 = bracket[0];
        String bracket1 = (bracket.length > 1) ? bracket[1] : bracket0;
        return bracket0 + input + bracket1;
    }

    protected String quote (String input) {
        return enclose (input, QUOTES);
    }

    abstract public String write (BagObject bagObject);
    abstract public String write (BagArray bagArray);

    // static type registration by name
    private static final Map<String, FormatWriter> formatWriters = new HashMap<>();

    public static void registerFormatWriter (String format, boolean replace, Supplier<FormatWriter> supplier) {
        if ((! replace) || (! formatWriters.containsKey(format))) {
            formatWriters.put(format, supplier.get());
        }
    }

    public static String write (BagObject bagObject, String format) {
        if (formatWriters.containsKey(format)) {
            return formatWriters.get(format).write (bagObject);
        }
        return null;
    }

    public static String write (BagArray bagArray, String format) {
        if (formatWriters.containsKey(format)) {
            return formatWriters.get(format).write (bagArray);
        }
        return null;
    }

    static {
        // autoload all the writer subclasses to force their static initializers to get
        // called (but only if the writer constructor is visible, i.e. it's an actual
        // writer endpoint in the class hierarchy and not just a helper or base class.)
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
