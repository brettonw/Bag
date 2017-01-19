package com.brettonw.bag.entry;

public abstract class HandlerArray implements Handler {
    protected Handler handler;

    protected HandlerArray (Handler handler) {
        this.handler = handler;
    }
}
