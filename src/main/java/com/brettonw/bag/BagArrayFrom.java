package com.brettonw.bag;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

public class BagArrayFrom {

    // from a string, with the mime type specified
    static public BagArray string (String string) {
        return string (string, MimeType.DEFAULT);
    }

    static public BagArray string (String string, Supplier<BagArray> fail) {
        return string (string, MimeType.DEFAULT, fail);
    }

    static public BagArray string (String string, String mimeType) {
        return string (string, mimeType, () -> null);
    }

    static public BagArray string (String string, String mimeType, Supplier<BagArray> fail) {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterReader(string, mimeType);
            return new BagArray (sourceAdapter);
        } catch (Exception exception) { }
        return fail.get ();
    }

    // from a file, with the mime type specified
    static public BagArray file (File file) {
        return file (file, () -> null);
    }

    static public BagArray file (File file, Supplier<BagArray> fail) {
        return file (file, MimeType.DEFAULT, fail);
    }

    static public BagArray file (File file, String mimeType) {
        return file (file, mimeType, () -> null);
    }

    static public BagArray file (File file, String mimeType, Supplier<BagArray> fail) {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterReader(file, mimeType);
            return new BagArray (sourceAdapter);
        } catch (Exception exception) { }
        return fail.get ();
    }

    // from a resource, with the mime type specified
    static public BagArray resource (Class context, String name) {
        return resource (context, name, () -> null);
    }

    static public BagArray resource (Class context, String name, Supplier<BagArray> fail) {
        return resource (context, name, MimeType.DEFAULT, fail);
    }

    static public BagArray resource (Class context, String name, String mimeType) {
        return resource (context, name, mimeType, () -> null);
    }

    static public BagArray resource (Class context, String name, String mimeType, Supplier<BagArray> fail) {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterReader (context, name, mimeType);
            return new BagArray (sourceAdapter);
        } catch (Exception exception) {
        }
        return fail.get ();
    }

    // from a stream, with the mime type specified
    static public BagArray inputStream (InputStream inputStream) {
        return inputStream (inputStream, MimeType.DEFAULT);
    }

    static public BagArray inputStream (InputStream inputStream, Supplier<BagArray> fail) {
        return inputStream (inputStream, MimeType.DEFAULT, fail);
    }

    static public BagArray inputStream (InputStream inputStream, String mimeType) {
        return inputStream (inputStream, mimeType, () -> null);
    }

    static public BagArray inputStream (InputStream inputStream, String mimeType, Supplier<BagArray> fail) {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterReader(inputStream, mimeType);
            return new BagArray (sourceAdapter);
        } catch (Exception exception) { }
        return fail.get ();
    }

    // from a HTTP connection (get)
    static public BagArray url (String urlString) {
        return url (urlString, () -> null);
    }

    static public BagArray url (String urlString, Supplier<BagArray> fail) {
        try {
            URL url = new URL (urlString);
            return url (url, fail);
        } catch (MalformedURLException exception) { }
        return fail.get ();
    }

    static public BagArray url (URL url) {
        return url (url, () -> null);
    }

    static public BagArray url (URL url, Supplier<BagArray> fail) {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterHttp(url);
            return new BagArray (sourceAdapter);
        } catch (Exception exception) { }
        return fail.get ();
    }

    // from a HTTP connection (post)
    static public BagArray url (String urlString, Bag postData, String postDataMimeType) {
        return url (urlString, postData, postDataMimeType, () -> null);
    }

    static public BagArray url (String urlString, Bag postData, String postDataMimeType, Supplier<BagArray> fail) {
        try {
            URL url = new URL (urlString);
            return url (url, postData, postDataMimeType, fail);
        } catch (MalformedURLException exception) { }
        return fail.get ();
    }

    static public BagArray url (URL url, Bag postData, String postDataMimeType) {
        return url (url, postData, postDataMimeType, () -> null);
    }

    static public BagArray url (URL url, Bag postData, String postDataMimeType, Supplier<BagArray> fail) {
        try {
            SourceAdapter sourceAdapter = new SourceAdapterHttp(url, postData, postDataMimeType);
            return new BagArray (sourceAdapter);
        } catch (Exception exception) { }
        return fail.get ();
    }
}
