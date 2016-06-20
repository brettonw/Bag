package com.brettonw.bag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class SelectKey {
    public static final String KEYS = "keys";
    public static final String TYPE = "type";
    public static final SelectType DEFAULT_TYPE = SelectType.INCLUDE;

    private Set<String> keys;
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
                    return keys.contains (key) ? key : notFound.get ();
                case EXCLUDE:
                    return (!keys.contains (key)) ? key : notFound.get ();
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
        keys = new HashSet<> ();
        return addKeys (keysArray);
    }

    public SelectKey addKeys (String... keysArray) {
        if (keysArray != null) {
            List<String> keysList = Arrays.asList (keysArray);
            keys.addAll (keysList);
        }
        return this;
    }
}
