package com.brettonw.bag.formats;

import com.brettonw.bag.BagObject;
import com.brettonw.bag.BagObjectFrom;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class FormatReaderTest {

    @Test
    public void testFormatReader () {
        BagObject bagObject = BagObjectFrom.file (new File ("data", "title.properties"));
        assertTrue (bagObject.getString ("note").equals ("this is a test"));
    }
}
