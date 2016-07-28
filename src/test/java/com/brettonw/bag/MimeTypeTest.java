package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.MimeType;
import org.junit.Test;

public class MimeTypeTest {
    @Test
    public void testMimeType () {
        MimeType mimeType = new MimeType ();
    }

    @Test
    public void testExtensions () {
        AppTest.report (MimeType.getFromExtension ("json"), MimeType.JSON, "Get the type associate with 'json'");
        //AppTest.report (MimeType.getFromExtension ("xml"), MimeType.XML, "Get the type associate with 'xml'");
        //AppTest.report (MimeType.getFromExtension ("csv"), MimeType.CSV, "Get the type associate with 'csv'");
        AppTest.report (MimeType.getFromExtension ("txt"), MimeType.TEXT, "Get the type associate with 'txt'");
        AppTest.report (MimeType.getFromExtension ("url"), MimeType.URL, "Get the type associate with 'url'");
        AppTest.report (MimeType.getFromExtension ("xxx"), null, "Get the type associate with 'xxx'");
    }
}
