package com.brettonw.bag.formats;

public abstract class EntryHandlerArray implements EntryHandler {
    protected EntryHandler entryHandler;

    protected EntryHandlerArray (EntryHandler entryHandler) {
        this.entryHandler = entryHandler;
    }
}
