package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.MimeType;
import org.junit.Test;

import java.io.File;

public class SourceAdapterReaderTest {
    @Test
    public void testSourceAdapterReader () {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterReader(new File("data/bagObject.json"));
            AppTest.report (sourceAdapter.getMimeType (), MimeType.JSON, "test mime type response");
            AppTest.report (sourceAdapter.getStringData () != null, true, "test for valid string data");

            sourceAdapter = new SourceAdapterReader(getClass().getResourceAsStream("/bagObject.json"), MimeType.JSON);
            AppTest.report (sourceAdapter.getMimeType (), MimeType.JSON, "test mime type response");
            AppTest.report (sourceAdapter.getStringData () != null, true, "test for valid string data");

            sourceAdapter = new SourceAdapterReader("{}", MimeType.JSON);
            AppTest.report (sourceAdapter.getMimeType (), MimeType.JSON, "test mime type response");
            AppTest.report (sourceAdapter.getStringData () != null, true, "test for valid string data");

            sourceAdapter = new SourceAdapterReader(getClass(), "/bagObject.json");
            AppTest.report (sourceAdapter.getMimeType (), MimeType.JSON, "test mime type response");
            AppTest.report (sourceAdapter.getStringData () != null, true, "test for valid string data");

        } catch (Exception exception) {
            AppTest.report(true, false, "Any exception is a failure");
        }
    }
}
