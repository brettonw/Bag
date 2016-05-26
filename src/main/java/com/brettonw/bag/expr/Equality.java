package com.brettonw.bag.expr;

import com.brettonw.bag.Bag;
import com.brettonw.bag.BagObject;

public class Equality extends BooleanExpr {
    public static final String EQUALITY = "=";

    private Expr left;
    private Expr right;
    public Equality (BagObject expr) {
        left = Exprs.get (expr.getObject (Exprs.LEFT));
        right = Exprs.get (expr.getObject (Exprs.RIGHT));
    }

    @Override
    public Object evaluate (Bag bag) {
        Object leftResult = left.evaluate (bag);
        Object rightResult = right.evaluate (bag);
        return (leftResult != null) ? leftResult.equals (rightResult) : (rightResult == null);
    }
}
