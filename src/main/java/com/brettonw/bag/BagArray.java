package com.brettonw.bag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A collection of text-based values stored in a zero-based indexed array.
 * <p>
 * Note: the BagArray class, from a memory allocation standpoint, is not designed to store very
 * large numbers of elements (more than 1,000). It will work, but we have not chosen to focus on
 * this as a potential use-case.
 */
public class BagArray {
    private static final Logger log = LogManager.getLogger (BagArray.class);

    private static final int START_SIZE = 1;
    private static final int DOUBLING_CAP = 128;
    private Object[] container;
    private int count;

    /**
     * Create a new BagArray with a default underlying storage size.
     */
    public BagArray () {
        count = 0;
        container = new Object[START_SIZE];
    }

    /**
     * Create a new BagArray with hint for the underlying storage size.
     *
     * @param size The expected number of elements in the BagArray, treated as a hint to optimize
     *             memory allocation. If additional elements are stored, the BagArray will revert
     *             to normal allocation behavior.
     */
    public BagArray (int size) {
        count = 0;
        container = new Object[size];
    }

    /**
     * Return the number of elements stored in the BagArray.
     *
     * @return the count of elements in the underlying store. This is distinct from the capacity of
     * the underlying store.
     */
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

    /**
     * Inserts the element at the given index of the underlying array store. The underlying store is
     * shifted to make space for the new element. The shift might cause the underlying store to be
     * resized if there is insufficient room.
     * <p>
     * Note that null values for the element ARE stored, as the underlying store is not a sparse
     * array.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @param object The element to store.
     * @return The BagArray, so that operations can be chained together.
     */
    public BagArray insert (int index, Object object) {
        grow (index);
        // note that arrays can store null objects, unlike bags
        container[index] = BagHelper.objectify (object);
        return this;
    }

    /**
     * Adds the element at the ed of the underlying array store. The underlying store might be
     * resized if there is insufficient room.
     * <p>
     * Note that null values for the element ARE stored, as the underlying store is not a sparse
     * array.
     *
     * @param object The element to store.
     * @return The BagArray, so that operations can be chained together.
     */
    public BagArray add (Object object) {
        return insert (count, object);
    }

    /**
     * Replaces the element at the given index of the underlying array store. The underlying store
     * is not shifted, and will not be resized.
     * <p>
     * Note that null values for the element ARE stored, as the underlying store is not a sparse
     * array.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @param object The element to store.
     * @return The BagArray, so that operations can be chained together.
     */
    public BagArray replace (int index, Object object) {
        // note that arrays can store null objects, unlike bags
        container[index] = BagHelper.objectify (object);
        return this;
    }

    /**
     * Removes the element at the given index of the underlying array store. The underlying store
     * is shifted to cover the removed item. The underlyig store will not be resized.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The BagArray, so that operations can be chained together.
     */
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

    private Object getObject (int index) {
        return ((index >= 0) && (index < count)) ? container[index] : null;
    }

    /**
     * Retrieve an indexed element and return it as a String.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a string, or null if the element is not found (or not a String).
     */
    public String getString (int index) {
        Object object = getObject (index);
        try {
            return (String) object;
        } catch (ClassCastException exception) {
            log.warn ("Cannot cast value type (" + object.getClass ().getName () + ") to String for index (" + index + ")");
        }
        return null;
    }

    /**
     * Retrieve an indexed element and return it as a Boolean.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a Boolean, or null if the element is not found.
     */
    public Boolean getBoolean (int index) {
        String string = getString (index);
        return (string != null) ? Boolean.parseBoolean (string) : null;
    }

    /**
     * Retrieve an indexed element and return it as a Long.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a Long, or null if the element is not found.
     */
    public Long getLong (int index) {
        String string = getString (index);
        return (string != null) ? Long.parseLong (string) : null;
    }

    /**
     * Retrieve an indexed element and return it as an Integer.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as an Integer, or null if the element is not found.
     */
    public Integer getInteger (int index) {
        Long value = getLong (index);
        return (value != null) ? value.intValue () : null;
    }

    /**
     * Retrieve an indexed element and return it as a Double.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a Double, or null if the element is not found.
     */
    public Double getDouble (int index) {
        String string = getString (index);
        return (string != null) ? Double.parseDouble (string) : null;
    }

    /**
     * Retrieve an indexed element and return it as a Float.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a Float, or null if the element is not found.
     */
    public Float getFloat (int index) {
        Double value = getDouble (index);
        return (value != null) ? value.floatValue () : null;
    }

    /**
     * Retrieve an indexed element and return it as a BagObject.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a BagObject, or null if the element is not found.
     */
    public BagObject getBagObject (int index) {
        Object object = getObject (index);
        try {
            return (BagObject) object;
        } catch (ClassCastException exception) {
            log.warn ("Cannot cast value type (" + object.getClass ().getName () + ") to BagObject for index (" + index + ")");
        }
        return null;
    }

    /**
     * Retrieve an indexed element and return it as a BagArray.
     *
     * @param index An integer value specifying the offset from the beginning of the array.
     * @return The element as a BagArray, or null if the element is not found.
     */
    public BagArray getBagArray (int index) {
        Object object = getObject (index);
        try {
            return (BagArray) object;
        } catch (ClassCastException exception) {
            log.warn ("Cannot cast value type (" + object.getClass ().getName () + ") to BagArray for index (" + index + ")");
        }
        return null;
    }

    /**
     * Returns the BagArray represented as JSON.
     *
     * @return A String containing the JSON representation of the underlying store.
     */
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

    /**
     * Returns a BagArray extracted from a JSON representation.
     *
     * @param  input A String containing a JSON encoding of a BagArray.
     * @return A new BagArray containing the elements encoded in the input.
     */
    public static BagArray fromString (String input) {
        // parse the string out... it is assumed to be a well formed BagArray serialization
        BagParser parser = new BagParser (input);
        return parser.ReadBagArray ();
    }
}
