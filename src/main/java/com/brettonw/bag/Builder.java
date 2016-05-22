package com.brettonw.bag;

class Builder {
    protected static final String SQUARE_BRACKETS[] = { "[", "]" };
    protected static final String CURLY_BRACKETS[] = { "{", "}" };
    protected static final String QUOTES[] = { "\"" };

    static String enclose (String input, String bracket[]) {
        String bracket0 = bracket[0];
        String bracket1 = (bracket.length > 1) ? bracket[1] : bracket0;
        return bracket0 + input + bracket1;
    }

    static String quote (String input) {
        return enclose (input, QUOTES);
    }
}
