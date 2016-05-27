package com.brettonw.bag.expr;

import com.brettonw.bag.BagObject;

import java.util.HashMap;
import java.util.Map;

public class Exprs {
    @FunctionalInterface
    interface ExprSupplier {
        Expr get (BagObject params);
    }

    private static final Map<String, ExprSupplier> exprSuppliers = new HashMap<> ();

    public static void register (String name, ExprSupplier exprSupplier) {
        exprSuppliers.put (name, exprSupplier);
    }

    public static Expr get (Object expr) {
        if (expr != null) {
            BagObject bagObject = null;
            if (expr instanceof BagObject) {
                bagObject = (BagObject) expr;
            } else if (expr instanceof String){
                bagObject = new BagObject ().put (Expr.OPERATOR, Value.VALUE).put (Value.VALUE, expr);
            }
            ExprSupplier exprSupplier = exprSuppliers.get (bagObject.getString (Expr.OPERATOR));
            return (exprSupplier != null) ? exprSupplier.get (bagObject) : null;
        }
        return null;
    }

    static {
        register ("=", Equality::new);
        register ("key", Key::new);
        register ("value", Value::new);
    }

    public static BooleanExpr equality (String key, Object value) {
        return (BooleanExpr) get (Equality.bag (Key.bag (key), Value.bag (value)));
    }

    public static BooleanExpr inequality (String key, Object value) {
        return (BooleanExpr) get (Not.bag (Equality.bag (Key.bag (key), Value.bag (value))));
    }
}
