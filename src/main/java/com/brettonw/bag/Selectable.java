package com.brettonw.bag;

import com.brettonw.bag.expr.BooleanExpr;

public interface Selectable<BagType extends Bag> {
    /**
     *
     * @param select
     * @return
     */
    BagType select (BagArray select);
}
