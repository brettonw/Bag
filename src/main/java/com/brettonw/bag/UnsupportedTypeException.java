package com.brettonw.bag;

public class UnsupportedTypeException extends RuntimeException {
    public UnsupportedTypeException (Class type) {
        super ("Unsupported type for storage in low-level bag container (" + type.getName () + "), did you mean to use the Serializer?");
    }
}