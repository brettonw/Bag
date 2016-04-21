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
    BagArray ReadBagArray() {
        // <Array> :: [ ] | [ <Elements> ]
        BagArray bagArray = new BagArray();
        return (Expect('[') && ReadElements(bagArray) && Expect(']')) ? bagArray : null;
    }

    @Override
    BagObject ReadBagObject() {
        // <Object> ::= { } | { <Members> }
        BagObject bagObject = new BagObject();
        return (Expect('{') && ReadMembers(bagObject) && Expect('}')) ? bagObject : null;
    }

    private void consumeWhiteSpace () {
        // consume white space (space, carriage return, tab, etc.
        while (Character.isWhitespace (input.charAt (index))) {
            ++index;
        }
    }

    private boolean Expect(char c) {
        consumeWhiteSpace ();

        // the next character should be the one we expect
        if (input.charAt (index) == c) {
            ++index;
            return true;
        }
        return false;
    }

    private boolean ReadElements(BagArray bagArray) {
        // <Elements> ::= <Value> | <Value> , <Elements>
        do {
            Object value = ReadValue ();
            if (value != null) {
                // special case for "null"
                if ((value instanceof String) && (((String) value).equalsIgnoreCase ("null"))) {
                    value = null;
                }
                bagArray.add (value);
                //log.info ((value != null) ? value.toString () : "null");
            }
        } while (Expect (','));
        return true;
    }

    private boolean ReadMembers(BagObject bagObject) {
        // <Members> ::= <Pair> | <Pair> , <Members>
        //return ReadPair (bagObject) ? (Expect (',') ? ReadMembers (bagObject, false) : true) : first;

        boolean loop;
        boolean result = true;
        boolean first = true;
        do {
            loop = false;
            if (ReadPair (bagObject)) {
                if (Expect (',')) {
                    loop = true;
                    first = false;
                }
            } else {
                result = first;
            }
        } while (loop);
        return result;
    }

    private boolean ReadPair(BagObject bagObject) {
        // <Pair> ::= <String> : <Value>
        String key = ReadString();
        if ((key != null) && (key.length () > 0) && Expect(':')) {
            Object value = ReadValue();
            if (value != null) {
                // special case for "null"
                if (!((value instanceof String) && (((String) value).equalsIgnoreCase ("null")))) {
                    bagObject.put (key, value);
                }
                return true;
            }
        }

        // this will only happen if we are reconstructing from invalid source
        return false;
    }

    private boolean isAllowedBareValue (char c) {
        return (Character.isLetterOrDigit (c)) || (".+-_$".indexOf (c) >= 0);
    }

    private String ReadString() {
        // " chars " | <chars>
        String result = null;
        if (Expect('"')) {
            int start = index;
            char c = input.charAt(index);
            while (c != '"') {
                // using the escape mechanism is like a free pass for the next character, but we
                // don't do any transformation on the substring, just return it as written
                if (c == '\\') {
                    ++index;
                }
                c = input.charAt(++index);
            }
            result = input.substring (start, index++);
        } else {
            // technically, we're being sloppy allowing bare values where quoted strings are
            // expected, but it's part of the simplified structure we support. This allows us to
            // read valid JSON files without handling every single case.
            int start = index;
            char c = input.charAt (index);
            while (isAllowedBareValue (c)) {
                c = input.charAt (++index);
            }

            // capture the result if we actually consumed some characters
            if (index > start) {
                result = input.substring (start, index);
            }
        }
        return result;
    }

    private Object ReadValue() {
        // <Value> ::= <String> | <Object> | <Array>
        consumeWhiteSpace ();

        Object value = null;
        switch (input.charAt (index)) {
            case '{':
                value = ReadBagObject();
                break;

            case '[':
                value = ReadBagArray();
                break;

            case '"':
            default:
                value = ReadString();
                break;
        }
        return value;
    }
}
