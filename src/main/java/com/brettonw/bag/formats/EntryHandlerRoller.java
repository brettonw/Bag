package com.brettonw.bag.formats;

public class EntryHandlerRoller implements EntryHandler {
    private EntryHandler[] entryHandlers;
    private int roll;

    public EntryHandlerRoller (EntryHandler... entryHandlers) {
        this.entryHandlers = entryHandlers;
        roll = 0;
    }

    @Override
    public Object getEntry (String input) {
        Object result = entryHandlers[roll].getEntry (input);
        roll = (roll + 1) % entryHandlers.length;
        return result;
    }
}
