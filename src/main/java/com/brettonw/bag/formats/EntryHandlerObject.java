package com.brettonw.bag.formats;

import com.brettonw.bag.BagObject;

import java.util.Arrays;

public abstract class EntryHandlerObject implements EntryHandler {
    private EntryHandler entryHandler;
    protected boolean accumulateEntries;

    protected EntryHandlerObject (EntryHandler entryHandler) {
        this.entryHandler = entryHandler;
        accumulateEntries = false;
    }

    public EntryHandlerObject accumulateEntries (boolean accumulateEntries) {
        this.accumulateEntries = accumulateEntries;
        return this;
    }

    protected abstract BagObject strategy (String input);

    @Override
    public Object getEntry (String input) {
        BagObject bagObject = strategy (input);
        if (bagObject != null) {
            Arrays.stream (bagObject.keys ()).forEach (key -> {
                String entry = bagObject.getString (key);
                if (entry != null) {
                    bagObject.put (key, entryHandler.getEntry (bagObject.getString (key)));
                } else {
                    bagObject.getBagArray (key).forEach (object -> entryHandler.getEntry ((String) object));
                    // XXX is it always the case that if there is no string then there is an array?
                    // XXX I'd love to simplify this code - as in, I think that if entry is null
                    // XXX I can just do the bagarray.forEach.... otherwise, I have to...
                    /*
                    if (accumulateEntries) {
                        BagArray bagArray = bagObject.getBagArray (key);
                        if (bagArray != null) {
                            bagArray.forEach (object -> entryHandler.getEntry ((String) object));
                        }
                    }
                    */
                }
            });
        }
        return bagObject;
    }
}
