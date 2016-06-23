package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

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
