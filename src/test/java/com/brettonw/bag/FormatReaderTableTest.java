package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.*;
import org.junit.Test;

public class FormatReaderTableTest {
    @Test
    public void testBasicFixedFormat () {
        String test = " a comment line\n\nA  B  C  D   \naaabbbcccdddd\nabcd\n11 22 33 4444\n";
        MimeType.addMimeTypeMapping (MimeType.FIXED);
        FormatReader.registerFormatReader (MimeType.FIXED, false, (input) ->
                new FormatReaderTable (test, new EntryHandlerArrayFromDelimited ("\n", new EntryHandlerArrayFromFixed (new int[]{3, 3, 3, 4})).ignore (" "))
        );
        BagArray bagArray = BagArrayFrom.string (test, MimeType.FIXED);

        AppTest.report (bagArray.getCount (), 3, "3 valid rows were provided");
        AppTest.report (bagArray.getBagObject (0).getString ("A"), "aaa", "row 0, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (0).getString ("D"), "dddd", "row 0, 4th element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("A"), 11, "row 1, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("D"), 4444, "row 1, 4th element reads correctly");
    }

    @Test
    public void testBasicFixedFormatWithFieldNames () {
        String test = " a comment line\n\naaabbbcccdddd\nabcd\n11 22 33 4444\n";
        BagArray fieldNames = new BagArray ().add ("A").add ("B").add ("C").add ("D");
        FormatReaderTable frt = new FormatReaderTable (test, new EntryHandlerArrayFromDelimited ("\n", new EntryHandlerArrayFromFixed (new int[]{3, 3, 3, 4})).ignore (" "), fieldNames);
        BagArray bagArray = frt.readBagArray ();

        AppTest.report (bagArray.getCount (), 3, "3 valid rows were provided");
        AppTest.report (bagArray.getBagObject (0).getString ("A"), "aaa", "row 0, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (0).getString ("D"), "dddd", "row 0, 4th element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("A"), 11, "row 1, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("D"), 4444, "row 1, 4th element reads correctly");
    }
}
