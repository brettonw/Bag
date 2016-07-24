package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import com.brettonw.bag.SourceAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.classindex.IndexSubclasses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@IndexSubclasses
abstract public class FormatReader {
    private static final Logger log = LogManager.getLogger (FormatReader.class);

    protected int index;
    protected final String input;
    protected final int inputLength;
    protected int lineNumber;
    protected int lastLineIndex;
    protected boolean error;

    /**
     *
     */
    public FormatReader () {
        this (null);
    }

    /**
     *
     * @param input
     */
    public FormatReader (String input) {
        this.input = input;
        inputLength = (input != null) ? input.length () : 0;
        index = 0;
        lineNumber = 1;
        lastLineIndex = 0;
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @param c
     * @return
     */
    protected boolean expect(char c) {
        consumeWhiteSpace ();

        // the next character should be the one we expect
        if (check() && (input.charAt (index) == c)) {
            ++index;
            return true;
        }
        return false;
    }

    /**
     *
     * @param c
     * @return
     */
    protected boolean require(char c) {
        return require (expect (c), "'" + c + "'");
    }

    /**
     *
     * @param condition
     * @param explanation
     * @return
     */
    protected boolean require (boolean condition, String explanation) {
        if (! condition) {
            onReadError (explanation + " REQUIRED");
        }
        return condition;
    }

    /**
     *
     * @param errorMessage
     */
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

    /**
     *
     * @param bagArray
     * @return
     */
    abstract public BagArray read (BagArray bagArray);

    /**
     *
     * @param bagObject
     * @return
     */
    abstract public BagObject read (BagObject bagObject);

    // static type registration by name
    private static final Map<String, Function<String, FormatReader>> formatReaders = new HashMap<> ();

    /**
     *
     * @param format
     * @param replace
     * @param factory
     */
    public static void registerFormatReader (String format, boolean replace, Function<String, FormatReader> factory) {
        format = format.toLowerCase ();
        if ((! replace) || (! formatReaders.containsKey(format))) {
            formatReaders.put(format, factory);
        }
    }

    private static FormatReader getFormatReader (String stringData, String mimeType) {
        // deduce the format, and create the format reader
        return ((mimeType != null) && formatReaders.containsKey(mimeType)) ? formatReaders.get(mimeType).apply(stringData) : null;
    }

    public static BagArray read (BagArray bagArray, SourceAdapter sourceAdapter) {
        FormatReader formatReader = getFormatReader(sourceAdapter.getStringData(), sourceAdapter.getMimeType());
        return (formatReader != null) ? formatReader.read (bagArray) : null;
    }

    public static BagObject read (BagObject bagObject, SourceAdapter sourceAdapter) {
        FormatReader formatReader = getFormatReader(sourceAdapter.getStringData(), sourceAdapter.getMimeType());
        return (formatReader != null) ? formatReader.read (bagObject) : null;
    }

}
