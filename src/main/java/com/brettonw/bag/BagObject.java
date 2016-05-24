package com.brettonw.bag;

import com.brettonw.bag.json.FormatReaderJson;
import com.brettonw.bag.json.FormatWriterJson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * A collection of text-based values store in key/value pairs (maintained in a sorted array).
 */
public class BagObject extends Bag {
    private static final Logger log = LogManager.getLogger (BagObject.class);

    private static final int DEFAULT_CONTAINER_SIZE = 1;
    private static final int DOUBLING_CAP = 16;
    static final String PATH_SEPARATOR = "/";

    private class Pair {
        final String key;
        Object value;

        Pair (String key) {
            this.key = key;
        }
    }

    private Pair[] container;
    private int count;

    /**
     * Create a new BagObject with a default underlying storage size.
     */
    public BagObject () {
        init (DEFAULT_CONTAINER_SIZE);
    }

    /**
     * Create a new BagObject with hint for the underlying storage size.
     * @param size The expected number of elements in the BagObject, treated as a hint to optimize
     *             memory allocation. If additional elements are stored, the BagObject will revert
     *             to normal allocation behavior.
     */
    public BagObject (int size) {
        init (size);
    }

    /**
     * Create a new BagObject as deep copy of another BagObject
     */
    public BagObject (BagObject bagObject) {
        try {
            init (bagObject.getCount (), FormatReaderJson.JSON_FORMAT, new StringReader (bagObject.toString (FormatWriterJson.JSON_FORMAT)));
        } catch (IOException exception) {
            // NOTE this should never happen unless there is a bug we don't know about, and I can't
            // generate a test case to cover it, so it reports as a lack of coverage
            log.error (exception);
        }
    }

    /**
     * Create a new BagObject initialized from a formatted string
     * @throws ReadException if the parser fails and the object is left in an unusable state
     */
    public BagObject (String formattedString) throws IOException, ReadException {
        init (DEFAULT_CONTAINER_SIZE, FormatReader.deduceFormat (null, null, FormatReaderJson.JSON_FORMAT), new StringReader (formattedString));
    }

    /**
     * Create a new BagObject initialized from a formatted string
     * @param format String name of the format to parse
     * @throws ReadException if the parser fails and the object is left in an unusable state
     */
    public BagObject (String format, String formattedString) throws IOException, ReadException {
        init (DEFAULT_CONTAINER_SIZE, FormatReader.deduceFormat (format, null, FormatReaderJson.JSON_FORMAT), new StringReader (formattedString));
    }

    /**
     * Create a new BagObject initialized from a formatted string read out of an inputStream
     * @param format String name of the format to parse
     * @throws ReadException if the parser fails and the object is left in an unusable state
     */
    public BagObject (String format, InputStream formattedInputStream) throws IOException, ReadException {
        init (DEFAULT_CONTAINER_SIZE, FormatReader.deduceFormat (format, null, FormatReaderJson.JSON_FORMAT), new InputStreamReader (formattedInputStream));
    }

    /**
     * Create a new BagObject initialized from a formatted string read out of a file
     * @throws ReadException if the parser fails and the object is left in an unusable state
     */
    public BagObject (File formattedFile) throws IOException, ReadException {
        init (DEFAULT_CONTAINER_SIZE, FormatReader.deduceFormat (null, formattedFile.getName (), FormatReaderJson.JSON_FORMAT), new FileReader (formattedFile));
    }

    /**
     * Create a new BagObject initialized from a formatted string read out of a file
     * @param format String name of the format to parse
     * @throws ReadException if the parser fails and the object is left in an unusable state
     */
    public BagObject (String format, File formattedFile) throws IOException, ReadException {
        init (DEFAULT_CONTAINER_SIZE, FormatReader.deduceFormat (format, formattedFile.getName (), FormatReaderJson.JSON_FORMAT), new FileReader (formattedFile));
    }

    private void init (int containerSize) {
        count = 0;
        container = new Pair[Math.max (containerSize, 1)];
    }

    private void init (int containerSize, String format, Reader reader) throws IOException, ReadException {
        init (containerSize);
        if (FormatReader.read (this, format, reader) == null) {
            throw new ReadException ();
        }
    }

    /**
     * Return the number of elements stored in the BagObject.
     *
     * @return the count of elements in the underlying store. This is distinct write the capacity of
     * the underlying store.
     */
    public int getCount () {
        return count;
    }

    private void grow (int gapIndex) {
        Pair[] src = container;
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
        // starting conditions mapped to either end of the internal store
        int low = 0;
        int high = count - 1;

        // loop as long as the bounds have not crossed
        while (low <= high) {
            // compute the midpoint, and compare the search term against the key stored there, this
            // uses the unsigned right shift in lieu of division by 2
            int mid = (low + high) >>> 1;
            int cmp = container[mid].key.compareTo (key);

            // check the result of the comparison
            if (cmp < 0) {
                // the current midpoint is below the target value, set 'low' to one past it so the
                // next loop will look only at the part of the array above the midpoint
                low = mid + 1;
            } else if (cmp > 0) {
                // the current midpoint is above the target value, set 'high' to one below it so the
                // next loop will look only at the part of the array below the midpoint
                high = mid - 1;
            } else {
                // "Found it!" she says in a sing-song voice
                return mid;
            }
        }
        // key not found, return an encoded version of where the key SHOULD be
        return -(low + 1);
    }

    private Pair getOrAddPair (String key) {
        // conduct a binary search for where the pair should be
        int index = binarySearch (key);
        if (index < 0) {
            // the binary search returns a funky encoding of the index where the new value
            // should go when it's not there, so we have to decode that number (-index - 1)
            index = -(index + 1);

            // make sure there is room in the underlying container, then store a new (empty) Pair
            grow (index);
            container[index] = new Pair (key);
        }
        return container[index];
    }

    /**
     * Return an object stored at the requested key value. The key may be a simple name, or it may
     * be a path (with keys separated by "/") to create a hierarchical "bag-of-bags" that is indexed
     * recursively.
     * <p>
     * Using a binary search of the underlying store, finds where the first component of the path
     * should be and returns it.
     *
     * @param key A string value used to index the element, using "/" as separators, for example:
     *             "com/brettonw/bag/key".
     * @return The indexed element (if found), or null
     */
    @Override
    public Object getObject (String key) {
        // separate the key into path components, the "local" key value is the first component, so
        // use that to conduct the search. We are only interested in values that indicate the search
        // found the requested key
        String[] path = Key.split (key);
        int index = binarySearch (path[0]);
        if (index >= 0) {
            // grab the found element... if the path was only one element long, this is the element
            // we were looking for, otherwise recur on the found element as another BagObject
            Pair pair = container[index];
            Object found = pair.value;
            return (path.length == 1) ? found : ((Bag) found).getObject (path[1]);
        }
        return null;
    }

    /**
     * Store an object at the requested key value. The key may be a simple name, or it may be a path
     * (with keys separated by "/") to create a hierarchical "bag-of-bags" that is indexed
     * recursively.
     * <p>
     * Using a binary search of the underlying store, finds where the first component of the path
     * should be. If it does not already exist, it is created (recursively in the case of a path),
     * and the underlying store is shifted to make a space for it. The shift might cause the
     * underlying store to be resized if there is insufficient room.
     * <p>
     * Note that null values for the element are NOT stored at the leaf of the tree denoted by
     * a path, as returning null write getObject would be indistinguishable write a call to getObject
     * with an unknown key. This check is performed before the tree traversal, so the underlying
     * store will NOT contain the path after an attempt to add a null value.
     *
     * @param key A string value used to index the element, using "/" as separators, for example:
     *             "com/brettonw/bag/key".
     * @param object The element to store.
     * @return The BagObject, so that operations can be chained together.
     */
    public BagObject put (String key, Object object) {
        // convert the element to internal storage format, and don't bother with the rest if that's
        // a null value (per the docs above)
        object = objectify (object);
        if (object != null) {
            // separate the key into path components, the "local" key value is the first component,
            // so use that to conduct the search. If there is an element there, we want to get it,
            // otherwise we want to create it.
            String[] path = Key.split (key);
            Pair pair = getOrAddPair (path[0]);
            if (path.length == 1) {
                // this was the only key in the path, so it's the end of the line, store the value
                pair.value = object;
            } else {
                // this is not the leaf key, so we set the pair value to be a new BagObject if
                // necessary, then traverse via recursion,
                BagObject bagObject = (BagObject) pair.value;
                if (bagObject == null) {
                    pair.value = (bagObject = new BagObject ());
                }
                bagObject.put (path[1], object);
            }
        }
        return this;
    }

    /**
     * Add an object to a BagArray stored at the requested key. The key may be a simple name, or it may be a path
     * (with keys separated by "/") to create a hierarchical "bag-of-bags" that is indexed
     * recursively. If the key does not already exist a non-null value will be stored as a bare
     * value, just as if "put" had been called. If it does exist, and is not already an array or the
     * stored value is null, then a new array will be created to store any existing values and the
     * requested element.
     * <p>
     * Using a binary search of the underlying store, finds where the first component of the path
     * should be. If it does not already exist, it is created (recursively in the case of a path),
     * and the underlying store is shifted to make a space for it. The shift might cause the
     * underlying store to be resized if there is insufficient room.
     * <p>
     * Note that null values for the BagArray ARE stored per the design decision for arrays.
     *
     * @param key A string value used to index the element, using "/" as separators, for example:
     *             "com/brettonw/bag/key".
     * @param object The element to store.
     * @return The BagObject, so that operations can be chained together.
     */
    public BagObject add (String key, Object object) {
        // separate the key into path components, the "local" key value is the first component,
        // so use that to conduct the search. If there is an element there, we want to get it,
        // otherwise we want to create it.
        String[] path = Key.split (key);
        Pair pair = getOrAddPair (path[0]);
        if (path.length == 1) {
            // this is the end of the line, so we want to store the requested object
            BagArray bagArray;
            Object found = pair.value;
            if ((object = objectify (object)) == null) {
                if (found == null) {
                    // 1) object is null, key does not exist - create array
                    pair.value = (bagArray = new BagArray ());
                } else if (found instanceof BagArray) {
                    // 2) object is null, key exists (is array)
                    bagArray = (BagArray) found;
                } else {
                    // 3) object is null, key exists (is not array) - create array, store existing value
                    pair.value = (bagArray = new BagArray (2));
                    bagArray.add (found);
                }

                // and store the null value in the array
                bagArray.add (null);
            } else {
                if (found == null) {
                    // 4) object is not null, key does not exist - store as bare value
                    pair.value = object;
                } else {
                    if (found instanceof BagArray) {
                        // 5) object is not null, key exists (is array) - add new value to array
                        bagArray = (BagArray) found;
                    } else {
                        // 6) object is not null, key exists (is not array) - create array, store existing value, store new value
                        pair.value = (bagArray = new BagArray (2));
                        bagArray.add (found);
                    }
                    bagArray.add (object);
                }
            }
        } else {
            // this is not the leaf key, so we set the pair value to be a new BagObject if
            // necessary, then traverse via recursion,
            BagObject bagObject = (BagObject) pair.value;
            if (bagObject == null) {
                pair.value = (bagObject = new BagObject ());
            }
            bagObject.add (path[1], object);
        }
        return this;
    }

    /**
     * Remove an object stored at the requested key. The key may be a simple name, or it may be a
     * path (with keys separated by "/") to create a hierarchical "bag-of-bags" that is indexed
     * recursively.
     * <p>
     * Using a binary search of the underlying store, finds where the element mapped to the key
     * should be, and removes it. If the element doesn't exist, nothing happens. If
     * the element is removed, the underlying store is shifted to close the space where it was.
     * removing elements will never cause the underlying store to shrink.
     *
     * @param key A string value used to index the element, using "/" as separators, for example:
     *             "com/brettonw/bag/key".
     * @return The BagObject, so that operations can be chained together.
     */
    public BagObject remove (String key) {
        String[] path = Key.split (key);
        int index = binarySearch (path[0]);
        if (index >= 0) {
            if (path.length == 1) {
                int gapIndex = index + 1;
                System.arraycopy (container, gapIndex, container, index, count - gapIndex);
                --count;
            } else {
                BagObject found = (BagObject) container[index].value;
                found.remove (path[1]);
            }
        }
        return this;
    }

    /**
     * Return whether or not the requested key or path is present in the BagObject or hierarchical
     * "bag-of-bags"
     *
     * @param key A string value used to index the element, using "/" as separators, for example:
     *             "com/brettonw/bag/key".
     * @return A boolean value, true if the key is present in the underlying store. Note that null
     * values are not stored (design decision), so this equivalent to checking for null.
     */
    public boolean has (String key) {
        String[] path = Key.split (key);
        int index = binarySearch (path[0]);
        try {
            return (index >= 0) &&
                    ((path.length == 1) ||
                            ((BagObject) container[index].value).has (path[1]));
        } catch (ClassCastException classCastException) {
            // if a requested value is not a BagObject - this should be an exceptional case
            return false;
        }
    }

    /**
     * Returns an array of the keys contained in the underlying container. it does not enumerate the
     * container and all of its children.
     *
     * @return The keys in the underlying map as an array of Strings.
     */
    public String[] keys () {
        String[] keys = new String[count];
        for (int i = 0; i < count; ++i) {
            keys[i] = container[i].key;
        }
        return keys;
    }

    @Override
    public String toString (String format) {
        return FormatWriter.write (this, format);
    }
}
