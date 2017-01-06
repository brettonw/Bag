package com.brettonw.bag.expr;

import com.brettonw.bag.Bag;
import com.brettonw.bag.BagObject;

public class Key extends Expr {
    public static final String KEY = "key";

    private String key;

    public Key (BagObject expr) {
        key = expr.getString (KEY);
    }

    @Override
    public Object evaluate (Bag bag) {
        return bag.getObject (key);
    }

    public static BagObject bag (String key) {
        return Expr.bag (KEY).put (KEY, key);
    }
}
