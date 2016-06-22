package com.brettonw.bag;

import java.io.*;

public class SourceAdapterReader extends SourceAdapter {
    public SourceAdapterReader (Reader reader, String mimeType) throws IOException {
        this.mimeType = mimeType;
        stringData = readString (reader);
    }

    public SourceAdapterReader (String inputString, String mimeType) throws IOException {
        this (new StringReader (inputString), mimeType);
    }

    public SourceAdapterReader (InputStream inputStream, String mimeType) throws IOException {
        this (new InputStreamReader (inputStream), mimeType);
    }

    public SourceAdapterReader (File file) throws IOException {
        this (file, deduceMimeType (file.getName ()));
    }

    public SourceAdapterReader (File file, String mimeType) throws IOException {
        this (new FileReader (file), mimeType);
    }
}
