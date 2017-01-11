package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;

public class EntryHandlerObjectFromPairsArray extends EntryHandlerObject {
    private EntryHandler arrayHandler;
    private EntryHandler pairHandler;
    private boolean accumulateEntries;


    public EntryHandlerObjectFromPairsArray (EntryHandler arrayHandler, EntryHandler pairHandler, EntryHandler entryHandler) {
        super (entryHandler);
        this.arrayHandler = arrayHandler;
        this.pairHandler = pairHandler;
        accumulateEntries = false;
    }

    public EntryHandlerObjectFromPairsArray accumulateEntries (boolean accumulateEntries) {
        this.accumulateEntries = accumulateEntries;
        return this;
    }

    @Override
    protected BagObject strategy (String input) {
        // read the bag array of the input, and check for success
        BagArray bagArray = (BagArray) arrayHandler.getEntry (input);
        if (bagArray != null) {
            // loop over the array, processing the pairs
            int count = bagArray.getCount ();
            for (int i = 0; i < count; ++i) {
                bagArray.replace (i, pairHandler.getEntry (bagArray.getString (i)));
            }

            // create a bag object from the array of pairs
            BagObject bagObject = new BagObject (count);
            for (int i = 0; i < count; ++i) {
                BagArray pair = bagArray.getBagArray (i);
                if (pair != null) {
                    if (accumulateEntries) {
                        bagObject.add (pair.getString (0), pair.getString (1));
                    } else {
                        bagObject.put (pair.getString (0), pair.getString (1));
                    }
                }
            }

            // return the result
            return bagObject;
        }
        return null;
    }
}
