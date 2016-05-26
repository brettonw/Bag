package com.brettonw.bag.expr;

import com.brettonw.bag.BagObject;

import java.util.HashMap;
import java.util.Map;

public class Exprs {
    public static final String OPERATOR = "operator";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";

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
                bagObject = new BagObject ().put (OPERATOR, Value.VALUE).put (Value.VALUE, expr);
            }
            ExprSupplier exprSupplier = exprSuppliers.get (bagObject.getString (OPERATOR));
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
        BagObject bagObject = new BagObject ()
                .put (OPERATOR, Equality.EQUALITY)
                .put (LEFT, new BagObject ().put (OPERATOR, Key.KEY).put (Key.KEY, key))
                .put (RIGHT, new BagObject ().put (OPERATOR, Value.VALUE).put (Value.VALUE, value));
        return (BooleanExpr) get (bagObject);
    }
}
