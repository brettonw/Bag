package com.brettonw.bag.formats.text;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatWriter;
import com.brettonw.bag.formats.MimeType;

/**
 * The FormatWriterText is a configurable text format writer for any format that uses a divider
 * between entries, and a divider between pairs.
 */
public class FormatWriterText extends FormatWriter {
    String entrySeparator;
    String pairSeparator;

    public FormatWriterText () { super (); }

    public FormatWriterText (String entrySeparator, String pairSeparator) {
        super ();
        this.entrySeparator = entrySeparator;
        this.pairSeparator = pairSeparator;
    }

    @Override
    public String write (BagArray bagArray) {
        StringBuilder stringBuilder = new StringBuilder ();
        for (int i = 0, end = bagArray.getCount (); i < end; ++i) {
            stringBuilder.append (bagArray.getString (i)).append (entrySeparator);
        }
        return stringBuilder.toString ();
    }

    @Override
    public String write (BagObject bagObject) {
        StringBuilder stringBuilder = new StringBuilder ();
        String[] keys = bagObject.keys ();
        for (String key : keys) {
            stringBuilder.append (key).append (pairSeparator).append (bagObject.getString (key)).append (entrySeparator);
        }
        return stringBuilder.toString ();
    }

    static {
        FormatWriter.registerFormatWriter (MimeType.PROP, false, () -> new FormatWriterText ("\n", "="));
        FormatWriter.registerFormatWriter (MimeType.URL, false, () -> new FormatWriterText ("&", "="));
    }
}
