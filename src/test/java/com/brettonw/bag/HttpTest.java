package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.json.FormatReaderJson;
import org.junit.Test;

import java.io.IOException;

public class HttpTest {
    @Test
    public void test () {
        new Http ();
    }

    @Test
    public void testGet () throws IOException {
        BagObject brettonw = Http.getForBagObject ("https://api.github.com/users/brettonw", () -> null);
        AppTest.report (brettonw.getString ("login"), "brettonw", "Got a valid BagObject - 1");

        brettonw = Http.getForBagObject (FormatReaderJson.JSON_FORMAT, "https://api.github.com/users/brettonw", () -> null);
        AppTest.report (brettonw.getString ("login"), "brettonw", "Got a valid BagObject - 2");

        BagArray repos = Http.getForBagArray ("https://api.github.com/users/brettonw/repos", () -> null);
        AppTest.report (repos.getCount () > 0, true, "Got a valid BagArray - 1");

        repos = Http.getForBagArray (FormatReaderJson.JSON_FORMAT, "https://api.github.com/users/brettonw/repos", () -> null);
        AppTest.report (repos.getCount () > 0, true, "Got a valid BagArray - 2");
    }

    @Test
    public void testPost () throws IOException {
        BagObject postResponseBagObject = Http.postForBagObject (FormatReaderJson.JSON_FORMAT, "http://jsonplaceholder.typicode.com/posts/",
                new BagObject ()
                        .put ("login", "brettonw")
                        .put ("First Name", "Bretton")
                        .put ("Last Name", "Wade"),
                () -> null
        );
        AppTest.report (postResponseBagObject.getString ("login"), "brettonw", "Got a valid BagObject - 1");

        postResponseBagObject = Http.postForBagObject (FormatReaderJson.JSON_FORMAT, "http://jsonplaceholder.typicode.com/posts/",
                new BagObject ()
                        .put ("login", "brettonw")
                        .put ("First Name", "Bretton")
                        .put ("Last Name", "Wade"),
                () -> null
        );
        AppTest.report (postResponseBagObject.getString ("login"), "brettonw", "Got a valid BagObject - 2");

        BagArray postResponseBagArray = Http.postForBagArray (FormatReaderJson.JSON_FORMAT, "http://jsonplaceholder.typicode.com/posts/",
                new BagArray ()
                        .add ("login")
                        .add ("brettonw")
                        .add ("First Name")
                        .add ("Bretton")
                        .add ("Last Name")
                        .add ("Wade"),
                () -> null
        );
        AppTest.report (postResponseBagArray.getString (1), "brettonw", "Got a valid BagArray - 1");

        postResponseBagArray = Http.postForBagArray (FormatReaderJson.JSON_FORMAT, "http://jsonplaceholder.typicode.com/posts/",
                new BagArray ()
                        .add ("login")
                        .add ("brettonw")
                        .add ("First Name")
                        .add ("Bretton")
                        .add ("Last Name")
                        .add ("Wade"),
                () -> null
        );
        AppTest.report (postResponseBagArray.getString (1), "brettonw", "Got a valid BagArray - 2");
    }

    @Test
    public void testBogusGet () throws IOException {
        BagObject bogus = Http.getForBagObject (FormatReaderJson.JSON_FORMAT, "http://gojsonogle.com", () -> null);
        AppTest.report (bogus, null, "Not a valid URL");
    }

    @Test
    public void testBogusPost () throws IOException {
        BagObject bogus = Http.postForBagObject (FormatReaderJson.JSON_FORMAT, "http://gojsonogle.com", new BagObject ().put ("a", "b"), () -> null);
        AppTest.report (bogus, null, "Not a valid URL");
    }
}
