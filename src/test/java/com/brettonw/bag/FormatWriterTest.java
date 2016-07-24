package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.formats.FormatWriter;
import com.brettonw.bag.formats.MimeType;
import com.brettonw.bag.formats.json.FormatWriterJson;
import org.junit.Test;

public class FormatWriterTest {

    static {
        new FormatWriterJson ();
    }
    @Test
    public void testBadFormat () {
        BagObject bagObject = new BagObject ()
                .put ("x", "y");
        String output = FormatWriter.write (bagObject, "Xxx_format");
        BagArray bagArray = new BagArray ()
                .add (bagObject);
        output = FormatWriter.write (bagArray, "Xyz_format");
    }

    @Test
    public void test () {
        BagObject bagObject = new BagObject ()
                .put ("x", "y")
                .put ("abc", 123)
                .put ("def", new BagObject ()
                        .put ("xyz", "pdq")
                )
                .put ("mno", new BagArray ()
                        .add (null)
                        .add (1)
                        .add (new BagObject ()
                                .put ("r", "s")
                        )
                );
        String output = FormatWriter.write (bagObject, MimeType.JSON);
        AppTest.report (output.length () > 0, true, "write...");

        BagObject recon = BagObjectFrom.string (output);
        AppTest.report (FormatWriter.write (recon, MimeType.JSON), output, "Json output is round-trippable");
        AppTest.report (recon.getString ("def/xyz"), "pdq", "Json output is valid");
    }
}
