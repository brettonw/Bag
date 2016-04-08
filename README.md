# Bag (com.brettonw.bag)

Bag is loosely based on a combination of XML, JSON (from www.json.org), and various other
serializers. Sometimes you want JSON, sometimes you want a serializer, and sometimes you want to
move seamlessly between the two.

The package provides two container classes for text-based storage of constrained types in an array
(BagArray) or as a map (BagObject), with a means of serializing objects to and from these container
types. Stored values are constrained to primitive types or their boxed analog, strings, and other
bags. More complex types can be stored using the Serializer.

* Type assignment is performed lazily on extraction, and presumes the user knows what they are
expecting to get.

* Strings are stored directly, and the parser is not smart about strings with quotes in them. We
could change this behavior, but the bottom line is we don't want the Bag classes to do any kind of
transformations on the stored data.

* These classes are primarily intended for messaging, events, and other applications that require
complex values to be shared in a text-based data interchange format without the formality of
declaring classes or establishing schemas.

 * Bag can consume a superset of valid JSON files, and Bag text output files are valid JSON. The
text format is generated using the "toString" and "fromString" operators, but the parsing is
slightly simplified compared to a JSON file.

* For now, the error handling philosophy is to return null and log failures. The user can choose to
throw an exception if they want, but Bag should be robust and continue chugging without killing the
parent application if an unanticipated fault happens.

* In the future, we will probably move to eliminate the log4j dependency.
