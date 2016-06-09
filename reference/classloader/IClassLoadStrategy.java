package com.brettonw.bag.classloader;

public interface IClassLoadStrategy
{
    ClassLoader getClassLoader (ClassLoadContext ctx);
} // End of interface
