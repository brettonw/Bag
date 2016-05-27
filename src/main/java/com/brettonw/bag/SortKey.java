package com.brettonw.bag;

class SortKey {
    public static final String KEY = "key";
    public static final String TYPE = "type";
    public static final String DEFAULT_TYPE = "ALPHABETIC";
    public static final String ORDER = "order";
    public static final String DEFAULT_ORDER = "ASCENDING";

    public String key;
    public SortType type;
    public SortOrder order;

    public SortKey (BagObject bagObject) {
        key = bagObject.getString (KEY);
        type = Enum.valueOf (SortType.class, bagObject.getString (TYPE, () -> DEFAULT_TYPE).toUpperCase ());
        order = Enum.valueOf (SortOrder.class, bagObject.getString (ORDER, () -> DEFAULT_ORDER).toUpperCase ());
    }

    public static BagArray keys (String... keys) {
        BagArray bagArray = new BagArray (keys.length);
        for (String key : keys) {
            bagArray.add (new BagObject ().put (KEY, key));
        }
        return bagArray;
    }
}
