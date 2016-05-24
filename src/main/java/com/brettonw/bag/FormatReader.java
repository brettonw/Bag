package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

abstract public class FormatReader {
    private static final Logger log = LogManager.getLogger (FormatReader.class);

    protected int index;
    protected final String input;
    protected final int inputLength;
    protected int lineNumber;
    protected int lastLineIndex;
    protected boolean error;

    public FormatReader (String input) {
        this.input = input;
        inputLength = input.length ();
        index = 0;
        lineNumber = 1;
        lastLineIndex = 0;
    }

    protected boolean check () {
        return (! error) && (index < inputLength);
    }

    protected void consumeWhiteSpace () {
        // consume white space (space, carriage return, tab, etc.
        while (check ()) {
            switch (input.charAt (index)) {
                // tab, space, nbsp
                case '\t': case ' ': case '\u00a0':
                    ++index;
                    break;
                // carriage return - the file reader converts all returns to \n
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
            onReadError (explanation + " REQUIRED");
        }
        return condition;
    }

    protected void onReadError (String errorMessage) {

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

            // build the error message, by computing a carat line, and adding the error message to it
            int errorIndex = index - lastLineIndex;
            char[] caratChars = new char[errorIndex + 2];
            Arrays.fill (caratChars, ' ');
            caratChars[errorIndex] = '^';
            String carat = new String (caratChars) + errorMessage;

            log.error (carat);

            // set the error state
            error = true;
        }
    }

    abstract public BagArray read (BagArray bagArray);
    abstract public BagObject read (BagObject bagObject);

    // static type registration by name
    private static final Map<String, Function<String, FormatReader>> formatReaders = new HashMap<> ();

    public static void registerFormatReader (String format, boolean replace, Function<String, FormatReader> factory) {
        if ((! replace) || (! formatReaders.containsKey(format))) {
            formatReaders.put(format, factory);
        }
    }

    protected static String getFileType (File file) {
        String fileName = file.getName ();
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i+1) : null;
    }

    public static String validFileType (File file, String notFound) {
        String extension = getFileType (file);
        return formatReaders.containsKey (extension) ? extension : notFound;
    }

    private static String readInput (Reader input) throws IOException {
        BufferedReader bufferedReader = new BufferedReader (input);
        StringBuilder stringBuilder = new StringBuilder ();
        String line;
        while ((line = bufferedReader.readLine ()) != null) {
            stringBuilder.append (line).append ('\n');
        }
        bufferedReader.close ();
        return stringBuilder.toString ();
    }

    public static BagArray read (BagArray bagArray, String format, Reader reader) throws IOException {
        if (formatReaders.containsKey(format)) {
            String input = readInput (reader);
            FormatReader formatReader = formatReaders.get(format).apply (input);
            return formatReader.read (bagArray);
        }
        return null;
    }

    public static BagObject read (BagObject bagObject, String format, Reader reader) throws IOException {
        if (formatReaders.containsKey(format)) {
            String input = readInput (reader);
            FormatReader formatReader = formatReaders.get(format).apply (input);
            return formatReader.read (bagObject);
        }
        return null;
    }
}
