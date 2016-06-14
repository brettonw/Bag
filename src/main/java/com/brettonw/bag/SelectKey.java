package com.brettonw.bag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectKey {
    public static final String KEYS = "keys";
    public static final String TYPE = "type";
    public static final SelectType DEFAULT_TYPE = SelectType.INCLUDE;

    private Set<String> keys;
    private SelectType type;

    public SelectKey (BagObject bagObject) {
        String[] keysArray = bagObject.getBagArray (KEYS).toArray (String.class);
        List<String> keysList = Arrays.asList(keysArray);
        keys = new HashSet<String> (keysList);
        type = Enum.valueOf (SelectType.class, bagObject.getString (TYPE, () -> DEFAULT_TYPE.name ()).toUpperCase ());
    }

    public boolean select (String key) {
        if (key != null) {
            switch (type) {
                case INCLUDE:
                    return keys.contains (key);
                case EXCLUDE:
                    return !keys.contains (key);
            }
        }
        return false;
    }
}
