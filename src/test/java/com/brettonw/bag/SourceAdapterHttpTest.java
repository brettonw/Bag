package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.MimeType;
import org.junit.Test;

import java.io.IOException;

public class SourceAdapterHttpTest {
    @Test
    public void testSourceAdapterHttpGet () {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterHttp ("http://bag-test-server.azurewebsites.net/api?command=ip");
            BagObject responseBagObject = BagObjectFrom.string (sourceAdapter.getStringData (), sourceAdapter.getMimeType ());
            AppTest.report (responseBagObject.getString ("response/ip") != null, true, "Got a valid response");
        } catch (IOException exception ){
            AppTest.report (true, false, "An exception is a failure");
        }
    }

    @Test
    public void testSourceAdapterHttpPost () {
        try {
            BagObject bagObject = new BagObject ()
                    .put ("login", "brettonw")
                    .put ("First Name", "Bretton")
                    .put ("Last Name", "Wade");
            SourceAdapter sourceAdapter = new SourceAdapterHttp ("http://bag-test-server.azurewebsites.net/api?command=post-data", bagObject, MimeType.JSON);
            BagObject responseBagObject = BagObjectFrom.string (sourceAdapter.getStringData (), sourceAdapter.getMimeType ());
            AppTest.report (responseBagObject.getString ("login"), "brettonw", "Got a valid response");
        } catch (IOException exception ){
            AppTest.report (true, false, "An exception is a failure");
        }
    }
}
