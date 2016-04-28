package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

abstract class Parser {
    private static final Logger log = LogManager.getLogger (Parser.class);

    protected int index;
    protected String input;
    protected int inputLength;
    protected int lineNumber;
    protected int lastLineIndex;
    protected boolean error;

    private String readInputStream (InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
        BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder ();
        String line;
        while ((line = bufferedReader.readLine ()) != null) {
            stringBuilder.append (line).append ('\n');
        }
        bufferedReader.close ();
        return stringBuilder.toString ();
    }

    private void init (String input) {
        this.input = input;
        inputLength = input.length ();
        index = 0;
        lineNumber = 1;
        lastLineIndex = 0;
    }

    Parser (String input) {
        init (input);
    }

    Parser (InputStream inputStream) throws IOException {
        init (readInputStream (inputStream));
    }

    Parser (File file) throws IOException {
        InputStream inputStream = new FileInputStream (file);
        init (readInputStream (inputStream));
    }

    protected boolean check () {
        return (! error) && (index < inputLength);
    }

    protected void consumeWhiteSpace () {
        // consume white space (space, carriage return, tab, etc.
        while (check ()) {
            switch (input.charAt (index)) {
                case '\t': case ' ':
                    ++index;
                    break;
                case '\n':
                    ++index;
                    ++lineNumber;
                    lastLineIndex = index;
                    break;
                default:
                    return;
            }
        }
    }

    protected boolean expect(char c) {
        consumeWhiteSpace ();

        // the next character should be the one we expect
        if (check() && (input.charAt (index) == c)) {
            ++index;
            return true;
        }
        return false;
    }

    protected boolean require(char c) {
        return require (expect (c), "'" + c + "'");
    }

    protected boolean require (boolean condition, String explanation) {
        if (! condition) {
            onParseError (explanation + " REQUIRED");
        }
        return condition;
    }

    protected void onParseError (String errorMessage) {

        // log the messages, we only need to output the line if this is the first time the error is
        // being reported
        if (! error) {
            // say where the error is
            log.error ("Error while parsing input on line " + lineNumber + ", near: ");
            // find the end of the current line. note: line endings could only be '\n' because the
            // input reader consumed the actual line endings for us and replaced them with '\n'
            int lineEnd = index;
            while ((lineEnd < inputLength) && (input.charAt (lineEnd) != '\n')) {
                ++lineEnd;
            }
            log.error (input.substring (lastLineIndex, lineEnd));

            // build the error message, by computing a carat line, and adding the error meesage to it
            int errorIndex = index - lastLineIndex;
            char caratChars[] = new char[errorIndex + 2];
            Arrays.fill (caratChars, ' ');
            caratChars[errorIndex] = '^';
            String carat = new String (caratChars) + errorMessage;

            log.error (carat);

            // set the error state
            error = true;
        }
    }


    abstract BagArray readBagArray ();
    abstract BagObject readBagObject ();
}
