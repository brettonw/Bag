package com.brettonw.bag.formats;

import com.brettonw.AppTest;
import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagArrayFrom;
import com.brettonw.bag.entry.HandlerArrayFromDelimited;
import com.brettonw.bag.entry.HandlerArrayFromFixed;
import com.brettonw.bag.entry.HandlerCollector;
import com.brettonw.bag.entry.HandlerRoller;
import com.brettonw.bag.formats.*;
import org.junit.Test;

import java.io.File;

public class FormatReaderTableTest {
    @Test
    public void testBasicFixedFormat () {
        String test = " a comment line\n\nA  B  C  D   \naaabbbcccdddd\nabcd\n11 22 33 4444\n";
        MimeType.addMimeTypeMapping (MimeType.FIXED);
        int[][] fields = HandlerArrayFromFixed.fieldsFromWidths (new int[]{3, 3, 3, 4});
        FormatReader.registerFormatReader (MimeType.FIXED, false, (input) ->
                new FormatReaderTable (test, new HandlerArrayFromDelimited ("\n", new HandlerArrayFromFixed (fields)).ignore (" "))
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
        int[][] fields = HandlerArrayFromFixed.fieldsFromWidths (new int[]{3, 3, 3, 4});
        FormatReaderTable frt = new FormatReaderTable (test, new HandlerArrayFromDelimited ("\n", new HandlerArrayFromFixed (fields)).ignore (" "), fieldNames);
        BagArray bagArray = frt.readBagArray ();

        AppTest.report (bagArray.getCount (), 3, "3 valid rows were provided");
        AppTest.report (bagArray.getBagObject (0).getString ("A"), "aaa", "row 0, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (0).getString ("D"), "dddd", "row 0, 4th element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("A"), 11, "row 1, 1st element reads correctly");
        AppTest.report (bagArray.getBagObject (2).getInteger ("D"), 4444, "row 1, 4th element reads correctly");
    }

    @Test
    public void test2le () {
        final String tleFormat = "test/2le";
        FormatReader.registerFormatReader (tleFormat, false, (input) ->
                new FormatReaderTable (input,
                        new HandlerCollector (2, new HandlerArrayFromDelimited ("\n", new HandlerRoller (
                                // exemplars, from https://www.celestrak.com/NORAD/documentation/tle-fmt.asp
                                //new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.widthsFromExemplar ("AAAAAAAAAAAAAAAAAAAAAAAA", ' ')),
                                new HandlerArrayFromFixed (HandlerArrayFromFixed.fieldsFromExemplar ("1 NNNNNU yyNNNAAA yyNNNNNNNNNNNN NNNNNNNNNN NNNNNNNN NNNNNNNN N NNNNc", ' ')),
                                new HandlerArrayFromFixed (HandlerArrayFromFixed.fieldsFromExemplar ("2 NNNNN NNNNNNNN NNNNNNNN NNNNNNN NNNNNNNN NNNNNNNN NNNNNNNNNNNnnnnnc", ' '))
                        ))),
                        BagArrayFrom.array (
                                "1", "A", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                                "2", "B", "O", "P", "Q", "R", "S", "T", "U", "V")));
        BagArray bagArray = BagArrayFrom.file (new File ("data/2le.txt"), tleFormat);
        AppTest.report (bagArray != null, true, "expect successful read");
    }

    @Test
    public void test3le () {
        final String tleFormat = "test/3le";
        FormatReader.registerFormatReader (tleFormat, false, (input) ->
                new FormatReaderTable (input,
                        new HandlerCollector (3, new HandlerArrayFromDelimited ("\n", new HandlerRoller (
                                // exemplars, from https://www.celestrak.com/NORAD/documentation/tle-fmt.asp
                                new HandlerArrayFromFixed (HandlerArrayFromFixed.fieldsFromExemplar ("0 AAAAAAAAAAAAAAAAAAAAAAAA", ' ')),
                                new HandlerArrayFromFixed (HandlerArrayFromFixed.fieldsFromExemplar ("1 NNNNNU yyNNNAAA yyNNNNNNNNNNNN NNNNNNNNNN NNNNNNNN NNNNNNNN N NNNNc", ' ')),
                                new HandlerArrayFromFixed (HandlerArrayFromFixed.fieldsFromExemplar ("2 NNNNN NNNNNNNN NNNNNNNN NNNNNNN NNNNNNNN NNNNNNNN NNNNNNNNNNNnnnnnc", ' '))
                        ))),
                        BagArrayFrom.array ("0", "NAME",
                                "1", "A", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                                "2", "B", "O", "P", "Q", "R", "S", "T", "U", "V")));
        BagArray bagArray = BagArrayFrom.file (new File ("data/3le.txt"), tleFormat);
        AppTest.report (bagArray != null, true, "expect successful read");
    }
}
