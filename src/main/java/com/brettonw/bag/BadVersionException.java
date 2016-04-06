package com.brettonw.bag;

public class BadVersionException extends RuntimeException {
    public BadVersionException (String got, String expected) {
        super ("Incorrect version (got: " + got + ", expected: " + expected + ")");
    }
}
