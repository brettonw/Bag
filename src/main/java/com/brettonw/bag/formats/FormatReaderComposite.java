package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;

public class FormatReaderComposite extends FormatReader implements ArrayFormatReader, ObjectFormatReader {
    private EntryHandler entryHandler;

    public FormatReaderComposite () {}

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

    public static FormatReaderComposite basicArrayReader (String input, String arrayDelimiter, String ignore) {
        return new FormatReaderComposite (input, new EntryHandlerArrayFromDelimited (arrayDelimiter).ignore (ignore));
    }

    public static FormatReaderComposite basicArrayReader (String input, String arrayDelimiter) {
        return new FormatReaderComposite (input, new EntryHandlerArrayFromDelimited (arrayDelimiter));
    }

    public static FormatReaderComposite basicObjectReader (String input, String arrayDelimiter, String pairDelimiter, boolean accumulateEntries) {
        return new FormatReaderComposite (input, new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited (arrayDelimiter, new EntryHandlerArrayFromDelimited (pairDelimiter))
        ).accumulateEntries (accumulateEntries));
    }

    public static FormatReaderComposite basicObjectReader (String input, String arrayDelimiter, String pairDelimiter) {
        return basicObjectReader (input, arrayDelimiter, pairDelimiter, false);
    }

    static {
        MimeType.addExtensionMapping (MimeType.PROP, "properties");
        MimeType.addMimeTypeMapping (MimeType.PROP);
        FormatReader.registerFormatReader (MimeType.PROP, false, (input) -> basicObjectReader (input, "\n", "="));

        MimeType.addExtensionMapping (MimeType.URL, "url");
        MimeType.addMimeTypeMapping (MimeType.URL);
        FormatReader.registerFormatReader (MimeType.URL, false, (input) -> basicObjectReader (input, "&", "="));
    }
}
