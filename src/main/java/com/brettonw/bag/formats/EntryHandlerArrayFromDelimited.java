package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;

import java.util.Arrays;

public class EntryHandlerArrayFromDelimited extends EntryHandlerArray {
    private String delimiter;
    private String ignore;

    public EntryHandlerArrayFromDelimited (String delimiter, EntryHandler entryHandler) {
        super(entryHandler);
        this.delimiter = delimiter;
    }

    public EntryHandlerArrayFromDelimited ignore (String ignore) {
        this.ignore = ignore;
        return this;
    }

    @Override
    protected BagArray strategy (String input) {
        String[] entries = input.split (delimiter);
        final BagArray bagArray = new BagArray (entries.length);
        Arrays.stream (entries)
                .filter ((entry) -> ((ignore == null) || (! entry.startsWith (ignore))))
                .forEachOrdered (entry -> bagArray.add (entry));
        return bagArray;
    }
}
