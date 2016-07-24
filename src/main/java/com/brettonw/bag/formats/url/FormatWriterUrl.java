package com.brettonw.bag.formats.url;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatWriter;
import com.brettonw.bag.formats.MimeType;

public class FormatWriterUrl extends FormatWriter {
    @Override
    public String write (BagArray bagArray) {
        StringBuilder stringBuilder = new StringBuilder ();
        for (int i = 0, end = bagArray.getCount (); i < end; ++i) {
            if (i > 0) {
                stringBuilder.append ("&");
            }
            stringBuilder.append (bagArray.getString (i));
        }
        return stringBuilder.toString ();
    }

    @Override
    public String write (BagObject bagObject) {
        StringBuilder stringBuilder = new StringBuilder ();
        String[] keys = bagObject.keys ();
        boolean first = true;
        for (String key : keys) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append ("&");
            }
            stringBuilder.append (bagObject.getString (key));
        }
        return stringBuilder.toString ();
    }

    public FormatWriterUrl () { super (); }
    static {
        FormatWriter.registerFormatWriter (MimeType.URL, false, FormatWriterUrl::new);
    }
}
