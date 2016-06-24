package com.brettonw;

import com.brettonw.bag.*;
import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SortKeyTest.class,
        SelectKeyTest.class,
        BagArrayTest.class,
        BagObjectTest.class,
        KeyTest.class,
        FromUrlTest.class,
        SerializerTest.class,
        FormatWriterTest.class,
        SourceAdapterTest.class,
        SourceAdapterHttpTest.class,
        SourceAdapterReaderTest.class,
        MimeTypeTest.class
})

public class AppTest {
    private static final Logger log = LogManager.getLogger (AppTest.class);

    public static void report (Object actual, Object expect, String message) {
        boolean result = (actual != null) ? actual.equals (expect) : (expect == null);
        log.info (message + " (" + (result ? "PASS" : "FAIL") + ")");
        TestCase.assertEquals (message, expect, actual);
    }
}
