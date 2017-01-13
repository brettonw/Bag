package com.brettonw.bag.formats;

public class EntryHandlerValue implements EntryHandler {
    public static final EntryHandlerValue ENTRY_HANDLER_VALUE = new EntryHandlerValue ();

    private EntryHandlerValue () {}

    @Override
    public Object getEntry (String input) {
        return input.trim ();
    }
}
