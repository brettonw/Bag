package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.test.TestClassA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class BagObjectTest {
    private static final Logger log = LogManager.getLogger (BagObject.class);

    @Test
    public void testBagObject() {
        BagObject bagObject = new BagObject ();
        bagObject.put ("name", "bretton wade");
        bagObject.put ("phone", "410.791.7108");
        bagObject.put ("address", "410 Charles St");
        bagObject.put ("city", "baltimore");
        bagObject.put ("state", "mx"); // note the typo that gets replaced...
        bagObject.put ("zip", "21201");
        bagObject.put ("state", "md");

        AppTest.report(bagObject.getCount (), 6, "BagObject simple count");

        String keys[] = bagObject.keys ();
        assertArrayEquals("Check keys", new String[]{"address", "city", "name", "phone", "state", "zip"}, keys);
        String state = bagObject.getString ("state");
        AppTest.report (state, "md", "Check state");

        // test removing an object
        bagObject.remove ("phone");
        AppTest.report(bagObject.getCount (), 5, "BagObject simple removed count");
        AppTest.report(bagObject.getString ("phone"), null, "BagObject simple retrieve removed element");

        // test a couple of exception cases
        AppTest.report(bagObject.getBagObject ("state"), null, "BagObject simple retrieve incorrect element type as BagObject");
        AppTest.report(bagObject.getBagArray ("state"), null, "BagObject simple retrieve incorrect element type as BagArray");

        // and try to store something we shouldn't
        class pojo {
            private int a; private int b;
            public pojo (int _a, int _b) { a = _a; b = _b; }
            int getA () { return a; }
            int getB () { return b; }
        }
        bagObject.put ("pojo", new pojo (1, 2));
        AppTest.report (bagObject.getObject ("pojo"), null, "BagObject simple store an invalid object");
    }

    @Test
    public void testSimpleStringSerialization() {
        // first round, a simple string serialization example
        BagObject testObject = new BagObject();
        testObject.put("First Name", "Bretton");

        AppTest.report (testObject.getString ("First Name"), "Bretton", "BagObject simple string extraction");

        String testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject simple ToString exercise (" + testString + ")");

        BagObject recon = BagObject.fromJsonString (testString);
        assertNotNull (recon);
        String reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject simple reconstitution");
    }

    @Test
    public void testComplexStringSerialization() {
        // second round, a bit more sophisticated
        BagObject testObject = new BagObject();
        testObject.put("First Name", "Bretton");
        testObject.put("Last Name", "Wade");
        testObject.put("Weight", 220.5);
        testObject.put("Married", true);
        testObject.put("Children", "");

        AppTest.report (testObject.getString ("First Name"), "Bretton", "BagObject simple string extraction");
        AppTest.report (testObject.getString ("Last Name"), "Wade", "BagObject simple string extraction");
        AppTest.report (testObject.getBoolean ("Married"), true, "BagObject simple bool extraction");
        AppTest.report (testObject.getDouble ("Weight"), 220.5, "BagObject simple double extraction");
        AppTest.report (testObject.getFloat ("Weight"), 220.5f, "BagObject simple float extraction");
        AppTest.report (testObject.getString ("Children"), "", "BagObject simple empty extraction");

        String testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject simple ToString exercise (" + testString + ")");

        BagObject recon = BagObject.fromJsonString (testString);
        assertNotNull (recon);
        String reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject simple reconstitution");
    }

    @Test
    public void testEscapedStrings() {
        // test an escaped string
        String escapedString = "a longer string with a \\\"quote from shakespeare\\\" in \\\"it\\\"";
        BagObject testObject = new BagObject ().put ("escaped", escapedString);
        AppTest.report (testObject.getString ("escaped"), escapedString, "BagObject simple test escaped string");
        String testString = testObject.toString ();
        BagObject recon = BagObject.fromJsonString (testString);
        AppTest.report (recon.getString ("escaped"), escapedString, "BagObject simple test escaped string from reconstituted bagobject");
    }

    @Test
    public void testBarePojo() {
        // try to put a bare POJO into a bagObject
        BagObject   bareBagObject = new BagObject ().put ("barecheck", new TestClassA (2, false, 123.456, "pdq"));
        AppTest.report (bareBagObject.getObject ("barecheck"), null, "BagObject - test bare POJO rejection");
    }

    @Test
    public void testSubObject() {
        // on with the show
        BagObject dateObject = new BagObject ()
                .put ("Year", 2015)
                .put ("Month", 11)
                .put ("Day", 18);

        AppTest.report (dateObject.getInteger ("Month"), 11, "BagObject simple int extraction");

        BagObject testObject = new BagObject()
                .put("First Name", "Bretton")
                .put("Last Name", "Wade")
                .put("Weight", 220.5)
                .put("Married", true)
                .put("Children", "")
                .put ("DOB", dateObject);
        String testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject complex ToString exercise (" + testString + ")");

        BagObject recon = BagObject.fromJsonString (testString);
        String reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject complex reconsititution");

        AppTest.report (recon.getBoolean ("Married"), true, "BagObject complex bag/bool extraction");
        AppTest.report (recon.getDouble ("Weight"), 220.5, "BagObject complex bag/double extraction");
        AppTest.report (recon.getBagObject ("DOB").getInteger ("Year"), 2015, "BagObject complex bag/int extraction");

        AppTest.report (recon.getBoolean ("DOB"), null, "BagObject simple bad type request (should be null)");
        AppTest.report (recon.getString ("Joseph"), null, "BagObject simple bad key request (should be null)");
    }

    @Test
    public void testEmptyObject() {
        // test reconstruction of an empty object
        BagObject bagObject = new BagObject ();
        String testString = bagObject.toString ();
        BagObject reconBagObject = BagObject.fromJsonString (testString);
        AppTest.report (reconBagObject.toString (), testString, "BagObject - reconstitute an empty object");
    }

    @Test
    public void testHandAuthoredJson() {
        // test a reconstruction from a hand-authored JSON string
        String jsonString = " { Married:\"true\",   \"Children\": [] ,       \"First Name\": \"Bretton\" , \"Last Name\" : \"Wade\" , \"Weight\":\"220.5\", Size:8 }";
        BagObject bagObject = BagObject.fromJsonString (jsonString);
        AppTest.report (bagObject.getString ("Last Name"), "Wade", "BagObject - reconstitute from a hand-crafted string should pass");
        AppTest.report (bagObject.has ("Married"), true, "BagObject - check that a tag is present");
        AppTest.report (bagObject.has ("Junk"), false, "BagObject - check that a tag is not present");
        AppTest.report (bagObject.getBoolean ("Married"), true, "BagObject - reconstitute from a hand-crafted string with bare names should pass");
        AppTest.report (bagObject.getInteger ("Size"), 8, "BagObject - reconstitute from a hand-crafted string with bare values should pass");
        AppTest.report (bagObject.getBagArray ("Children").getCount (), 0, "BagObject - reconstitute from a hand-crafted string with empty array should be size 0");
    }

    @Test
    public void testBogusJson() {
        // test a reconstruction from a bogus string
        String bogusString = "{\"Children\":\"\",\"First Name\":\"Bretton\",\"\"Wade\",\"Married\":\"true\",\"Weight\":\"220.5\"}";
        BagObject bogusBagObject = BagObject.fromJsonString (bogusString);
        AppTest.report (bogusBagObject, null, "BagObject - reconstitute from a bogus string should fail");
    }

    @Test
    public void testHierarchical() {
        // hierarchical values
        BagObject bagObject = new BagObject ().put ("com/brettonw/bag/name", "test");
        AppTest.report (bagObject.has ("com/brettonw/test"), false, "BagObject - test that an incorrect path returns false");
        AppTest.report (bagObject.has ("com/brettonw/bag/name/xxx"), false, "BagObject - test that a longer incorrect path returns false");
        AppTest.report (bagObject.has ("com/brettonw/bag/name"), true, "BagObject - test that a correct path returns true");
        AppTest.report (bagObject.getString ("com/brettonw/bag/name"), "test", "BagObject - test that a hierarchical fetch yields the correct result");
        AppTest.report (bagObject.getBagObject ("com/bretton/bag"), null, "BagObject - test that an incorrect hierarchical fetch yields null");
        bagObject.remove ("com/brettonw/bag");
        AppTest.report (bagObject.has ("com/brettonw/bag/name"), false, "BagObject - test that a path is correctly removed");
    }

    @Test
    public void testAdd() {
        // add
        BagObject bagObject = new BagObject ().add ("com/bretton/testArray", 5).add ("com/bretton/testArray", 6).add ("com/bretton/testArray", 7).add ("com/bretton/testArray", null).add ("com/bretton/testArray", 9);
        AppTest.report (bagObject.getBagArray ("com/bretton/testArray").getInteger (2), 7, "BagObject - test array get");

        bagObject = new BagObject ().add ("null", null);
        AppTest.report (bagObject.getBagArray ("null").getCount (), 1, "BagObject - test case 1, add null to non existent key");

        bagObject = new BagObject ().add ("null", 5).add ("null", null);
        AppTest.report (bagObject.getBagArray ("null").getCount (), 2, "BagObject - test case 3, add null to non array key");
    }

    @Test
    public void testFileAndStream() {
        // file and stream tests
        try {
            File testFile = new File ("data", "bagObject.json");
            BagObject bagObject = BagObject.fromFile (testFile);
            bagObject = BagObject.fromStream (new FileInputStream (testFile));
            AppTest.report (bagObject.getString ("glossary/title"), "example glossary", "BagObject - basic test that load from stream succeeds");
            AppTest.report (bagObject.getString ("glossary/GlossDiv/GlossList/GlossEntry/ID"), "SGML", "BagObject - complex test that load from stream succeeds");
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }

    }

    @Test
    public void testXml() {
        BagObject bagObject = new BagObject ();
        bagObject.put ("name", "bretton wade");
        bagObject.put ("phone", "410.791.7108");
        bagObject.put ("address", "410 Charles St");
        bagObject.put ("city", "baltimore");
        bagObject.put ("zip", "21201");
        bagObject.put ("state", "md");
        String xmlString = bagObject.toXmlString ("xml");
        String expect = "<xml><address>410 Charles St</address><city>baltimore</city><name>bretton wade</name><phone>410.791.7108</phone><state>md</state><zip>21201</zip></xml>";
        AppTest.report (xmlString, expect, "BagObject - test XML");
    }

    @Test
    public void testJohnF() {
        try {
            File testFile = new File ("data", "JohnF.json");
            BagObject bagObject = BagObject.fromFile (testFile);
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }
    }

    @Test
    public void testBadFiles() {
        try {
            BagObject bagObject = BagObject.fromFile (new File ("data", "badFile.json"));
            AppTest.report (bagObject, null, "BagObject - Test that a bad file returns null (check error message in log)");
            bagObject = BagObject.fromFile (new File ("data", "badFile2.json"));
            AppTest.report (bagObject, null, "BagObject - Test that a bad file returns null 2 (check error message in log)");
            bagObject = BagObject.fromFile (new File ("data", "badFile3.json"));
            AppTest.report (bagObject, null, "BagObject - Test that a bad file returns null 3 (check error message in log)");
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }
    }
}
