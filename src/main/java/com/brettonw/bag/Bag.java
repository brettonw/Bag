package com.brettonw.bag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A high-level container for typed objects based on BagObject.
 */
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
        container.put (key, Serializer.serialize (object));
        return this;
    }

    public <WorkingType> WorkingType get (String key) {
        BagObject serializedObject = container.getBagObject (key);
        return (serializedObject != null) ? (WorkingType) Serializer.deserialize (serializedObject) : null;
    }

    public String toString () {
        return container.toString ();
    }
}
