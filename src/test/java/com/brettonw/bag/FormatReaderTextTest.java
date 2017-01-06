package com.brettonw.bag;

import com.brettonw.bag.formats.MimeType;
import com.brettonw.bag.formats.text.FormatReaderText;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FormatReaderTextTest {
    @Test
    public void testAccumulate () {
        String testQuery = "command=goodbye\nparam1=1\nparam1=2\n";
        FormatReaderText frt = new FormatReaderText (testQuery, "\n", true, "=");
        BagObject bagObject = frt.read (new BagObject ());
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue (bagObject.getBagArray ("param1").getCount () == 2);
        assertTrue (bagObject.getBagArray ("param1").getInteger (0) == 1);
        assertTrue (bagObject.getBagArray ("param1").getInteger (1) == 2);
    }

    @Test
    public void testPropArray () {
        String testQuery = "command\nparam1\nparam2\n";
        FormatReaderText frt = new FormatReaderText (testQuery, "\n", false, "=");
        BagArray bagArray = frt.read (new BagArray ());
        assertTrue ("command".equals (bagArray.getString (0)));
        assertTrue ("param1".equals (bagArray.getString (1)));
        assertTrue ("param2".equals (bagArray.getString (2)));
    }

    @Test
    public void testPropObject () {
        String testQuery = "command=goodbye\nparam1=1\nparam2=2\n";
        FormatReaderText frt = new FormatReaderText (testQuery, "\n", false, "=");
        BagObject bagObject = frt.read (new BagObject ());
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue ("1".equals (bagObject.getString ("param1")));
        assertTrue ("2".equals (bagObject.getString ("param2")));
    }

    @Test
    public void testMimeTypePropMapping () {
        String testQuery = "command=goodbye\nparam1=1\nparam2=2\n";
        BagObject bagObject = BagObjectFrom.string (testQuery, MimeType.PROP);
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue ("1".equals (bagObject.getString ("param1")));
        assertTrue ("2".equals (bagObject.getString ("param2")));
    }

    @Test
    public void testUrlArray () {
        String testQuery = "command&param1&param2";
        FormatReaderText frt = new FormatReaderText (testQuery, "&", "#", false, "=");
        BagArray bagArray = frt.read (new BagArray ());
        assertTrue ("command".equals (bagArray.getString (0)));
        assertTrue ("param1".equals (bagArray.getString (1)));
        assertTrue ("param2".equals (bagArray.getString (2)));
    }

    @Test
    public void testUrlObject () {
        String testQuery = "command=goodbye&param1=1&param2=2";
        FormatReaderText frt = new FormatReaderText (testQuery, "&", "#", false, "=");
        BagObject bagObject = frt.read (new BagObject ());
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue ("1".equals (bagObject.getString ("param1")));
        assertTrue ("2".equals (bagObject.getString ("param2")));
    }

    @Test
    public void testMimeTypeUrlMapping () {
        String testQuery = "command=goodbye&param1=1&param2=2";
        BagObject bagObject = BagObjectFrom.string (testQuery, MimeType.URL);
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue ("1".equals (bagObject.getString ("param1")));
        assertTrue ("2".equals (bagObject.getString ("param2")));
    }

}
