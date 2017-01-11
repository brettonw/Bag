package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;

public class FormatReaderComposite extends FormatReader {
    private EntryHandler entryHandler;

    public FormatReaderComposite (String input, EntryHandler entryHandler) {
        super (input);
        this.entryHandler = entryHandler;
    }

    @Override
    public BagArray read (BagArray bagArray) {
        // build a bag array and copy its values over...
        BagArray tmpBagArray = (BagArray) entryHandler.getEntry (input);
        if (bagArray == null) {
            bagArray = tmpBagArray;
        } else {
            for (int i = 0, end = tmpBagArray.getCount (); i < end; ++i) {
                bagArray.add (tmpBagArray.getObject (i));
            }
        }
        return bagArray;
    }

    @Override
    public BagObject read (BagObject bagObject) {
        // build a bag object and copy its values over...
        BagObject tmpBagObject = (BagObject) entryHandler.getEntry (input);
        if (bagObject == null) {
            bagObject = tmpBagObject;
        } else {
            String[] keys = tmpBagObject.keys ();
            for (String key : keys) {
                bagObject.put (key, tmpBagObject.getObject (key));
            }
        }
        return bagObject;
    }

    public FormatReaderComposite () {}

    static {
        /*
        MimeType.addExtensionMapping (MimeType.PROP, "properties");
        MimeType.addMimeTypeMapping (MimeType.PROP);
        FormatReader.registerFormatReader (MimeType.PROP, false, (input) -> new FormatReaderComposite (input,
                new "\n", "#", false, "="));
        */
    }
}
