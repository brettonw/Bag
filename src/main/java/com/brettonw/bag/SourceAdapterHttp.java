package com.brettonw.bag;

import com.brettonw.bag.formats.MimeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SourceAdapterHttp extends SourceAdapter {
    private static final Logger log = LogManager.getLogger (SourceAdapterHttp.class);

    private static final String UTF_8 = StandardCharsets.UTF_8.name ();

    public SourceAdapterHttp (String urlString) throws IOException {
        this (new URL (urlString));
    }

    public SourceAdapterHttp (URL url) throws IOException {
        this (url, null, null);
    }

    public SourceAdapterHttp (String urlString, Bag postData, String postDataMimeType) throws IOException {
        this (new URL (urlString), postData, postDataMimeType);
    }

    public SourceAdapterHttp (URL url, Bag postData, String postDataMimeType) throws IOException {
        // create the connection, see if it was successful
        HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
        if (connection != null) {
            // don't use the caches
            connection.setUseCaches(false);

            // set up the request, POST if there is post data, otherwise, GET
            if (postData != null) {
                // prepare the post data
                String postDataString = postData.toString (postDataMimeType);
                byte[] postDataBytes = postDataString.getBytes ();

                // setup the headers
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", postDataMimeType + ";charset=UTF-8"); // "application/json"
                connection.setRequestProperty("Content-Length", Integer.toString(postDataBytes.length));

                // write out the request data
                connection.setDoOutput (true);
                OutputStream outputStream = connection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.write(postDataBytes);
                dataOutputStream.close();
            } else {
                // setup the header
                connection.setRequestMethod("GET");
            }

            // get the response type (this will trigger the actual fetch), then tease out the
            // response type (use a default if it's not present) and the charset (if given,
            // otherwise default to UTF-8, because that's what it will be in Java)
            String contentTypeHeader = connection.getHeaderField("Content-Type");
            String charset = UTF_8;
            mimeType = MimeType.DEFAULT;
            if (contentTypeHeader != null) {
                String[] contentType = contentTypeHeader.replace (" ", "").split (";");
                mimeType = contentType[0];
                if (contentType.length > 1) {
                    charset = contentType[1].split ("=", 2)[1];
                }
            }

            // get the response data
            InputStream inputStream = connection.getInputStream();
            Reader inputStreamReader = new InputStreamReader (inputStream, charset);
            stringData = readString (inputStreamReader);
            connection.disconnect();
        }
    }
}
