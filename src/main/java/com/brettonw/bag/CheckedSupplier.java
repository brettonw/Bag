package com.brettonw.bag;

@FunctionalInterface
public interface CheckedSupplier<ReturnType, ExceptionType extends Throwable> {
    ReturnType get () throws ExceptionType;
}
