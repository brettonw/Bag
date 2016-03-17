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
        bagObject.put ("state", "mx");
        bagObject.put ("zip", "21201");
        bagObject.put ("state", "md");

        String keys[] = bagObject.keys ();
        assertArrayEquals("Check keys", new String[]{"address", "city", "name", "phone", "state", "zip"}, keys);
        String state = bagObject.getString ("state");
        assertEquals ("Check state", "md", state);

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
        AppTest.report (testObject.getString ("Children"), "", "BagObject simple empty extraction");

        testString = testObject.toString ();
        AppTest.report (testString, testString, "BagObject simple ToString exercise (" + testString + ")");

        recon = BagObject.fromString (testString);
        assertNotNull (recon);
        reconString = recon.toString ();
        AppTest.report (reconString, testString, "BagObject simple reconstitution");

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
