package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;

public class FormatReaderComposite extends FormatReader implements ArrayFormatReader, ObjectFormatReader {
    private EntryHandler entryHandler;

    public FormatReaderComposite (String input, EntryHandler entryHandler) {
        super (input);
        this.entryHandler = entryHandler;
    }

    @Override
    public BagArray readBagArray () {
        return (BagArray) entryHandler.getEntry (input);
    }

    @Override
    public BagObject readBagObject () {
        return (BagObject) entryHandler.getEntry (input);
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
