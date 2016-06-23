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
        BagObject brettonw = Http.getForBagObject ("https://httpbin.org/ip", () -> null);
        AppTest.report (brettonw.getString ("origin") != null, true, "Got a valid BagObject - 1");

        brettonw = Http.getForBagObject (MimeType.JSON, "https://httpbin.org/ip", () -> null);
        AppTest.report (brettonw.getString ("origin") != null, true, "Got a valid BagObject - 2");

        BagArray repos = Http.getForBagArray ("https://api.github.com/users/brettonw/repos", () -> null);
        AppTest.report (repos.getCount () > 0, true, "Got a valid BagArray - 1");

        repos = Http.getForBagArray (MimeType.JSON, "https://api.github.com/users/brettonw/repos", () -> null);
        AppTest.report (repos.getCount () > 0, true, "Got a valid BagArray - 2");
    }

    @Test
    public void testPost () throws IOException {
        BagObject postResponseBagObject = Http.postForBagObject (MimeType.JSON, "http://jsonplaceholder.typicode.com/posts/",
                new BagObject ()
                        .put ("login", "brettonw")
                        .put ("First Name", "Bretton")
                        .put ("Last Name", "Wade"),
                () -> null
        );
        AppTest.report (postResponseBagObject.getString ("login"), "brettonw", "Got a valid BagObject - 1");

        postResponseBagObject = Http.postForBagObject (MimeType.JSON, "http://jsonplaceholder.typicode.com/posts/",
                new BagObject ()
                        .put ("login", "brettonw")
                        .put ("First Name", "Bretton")
                        .put ("Last Name", "Wade"),
                () -> null
        );
        AppTest.report (postResponseBagObject.getString ("login"), "brettonw", "Got a valid BagObject - 2");

        BagArray postResponseBagArray = Http.postForBagArray (MimeType.JSON, "http://jsonplaceholder.typicode.com/posts/",
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

        postResponseBagArray = Http.postForBagArray (MimeType.JSON, "http://jsonplaceholder.typicode.com/posts/",
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
        BagObject bogus = Http.getForBagObject (MimeType.JSON, "http://gojsonogle.com", () -> null);
        AppTest.report (bogus, null, "Not a valid URL");
    }

    @Test
    public void testBogusPost () throws IOException {
        BagObject bogus = Http.postForBagObject (MimeType.JSON, "http://gojsonogle.com", new BagObject ().put ("a", "b"), () -> null);
        AppTest.report (bogus, null, "Not a valid URL");
    }
}
