package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.*;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class FormatReaderCompositeTest {
    @Test
    public void testCompositeReader () {
        String test = "command=goodbye&param1=1&param2=2";

        FormatReaderComposite frc = new FormatReaderComposite (test, new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited ("&", new EntryHandlerArrayFromDelimited ("="))
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
        FormatReader.registerFormatReader (testMimeType, false, (input) -> new FormatReaderComposite (input, new EntryHandlerObjectFromPairsArray (
                new EntryHandlerArrayFromDelimited ("&", new EntryHandlerArrayFromDelimited ("="))
        )));
        BagObject bagObject = BagObjectFrom.string (test, testMimeType);
        AppTest.report (bagObject.getCount () == 3, true, "expect 3 elements in bagObject");
        AppTest.report (bagObject.getString ("command"), "goodbye", "expect text elements in bagObject");
        AppTest.report (bagObject.getInteger ("param2"), 2, "expect int elements in bagObject");
    }

    @Test
    public void testAccumulate () {

        String testQuery = "command=goodbye\nparam1=1\nparam1=2\n";
        ObjectFormatReader frc = FormatReaderComposite.basicObjectReader (testQuery, "\n", "=", true);
        BagObject bagObject = frc.readBagObject ();
        assertTrue ("goodbye".equals (bagObject.getString ("command")));
        assertTrue (bagObject.getBagArray ("param1").getCount () == 2);
        assertTrue (bagObject.getBagArray ("param1").getInteger (0) == 1);
        assertTrue (bagObject.getBagArray ("param1").getInteger (1) == 2);
    }

    @Test
    public void testPropArray () {
        String testQuery = "command\nparam1\nparam2\n";
        ArrayFormatReader frc = FormatReaderComposite.basicArrayReader (testQuery, "\n");
        BagArray bagArray = frc.readBagArray ();
        assertTrue ("command".equals (bagArray.getString (0)));
        assertTrue ("param1".equals (bagArray.getString (1)));
        assertTrue ("param2".equals (bagArray.getString (2)));
    }

    @Test
    public void testPropObject () {
        String testQuery = "command=goodbye\nparam1=1\nparam2=2\n";
        ObjectFormatReader frc = FormatReaderComposite.basicObjectReader (testQuery, "\n", "=");
        BagObject bagObject = frc.readBagObject ();
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
    public void testUrlObject () {
        String testQuery = "command=goodbye&param1=1&param2=2";
        ObjectFormatReader frc = FormatReaderComposite.basicObjectReader (testQuery, "&", "=");
        BagObject bagObject = frc.readBagObject ();
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

    @Test
    public void test2le () {
        final String tleFormat = "test/tle";
        FormatReader.registerFormatReader (tleFormat, false, (input) ->
                new FormatReaderComposite (input,
                        new EntryHandlerObjectFromTitlesArray (new BagArray ()
                                .add ("line number 1").add ("satellite catalog number").add ("classification").add ("designation").add ("epoch").add ("d1").add ("d2").add ("drag").add ("type").add ("number").add ("checksum 1")
                                .add ("line number 2").add ("satellite catalog number 2").add ("orbit inclination").add ("ra of ascending node").add ("eccentricity").add ("argument of perigee").add ("mean anomaly").add ("mean motion").add ("revolution number").add ("checksum 2"),
                new EntryHandlerCollector (3, new EntryHandlerArrayFromDelimited ("\n", new EntryHandlerRoller (
                        // exemplars, from https://www.celestrak.com/NORAD/documentation/tle-fmt.asp
                        new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.widthsFromExemplar ("AAAAAAAAAAAAAAAAAAAAAAAA", ' ')),
                        new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.widthsFromExemplar ("1 NNNNNU NNNNNAAA NNNNN.NNNNNNNN +.NNNNNNNN +NNNNN-N +NNNNN-N N NNNNN", ' ')),
                        new EntryHandlerArrayFromFixed (EntryHandlerArrayFromFixed.widthsFromExemplar ("2 NNNNN NNN.NNNN NNN.NNNN NNNNNNN NNN.NNNN NNN.NNNN NN.NNNNNNNNNNNNNN", ' '))
        ))))));
        BagObject bagObject = BagObjectFrom.file (new File ("data/2le.txt"), tleFormat);
    }
}
