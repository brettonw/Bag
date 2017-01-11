package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;

public class EntryHandlerArrayFromDelimited extends EntryHandlerArray {
    private String delimiter;

    public EntryHandlerArrayFromDelimited (String delimiter, EntryHandler entryHandler) {
        super(entryHandler);
        this.delimiter = delimiter;
    }

    @Override
    protected BagArray strategy (String input) {
        String[] entries = input.split (delimiter);
        BagArray bagArray = new BagArray (entries.length);
        for (String entry : entries) {
            bagArray.add (entry);
        }
        return bagArray;
    }
}
