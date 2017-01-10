package com.brettonw.bag;

import com.brettonw.bag.formats.MimeType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FormatWriterTextTest {
    @Test
    public void testFormatWriterProp () {
        BagObject queryBagObject = new BagObject ().put ("xxx", "yyy").put ("aaa", "bbb");
        String queryString = queryBagObject.toString (MimeType.PROP);
        assertTrue (queryString != null);
        assertTrue (queryString.equalsIgnoreCase ("aaa=bbb\nxxx=yyy\n"));
    }

    @Test
    public void testFormatWriterUrl () {
        BagObject queryBagObject = new BagObject ().put ("xxx", "yyy").put ("aaa", "bbb");
        String queryString = queryBagObject.toString (MimeType.URL);
        assertTrue (queryString != null);
        assertTrue (queryString.equalsIgnoreCase ("aaa=bbb&xxx=yyy&"));
    }
    @Test

    public void testFormatWriterAccumulated () {
        BagObject queryBagObject = new BagObject ().put ("xxx", "yyy").put ("aaa", "bbb").add ("bbb", "abc").add ("bbb", "def");
        String queryString = queryBagObject.toString (MimeType.URL);
        assertTrue (queryString != null);
        assertTrue (queryString.equalsIgnoreCase ("aaa=bbb&bbb=abc&bbb=def&xxx=yyy&"));
    }
}
