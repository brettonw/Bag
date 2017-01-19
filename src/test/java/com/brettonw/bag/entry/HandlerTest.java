package com.brettonw.bag.entry;

import com.brettonw.AppTest;
import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import org.junit.Test;

public class HandlerTest {
    @Test
    public void test () {
        String test = "command=goodbye&param1=1&param2=2";

        Handler eh = new HandlerObjectFromPairsArray (
                new HandlerArrayFromDelimited ("&", new HandlerArrayFromDelimited ("="))
        );

        BagObject bagObject = (BagObject) eh.getEntry (test);
        AppTest.report (bagObject.getCount () == 3, true, "expect 3 elements in bagObject");
    }

    @Test
    public void testMultiLine () {
        String test = "command=goodbye&param1=1&param2=2\ncommand=hello&param1=2&param2=3\ncommand=dolly&param1=3&param2=5";

        Handler eh = new HandlerArrayFromDelimited ("\n",
                new HandlerObjectFromPairsArray (
                        new HandlerArrayFromDelimited ("&", new HandlerArrayFromDelimited ("="))
                )
        );

        BagArray bagArray = (BagArray) eh.getEntry (test);
        AppTest.report (bagArray.getCount () == 3, true, "expect 3 elements in bagArray");
        AppTest.report (bagArray.getBagObject (1).getCount () == 3, true, "expect 3 elements in contained bagObject");
        AppTest.report (bagArray.getBagObject (1).getInteger ("param2") == 3, true, "expect bagObject value");
    }

    @Test
    public void testFixedFieldsHelperFromPositions () {
        int[][] fields = HandlerArrayFromFixed.fieldsFromPositions (1, 1, 5, 9, 15);
        AppTest.report (fields.length, 3, "check that the correct number of fields are returned");
        AppTest.report (fields[0][0], 0, "check that the first field starts in the correct place");
        AppTest.report (fields[0][1], 4, "check that the first field is the correct length");
        AppTest.report (fields[1][0], 4, "check that the second field starts in the correct place");
        AppTest.report (fields[1][1], 8, "check that the second field is the correct length");
        AppTest.report (fields[2][0], 8, "check that the third field starts in the correct place");
        AppTest.report (fields[2][1], 14, "check that the third field is the correct length");
    }

    @Test
    public void testFixedFieldsHelperFromWidths () {
        int[][] fields = HandlerArrayFromFixed.fieldsFromWidths (4, 4, 6);
        AppTest.report (fields.length, 3, "check that the correct number of fields are returned");
        AppTest.report (fields[0][0], 0, "check that the first field starts in the correct place");
        AppTest.report (fields[0][1], 4, "check that the first field is the correct length");

        AppTest.report (fields[1][0], 4, "check that the second field starts in the correct place");
        AppTest.report (fields[1][1], 8, "check that the second field is the correct length");

        AppTest.report (fields[2][0], 8, "check that the third field starts in the correct place");
        AppTest.report (fields[2][1], 14, "check that the third field is the correct length");
    }

    @Test
    public void testFixedFieldsHelperFromExemplar () {
        int[][] fields = HandlerArrayFromFixed.fieldsFromExemplar ("aaa bbb ccccccc", ' ');
        AppTest.report (fields.length, 3, "check that the correct number of fields are returned");
        AppTest.report (fields[0][0], 0, "check that the first field starts in the correct place");
        AppTest.report (fields[0][1], 3, "check that the first field is the correct length");

        AppTest.report (fields[1][0], 4, "check that the second field starts in the correct place");
        AppTest.report (fields[1][1], 7, "check that the second field is the correct length");

        AppTest.report (fields[2][0], 8, "check that the third field starts in the correct place");
        AppTest.report (fields[2][1], 15, "check that the third field is the correct length");
    }

    @Test
    public void testFixedFieldsHelperFromExemplar2 () {
        int[][] fields = HandlerArrayFromFixed.fieldsFromExemplar ("aaa.bbb..ccdddeeee.fff", '.');
        AppTest.report (fields.length, 6, "check that the correct number of fields are returned");
        AppTest.report (fields[0][0], 0, "check that the first field starts in the correct place");
        AppTest.report (fields[0][1], 3, "check that the first field is the correct length");

        AppTest.report (fields[1][0], 4, "check that the second field starts in the correct place");
        AppTest.report (fields[1][1], 7, "check that the second field is the correct length");

        AppTest.report (fields[2][0], 9, "check that the third field starts in the correct place");
        AppTest.report (fields[2][1], 11, "check that the third field is the correct length");

        AppTest.report (fields[3][0], 11, "check that the third field starts in the correct place");
        AppTest.report (fields[3][1], 14, "check that the third field is the correct length");

        AppTest.report (fields[4][0], 14, "check that the third field starts in the correct place");
        AppTest.report (fields[4][1], 18, "check that the third field is the correct length");

        AppTest.report (fields[5][0], 19, "check that the third field starts in the correct place");
        AppTest.report (fields[5][1], 22, "check that the third field is the correct length");
    }
}
