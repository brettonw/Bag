package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.entry.Handler;
import com.brettonw.bag.entry.HandlerArrayFromDelimited;
import com.brettonw.bag.entry.HandlerObjectFromPairsArray;

public class FormatReaderComposite extends FormatReader implements ArrayFormatReader, ObjectFormatReader {
    private Handler handler;

    public FormatReaderComposite () {}

    public FormatReaderComposite (String input, Handler handler) {
        super (input);
        this.handler = handler;
    }

    @Override
    public BagArray readBagArray () {
        return (BagArray) handler.getEntry (input);
    }

    @Override
    public BagObject readBagObject () {
        return (BagObject) handler.getEntry (input);
    }

    public static FormatReaderComposite basicArrayReader (String input, String arrayDelimiter, String ignore) {
        return new FormatReaderComposite (input, new HandlerArrayFromDelimited (arrayDelimiter).ignore (ignore));
    }

    public static FormatReaderComposite basicArrayReader (String input, String arrayDelimiter) {
        return new FormatReaderComposite (input, new HandlerArrayFromDelimited (arrayDelimiter));
    }

    public static FormatReaderComposite basicObjectReader (String input, String arrayDelimiter, String pairDelimiter, boolean accumulateEntries) {
        return new FormatReaderComposite (input, new HandlerObjectFromPairsArray (
                new HandlerArrayFromDelimited (arrayDelimiter, new HandlerArrayFromDelimited (pairDelimiter))
        ).accumulateEntries (accumulateEntries));
    }

    public static FormatReaderComposite basicObjectReader (String input, String arrayDelimiter, String pairDelimiter) {
        return basicObjectReader (input, arrayDelimiter, pairDelimiter, false);
    }

    static {
        MimeType.addExtensionMapping (MimeType.PROP, "properties");
        FormatReader.registerFormatReader (MimeType.PROP, false, (input) -> basicObjectReader (input, "\n", "="));

        MimeType.addExtensionMapping (MimeType.URL, "url");
        FormatReader.registerFormatReader (MimeType.URL, false, (input) -> basicObjectReader (input, "&", "="));
    }
}
