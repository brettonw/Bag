package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class XmlParser extends Parser {
    private static final Logger log = LogManager.getLogger (XmlParser.class);

    XmlParser (String input) throws IOException {
        super (input);
    }

    XmlParser (InputStream inputStream) throws IOException {
        super (inputStream);
    }

    XmlParser (File file) throws IOException {
        super (file);
    }

    BagArray readBagArray () {
        // <Array> :: [ ] | [ <Elements> ]
        BagArray bagArray = new BagArray();
        return (expect('[') && readElements (bagArray) && expect(']')) ? bagArray : null;
    }

    BagObject readBagObject () {
        // <Object> ::= { } | { <Members> }
        BagObject bagObject = new BagObject();
        return (expect('{') && readMembers (bagObject, true) && expect('}')) ? bagObject : null;
    }

    private boolean readElements (BagArray bagArray) {
        // <Elements> ::= <Value> | <Value> , <Elements>
        Object value = readValue ();
        if (value != null) {
            // special case for "null"
            if ((value instanceof String) && (((String) value).equalsIgnoreCase ("null"))) {
                value = null;
            }
            bagArray.add (value);
            //log.info ((value != null) ? value.toString () : "null");
        }
        //noinspection PointlessBooleanExpression
        return (expect(',') && readElements (bagArray)) || true;
    }

    private boolean readMembers (BagObject bagObject, boolean first) {
        // <Members> ::= <Pair> | <Pair> , <Members>
        return readPair (bagObject) ? (expect (',') ? readMembers (bagObject, false) : true) : first;
    }

    private boolean readPair (BagObject bagObject) {
        return false;
    }

    private boolean isAllowedBareValue (char c) {
        return (Character.isLetterOrDigit (c)) || (".+-_$".indexOf (c) >= 0);
    }

    private String readString () {
        return "";
    }

    private Object readValue () {
        // <Value> ::= <String> | <Object> | <Array>
        consumeWhiteSpace ();

        Object value = null;
        return value;
    }
}
