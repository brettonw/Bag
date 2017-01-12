package com.brettonw.bag.formats;

import com.brettonw.bag.BagObject;

import java.util.Arrays;

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
            Arrays.stream (bagObject.keys ()).forEach (key ->bagObject.put (key, entryHandler.getEntry (bagObject.getString (key))));
        }
        return bagObject;
    }
}
