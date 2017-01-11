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

            sourceAdapter = new SourceAdapterReader(getClass().getResourceAsStream("/bagObject.json"), MimeType.JSON);

            sourceAdapter = new SourceAdapterReader("{}", MimeType.JSON);

        } catch (Exception exception) {
            AppTest.report(true, false, "Any exception is a failure");
        }
    }
}
