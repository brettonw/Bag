package com.brettonw.bag;

// The JsonParser is loosely modeled after a JSON parser grammar from the site (http://www.json.org).
// The main difference is that we ignore differences between value types (all of them will be
// strings internally), and assume the input is a well formed string representation of a BagObject
// or BagArray in JSON-ish format

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class JsonParser extends Parser {
    private static final Logger log = LogManager.getLogger (JsonParser.class);

    JsonParser (String input) {
        super (input);
    }

    JsonParser (InputStream inputStream) throws IOException {
        super (inputStream);
    }

    JsonParser (File file) throws IOException {
        super (file);
    }

    @Override
    BagArray readBagArray () {
        // <Array> :: [ ] | [ <Elements> ]
        BagArray bagArray = new BagArray();
        return (expect('[') && readElements (bagArray) && require(']')) ? bagArray : null;
    }

    @Override
    BagObject readBagObject () {
        // <Object> ::= { } | { <Members> }
        BagObject bagObject = new BagObject();
        return (expect('{') && readMembers (bagObject) && require('}')) ? bagObject : null;
    }

    private boolean storeValue (BagArray bagArray) {
        Object value = readValue ();
        if (value != null) {
            // special case for "null"
            if ((value instanceof String) && (((String) value).equalsIgnoreCase ("null"))) {
                value = null;
            }
            bagArray.add (value);
            return true;
        }
        return false;
    }

    private boolean readElements (BagArray bagArray) {
        // <Elements> ::= <Value> | <Value> , <Elements>
        boolean result = true;
        if (storeValue (bagArray)) {
            while (expect (',')) {
                result = require (storeValue (bagArray), "Valid Value");
            }
        }
        return result;
    }

    private boolean readMembers (BagObject bagObject) {
        // <Members> ::= <Pair> | <Pair> , <Members>
        boolean result = true;
        if (readPair (bagObject)) {
            while (expect (',')) {
                result = require (readPair (bagObject), "Valid Pair");
            }
        }
        return result;
    }

    private boolean storeValue (BagObject bagObject, String key) {
        Object value = readValue ();
        if (value != null) {
            // special case for "null"
            if (!((value instanceof String) && (((String) value).equalsIgnoreCase ("null")))) {
                bagObject.put (key, value);
            }
            return true;
        }
        return false;
    }

    private boolean readPair (BagObject bagObject) {
        // <Pair> ::= <String> : <Value>
        String key = readString ();
        if ((key != null) && (key.length () > 0) && require (':')) {
            return require (storeValue (bagObject, key), "Valid Value");
        }
        return false;
    }

    private boolean isAllowedBareValue (char c) {
        return (Character.isLetterOrDigit (c)) || (".+-_$".indexOf (c) >= 0);
    }

    private String readString () {
        // " chars " | <chars>
        String result = null;
        char c;
        if (expect('"')) {
            int start = index;
            while (check () && ((c = input.charAt(index)) != '"')) {
                // using the escape mechanism is like a free pass for the next character, but we
                // don't do any transformation on the substring, just return it as written
                index += (c == '\\') ? 2 : 1;
            }
            result = input.substring (start, index++);
        } else {
            // technically, we're being sloppy allowing bare values where quoted strings are
            // expected, but it's part of the simplified structure we support. This allows us to
            // read valid JSON files without handling every single case.
            int start = index;
            while (check () && isAllowedBareValue (c = input.charAt (index))) {
                ++index;
            }

            // capture the result if we actually consumed some characters
            if (index > start) {
                result = input.substring (start, index);
            }
        }
        return result;
    }

    private Object readValue () {
        // <Value> ::= <String> | <Object> | <Array>
        consumeWhiteSpace ();

        Object value = null;
        if (check ()) {
            switch (input.charAt (index)) {
                case '{':
                    value = readBagObject ();
                    break;

                case '[':
                    value = readBagArray ();
                    break;

                case '"':
                default:
                    value = readString ();
                    break;
            }
        }
        return value;
    }
}
