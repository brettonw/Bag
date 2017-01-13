package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.EntryHandler;
import com.brettonw.bag.formats.EntryHandlerArrayFromDelimited;
import com.brettonw.bag.formats.EntryHandlerObjectFromPairsArray;
import org.junit.Test;

public class EntryHandlerTest {
    @Test
    public void test () {
        String test = "command=goodbye&param1=1&param2=2";

        EntryHandler eh = new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited ("&", new EntryHandlerArrayFromDelimited ("="))
        );

        BagObject bagObject = (BagObject) eh.getEntry (test);
        AppTest.report (bagObject.getCount () == 3, true, "expect 3 elements in bagObject");
    }

    @Test
    public void testMultiLine () {
        String test = "command=goodbye&param1=1&param2=2\ncommand=hello&param1=2&param2=3\ncommand=dolly&param1=3&param2=5";

        EntryHandler eh = new EntryHandlerArrayFromDelimited ("\n",
                new EntryHandlerObjectFromPairsArray (
                        new EntryHandlerArrayFromDelimited ("&", new EntryHandlerArrayFromDelimited ("="))
                )
        );

        BagArray bagArray = (BagArray) eh.getEntry (test);
        AppTest.report (bagArray.getCount () == 3, true, "expect 3 elements in bagArray");
        AppTest.report (bagArray.getBagObject (1).getCount () == 3, true, "expect 3 elements in contained bagObject");
        AppTest.report (bagArray.getBagObject (1).getInteger ("param2") == 3, true, "expect bagObject value");
    }
}
