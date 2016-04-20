package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;

public class BagArrayTest {
    @Test
    public void test() {
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
        BagArray reconBagArray = BagArray.fromJsonString (bagArrayAsString);
        AppTest.report (reconBagArray.toString (), bagArrayAsString, "BagArray - simple round trip with null values");

        // a more complicated array test
        BagArray testArray = new BagArray();
        testArray.add("Bretton");
        testArray.add("Wade");
        testArray.add(220.5);
        testArray.add(true);
        testArray.add(42);

        AppTest.report (testArray.getCount (), 5, "BagArray simple count check");
        AppTest.report(testArray.getString(0), "Bretton", "BagArray simple string extraction");
        AppTest.report(testArray.getDouble(2), 220.5, "BagArray simple double extraction");
        AppTest.report(testArray.getFloat(2), 220.5f, "BagArray simple float extraction");
        AppTest.report(testArray.getBoolean(3), true, "BagArray simple bool extraction");
        AppTest.report(testArray.getInteger (4), 42, "BagArray simple int extraction");

        testArray.remove (2);
        AppTest.report (testArray.getCount (), 4, "BagArray simple removal - count updated");
        AppTest.report(testArray.getBoolean(2), true, "BagArray simple bool extraction");
        AppTest.report(testArray.getInteger (3), 42, "BagArray simple int extraction");

        String testString = testArray.toString();
        AppTest.report(testString, testString, "BagArray simple toString exercise (" + testString + ")");

        BagArray reconArray = BagArray.fromJsonString(testString);
        String reconString = reconArray.toString();
        AppTest.report(reconString, testString, "BagArray simple reconstitution");

        BagObject dateObject = new BagObject ();
        dateObject.put ("Year", 2015);
        dateObject.put ("Month", 11);
        dateObject.put ("Day", 18);

        reconArray.insert(1, dateObject);
        testString = reconArray.toString();
        AppTest.report(testString, testString, "BagArray complex toString exercise (" + testString + ")");

        reconArray = BagArray.fromJsonString(testString);
        reconString = reconArray.toString();
        AppTest.report(reconString, testString, "BagArray complex reconstitution");

        AppTest.report(reconArray.getString(2), "Wade", "BagArray simple string extraction after insert");
        AppTest.report(reconArray.getBagObject(1).getInteger ("Year"), 2015, "BagArray complex bag/int extraction");
        //AppTest.report (false, "Test Failure");

        // a couple of invalid retrievals
        AppTest.report (reconArray.getString (1), null, "BagArray simple invalid type extraction as String");
        AppTest.report (reconArray.getBagArray (1), null, "BagArray simple invalid type extraction as BagArray");

        // put a bag array in the bag array
        BagArray childArray = new BagArray ().add ("hello").add ("world");
        reconArray.replace (1, childArray);
        AppTest.report (reconArray.getBagArray (1), childArray, "BagArray store and retrieve a BagArray");
        AppTest.report (reconArray.getBagObject (1), null, "BagArray simple invalid type extraction as BagObject");
        reconString = reconArray.toString ();
        testArray = BagArray.fromJsonString (reconString);
        AppTest.report (testArray.toString (), reconString, "BagArray reconstitute with an array containing an array");

        // regression test
        try {
            File testFile = new File ("data", "UCS_Satellite_Database_2-1-14.json");
            bagArray = BagArray.fromFile (testFile);
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 1");
            bagArray = BagArray.fromStream (new FileInputStream (testFile));
            AppTest.report (bagArray != null, true, "BagArray - Regression Test 2");
        } catch (Exception exception) {
            AppTest.report (false, true, "BagArray - Regression Test 1 - Exception failure");
        }
    }
}
