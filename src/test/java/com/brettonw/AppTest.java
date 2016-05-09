package com.brettonw;

import com.brettonw.bag.BagArrayTest;
import com.brettonw.bag.BagObjectTest;
import com.brettonw.bag.KeyTest;
import com.brettonw.bag.SerializerTest;
import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BagArrayTest.class,
        BagObjectTest.class,
        KeyTest.class,
        SerializerTest.class
})

public class AppTest {
    private static final Logger log = LogManager.getLogger (AppTest.class);

    public static void report (Object actual, Object expect, String message) {
        boolean result = (actual != null) ? actual.equals (expect) : (expect == null);
        log.info (message + " (" + (result ? "PASS" : "FAIL") + ")");
        TestCase.assertEquals (message, expect, actual);
    }
}
