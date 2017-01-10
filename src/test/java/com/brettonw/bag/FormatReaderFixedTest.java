package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.fixed.FormatReaderFixed;
import org.junit.Test;

public class FormatReaderFixedTest {
    @Test
    public void testBasicFixedFormat () {
        String test = " a comment line\n\nA  B  C  D   \naaabbbcccdddd\nabcd\n11 22 33 4444\n";
        FormatReaderFixed frf = new FormatReaderFixed (test, new int[]{3, 3, 3, 4});
        //FormatReader.registerFormatReader (MimeType.FIXED, false, (input) -> new FormatReaderFixed (test, new int[]{3, 3, 3, 4}));
        BagArray bagArray = frf.read (new BagArray ());

        AppTest.report (bagArray.getCount (), 2, "Only 2 valid rows were provided");
        AppTest.report (bagArray.getBagObject (0).getString ("A"), "aaa", "row 0, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (0).getString ("D"), "dddd", "row 0, 4th element reads correctly");
        AppTest.report (bagArray.getBagObject (1).getInteger ("A"), 11, "row 1, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (1).getInteger ("D"), 4444, "row 1, 4th element reads correctly");
    }

    @Test
    public void testBasicFixedFormatNoFieldNames () {
        String test = " a comment line\n\nA  B  C  D   \naaabbbcccdddd\nabcd\n11 22 33 4444\n";
        FormatReaderFixed frf = new FormatReaderFixed (test, new int[]{3, 3, 3, 4}, FormatReaderFixed.NO_FIELD_NAMES);
        //FormatReader.registerFormatReader (MimeType.FIXED, false, (input) -> new FormatReaderFixed (test, new int[]{3, 3, 3, 4}));
        BagArray bagArray = frf.read (new BagArray ());

        AppTest.report (bagArray.getCount (), 3, "3 valid rows were provided");
        AppTest.report (bagArray.getBagArray (0).getString (0), "A", "row 0, 1st element reads correctly");
        AppTest.report (bagArray.getBagArray (0).getString (3), "D", "row 0, 4th element reads correctly");
        AppTest.report (bagArray.getBagArray (1).getString (0), "aaa", "row 1, 1st element reads correctly");
        AppTest.report (bagArray.getBagArray (1).getString (3), "dddd", "row 1, 4th element reads correctly");
        AppTest.report (bagArray.getBagArray (2).getInteger (0), 11, "row 2, 1st element reads correctly");
        AppTest.report (bagArray.getBagArray (2).getInteger (3), 4444, "row 2, 4th element reads correctly");
    }
}
