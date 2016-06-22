package com.brettonw.bag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Supplier;

public class SourceAdapter {
    protected String mimeType;
    protected String stringData;

    public SourceAdapter () {}

    public SourceAdapter (String mimeType, String stringData) {
        this.mimeType = mimeType;
        this.stringData = stringData;
    }

    public SourceAdapter setMimeType (String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public SourceAdapter setStringData (String stringData) {
        this.stringData = stringData;
        return this;
    }

    public String getMimeType () {
        return mimeType;
    }

    public String getMimeType (Supplier<String> noMimeType) {
        return (mimeType != null) ? mimeType : noMimeType.get ();
    }

    public String getStringData () {
        return stringData;
    }

    public String getStringData (Supplier<String> noStringData) {
        return (stringData != null) ? stringData : noStringData.get ();
    }

    static String deduceMimeType (String name) {
        // XXX yeah, no
        return "application/json";
    }

    static String readString (Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader (reader);
        StringBuilder stringBuilder = new StringBuilder ();
        String line;
        while ((line = bufferedReader.readLine ()) != null) {
            stringBuilder.append (line).append ('\n');
        }
        bufferedReader.close ();
        return stringBuilder.toString ();
    }
}
