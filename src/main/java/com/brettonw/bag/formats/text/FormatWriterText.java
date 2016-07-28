package com.brettonw.bag.formats.text;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatWriter;
import com.brettonw.bag.formats.MimeType;

public class FormatWriterText extends FormatWriter {
    @Override
    public String write (BagArray bagArray) {
        StringBuilder stringBuilder = new StringBuilder ();
        for (int i = 0, end = bagArray.getCount (); i < end; ++i) {
            stringBuilder.append (bagArray.getString (i)).append ("\n");
        }
        return stringBuilder.toString ();
    }

    @Override
    public String write (BagObject bagObject) {
        StringBuilder stringBuilder = new StringBuilder ();
        String[] keys = bagObject.keys ();
        for (String key : keys) {
            stringBuilder.append (key).append (":").append (bagObject.getString (key)).append ("\n");
        }
        return stringBuilder.toString ();
    }

    public FormatWriterText () { super (); }
    static {
        FormatWriter.registerFormatWriter (MimeType.TEXT, false, FormatWriterText::new);
    }
}
