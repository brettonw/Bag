package com.brettonw.bag.entry;

import com.brettonw.bag.BagArray;

import java.util.Arrays;

public class HandlerArrayFromDelimited extends HandlerArray {
    private String delimiter;
    private String ignore;

    public HandlerArrayFromDelimited (String delimiter) {
        this (delimiter, HandlerValue.HANDLER_VALUE);
    }

    public HandlerArrayFromDelimited (String delimiter, Handler handler) {
        super(handler);
        this.delimiter = delimiter;
    }

    public HandlerArrayFromDelimited ignore (String ignore) {
        this.ignore = ignore;
        return this;
    }

    @Override
    public Object getEntry (String input) {
        String[] entries = input.split (delimiter);
        final BagArray bagArray = new BagArray (entries.length);
        Arrays.stream (entries)
                .filter ((entry) -> ((ignore == null) || (! entry.startsWith (ignore))))
                .forEachOrdered (entry -> bagArray.add (handler.getEntry (entry)));
        return bagArray;
    }
}
