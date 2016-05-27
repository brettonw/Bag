package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.expr.BooleanExpr;
import com.brettonw.bag.expr.Exprs;
import com.brettonw.bag.json.FormatReaderJson;
import com.brettonw.bag.json.FormatWriterJson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BagArrayTest {
    private static final Logger log = LogManager.getLogger (BagArrayTest.class);

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
            BagArray bagArray = new BagArray (FormatReaderJson.JSON_FORMAT, testFile);
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 1");
            bagArray = new BagArray (testFile);
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 2");
            bagArray = new BagArray (FormatReaderJson.JSON_FORMAT, new FileInputStream (testFile));
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 3");
            bagArray = new BagArray (new FileInputStream (testFile));
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 4");

            String string = bagArray.toString (FormatWriterJson.JSON_FORMAT);
            bagArray = new BagArray (FormatReaderJson.JSON_FORMAT, string);
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 4");
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

    @Test
    public void testQueryAndSort () {
        try {
            File testFile = new File ("data", "UCS_Satellite_Database_2-1-14.json");
            BagArray bagArray = new BagArray (testFile);
            BooleanExpr equality = Exprs.equality ("Country of Operator/Owner", "USA");
            BagArray queried = bagArray.query (equality, new BagArray().add ("Current Official Name of Satellite").add ("Country of Operator/Owner"));
            AppTest.report (queried.getCount () > 0, true, "Queried Array returned some results");
            AppTest.report (queried.getString ("53/Country of Operator/Owner"), "USA", "Query results for #53 should match the query");

            // now try sorting
            queried.sort (new BagArray ().add (new BagObject ().put (SortKey.KEY, "Current Official Name of Satellite")));
            AppTest.report (queried.getString ("36/Country of Operator/Owner"), "USA", "Query results for #36 should match the query");
        } catch (Exception exception) {
            log.error (exception);
            exception.printStackTrace ();
            AppTest.report (true, false, "Query of array should succeed");
        }
    }

    @Test
    public void testQuery2 () {
        try {
            File testFile = new File ("data", "spark-applications.json");
            BagArray bagArray = new BagArray (testFile);
            BagArray queried = bagArray.query (Exprs.equality ("attempts/#last/completed", true), null);
            AppTest.report (queried.getCount () > 0, true, "Verify good load from sample file with query");

        } catch (IOException exception) {
            // whatever
        }
    }

    @Test
    public void testSort () {
        try {
            BagArray bagArray = new BagArray ();

            int count = 20;

            for (int i = 0; i < count; ++i) {
                int random = (int) (Math.random () * 10 + 1);
                int random2 = (int) (Math.random () * 20 + 1);
                bagArray.add (new BagObject ().put ("id", random).put ("value", random2));
            }

            {
                BagArray sortKeys = SortKey.keys ("id", "value");
                BagArray sortedBagArray = new BagArray (bagArray).sort (sortKeys);

                BagObject lastBagObject = sortedBagArray.getBagObject (0);
                for (int i = 1; i < count; ++i) {
                    BagObject nextBagObject = sortedBagArray.getBagObject (i);
                    AppTest.report (nextBagObject.getString ("id").compareTo (lastBagObject.getString ("id")) >= 0, true, "sorted by id, alphabetic, ascending...");
                    lastBagObject = nextBagObject;
                }
            }
            {
                BagArray sortKeys = SortKey.keys ("id", "value");
                sortKeys.getBagObject (0).put (SortKey.ORDER, SortOrder.DESCENDING.name ());
                BagArray sortedBagArray = new BagArray (bagArray).sort (sortKeys);

                BagObject lastBagObject = sortedBagArray.getBagObject (0);
                for (int i = 1; i < count; ++i) {
                    BagObject nextBagObject = sortedBagArray.getBagObject (i);
                    AppTest.report (nextBagObject.getString ("id").compareTo (lastBagObject.getString ("id")) <= 0, true, "sorted by id, alphabetic, descending...");
                    lastBagObject = nextBagObject;
                }
            }
            {
                BagArray sortKeys = SortKey.keys ("id", "value");
                sortKeys.getBagObject (0).put (SortKey.TYPE, SortType.NUMERIC.name ());
                BagArray sortedBagArray = new BagArray (bagArray).sort (sortKeys);

                BagObject lastBagObject = sortedBagArray.getBagObject (0);
                for (int i = 1; i < count; ++i) {
                    BagObject nextBagObject = sortedBagArray.getBagObject (i);
                    AppTest.report (nextBagObject.getInteger ("id") >= lastBagObject.getInteger ("id"), true, "sorted by id, numeric, ascending...");
                    lastBagObject = nextBagObject;
                }
            }
            {
                BagArray sortKeys = SortKey.keys ("id", "value");
                sortKeys.getBagObject (0).put (SortKey.TYPE, SortType.NUMERIC.name ()).put (SortKey.ORDER, SortOrder.DESCENDING.name ());
                BagArray sortedBagArray = new BagArray (bagArray).sort (sortKeys);

                BagObject lastBagObject = sortedBagArray.getBagObject (0);
                for (int i = 1; i < count; ++i) {
                    BagObject nextBagObject = sortedBagArray.getBagObject (i);
                    AppTest.report (nextBagObject.getInteger ("id") <= lastBagObject.getInteger ("id"), true, "sorted by id, numeric, descending...");
                    lastBagObject = nextBagObject;
                }
            }
        }catch (IOException exception) {
            log.error (exception);
        }
    }
}
