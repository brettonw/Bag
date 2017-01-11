package com.brettonw.bag.formats;

import com.brettonw.bag.BagObject;

public abstract class EntryHandlerObject implements EntryHandler {
    private EntryHandler entryHandler;

    public EntryHandlerObject (EntryHandler entryHandler) {
        this.entryHandler = entryHandler;
    }

    protected abstract BagObject strategy (String input);

    @Override
    public Object getEntry (String input) {
        BagObject bagObject = strategy (input);
        if (bagObject != null) {
            String[] keys = bagObject.keys ();
            for (String key : keys) {
                bagObject.put (key, entryHandler.getEntry (bagObject.getString (key)));
            }
        }
        return bagObject;
    }
}
