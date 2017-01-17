package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.*;
import org.junit.Test;

import java.io.File;

public class FormatReaderTableTest {
    @Test
    public void testBasicFixedFormat () {
        String test = " a comment line\n\nA  B  C  D   \naaabbbcccdddd\nabcd\n11 22 33 4444\n";
        MimeType.addMimeTypeMapping (MimeType.FIXED);
        int[][] fields = EntryHandlerArrayFromFixed.fieldsFromWidths (new int[]{3, 3, 3, 4});
        FormatReader.registerFormatReader (MimeType.FIXED, false, (input) ->
                new FormatReaderTable (test, new EntryHandlerArrayFromDelimited ("\n", new EntryHandlerArrayFromFixed (fields)).ignore (" "))
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
        int[][] fields = EntryHandlerArrayFromFixed.fieldsFromWidths (new int[]{3, 3, 3, 4});
        FormatReaderTable frt = new FormatReaderTable (test, new EntryHandlerArrayFromDelimited ("\n", new EntryHandlerArrayFromFixed (fields)).ignore (" "), fieldNames);
        BagArray bagArray = frt.readBagArray ();

        AppTest.report (bagArray.getCount (), 3, "3 valid rows were provided");
        AppTest.report (bagArray.getBagObject (0).getString ("A"), "aaa", "row 0, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (0).getString ("D"), "dddd", "row 0, 4th element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("A"), 11, "row 1, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("D"), 4444, "row 1, 4th element reads correctly");
    }


    @Test
    public void test2le () {
        final String tleFormat = "test/tle";
        FormatReader.registerFormatReader (tleFormat, false, (input) ->
                new FormatReaderTable (input,
                        new EntryHandlerCollector (2, new EntryHandlerArrayFromDelimited ("\n", new EntryHandlerRoller (
                                // exemplars, from https://www.celestrak.com/NORAD/documentation/tle-fmt.asp
                                //new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.widthsFromExemplar ("AAAAAAAAAAAAAAAAAAAAAAAA", ' ')),
                                new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.fieldsFromExemplar ("1 NNNNNU NNNNNAAA NNNNN.NNNNNNNN +.NNNNNNNN +NNNNN-N +NNNNN-N N NNNNN", ' ')),
                                new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.fieldsFromExemplar ("2 NNNNN NNN.NNNN NNN.NNNN NNNNNNN NNN.NNNN NNN.NNNN NN.NNNNNNNNNNNNNN", ' '))
                        ))),
                        BagArrayFrom.array ("1", "a", "i", "c", "d", "e", "f", "g", "h",
                                            "2", "b", "j", "k", "m", "n", "o", "p")));
        BagArray bagArray = BagArrayFrom.file (new File ("data/2le.txt"), tleFormat);
        AppTest.report (bagArray != null, true, "expect successful read");
    }
}
