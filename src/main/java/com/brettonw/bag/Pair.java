package com.brettonw.bag;

import java.util.Comparator;

// a Pair is the joining of a name and an object, a key-value pairing if you will
class Pair implements Comparable<Pair> {
    protected String key;
    protected Object value;

    public Pair (String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Pair (String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Object getValue () {
        return value;
    }

    public Pair setValue (Object value) {
        this.value = value;
        return this;
    }

    public int compareTo (Pair o) {
        return key.compareTo (o.key);
    }
}
