package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

public class SourceAdapterTest {

    @Test
    public void testSourceAdapter () {
        SourceAdapter sourceAdapter = new SourceAdapter ();
        AppTest.report (sourceAdapter.getMimeType (() -> "abc"), "abc", "test getMimeType");
        AppTest.report (sourceAdapter.getStringData (() -> "def"), "def", "test getStringData");

        sourceAdapter.setMimeType ("application/json").setStringData ("xxx");
        AppTest.report (sourceAdapter.getMimeType (), "application/json", "test getMimeType");
        AppTest.report (sourceAdapter.getStringData (), "xxx", "test getStringData");

        sourceAdapter = new SourceAdapter ("yyy", "zzz");
        AppTest.report (sourceAdapter.getMimeType (), "yyy", "test getMimeType");
        AppTest.report (sourceAdapter.getStringData (), "zzz", "test getStringData");

        AppTest.report (sourceAdapter.getMimeType (() -> "abc"), "yyy", "test getMimeType");
        AppTest.report (sourceAdapter.getStringData (() -> "def"), "zzz", "test getStringData");
    }
}
