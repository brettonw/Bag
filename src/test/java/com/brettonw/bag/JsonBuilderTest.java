package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

public class JsonBuilderTest {
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
        String output = JsonBuilder.toJsonString (bagObject);
        AppTest.report (output.length () > 0, true, "toJsonString...");
    }
}
