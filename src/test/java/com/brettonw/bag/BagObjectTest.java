package com.brettonw.bag;

import com.brettonw.AppTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

public class BagObjectTest {
    private static final Logger log = LogManager.getLogger (BagObject.class);

    @Test
    public void test() {
        BagObject bagObject = new BagObject ();
        bagObject.put ("name", "bretton wade");
        bagObject.put ("phone", "410.710.7918");
        bagObject.put ("address", "610 Cathedral St Apt 3");
        bagObject.put ("city", "baltimore");
        bagObject.put ("state", "mx"); // note the typo that gets replaced...
        bagObject.put ("zip", "21201");
        bagObject.put ("state", "md");

        AppTest.report(bagObject.getCount (), 6, "BagObject simple count");

        String keys[] = bagObject.keys ();
        assertArrayEquals("Check keys", new String[]{"address", "city", "name", "phone", "state", "zip"}, keys);
        String state = bagObject.getString ("state");
        assertEquals ("Check state", "md", state);

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

        // first round, a simple example
        BagObject testObject = new BagObject();
        testObject.put("First Name", "Bretton");

        AppTest.report (testObject.getString ("First Name"), "Bretton", "BagObject simple string extraction");

        String testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject simple ToString exercise (" + testString + ")");

        BagObject recon = BagObject.fromString (testString);
        assertNotNull (recon);
        String reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject simple reconstitution");

        // second round, a bit more sophisticated
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

        testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject simple ToString exercise (" + testString + ")");

        recon = BagObject.fromString (testString);
        assertNotNull (recon);
        reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject simple reconstitution");

        // test an escaped string
        /*
        String escapedString = "a longer string with a \\\"quote from shakespeare\\\" in it";
        testObject.put ("escaped", escapedString);
        AppTest.report (testObject.getString ("escaped"), escapedString, "BagObject simple test escaped string");
        testString = testObject.toString ();
        recon = BagObject.fromString (testString);
        AppTest.report (recon.getString ("escaped"), escapedString, "BagObject simple test escaped string from reconstituted bagobject");
        */

        // on with the show
        BagObject dateObject = new BagObject ();
        dateObject.put ("Year", 2015);
        dateObject.put ("Month", 11);
        dateObject.put ("Day", 18);

        AppTest.report (dateObject.getInteger ("Month"), 11, "BagObject simple int extraction");

        testObject.put ("DOB", dateObject);
        testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject complex ToString exercise (" + testString + ")");

        recon = BagObject.fromString (testString);
        reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject complex reconsititution");

        AppTest.report (recon.getBoolean ("Married"), true, "BagObject complex bag/bool extraction");
        AppTest.report (recon.getDouble ("Weight"), 220.5, "BagObject complex bag/double extraction");
        AppTest.report (recon.getBagObject ("DOB").getInteger ("Year"), 2015, "BagObject complex bag/int extraction");

        AppTest.report (recon.getBoolean ("DOB"), null, "BagObject simple bad type request (should be null)");
        AppTest.report (recon.getString ("Joseph"), null, "BagObject simple bad key request (should be null)");
    }
}
