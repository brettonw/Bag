package com.brettonw.bag;

public interface Selectable<BagType extends Bag> {
    /**
     *
     * @param select
     * @return
     */
    BagType select (BagArray select);
    BagType drop (BagArray drop);
}
