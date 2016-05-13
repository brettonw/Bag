package com.brettonw.bag;

import java.time.*;

public class TypeExtensions {
    TypeExtensions () {}
    static {
        // add default constructors for some common types
        Serializer.forType (OffsetDateTime.class, () -> OffsetDateTime.now ());
        Serializer.forType (LocalDateTime.class, () -> LocalDateTime.now ());
        Serializer.forType (LocalTime.class, () -> LocalTime.now ());
        Serializer.forType (LocalDate.class, () -> LocalDate.now ());
        Serializer.forType (ZoneOffset.class, () -> ZoneOffset.ofHours (0));
    }
}
