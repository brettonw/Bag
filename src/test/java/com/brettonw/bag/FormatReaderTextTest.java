package com.brettonw.bag;

import com.brettonw.bag.formats.text.FormatReaderText;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FormatReaderTextTest {

    @Test
    public void testArray () {

        String testQuery = "command\nparam1\nparam2";
        FormatReaderText frt = new FormatReaderText (testQuery);
        BagArray bagArray = frt.read (new BagArray ());
        assertTrue ("command".equals (bagArray.getString (0)));
        assertTrue ("param1".equals (bagArray.getString (1)));
        assertTrue ("param2".equals (bagArray.getString (2)));
    }

    @Test
    public void testObject () {
        String testQuery = "command=goodbye\nparam1=1\nparam2=2";
        FormatReaderText frt = new FormatReaderText (testQuery);
        BagObject bagObject = frt.read (new BagObject ());
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue ("1".equals (bagObject.getString ("param1")));
        assertTrue ("2".equals (bagObject.getString ("param2")));
    }
}
