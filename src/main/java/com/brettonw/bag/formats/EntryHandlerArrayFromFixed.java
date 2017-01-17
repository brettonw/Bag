package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;

import java.util.ArrayList;
import java.util.List;

public class EntryHandlerArrayFromFixed extends EntryHandlerArray {
    private int[] widths;
    private int totalWidth;

    public EntryHandlerArrayFromFixed (int[] widths) {
        this (widths, EntryHandlerValue.ENTRY_HANDLER_VALUE);
    }

    public EntryHandlerArrayFromFixed (int[] widths, EntryHandler entryHandler) {
        super (entryHandler);
        this.widths = widths;
        totalWidth = 0;
        for (int width : widths) {
            totalWidth += width;
        }
    }

    /**
     *
     * @param first The relative offset of the beginning of the line compared to the
     *              offsets in 'positions'. I find it easier to use my text editor to
     *              identify positions, and it counts the first column as '1', so I can
     *              set 'first' = 1, and then use the column number of the rest of the
     *              fields as reported by my text editor.
     * @param positions An array of numbers indicating relative offsets between fields.
     * @return
     */
    public static int[] widthsFromPositions (int first, int... positions) {
        int[] widths = new int[positions.length];
        for (int i = 0; i < positions.length; ++i) {
            widths[i] = positions[i] - first;
            first = positions[i];
        }
        return widths;
    }

    /**
     *
     * @param exemplar a string representing an example record to extract the positions from,
     *                 elements are assumed to be left-justified within the fields
     * @param separator a character that is expected to separate the entries (at least one will
     *                  appear before the next field)
     * @return
     */
    public static int[] widthsFromExemplar (String exemplar, char separator) {
        // walk the line to figure the positions
        List<Integer> positions = new ArrayList<> ();
        boolean between = false;
        int at = 0;
        for (int end = exemplar.length (); at < end; ++at) {
            char c = exemplar.charAt (at);
            if (between) {
                if (c != separator) {
                    positions.add (at);
                    between = false;
                }
            } else if (c == separator) {
                between = true;
            }
            ++at;
        }
        positions.add (at);

        // convert the positions into widths
        int size = positions.size ();
        int[] widths = new int[size];
        int last = 0;
        for (int i = 0; i < size; ++i) {
            int position = positions.get (i);
            widths[i] = position - last;
            last = position;
        }
        return widths;
    }

    @Override
    public Object getEntry (String input) {
        // create the array
        BagArray bagArray = new BagArray (widths.length);

        // ignore empty lines
        if (input.length () > 0) {
            // pad the input with spaces to match the expected width
            input = String.format ("%1$-" + totalWidth + "s", input);

            // split the input up into all the little substrings...
            for (int i = 0, start = 0; i < widths.length; ++i) {
                int end = start + widths[i];
                String entry = input.substring (start, end).trim ();
                start = end;
                bagArray.add (entryHandler.getEntry (entry));
            }
        }

        // return the populated array
        return bagArray;
    }
}
