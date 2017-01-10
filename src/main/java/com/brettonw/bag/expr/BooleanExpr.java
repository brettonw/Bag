package com.brettonw.bag.expr;

import com.brettonw.bag.Bag;

abstract public class BooleanExpr extends Expr {
    public boolean evaluateIsTrue (Bag bag) {
        return (Boolean) evaluate (bag);
    }
}
