package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.*;
import org.junit.Test;

import static com.brettonw.bag.formats.EntryHandlerValue.ENTRY_HANDLER_VALUE;

public class EntryHandlerTest {
    @Test
    public void testCompositeReader () {
        String test = "command=goodbye&param1=1&param2=2";

        FormatReaderComposite frc = new FormatReaderComposite (test, new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited ("&", ENTRY_HANDLER_VALUE),
                new EntryHandlerArrayFromDelimited ("=", ENTRY_HANDLER_VALUE),
                ENTRY_HANDLER_VALUE
        ));
        BagObject bagObject = frc.readBagObject ();
        AppTest.report (bagObject.getCount () == 3, true, "expect 3 elements in bagObject");
        AppTest.report (bagObject.getString ("command"), "goodbye", "expect text elements in bagObject");
        AppTest.report (bagObject.getInteger ("param2"), 2, "expect int elements in bagObject");
    }

    @Test
    public void testRegisteredCompositeReader () {
        String test = "command=goodbye&param1=1&param2=2";
        final String testMimeType = "test/test2";
        MimeType.addMimeTypeMapping (testMimeType);
        FormatReader.registerFormatReader (testMimeType, false, (input) -> new FormatReaderComposite (input, new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited ("&", ENTRY_HANDLER_VALUE),
                new EntryHandlerArrayFromDelimited ("=", ENTRY_HANDLER_VALUE),
                ENTRY_HANDLER_VALUE
        )));
        BagObject bagObject = BagObjectFrom.string (test, testMimeType);
        AppTest.report (bagObject.getCount () == 3, true, "expect 3 elements in bagObject");
        AppTest.report (bagObject.getString ("command"), "goodbye", "expect text elements in bagObject");
        AppTest.report (bagObject.getInteger ("param2"), 2, "expect int elements in bagObject");
    }

    @Test
    public void test () {
        String test = "command=goodbye&param1=1&param2=2";

        EntryHandler eh = new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited ("&", ENTRY_HANDLER_VALUE),
                new EntryHandlerArrayFromDelimited ("=", ENTRY_HANDLER_VALUE),
                ENTRY_HANDLER_VALUE
        );

        BagObject bagObject = (BagObject) eh.getEntry (test);
        AppTest.report (bagObject.getCount () == 3, true, "expect 3 elements in bagObject");
    }

    @Test
    public void testMultiLine () {
        String test = "command=goodbye&param1=1&param2=2\ncommand=hello&param1=2&param2=3\ncommand=dolly&param1=3&param2=5";

        EntryHandler eh = new EntryHandlerArrayFromDelimited ("\n",
                new EntryHandlerObjectFromPairsArray (
                        new EntryHandlerArrayFromDelimited ("&", ENTRY_HANDLER_VALUE),
                        new EntryHandlerArrayFromDelimited ("=", ENTRY_HANDLER_VALUE),
                        ENTRY_HANDLER_VALUE
                )
        );

        BagArray bagArray = (BagArray) eh.getEntry (test);
        AppTest.report (bagArray.getCount () == 3, true, "expect 3 elements in bagArray");
        AppTest.report (bagArray.getBagObject (1).getCount () == 3, true, "expect 3 elements in contained bagObject");
        AppTest.report (bagArray.getBagObject (1).getInteger ("param2") == 3, true, "expect bagObject value");
    }
}
