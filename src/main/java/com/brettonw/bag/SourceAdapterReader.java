package com.brettonw.bag;

import com.brettonw.bag.formats.MimeType;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SourceAdapterReader extends SourceAdapter {
    public SourceAdapterReader (Reader reader, String mimeType) throws IOException {
        this.mimeType = mimeType;
        stringData = readString (reader);
    }

    public SourceAdapterReader (String string, String mimeType) throws IOException {
        this (new StringReader (string), mimeType);
    }

    public SourceAdapterReader (InputStream inputStream, String mimeType) throws IOException {
        // always force UTF-8 for input streams
        this (new InputStreamReader (inputStream, StandardCharsets.UTF_8), mimeType);
    }

    public SourceAdapterReader (File file) throws IOException {
        this (file, MimeType.DEFAULT);
    }

    public SourceAdapterReader (File file, String mimeType) throws IOException {
        this (new FileInputStream (file), deduceMimeType(mimeType, file.getName()));
    }

    public SourceAdapterReader (Class context, String name) throws IOException {
        this (context, name, MimeType.DEFAULT);
    }

    public SourceAdapterReader (Class context, String name, String mimeType) throws IOException {
        this (context.getResourceAsStream (name), deduceMimeType (mimeType, name));
    }
}
