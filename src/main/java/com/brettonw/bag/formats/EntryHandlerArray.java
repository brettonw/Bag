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
        final BagArray bagArray = strategy (input);
        return (bagArray != null) ? bagArray.map (object -> entryHandler.getEntry ((String)object)) : bagArray;
    }
}
