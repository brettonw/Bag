package com.github.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

// A BagObject is a collection of values in text-based key/value pairs. Typing is performed lazily
// on extraction, and presumes the user knows what they are expecting to get. This class is
// primarily intended for messaging, events, and other applications that require complex values to
// be shared in a text- based data interchange format. It is loosely based on a combination of XML
// in use, and the JSONObject from the website (http://www.json.org).
public class BagObject {
    private static final Logger log = LogManager.getLogger (BagObject.class);

    protected static final int START_SIZE = 1;
    protected static final int DOUBLING_CAP = 16;
    protected Pair[] container;
    protected int count;

    public BagObject () {
        count = 0;
        container = new Pair[START_SIZE];
    }

    public BagObject (int size) {
        count = 0;
        container = new Pair[size];
    }

    private void grow (int gapIndex) {
        Pair src[] = container;
        if (count == container.length) {
            // if the array is smaller than the cap then double its size, otherwise just add the block
            int newSize = (count > DOUBLING_CAP) ? (count + DOUBLING_CAP) : (count * 2);
            container = new Pair[newSize];
            System.arraycopy (src, 0, container, 0, gapIndex);
        }
        System.arraycopy (src, gapIndex, container, gapIndex + 1, count - gapIndex);
        ++count;
    }

    private int binarySearch (String key) {
        Pair term = new Pair (key);
        return Arrays.binarySearch (container, 0, count, term);
    }

    public Object getObject (String key) {
        int index = binarySearch (key);
        if (index >= 0) {
            Pair pair = container[index];
            return pair.getValue ();
        }
        return null;
    }

    public BagObject put (String key, Object object) {
        // convert the incoming object to the internal store format, we don't store null values, as
        // that is indistinguishable on the get from fetching a non-existent key
        object = BagHelper.objectify (object);
        if (object != null) {
            int index = binarySearch (key);
            if (index >= 0) {
                Pair pair = container[index];
                pair.setValue (object);
                //log.debug ("Replace - Key (" + key + "), Count (" + count + "), Index (" + index + ")");
            } else {
                // the binary search returns a funky encoding of the index where the new value
                // should go when it's not there, so we have to decode that number (-index - 1)
                index = -(index + 1);
                grow (index);
                container[index] = new Pair (key, object);
                //log.debug ("Add - Key (" + key + "), Count (" + count + "), Index (" + index + ")");
            }
        }
        return this;
    }

    public BagObject remove (String key) {
        int index = binarySearch (key);
        if (index >= 0) {
            int gapIndex = index + 1;
            System.arraycopy (container, gapIndex, container, index, count - gapIndex);
            --count;
        } else {
            // XXX what would we like to have happen here? do we care?
        }
        return this;
    }

    // all of these are helpers
    public String getString (String key) {
        return (String) getObject (key);
    }

    public Boolean getBoolean (String key) {
        try {
            return Boolean.parseBoolean (getString (key));
        } catch (ClassCastException exc) {
            log.debug ("intentional catch to return null");
        }
        return null;
    }

    public Long getLong (String key) {
        try {
            return Long.parseLong (getString (key));
        } catch (ClassCastException exc) {
            log.debug ("intentional catch to return null");
        }
        return null;
    }

    public Integer getInteger (String key) {
        Long value = getLong (key);
        return (value != null) ? value.intValue () : null;
    }

    public Double getDouble (String key) {
        try {
            return Double.parseDouble (getString (key));
        } catch (ClassCastException exc) {
            log.debug ("intentional catch to return null");
        }
        return null;
    }

    public Float getFloat (String key) {
        Double value = getDouble (key);
        return (value != null) ? value.floatValue () : null;
    }

    public BagObject getBagObject (String key) {
        return (BagObject) getObject (key);
    }

    public BagArray getBagArray (String key) {
        return (BagArray) getObject (key);
    }

    public String[] keys () {
        String keys[] = new String[count];
        for (int i = 0; i < count; ++i) {
            keys[i] = container[i].getKey ();
        }
        return keys;
    }

    @Override
    public String toString () {
        StringBuilder result = new StringBuilder ();
        boolean isFirst = true;
        for (int i = 0; i < count; ++i) {
            result.append (isFirst ? "" : ",");
            isFirst = false;

            Pair pair = container[i];
            result
                    .append (BagHelper.quote (pair.getKey ()))
                    .append (":")
                    .append (BagHelper.stringify (pair.getValue ()));
        }
        return BagHelper.enclose (result.toString (), "{}");
    }

    public static BagObject fromString (String input) {
        // parse the string out... it is assumed to be a well formed BagObject serialization
        BagParser parser = new BagParser (input);
        return parser.ReadBagObject ();
    }

}
