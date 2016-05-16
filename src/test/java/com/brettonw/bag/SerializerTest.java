package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.test.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;


public class SerializerTest {
    private static final Logger log = LogManager.getLogger (SerializerTest.class);

    @Test
    public void testBareType() {
        // serialize a bare type
        int x = 24;
        BagObject serializedX = Serializer.toBagObject (x);
        int deserializedX = Serializer.fromBagObject (serializedX);
        AppTest.report (deserializedX, x, "Serializer - test bare type");
    }

    @Test
    public void testNewSerializer() {
        new Serializer ();
    }

    @Test
    public void testPojo() {
        // serialize a POJO
        TestClassA testClass = new TestClassA (5, true, 123.0, "pdq", TestEnumXYZ.ABC);
        BagObject bagObject = Serializer.toBagObject (testClass);
        log.info (bagObject.toString ());

        TestClassA reconClass = Serializer.fromBagObject (bagObject);
        BagObject reconBagObject = Serializer.toBagObject (reconClass);
        AppTest.report (reconBagObject.toString (),bagObject.toString (), "Serializer test round trip");
    }

    @Test
    public void testArray() {
        Integer testArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        BagObject bagObject = Serializer.toBagObject (testArray);
        log.info (bagObject.toString ());
        Integer reconArray[] = Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray, reconArray);

        int testArray2[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        bagObject = Serializer.toBagObject (testArray2);
        log.info (bagObject.toString ());
        int reconArray2[] = Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray2, reconArray2);

        int testArray3[][] = { {0,0}, {1,1}, {2,2} };
        bagObject = Serializer.toBagObject (testArray3);
        log.info (bagObject.toString ());
        int reconArray3[][] = Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray3, reconArray3);
    }

    @Test
    public void testArrayList() {
        ArrayList<Integer> arrayList = new ArrayList<> (3);
        arrayList.add(1);
        arrayList.add (3);
        arrayList.add (5);
        BagObject bagObject = Serializer.toBagObject (arrayList);
        log.info (bagObject.toString ());
        ArrayList<Integer> reconArrayList = Serializer.fromBagObject (bagObject);
        assertArrayEquals ("Check array list reconstitution", arrayList.toArray (), reconArrayList.toArray ());
    }

    @Test
    public void testMap() {
        HashMap<String, Integer> hashMap = new HashMap<> (3);
        hashMap.put ("A", 1);
        hashMap.put ("B", 3);
        hashMap.put ("C", 5);
        BagObject bagObject = Serializer.toBagObject (hashMap);
        log.info (bagObject.toString ());
        HashMap<String, Integer> reconHashMap = Serializer.fromBagObject (bagObject);
        assertArrayEquals ("Check hash map reconstitution - keys", hashMap.keySet ().toArray (), reconHashMap.keySet ().toArray ());
        assertArrayEquals ("Check hash map reconstitution - values", hashMap.values ().toArray (), reconHashMap.values ().toArray ());

        // add a few other simple serializations...
        BagObject anotherBagObject = Serializer.toBagObject (bagObject);
        AppTest.report (Serializer.fromBagObject (anotherBagObject), bagObject, "Serializer test reconstituting a bag object");
    }

    @Test
    public void testBagArray() {
        BagArray    bagArray = new BagArray (2).add (1).add (7.0);
        BagObject anotherBagObject = Serializer.toBagObject (bagArray);
        AppTest.report (Serializer.fromBagObject (anotherBagObject), bagArray, "Serializer test reconstituting a bag array");
        log.info ("got here");
    }

    @Test
    public void testVersionHandler() {
        try {
            String serializedString = "{\"type\":\"java.lang.String\",\"v\":\"0.9\",\"value\":\"pdq\"}";
            BagObject serializedStringBagObject = new BagObject (serializedString);
            String deserializedString = Serializer.fromBagObject (serializedStringBagObject);
            AppTest.report (deserializedString, null, "Serializer test reconstituting a string with a bad version");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testError() {
        try {
            String serializedString = "{\"type\":\"java.lang.Sring\",\"v\":\"1.0\",\"value\":\"pdq\"}";
            BagObject serializedStringBagObject = new BagObject (serializedString);
            String deserializedString = Serializer.fromBagObject (serializedStringBagObject);
            AppTest.report (deserializedString, null, "Serializer test reconstituting a modified source");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testNonPojo() {
        TestClassC  testClassC = new TestClassC (1, 2L, 3.0f, 10, 20L, 30.0f);
        BagObject bagObjectC = Serializer.toBagObject (testClassC);
        log.info (bagObjectC.toString ());
        TestClassC  reconClassC = Serializer.fromBagObject (bagObjectC);
        AppTest.report (reconClassC.test (1, 2L, 3.0f, 10, 20L, 30.0f), true, "Serializer - Confirm reconstituted object matches original");
    }

    @Test
    public void testArrayTypes() {
        long testArrayLong[] = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L};
        BagObject bagObject = Serializer.toBagObject (testArrayLong);
        assertArrayEquals (testArrayLong, Serializer.fromBagObject (bagObject));

        short testArrayShort[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayShort);
        assertArrayEquals (testArrayShort, Serializer.fromBagObject (bagObject));

        byte testArrayByte[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayByte);
        assertArrayEquals (testArrayByte, Serializer.fromBagObject (bagObject));

        double testArrayDouble[] = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        bagObject = Serializer.toBagObject (testArrayDouble);
        assertArrayEquals (testArrayDouble, Serializer.fromBagObject (bagObject), 1.0e-9);

        float testArrayFloat[] = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f};
        bagObject = Serializer.toBagObject (testArrayFloat);
        assertArrayEquals (testArrayFloat, Serializer.fromBagObject (bagObject), 1.0e-6f);

        boolean testArrayBoolean[] = {true, false, true, false, true, false, true, false};
        bagObject = Serializer.toBagObject (testArrayBoolean);
        assertArrayEquals (testArrayBoolean, Serializer.fromBagObject (bagObject));

        char testArrayCharacter[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        bagObject = Serializer.toBagObject (testArrayCharacter);
        assertArrayEquals (testArrayCharacter, Serializer.fromBagObject (bagObject));
    }

    @Test
    public void testBoxedArrayTypes() {
        Long testArrayLong[] = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L};
        BagObject bagObject = Serializer.toBagObject (testArrayLong);
        assertArrayEquals (testArrayLong, Serializer.fromBagObject (bagObject));

        Short testArrayShort[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayShort);
        assertArrayEquals (testArrayShort, Serializer.fromBagObject (bagObject));

        Byte testArrayByte[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayByte);
        assertArrayEquals (testArrayByte, Serializer.fromBagObject (bagObject));

        Double testArrayDouble[] = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        bagObject = Serializer.toBagObject (testArrayDouble);
        assertArrayEquals (testArrayDouble, Serializer.fromBagObject (bagObject));

        Float testArrayFloat[] = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f};
        bagObject = Serializer.toBagObject (testArrayFloat);
        assertArrayEquals (testArrayFloat, Serializer.fromBagObject (bagObject));

        Boolean testArrayBoolean[] = {true, false, true, false, true, false, true, false};
        bagObject = Serializer.toBagObject (testArrayBoolean);
        assertArrayEquals (testArrayBoolean, Serializer.fromBagObject (bagObject));

        Character testArrayCharacter[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        bagObject = Serializer.toBagObject (testArrayCharacter);
        assertArrayEquals (testArrayCharacter, Serializer.fromBagObject (bagObject));
    }

    @Test
    public void testPojoArray() {
        TestClassA  testArrayA[] = {
                new TestClassA (1, true, 3.5, "Joe", TestEnumXYZ.ABC),
                new TestClassA (2, true, 3.6, "Dave", TestEnumXYZ.DEF),
                new TestClassA (3, false, 19.2, "Bret", TestEnumXYZ.GHI),
                new TestClassA (4, true, 4.5, "Roxy", TestEnumXYZ.GHI)
        };
        BagObject bagObject = Serializer.toBagObject (testArrayA);
        TestClassA reconTestArrayA[] = Serializer.fromBagObject (bagObject);
        boolean pass = true;
        for (int i = 0, end = testArrayA.length; i < end; ++i) {
            TestClassA left = testArrayA[i];
            TestClassA right = reconTestArrayA[i];

            // not a *COMPLETE* test, but spot checking
            pass = pass && (left.abc.equals (right.abc)) && (left.sub.b == right.sub.b);
        }
        AppTest.report (pass, true, "Serializer - test array of complex POJOs");
    }

    @Test
    public void testBogusArrayString() {
        try {
            String bogusArrayString = "{\"type\":\"[java.lang.Integer;\",\"v\":\"1.0\",\"value\":[{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"0\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"1\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"2\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"3\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"4\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"5\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"6\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"7\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"8\"},{\"type\":\"java.lang.Integer\",\"v\":\"1.0\",\"value\":\"9\"}]}";
            BagObject bogusArray = new BagObject (bogusArrayString);
            Object result = Serializer.fromBagObject (bogusArray);
            AppTest.report (result, null, "Serializer - test bogus array string");
        } catch (IOException exception) {
            AppTest.report (false, true, "An exception is a failure case");
        }
    }

    @Test
    public void testOffsetDateTime () {
        // deal with a type that has a no default constructor?
        OffsetDateTime  odt = OffsetDateTime.now ();
        BagObject bagObject = Serializer.toBagObject (odt);
        OffsetDateTime  reconOdt = Serializer.fromBagObject (bagObject);
        AppTest.report (odt, reconOdt, "Reconstructed OffsetDateTime should match the original");
    }

    @Test
    public void testBogusType () {
        // deal with a type that has a no default constructor?
        OffsetDateTime  odt = OffsetDateTime.now ();
        BagObject bagObject = Serializer.toBagObject (odt);
        try {
            LocalTime localTime = Serializer.fromBagObject (bagObject);
            AppTest.report  (odt, localTime, "This should fail");
        } catch (ClassCastException exception) {
            AppTest.report (false, false, "Properly throw an exception if we can't cast the value");
        }
    }

    @Test
    public void testBadConstructorHandling () {
        // deal with a type that has a no default constructor and no registered extension
        TestClassD  d = new TestClassD ("Hello");
        BagObject bagObject = Serializer.toBagObject (d);
        TestClassD xxx = Serializer.fromBagObject (bagObject);
        AppTest.report (d != xxx, true, "Properly fail on a type without a default constructor or registered extension");
    }

    @Test
    public void testClassE () {
        TestClassE  d = new TestClassE ("Hello");
        BagObject bagObject = Serializer.toBagObject (d);
        TestClassE xxx = Serializer.fromBagObject (bagObject);
        AppTest.report (d, xxx, "Properly handle a serialized typen");
    }
}
