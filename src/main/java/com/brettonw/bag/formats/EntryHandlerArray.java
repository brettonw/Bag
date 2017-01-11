package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;

public abstract class EntryHandlerArray implements EntryHandler {
    private EntryHandler entryHandler;

    public EntryHandlerArray (EntryHandler entryHandler) {
        this.entryHandler = entryHandler;
    }

    protected abstract BagArray strategy(String input);

    @Override
    public Object getEntry (String input) {
        BagArray bagArray = strategy (input);
        if (bagArray != null) {
            for (int i = 0, end = bagArray.getCount (); i < end; ++i) {
                bagArray.replace (i, entryHandler.getEntry (bagArray.getString (i)));
            }
        }
        return bagArray;
    }
}
