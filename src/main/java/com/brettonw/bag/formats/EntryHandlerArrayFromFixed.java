package com.brettonw.bag.formats;

import com.brettonw.bag.BagArray;

import java.util.ArrayDeque;
import java.util.Queue;

public class EntryHandlerArrayFromFixed extends EntryHandlerArray {
    private int[][] fields;
    private int fieldCount;
    private int totalWidth;

    public EntryHandlerArrayFromFixed (int[][] fields) {
        this (fields, EntryHandlerValue.ENTRY_HANDLER_VALUE);
    }

    /**
     *
     * @param fields an array of field descriptions, where each field description is a 2 element
     *               array (start and end - not inclusive)
     * @param entryHandler
     */
    public EntryHandlerArrayFromFixed (int[][] fields, EntryHandler entryHandler) {
        super (entryHandler);
        this.fields = fields;
        fieldCount = fields.length;
        totalWidth = 0;
        for (int[] field : fields) {
            totalWidth += field[1];
        }
    }

    /**
     * this method returns a field description array given a set of positions (assuming there are no
     * pads in the intended string)
     * @param offset The relative offset of the beginning of the line compared to the
     *              offsets in 'positions'. I find it easier to use my text editor to
     *              identify positions, and it counts the first column as '1', so I can
     *              set 'first' = 1, and then use the column number of the rest of the
     *              fields as reported by my text editor.
     * @param positions An array of numbers indicating relative offsets between fields.
     * @return
     */
    public static int[][] fieldsFromPositions (int offset, int... positions) {
        int[][] fields = new int[positions.length][2];
        int last = positions[0] - offset;
        for (int i = 0; i < positions.length; ++i) {
            fields[i][0] = last;
            last = positions[i] - offset;
            fields[i][1] = last;
        }
        return fields;
    }

    /**
     * this method returns a field description array given a set of widths (assuming there are no
     * pads in the intended string)
     * @param widths an array of numbers indicating the width of each field
     * @return
     */
    public static int[][] fieldsFromWidths (int... widths) {
        int[][] fields = new int[widths.length][2];
        int start = 0;
        for (int i = 0; i < widths.length; ++i) {
            fields[i][0] = start;
            start = (fields[i][1] = start + widths[i]);
        }
        return fields;
    }

    /**
     *
     * @param exemplar a string representing an example record to extract the positions from,
     *                 elements are assumed to be left-justified within the fields
     * @param separator a character that is expected to separate the entries (at least one will
     *                  appear before the next field)
     * @return
     */
    public static int[][] fieldsFromExemplar (String exemplar, char separator) {
        // walk the line to figure the positions
        Queue<Integer> queue = new ArrayDeque<> ();
        boolean between = true;
        for (int at = 0, end = exemplar.length (); at < end; ++at) {
            char c = exemplar.charAt (at);
            if (between) {
                if (c != separator) {
                    queue.add (at);
                    between = false;
                }
            } else if (c == separator) {
                queue.add (at);
                between = true;
            }
        }
        queue.add (exemplar.length ());

        // convert the positions list into a fields description, we take them two at a time
        if ((queue.size () & 0x01) == 0) {
            int fieldCount = queue.size () / 2;
            int[][] fields = new int[fieldCount][2];
            for (int i = 0; i < fieldCount; ++i) {
                fields[i][0] = queue.remove (); // position
                fields[i][1] = queue.remove (); // end
            }
            return fields;
        }
        return null;
    }

    @Override
    public Object getEntry (String input) {
        // create the array
        BagArray bagArray = new BagArray (fieldCount);

        // ignore empty lines
        if (input.length () > 0) {
            // pad the input with spaces to match the expected width
            input = String.format ("%1$-" + totalWidth + "s", input);

            // split the input up into all the little substrings...
            for (int[] field : fields) {
                String entry = input.substring (field[0], field[1]).trim ();
                bagArray.add (entryHandler.getEntry (entry));
            }
        }

        // return the populated array
        return bagArray;
    }
}
