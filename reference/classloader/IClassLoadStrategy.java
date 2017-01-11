package com.brettonw.bag.test;

public interface IClassLoadStrategy
{
    ClassLoader getClassLoader (ClassLoadContext ctx);
} // End of interface
