package com.brettonw.bag.formats.url;

// The FormatReaderJson is loosely modeled after a JSON parser grammar from the site (http://www.json.org).
// The main difference is that we ignore differences between value types (all of them will be
// strings internally), and assume the input is a well formed string representation of a BagObject
// or BagArray in JSON-ish format

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatReader;
import com.brettonw.bag.formats.MimeType;

public class FormatReaderUrl extends FormatReader {
    public FormatReaderUrl (String input) {
        super (input);
    }

    @Override
    public BagArray read (BagArray bagArray) {
        if (bagArray == null) bagArray = new BagArray ();

            String[] queryParameters = input.split ("&");
            for (String queryParameter : queryParameters) {
                bagArray.add (queryParameter);
            }

        return bagArray;
    }

    @Override
    public BagObject read (BagObject bagObject) {
        if (bagObject == null) bagObject = new BagObject ();

        String[] queryParameters = input.split ("&");
        for (String queryParameter : queryParameters) {
            String[] pair = queryParameter.split ("=");
            bagObject.put (pair[0], pair[1]);
        }

        return bagObject;
    }

    public FormatReaderUrl () { super (); }
    static {
        FormatReader.registerFormatReader (MimeType.URL, false, FormatReaderUrl::new);
    }
}
