package com.brettonw.bag;

import com.brettonw.bag.formats.url.FormatReaderUrl;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FormatReaderUrlTest {

    @Test
    public void testArray () {
        String testQuery = "command&param1&param2";
        FormatReaderUrl fru = new FormatReaderUrl (testQuery);
        BagArray bagArray = fru.read (new BagArray ());
        assertTrue ("command".equals (bagArray.getString (0)));
        assertTrue ("param1".equals (bagArray.getString (1)));
        assertTrue ("param2".equals (bagArray.getString (2)));
    }

    @Test
    public void testObject () {
        String testQuery = "command=goodbye&param1=1&param2=2";
        FormatReaderUrl fru = new FormatReaderUrl (testQuery);
        BagObject bagObject = fru.read (new BagObject ());
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue ("1".equals (bagObject.getString ("param1")));
        assertTrue ("2".equals (bagObject.getString ("param2")));
    }
}
