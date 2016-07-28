package com.brettonw.bag.formats.text;

// The FormatReaderJson is loosely modeled after a JSON parser grammar from the site (http://www.json.org).
// The main difference is that we ignore differences between value types (all of them will be
// strings internally), and assume the input is a well formed string representation of a BagObject
// or BagArray in JSON-ish format

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatReader;
import com.brettonw.bag.formats.MimeType;

public class FormatReaderText extends FormatReader {
    public FormatReaderText (String input) {
        super (input);
    }

    @Override
    public BagArray read (BagArray bagArray) {
        if (bagArray == null) bagArray = new BagArray ();
        // treat each line as an element in the array
        String[] lines = input.split ("\n");
        for (String line : lines) {
            line = line.trim ();
            if (! (line.startsWith ("#") || line.startsWith ("//"))) {
                if (line.length () > 0) {
                    bagArray.add (line);
                }
            }
        }
        return bagArray;
    }

    @Override
    public BagObject read (BagObject bagObject) {
        if (bagObject == null) bagObject = new BagObject ();
        // treat each line as an element description
        String[] lines = input.split ("\n");
        for (String line : lines) {
            line = line.trim ();
            if (! (line.startsWith ("#") || line.startsWith ("//"))) {
                // merge : and = characters, they seem to be used consistently one way or another
                line = line.replace (":", "=");
                String[] pair = line.split ("=");
                if (pair.length == 2) {
                    bagObject.put (pair[0], pair[1]);
                }
            }
        }

        return bagObject;
    }

    public FormatReaderText () { super (); }
    static {
        MimeType.addExtensionMapping (MimeType.TEXT, "txt", "text");
        MimeType.addMimeTypeMapping (MimeType.TEXT, "text/text");
        FormatReader.registerFormatReader (MimeType.TEXT, false, FormatReaderText::new);
    }
}
