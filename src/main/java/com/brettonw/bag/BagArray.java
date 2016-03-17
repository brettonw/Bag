package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// A BagArray is a collection of constrained type values in an array. Typing is performed lazily
// on extraction, and presumes the user knows what they are expecting to get. This class is
// primarily intended for messaging, events, and other applications that require complex values to
// be shared in a text-based data interchange format. It is loosely based on a combination of XML
// in use, and the JSONObject from the website (http://www.json.org).
public class BagArray {
    private static final Logger log = LogManager.getLogger (BagObject.class);

    protected static final int START_SIZE = 1;
    protected static final int DOUBLING_CAP = 128;
    protected Object[] container;
    protected int count;

    public BagArray () {
        count = 0;
        container = new Object[START_SIZE];
    }

    public BagArray (int size) {
        count = 0;
        container = new Object[size];
    }

    public int getCount () {
        return count;
    }

    private void grow (int gapIndex) {
        Object src[] = container;
        if (count == container.length) {
            // if the array is smaller than the cap then double its size, otherwise just add the block
            int newSize = (count > DOUBLING_CAP) ? (count + DOUBLING_CAP) : (count * 2);
            container = new Object[newSize];
            System.arraycopy (src, 0, container, 0, gapIndex);
        }
        System.arraycopy (src, gapIndex, container, gapIndex + 1, count - gapIndex);
        ++count;
    }

    public BagArray insert (int index, Object object) {
        grow (index);
        // note that arrays can store null objects, unlike bags
        container[index] = BagHelper.objectify (object);
        return this;
    }

    public BagArray add (Object object) {
        return insert (count, object);
    }

    public BagArray replace (int index, Object object) {
        // note that arrays can store null objects, unlike bags
        container[index] = BagHelper.objectify (object);
        return this;
    }

    public BagArray remove (int index) {
        if ((index >= 0) && (index < count)) {
            int gapIndex = index + 1;
            System.arraycopy (container, gapIndex, container, index, count - gapIndex);
            --count;
        } else {
            // XXX what would we like to have happen here? do we care?
        }
        return this;
    }

    // all of these are helpers
    public String getString (int index) {
        return (String) container[index];
    }

    public Boolean getBoolean (int index) {
        return Boolean.parseBoolean (getString (index));
    }

    public Long getLong (int index) {
        return Long.parseLong (getString (index));
    }

    public Integer getInteger (int index) {
        return Integer.parseInt (getString (index));
    }

    public Double getDouble (int index) {
        return Double.parseDouble (getString (index));
    }

    public Float getFloat (int index) {
        return Float.parseFloat (getString (index));
    }

    public BagObject getBagObject (int index) {
        return (BagObject) container[index];
    }

    public BagArray getBagArray (int index) {
        return (BagArray) container[index];
    }

    @Override
    public String toString () {
        StringBuilder result = new StringBuilder ();
        boolean first = true;
        for (int i = 0; i < count; ++i) {
            result.append (first ? "" : ",");
            first = false;
            result.append (BagHelper.stringify (container[i]));
        }
        return BagHelper.enclose (result.toString (), "[]");
    }

    public static BagArray fromString (String input) {
        // parse the string out... it is assumed to be a well formed BagArray serialization
        BagParser parser = new BagParser (input);
        return parser.ReadBagArray ();
    }
}
