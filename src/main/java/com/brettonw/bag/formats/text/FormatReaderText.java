package com.brettonw.bag.formats.text;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatReader;
import com.brettonw.bag.formats.MimeType;

/**
 * The FormatReaderText is a configurable text format reader for any format that uses a divider
 * between entries, and a divider between pairs. An optional "comment" character is supported to
 * allow some entries to be skipped on load
 */
public class FormatReaderText extends FormatReader {
    String entrySeparator;
    String ignoreEntryMarker;
    boolean accumulateEntries;
    String pairSeparator;

    public FormatReaderText (String input, String entrySeparator, boolean accumulateEntries, String pairSeparator) {
        this (input, entrySeparator, " ", accumulateEntries, pairSeparator);
    }

    public FormatReaderText (String input, String entrySeparator, String ignoreEntryMarker, boolean accumulateEntries, String pairSeparator) {
        super (input);
        this.entrySeparator = entrySeparator;
        this.ignoreEntryMarker = ignoreEntryMarker;
        this.accumulateEntries = accumulateEntries;
        this.pairSeparator = pairSeparator;
    }

    @Override
    public BagArray read (BagArray bagArray) {
        if (bagArray == null) bagArray = new BagArray ();
        String[] entries = input.split (entrySeparator);
        for (String entry : entries) {
            entry = entry.trim ();
            if ((entry.length () > 0) && (! (entry.startsWith (ignoreEntryMarker)))) {
                bagArray.add (entry);
            }
        }
        return bagArray;
    }

    @Override
    public BagObject read (BagObject bagObject) {
        if (bagObject == null) bagObject = new BagObject ();
        String[] entries = input.split (entrySeparator);
        for (String entry : entries) {
            entry = entry.trim ();
            if ((entry.length () > 0) && (! (entry.startsWith (ignoreEntryMarker)))) {
                String[] pair = entry.split (pairSeparator, 2);
                if (pair.length == 2) {
                    String key = pair[0].trim ();
                    String value = pair[1].trim ();
                    if ((key.length () > 0) && (value.length () > 0)) {
                        if (accumulateEntries) {
                            bagObject.add (key, value);
                        } else {
                            bagObject.put (key, value);
                        }
                    }
                }
            }
        }
        return bagObject;
    }

    public FormatReaderText () { super (); }
    static {
        MimeType.addExtensionMapping (MimeType.PROP, "properties");
        MimeType.addMimeTypeMapping (MimeType.PROP);
        FormatReader.registerFormatReader (MimeType.PROP, false, (input) -> new FormatReaderText (input, "\n", "#", false, "="));

        MimeType.addExtensionMapping (MimeType.URL, "url");
        MimeType.addMimeTypeMapping (MimeType.URL);
        FormatReader.registerFormatReader (MimeType.URL, false, (input) -> new FormatReaderText (input, "&", false, "="));
    }
}
