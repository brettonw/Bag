package com.brettonw.bag;

import com.brettonw.bag.formats.MimeType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FormatWriterTextTest {
    @Test
    public void testFormatWriterUrl () {
        BagObject queryBagObject = new BagObject ().put ("xxx", "yyy").put ("aaa", "bbb");
        String queryString = queryBagObject.toString (MimeType.URL);
        assertTrue (queryString != null);
        assertTrue (queryString.equalsIgnoreCase ("aaa=bbb&xxx=yyy"));
    }
}
