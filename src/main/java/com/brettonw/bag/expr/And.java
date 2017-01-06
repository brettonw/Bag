package com.brettonw.bag.expr;

import com.brettonw.bag.Bag;
import com.brettonw.bag.BagObject;

public class And extends BooleanExpr {
    public static final String AND = "and";

    private BooleanExpr left;
    private BooleanExpr right;

    public And (BagObject expr) {
        left = (BooleanExpr) Exprs.get (expr.getObject (LEFT));
        right = (BooleanExpr) Exprs.get (expr.getObject (RIGHT));
    }

    @Override
    Object evaluate (Bag bag) {
        return left.evaluateIsTrue (bag) && right.evaluateIsTrue (bag);
    }

    public static BagObject bag (BagObject left, BagObject right) {
        return bag (AND, left, right);
    }
}
