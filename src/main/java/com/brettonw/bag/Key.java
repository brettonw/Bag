package com.brettonw.bag;

/**
 * A helper class for composing paths used in BagObject indexing.
 */
public final class Key {
    Key () {}

    /**
     * Concatenate multiple string components to make a path
     * @param components the different levels of the hierarchy to index
     * @return a String with the ponents in path form for indexing into a bag object
     */
    public static String cat (String... components) {
        String key = null;
        if (components.length > 0) {
            key = components[0];
            for (int i = 1; i < components.length; ++i) {
                key += BagObject.PATH_SEPARATOR + components[i];
            }
        }
        return key;
    }

    static String[] split (String key) {
        return key.split (BagObject.PATH_SEPARATOR, 2);
    }
}
