package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SelectKey {
    private static final Logger log = LogManager.getLogger (SelectKey.class);

    public static final String KEYS = "keys";
    public static final String TYPE = "type";
    public static final SelectType DEFAULT_TYPE = SelectType.INCLUDE;

    private Map<String, String> keys;
    private SelectType type;

    public SelectKey () {
        this (DEFAULT_TYPE, (String[]) null);
    }

    public SelectKey (SelectType type, String... keysArray) {
        this.type = type;
        setKeys (keysArray);
    }

    public SelectKey (String... keysArray) {
        this (DEFAULT_TYPE, keysArray);
    }

    public SelectKey (BagArray bagArray) {
        this (DEFAULT_TYPE, bagArray);
    }

    public SelectKey (SelectType type, BagArray bagArray) {
        this (type, bagArray.toArray (String.class));
    }

    public SelectKey (BagObject bagObject) {
        this (bagObject.getEnum (TYPE, SelectType.class, () -> DEFAULT_TYPE), bagObject.getBagArray (KEYS));
    }

    public String select (String key, Supplier<String> notFound) {
        if (key != null) {
            switch (type) {
                case INCLUDE:
                    // in the include case, we can map the requested to to an "as" key
                    if (keys.containsKey (key)) return keys.get (key);
                    break;
                case EXCLUDE:
                    if (!keys.containsKey (key)) return key;
                    break;
            }
        }
        return notFound.get ();
    }

    public String select (String key) {
        return select (key, () -> null);
    }

    public SelectKey setType (SelectType type) {
        this.type = type;
        return this;
    }

    public SelectType getType () {
        return type;
    }

    public SelectKey setKeys (String... keysArray) {
        keys = new HashMap<> ();
        return addKeys (keysArray);
    }

    public SelectKey addKeys (String... keysArray) {
        return addKeys (keysArray, null);
    }
    public SelectKey addKeys (String[] keysArray, String[] asArray) {
        if (keysArray != null) {
            // if asArray is not supplied, use the keysArray itself
            if (asArray == null) asArray = keysArray;

            // check that the two arrays are the same size, and build the map if they are.
            if (keysArray.length == asArray.length) {
                List<String> keysList = Arrays.asList (keysArray);
                for (int i = 0, end = keysArray.length; i < end; ++i) {
                    keys.put (keysArray[i], asArray[i]);
                }
            } else {
                // what? throw an exception
                log.error ("Invalid 'AS' mapping specified");
            }

        }
        return this;
    }
}
