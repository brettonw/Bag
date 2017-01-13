package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;

public class EntryHandlerObjectFromPairsArray extends EntryHandlerObject {
    private EntryHandler arrayHandler;
    private EntryHandler pairHandler;


    public EntryHandlerObjectFromPairsArray (EntryHandler arrayHandler, EntryHandler pairHandler) {
        this (arrayHandler, pairHandler, EntryHandlerValue.ENTRY_HANDLER_VALUE);
    }

    public EntryHandlerObjectFromPairsArray (EntryHandler arrayHandler, EntryHandler pairHandler, EntryHandler entryHandler) {
        super (entryHandler);
        this.arrayHandler = arrayHandler;
        this.pairHandler = pairHandler;
    }

    @Override
    protected BagObject strategy (String input) {
        // read the bag array of the input, and check for success
        BagArray bagArray = (BagArray) arrayHandler.getEntry (input);
        if (bagArray != null) {
            // loop over the array, processing the pairs
            bagArray = bagArray.map (string -> pairHandler.getEntry ((String) string));

            // create a bag object from the array of pairs
            BagObject bagObject = new BagObject (bagArray.getCount ());
            bagArray.forEach (object -> {
                BagArray pair = (BagArray) object;
                if (pair != null) {
                    if (accumulateEntries) {
                        bagObject.add (pair.getString (0), pair.getString (1));
                    } else {
                        bagObject.put (pair.getString (0), pair.getString (1));
                    }
                }
            });

            // return the result
            return bagObject;
        }
        return null;
    }
}
