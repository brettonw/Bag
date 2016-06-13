package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.json.FormatReaderJson;
import com.brettonw.bag.test.TestClassA;
import com.brettonw.bag.test.TestEnumXYZ;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

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
    }

    @Test
    public void testSimpleStringSerialization() {
        try {
            // first round, a simple string serialization example
            BagObject testObject = new BagObject ();
            testObject.put ("First Name", "Bretton");

            AppTest.report (testObject.getString ("First Name"), "Bretton", "BagObject simple string extraction");

            String testString = testObject.toString ();
            AppTest.report (testString, testString, "BagObject simple ToString exercise (" + testString + ")");

            BagObject recon = new BagObject (testString);
            assertNotNull (recon);
            String reconString = recon.toString ();
            AppTest.report (reconString, testString, "BagObject simple reconstitution");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testComplexStringSerialization() {
        try {
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

            BagObject recon = new BagObject (testString);
            assertNotNull (recon);
            String reconString = recon.toString ();
            AppTest.report (reconString, testString, "BagObject simple reconstitution");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testEscapedStrings() {
        try {
            // test an escaped string
            String escapedString = "a longer string with a \\\"quote from shakespeare\\\" in \\\"it\\\"";
            BagObject testObject = new BagObject ().put ("escaped", escapedString);
            AppTest.report (testObject.getString ("escaped"), escapedString, "BagObject simple test escaped string");
            String testString = testObject.toString ();
            BagObject recon = new BagObject (testString);
            AppTest.report (recon.getString ("escaped"), escapedString, "BagObject simple test escaped string from reconstituted bagobject");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testBarePojo() {
        // try to put a bare POJO into a bagObject
        try {
            BagObject bareBagObject = new BagObject ().put ("barecheck", new TestClassA (2, false, 123.456, "pdq", TestEnumXYZ.DEF));
            AppTest.report (bareBagObject != null, false, "BagObject - test bare POJO rejection");
        } catch (UnsupportedTypeException exception) {
            AppTest.report (true, true, "BagObject - test bare POJO rejection");
        }
    }

    @Test
    public void testSubObject() {
        try {
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

            BagObject recon = new BagObject (testString);
            String reconString = recon.toString ();
            AppTest.report (recon, testObject, "BagObject complex object equals");
            AppTest.report (reconString, testString, "BagObject complex reconsititution");

            AppTest.report (recon.getBoolean ("Married"), true, "BagObject complex bag/bool extraction");
            AppTest.report (recon.getDouble ("Weight"), 220.5, "BagObject complex bag/double extraction");
            AppTest.report (recon.getBagObject ("DOB").getInteger ("Year"), 2015, "BagObject complex bag/int extraction");

            AppTest.report (recon.getBoolean ("DOB"), null, "BagObject simple bad type request (should be null)");
            AppTest.report (recon.getString ("Joseph"), null, "BagObject simple bad key request (should be null)");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testEmptyObject() {
        try {
            // test reconstruction of an empty object
            BagObject bagObject = new BagObject ();
            String testString = bagObject.toString ();
            BagObject reconBagObject = new BagObject (testString);
            AppTest.report (reconBagObject.toString (), testString, "BagObject - reconstitute an empty object");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testHandAuthoredJson() {
        try {
            // test a reconstruction from a hand-authored JSON string
            String jsonString = " { Married:\"true\",   \"Children\": [] ,       \"First Name\": \"Bretton\" , \"Last Name\" : \"Wade\" , \"Weight\":\"220.5\", Size:8 }";
            BagObject bagObject = new BagObject (FormatReaderJson.JSON_FORMAT, jsonString);
            AppTest.report (bagObject.getString ("Last Name"), "Wade", "BagObject - reconstitute from a hand-crafted string should pass");
            AppTest.report (bagObject.has ("Married"), true, "BagObject - check that a tag is present");
            AppTest.report (bagObject.has ("Junk"), false, "BagObject - check that a tag is not present");
            AppTest.report (bagObject.getBoolean ("Married"), true, "BagObject - reconstitute from a hand-crafted string with bare names should pass");
            AppTest.report (bagObject.getInteger ("Size"), 8, "BagObject - reconstitute from a hand-crafted string with bare values should pass");
            AppTest.report (bagObject.getBagArray ("Children").getCount (), 0, "BagObject - reconstitute from a hand-crafted string with empty array should be size 0");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testBogusJson() {
        try {
            // test a reconstruction from a bogus string
            String bogusString = "{\"Children\":\"\",\"First Name\":\"Bretton\",\"\"Wade\",\"Married\":\"true\",\"Weight\":\"220.5\"}";
            BagObject bogusBagObject = new BagObject (bogusString);
            AppTest.report (bogusBagObject, null, "BagObject - reconstitute from a bogus string should fail");
        } catch (ReadException readException) {
            AppTest.report (false, false, "BagObject - reconstitute from a bogus string should fail");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
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
            BagObject bagObject = new BagObject (testFile);
            AppTest.report (bagObject != null, true, "BagObject - verify a successful load from a file - 1");
            bagObject = new BagObject (FormatReaderJson.JSON_FORMAT, testFile);
            AppTest.report (bagObject != null, true, "BagObject - verify a successful load from a file - 2");
            bagObject = new BagObject (new FileInputStream (testFile));
            AppTest.report (bagObject.getString ("glossary/title"), "example glossary", "BagObject - basic test that load from stream succeeds - 1");
            bagObject = new BagObject (FormatReaderJson.JSON_FORMAT, new FileInputStream (testFile));
            AppTest.report (bagObject.getString ("glossary/title"), "example glossary", "BagObject - basic test that load from stream succeeds - 2");
            AppTest.report (bagObject.getString ("glossary/GlossDiv/GlossList/GlossEntry/ID"), "SGML", "BagObject - complex test that load from stream succeeds");
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }

    }

    @Test
    public void testJohnF() {
        try {
            File testFile = new File ("data", "JohnF.json");
            BagObject bagObject = new BagObject (testFile);
            AppTest.report (bagObject != null, true, "BagObject - test regression case on complex file successfully loaded");
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }
    }

    @Test
    public void testJohnF2() {
        try {
            File testFile = new File ("data", "JohnF2.json");
            BagObject bagObject = new BagObject (testFile);
            AppTest.report (bagObject != null, true, "BagObject - test regression case on complex file 2 successfully loaded");
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }
    }

    @Test
    public void testBadFiles() {
        try {
            try {
                new BagObject (new File ("data", "badFile.json"));
            } catch (ReadException readException) {
                AppTest.report (false, false, "BagObject - Test that a bad file fails to parse (check error message in log)");
            }

            try {
                new BagObject (new File ("data", "badFile2.json"));
            } catch (ReadException readException) {
                AppTest.report (false, false, "BagObject - Test that a bad file fails to parse 2 (check error message in log)");
            }

            try {
                new BagObject (new File ("data", "badFile3.json"));
            } catch (ReadException readException) {
                AppTest.report (false, false, "BagObject - Test that a bad file fails to parse 3 (check error message in log)");
            }
        } catch (IOException exception) {
            AppTest.report (false, true, exception.getMessage ());
        }
    }

    @Test
    public void testNotFound() {
        try {
            BagObject bagObject = new BagObject ()
                    .put ("x", "y")
                    .put ("a", "b")
                    .put ("c", true)
                    .put ("d", 3.141592654)
                    .put ("e", 1234567L)
                    .put ("f", new BagObject ().put ("hello", "world"))
                    .put ("g", new BagArray ().add (123));

            // test the copy
            bagObject = new BagObject (bagObject);

            AppTest.report (bagObject.getString ("x"), "y", "BagObject - Test that 'notFound' method still correctly returns requested string value");
            AppTest.report (bagObject.getString ("x", () -> "xxx"), "y", "BagObject - Test that 'notFound' method still correctly returns requested string value");
            AppTest.report (bagObject.getString ("xxx", () -> "yyy"), "yyy", "BagObject - Test that 'notFound' method correctly returns notFound string value");

            AppTest.report (bagObject.getBoolean ("c"), true, "BagObject - Test that 'notFound' method still correctly returns requested boolean value");
            AppTest.report (bagObject.getBoolean ("c", () -> false), true, "BagObject - Test that 'notFound' method still correctly returns requested boolean value");
            AppTest.report (bagObject.getBoolean ("ccc", () -> true), true, "BagObject - Test that 'notFound' method correctly returns notFound boolean value");

            AppTest.report (bagObject.getInteger ("e"), 1234567, "BagObject - Test that 'notFound' method still correctly returns requested integer value");
            AppTest.report (bagObject.getInteger ("e", () -> 345), 1234567, "BagObject - Test that 'notFound' method still correctly returns requested integer value");
            AppTest.report (bagObject.getInteger ("eee", () -> 345), 345, "BagObject - Test that 'notFound' method correctly returns notFound integer value");

            AppTest.report (bagObject.getLong ("e"), 1234567L, "BagObject - Test that 'notFound' method still correctly returns requested long value");
            AppTest.report (bagObject.getLong ("e", () -> 345L), 1234567L, "BagObject - Test that 'notFound' method still correctly returns requested long value");
            AppTest.report (bagObject.getLong ("eee", () -> 345L), 345L, "BagObject - Test that 'notFound' method correctly returns notFound long value");

            AppTest.report (bagObject.getFloat ("d"), 3.141592654f, "BagObject - Test that 'notFound' method still correctly returns requested float value");
            AppTest.report (bagObject.getFloat ("d", () -> 6.28f), 3.141592654f, "BagObject - Test that 'notFound' method still correctly returns requested float value");
            AppTest.report (bagObject.getFloat ("ddd", () -> 6.28f), 6.28f, "BagObject - Test that 'notFound' method correctly returns notFound float value");

            AppTest.report (bagObject.getDouble ("d"), 3.141592654, "BagObject - Test that 'notFound' method still correctly returns requested double value");
            AppTest.report (bagObject.getDouble ("d", () -> 6.28), 3.141592654, "BagObject - Test that 'notFound' method still correctly returns requested double value");
            AppTest.report (bagObject.getDouble ("ddd", () -> 6.28), 6.28, "BagObject - Test that 'notFound' method correctly returns notFound double value");

            AppTest.report (bagObject.getBagObject ("q"), null, "BagObject - test that method correctly fails on not found BagObject");
            AppTest.report (bagObject.getBagObject ("f", () -> new BagObject ().put ("hello", "moto")).getString ("hello"), "world", "BagObject - test that 'notFound' method correctly returns found BagObject");
            AppTest.report (bagObject.getBagObject ("fff", () -> new BagObject ().put ("hello", "moto")).getString ("hello"), "moto", "BagObject - test that 'notFound' method correctly returns notFound BagObject");

            AppTest.report (bagObject.getBagArray ("q"), null, "BagObject - test that method correctly fails on not found BagObject");
            AppTest.report (bagObject.getBagArray ("g", () -> new BagArray ().add (345)).getInteger (0), 123, "BagObject - test that 'notFound' method correctly returns found BagArray");
            AppTest.report (bagObject.getBagArray ("ggg", () -> new BagArray ().add (345)).getInteger (0), 345, "BagObject - test that 'notFound' method correctly returns notFound BagArray");

            try {
                bagObject = new BagObject ((BagObject) null);
                AppTest.report (false, true, "BagObject - clone of null should fail");
            } catch (Exception exception) {
                //assertEquals (bagObject, null);
                AppTest.report (false, false, "BagObject - clone of null should fail");
            }
        } catch (Exception exception) {
            AppTest.report (true, false, "BagObject - any exception is a failure");
        }
    }

    @Test
    public void testCopyOfEmptyObject () {
        try {
            BagObject emptyObject = new BagObject ();
            BagObject copy = new BagObject (emptyObject);
            AppTest.report (emptyObject, copy, "Copy of empty object should succeed");
            copy.put ("test", "x");
            AppTest.report (copy.getString ("test"), "x", "Copy of empty object should yield usable object");
        } catch (Exception exception) {
            AppTest.report (true, false, "Copy of empty object should succeed");
        }
    }

    @Test
    public void testHashCode () {
        BagObject bagObject = new BagObject ().put ("x", "abcdegoldfish");
        int hash = bagObject.hashCode ();
        AppTest.report (hash != 0, true, "test hash code (" + hash + ")");
    }

    @Test
    public void testGetWithDefaultStringConstructor () throws IOException {
        BagObject bagObject = new BagObject ().put ("x", "y");
        BagObject bagObject2 = new BagObject ().put ("m", "n");
        BagObject fetched = bagObject.getBagObject ("q", () -> new BagObject (bagObject2));
        AppTest.report (fetched.getString ("m"), "n", "CheckedSupplier doesn't throw");
    }

    @Test
    public void testBogusInstantiationFallback () {
        BagObject bagObject = new BagObject ().put ("x", "y");
        try {
            BagObject fetched = bagObject.getBagObject ("q", () -> new BagObject (new File ("bogus.txt")));
            AppTest.report (false, true, "BagObject should throw exception when constructing from bogus source");
        } catch (IOException exception) {
            AppTest.report (true, true, "BagObject should throw exception when constructing from bogus source");
        }
    }
}
