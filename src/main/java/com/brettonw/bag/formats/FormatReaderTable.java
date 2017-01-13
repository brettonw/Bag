package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;
import com.brettonw.bag.BagObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormatReaderTable extends FormatReader implements ArrayFormatReader {
    private static final Logger log = LogManager.getLogger (FormatReaderTable.class);

    private EntryHandler arrayHandler;
    private BagArray titlesArray;

    public FormatReaderTable () {}

    public FormatReaderTable (String input, EntryHandler arrayHandler) {
        this (input, arrayHandler, null);
    }

    /**
     * @param input
     * @param arrayHandler a handler to return an array of arrays
     * @param titlesArray
     */
    public FormatReaderTable (String input, EntryHandler arrayHandler, BagArray titlesArray) {
        super (input);
        this.arrayHandler = arrayHandler;
        this.titlesArray = titlesArray;
    }

    @Override
    public BagArray readBagArray () {
        BagArray bagArray = (BagArray) arrayHandler.getEntry (input);
        if ((bagArray != null) && (bagArray.getCount () > 0)) {
            // if we have a titles array, use it, otherwise use the first row of the array
            final BagArray titlesArray = (this.titlesArray != null) ? this.titlesArray : (BagArray) bagArray.dequeue ();
            final int count = titlesArray.getCount ();

            // walk over the array replacing each entry with a bag object using the titles array
            bagArray = bagArray.map (object -> {
                BagArray entryArray = (BagArray) object;
                if (count == entryArray.getCount ()) {
                    BagObject bagObject = new BagObject (count);
                    for (int i = 0; i < count; ++i) {
                        bagObject.put (titlesArray.getString (i), entryArray.getObject (i));
                    }
                    return bagObject;
                } else {
                    log.warn ("Mismatched size of entry and titles (skipping row)");
                    return null;
                }
            });
        }
        return bagArray;
    }
}
