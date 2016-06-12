package com.brettonw.bag.test;

public class DefaultClassLoadStrategy implements IClassLoadStrategy
{
    public ClassLoader getClassLoader (final ClassLoadContext ctx)
    {
        final ClassLoader callerLoader = ctx.getCallerClass ().getClassLoader ();
        final ClassLoader contextLoader = Thread.currentThread ().getContextClassLoader ();

        ClassLoader result;

        // If 'callerLoader' and 'contextLoader' are in a parent-child
        // relationship, always choose the child:

        if (isChild (contextLoader, callerLoader))
            result = callerLoader;
        else if (isChild (callerLoader, contextLoader))
            result = contextLoader;
        else
        {
            // This else branch could be merged into the previous one,
            // but I show it here to emphasize the ambiguous case:
            result = contextLoader;
        }

        final ClassLoader systemLoader = ClassLoader.getSystemClassLoader ();

        // Precaution for when deployed as a bootstrap or extension class:
        if (isChild (result, systemLoader))
            result = systemLoader;

        return result;
    }

    //... more methods ...
} // End of class
