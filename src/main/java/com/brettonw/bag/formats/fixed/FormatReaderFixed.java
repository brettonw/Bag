package com.brettonw.bag.formats.fixed;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.formats.FormatReader;
import com.brettonw.bag.formats.MimeType;

/**
 * The FormatReaderFixed is a fixed-width field format reader, with an option to
 * read the first data row as field names, or to take an array of field names
 */
public class FormatReaderFixed extends FormatReader {
    public static final String[] NO_FIELD_NAMES = {};

    int expectedLineLength;
    int[] fieldLengths;
    String[] fieldNames;

    public FormatReaderFixed (String input, int[] fieldLengths) {
        this (input, fieldLengths, null);
    }

    public FormatReaderFixed (String input, int[] fieldLengths, String[] fieldNames) {
        super (input);

        // set up the field lengths and expected line length as a sum of all fields plus
        // the carriage return
        this.fieldLengths = fieldLengths;
        expectedLineLength = 1;
        for (int fieldLength: fieldLengths) {
            expectedLineLength += fieldLength;
        }

        // try to get the field names, either as provided, or from the first row - pass
        // NO_FIELD_NAMES to force the reader to process each line as an array rather than
        // as a bag
        this.fieldNames = (fieldNames != null) ? ((fieldNames.length > 0) ? fieldNames : null) : getFieldValues ();
    }

    private String getDataLine () {
        // XXX probably need a way to say, data starts on line x, or a comment character
        // XXX that can be used at the front of the line
        String line;
        // a zero length line is the end of the file
        while ((line = line ()).length() > 0) {
            // if the line is the expected length, return it, otherwise keep reading
            if (line.length () == expectedLineLength) {
                return line;
            }
        }
        return line;
    }

    private String[] getFieldValues () {
        String line = getDataLine ();
        if (line.length () > 0) {
            String[] result = new String[fieldLengths.length];
            int start = 0;
            for (int i = 0; i < fieldLengths.length; ++i) {
                int end = start + fieldLengths[i];
                result[i] = line.substring (start, end).trim ();
                start = end;
            }
            return result;
        }
        return null;
    }

    @Override
    public BagArray read (BagArray bagArray) {
        if (bagArray == null) bagArray = new BagArray ();
        String[] fieldValues;
        while ((fieldValues = getFieldValues ()) != null) {
            if (fieldNames != null) {
                BagObject rowBagObject = new BagObject ();
                for (int i = 0; i < fieldNames.length; ++i) {
                    rowBagObject.add (fieldNames[i], fieldValues[i]);
                }
                bagArray.add (rowBagObject);
            } else {
                BagArray rowBagArray = new BagArray (fieldValues.length);
                for (String fieldValue : fieldValues) {
                    rowBagArray.add (fieldValue);
                }
                bagArray.add (rowBagArray);
            }
        }
        return bagArray;
    }

    @Override
    public BagObject read (BagObject bagObject) {
        if (bagObject == null) bagObject = new BagObject ();
        // XXX not sure what this should do, perhaps use the first field in a line as a row name?
        return bagObject;
    }

    public FormatReaderFixed () {}

    static {
        MimeType.addMimeTypeMapping (MimeType.FIXED);
    }
}
