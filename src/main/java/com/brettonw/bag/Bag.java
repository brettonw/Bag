package com.brettonw.bag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Bag {
    private BagObject container;

    public Bag () {
        container = new BagObject ();
    }

    public Bag (int size) {
        container = new BagObject (size);
    }

    public Bag (String string) throws IOException {
        container = new BagObject (string);
    }

    public Bag (InputStream stream) throws IOException {
        container = new BagObject (stream);
    }

    public Bag (File file) throws IOException {
        container = new BagObject (file);
    }

    public Bag (BagObject container) {
        this.container = container;
    }

    public Bag (Bag bag) {
        // note, not a deep copy
        container = bag.container;
    }

    public Bag put (String key, Object object) {
        container.put (key, Serializer.toBagObject (object));
        return this;
    }

    public Object get (String key) {
        BagObject serializedObject = container.getBagObject (key);
        return (serializedObject != null) ? Serializer.fromBagObject (serializedObject) : null;
    }

    public String toString () {
        return container.toString ();
    }
}
