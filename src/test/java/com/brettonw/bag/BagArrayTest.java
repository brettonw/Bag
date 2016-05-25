package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.json.FormatReaderJson;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BagArrayTest {
    @Test
    public void testBagArray() {
        try {
            // a first basic test
            BagArray bagArray = new BagArray ()
                    .add ("abdefg")
                    .add (123456)
                    .add (123.456)
                    .add (true);
            bagArray.insert (1, 234567);
            bagArray.replace (2, 345678);

            assertEquals ("Check get double", 123.456, bagArray.getDouble (3), 1.0e-9);
            assertEquals ("Check size", 5, bagArray.getCount ());

            bagArray.insert (10, 456789);
            assertEquals ("Check size", 11, bagArray.getCount ());

            // convert that bag to a string
            String bagArrayAsString = bagArray.toString ();
            BagArray reconBagArray = new BagArray (bagArrayAsString);
            AppTest.report (reconBagArray.toString (), bagArrayAsString, "BagArray - simple round trip with null values");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testCopyConstructor() {
        try {
        // a first basic test
        BagArray bagArray = new BagArray ()
                .add ("abdefg")
                .add (123456)
                .add (123.456)
                .add (true);
        bagArray.insert (1, 234567);
        bagArray.replace (2, 345678);

        BagArray duplicate = new BagArray (bagArray);
        AppTest.report (bagArray, duplicate, "BagArray - deep copy should succeed and be equal to the original");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testComplicated() {
        try {
            // a more complicated array test
            BagArray testArray = new BagArray ();
            testArray.add ("Bretton");
            testArray.add ("Wade");
            testArray.add (220.5);
            testArray.add (true);
            testArray.add (42);

            AppTest.report (testArray.getCount (), 5, "BagArray simple count check");
            AppTest.report (testArray.getString (0), "Bretton", "BagArray simple string extraction");
            AppTest.report (testArray.getDouble (2), 220.5, "BagArray simple double extraction");
            AppTest.report (testArray.getFloat (2), 220.5f, "BagArray simple float extraction");
            AppTest.report (testArray.getBoolean (3), true, "BagArray simple bool extraction");
            AppTest.report (testArray.getInteger (4), 42, "BagArray simple int extraction");

            testArray.remove (2);
            AppTest.report (testArray.getCount (), 4, "BagArray simple removal - count updated");
            AppTest.report (testArray.getBoolean (2), true, "BagArray simple bool extraction");
            AppTest.report (testArray.getInteger (3), 42, "BagArray simple int extraction");

            String testString = testArray.toString ();
            AppTest.report (testString, testString, "BagArray simple toString exercise (" + testString + ")");

            BagArray reconArray = new BagArray (testString);
            String reconString = reconArray.toString ();
            AppTest.report (reconString, testString, "BagArray simple reconstitution");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testArrayWithChildren() {
        try {
            BagArray testArray = new BagArray ();
            testArray.add ("Bretton");
            testArray.add ("Wade");
            testArray.add (220.5);
            testArray.add (true);
            testArray.add (42);

            BagObject dateObject = new BagObject ();
            dateObject.put ("Year", 2015);
            dateObject.put ("Month", 11);
            dateObject.put ("Day", 18);

            testArray.insert (1, dateObject);
            String testString = testArray.toString ();
            AppTest.report (testString, testString, "BagArray complex toString exercise (" + testString + ")");

            BagArray reconArray = new BagArray (testString);
            String reconString = reconArray.toString ();
            AppTest.report (reconString, testString, "BagArray complex reconstitution");

            AppTest.report (reconArray.getString (2), "Wade", "BagArray simple string extraction after insert");
            AppTest.report (reconArray.getBagObject (1).getInteger ("Year"), 2015, "BagArray complex bag/int extraction");

            // a couple of invalid retrievals
            AppTest.report (reconArray.getString (1), null, "BagArray simple invalid type extraction as String");
            AppTest.report (reconArray.getBagArray (1), null, "BagArray simple invalid type extraction as BagArray");

            // put a bag array in the bag array
            BagArray childArray = new BagArray ().add ("hello").add ("world");
            reconArray.replace (1, childArray);
            AppTest.report (reconArray.getBagArray (1), childArray, "BagArray store and retrieve a BagArray");
            AppTest.report (reconArray.getBagObject (1), null, "BagArray simple invalid type extraction as BagObject");
            reconString = reconArray.toString ();
            testArray = new BagArray (reconString);
            AppTest.report (testArray.toString (), reconString, "BagArray reconstitute with an array containing an array");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testRegressionCase() {
        try {
            File testFile = new File ("data", "UCS_Satellite_Database_2-1-14.json");
            BagArray bagArray = new BagArray (testFile);
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 1");
            bagArray = new BagArray (FormatReaderJson.JSON_FORMAT, new FileInputStream (testFile));
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 2");
        } catch (Exception exception) {
            AppTest.report (false, true, "BagArray - Regression Test 1 - Exception failure");
        }
    }

    @Test
    public void testEmptyArrayStrings() {
        try {
            BagArray bagArray = new BagArray ("[]");
            AppTest.report (bagArray != null, true, "BagArray - test empty shell");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testReconstructFromBogusStrings() {
        try {
            BagArray bagArray = new BagArray ("[123 234 345 456]");
            AppTest.report (bagArray != null, true, "BagArray - test empty shell");
        } catch (ReadException readException) {
            AppTest.report (false, false, "Bogus array parsing should fail");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testCopyOfEmptyArray () {
        try {
            BagArray empty = new BagArray ();
            BagArray copy = new BagArray (empty);
            AppTest.report (empty, copy, "Copy of empty array should succeed");
            copy.add ("test");
            AppTest.report (copy.getString (0), "test", "Copy of empty array should yield usable array");
        } catch (Exception exception) {
            AppTest.report (true, false, "Copy of empty array should succeed");
        }
    }

    @Test
    public void testKeyIndex () {
        BagObject bagObject = new BagObject ()
                .put ("a", new BagArray ()
                        .add (new BagObject ()
                                .put ("x", "y")
                        )
                );
        AppTest.report (bagObject.getString ("a/#first/x"), "y", "Hierarchical indexing of arrays using strings - 1");
        AppTest.report (bagObject.getString ("a/#last/x"), "y", "Hierarchical indexing of arrays using strings - 2");
        AppTest.report (bagObject.getString ("a/0/x"), "y", "Hierarchical indexing of arrays using strings - 3");
        AppTest.report (bagObject.getString ("a/100/x"), null, "Hierarchical indexing of arrays using strings - 4");
    }
}
