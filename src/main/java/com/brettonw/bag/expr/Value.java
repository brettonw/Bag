package com.brettonw.bag.expr;

import com.brettonw.bag.Bag;
import com.brettonw.bag.BagObject;

public class Value extends Expr {
    public static final String VALUE = "value";

    private String value;

    public Value (BagObject expr) {
        value = expr.getString (VALUE);
    }

    @Override
    public Object evaluate (Bag bag) {
        return value;
    }
}
