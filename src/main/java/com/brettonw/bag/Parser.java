package com.brettonw.bag;

import java.io.*;

abstract class Parser {
    protected int index;
    protected final String input;

    private String readInputStream (InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
        BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder ();
        String line;
        while ((line = bufferedReader.readLine ()) != null) {
            stringBuilder.append (line);
        }
        bufferedReader.close ();
        return stringBuilder.toString ();
    }

    Parser (String input) {
        this.input = input;
        index = 0;
    }

    Parser (InputStream inputStream) throws IOException {
        input = readInputStream (inputStream);
        index = 0;
    }

    Parser (File file) throws IOException {
        InputStream inputStream = new FileInputStream (file);
        input = readInputStream (inputStream);
        index = 0;
    }

    abstract BagArray ReadBagArray();
    abstract BagObject ReadBagObject();
}
