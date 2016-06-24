package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

import java.io.IOException;

public class SourceAdapterHttpTest {
    @Test
    public void testSourceAdapterHttpGet () {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterHttp ("https://httpbin.org/ip");
            BagObject responseBagObject = BagObjectFrom.string (sourceAdapter.getStringData (), sourceAdapter.getMimeType ());
            AppTest.report (responseBagObject.getString ("origin") != null, true, "Got a valid response");
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
            SourceAdapter sourceAdapter = new SourceAdapterHttp ("http://jsonplaceholder.typicode.com/posts/", bagObject, MimeType.JSON);
            BagObject responseBagObject = BagObjectFrom.string (sourceAdapter.getStringData (), sourceAdapter.getMimeType ());
            AppTest.report (responseBagObject.getString ("login"), "brettonw", "Got a valid response");
        } catch (IOException exception ){
            AppTest.report (true, false, "An exception is a failure");
        }
    }
}
