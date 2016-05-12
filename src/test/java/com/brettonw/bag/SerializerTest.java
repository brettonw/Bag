package com.brettonw.bag;

import com.brettonw.AppTest;
import com.brettonw.bag.test.TestClassA;
import com.brettonw.bag.test.TestClassC;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;


public class SerializerTest {
    private static final Logger log = LogManager.getLogger (SerializerTest.class);

    @Test
    public void test() {
        new Serializer ();
    }

    @Test
    public void testBareType() {
        // serialize a bare type
        int x = 24;
        BagObject serializedX = Serializer.toBagObject (x);
        int deserializedX = (int) Serializer.fromBagObject (serializedX);
        AppTest.report (deserializedX, x, "Serializer - test bare type");
    }

    @Test
    public void testPojo() {
        // serialize a POJO
        TestClassA testClass = new TestClassA (5, true, 123.0, "pdq");
        BagObject bagObject = Serializer.toBagObject (testClass);
        log.info (bagObject.toString ());

        TestClassA reconClass = (TestClassA) Serializer.fromBagObject (bagObject);
        BagObject reconBagObject = Serializer.toBagObject (reconClass);
        AppTest.report (reconBagObject.toString (),bagObject.toString (), "Serializer test round trip");
    }

    @Test
    public void testArray() {
        Integer testArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        BagObject bagObject = Serializer.toBagObject (testArray);
        log.info (bagObject.toString ());
        Integer reconArray[] = (Integer[]) Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray, reconArray);

        int testArray2[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        bagObject = Serializer.toBagObject (testArray2);
        log.info (bagObject.toString ());
        int reconArray2[] = (int[]) Serializer.fromBagObject (bagObject);
        assertArrayEquals("Check array reconstitution", testArray2, reconArray2);

        int testArray3[][] = { {0,0}, {1,1}, {2,2} };
        bagObject = Serializer.toBagObject (testArray3);
        log.info (bagObject.toString ());
        int reconArray3[][] = (int[][]) Serializer.fromBagObject (bagObject);
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
        ArrayList<Integer> reconArrayList = (ArrayList<Integer>) Serializer.fromBagObject (bagObject);
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
        HashMap<String, Integer> reconHashMap = (HashMap<String, Integer>) Serializer.fromBagObject (bagObject);
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
            BagObject serializedStringBagObject = BagObject.fromJsonString (serializedString);
            String deserializedString = (String) Serializer.fromBagObject (serializedStringBagObject);
            AppTest.report (deserializedString, null, "Serializer test reconstituting a string with a bad version");
        } catch (IOException exception) {
            AppTest.report (false, true, "An excption is a failure case");
        }
    }

    @Test
    public void testError() {
        try {
            String serializedString = "{\"type\":\"java.lang.Sring\",\"v\":\"1.0\",\"value\":\"pdq\"}";
            BagObject serializedStringBagObject = BagObject.fromJsonString (serializedString);
            String deserializedString = (String) Serializer.fromBagObject (serializedStringBagObject);
            AppTest.report (deserializedString, null, "Serializer test reconstituting a modified source");
        } catch (IOException exception) {
            AppTest.report (false, true, "An excption is a failure case");
        }
    }

    @Test
    public void testNonPojo() {
        TestClassC  testClassC = new TestClassC (1, 2L, 3.0f, 10, 20L, 30.0f);
        BagObject bagObjectC = Serializer.toBagObject (testClassC);
        log.info (bagObjectC.toString ());
        TestClassC  reconClassC = (TestClassC) Serializer.fromBagObject (bagObjectC);
        AppTest.report (reconClassC.test (1, 2L, 3.0f, 10, 20L, 30.0f), true, "Serializer - Confirm reconstituted object matches original");
    }

    @Test
    public void testArrayTypes() {
        long testArrayLong[] = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L};
        BagObject bagObject = Serializer.toBagObject (testArrayLong);
        assertArrayEquals (testArrayLong, (long[]) Serializer.fromBagObject (bagObject));

        short testArrayShort[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayShort);
        assertArrayEquals (testArrayShort, (short[]) Serializer.fromBagObject (bagObject));

        byte testArrayByte[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayByte);
        assertArrayEquals (testArrayByte, (byte[]) Serializer.fromBagObject (bagObject));

        double testArrayDouble[] = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        bagObject = Serializer.toBagObject (testArrayDouble);
        assertArrayEquals (testArrayDouble, (double[]) Serializer.fromBagObject (bagObject), 1.0e-9);

        float testArrayFloat[] = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f};
        bagObject = Serializer.toBagObject (testArrayFloat);
        assertArrayEquals (testArrayFloat, (float[]) Serializer.fromBagObject (bagObject), 1.0e-6f);

        boolean testArrayBoolean[] = {true, false, true, false, true, false, true, false};
        bagObject = Serializer.toBagObject (testArrayBoolean);
        assertArrayEquals (testArrayBoolean, (boolean[]) Serializer.fromBagObject (bagObject));

        char testArrayCharacter[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        bagObject = Serializer.toBagObject (testArrayCharacter);
        assertArrayEquals (testArrayCharacter, (char[]) Serializer.fromBagObject (bagObject));
    }

    @Test
    public void testBoxedArrayTypes() {
        Long testArrayLong[] = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L};
        BagObject bagObject = Serializer.toBagObject (testArrayLong);
        assertArrayEquals (testArrayLong, (Long[]) Serializer.fromBagObject (bagObject));

        Short testArrayShort[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayShort);
        assertArrayEquals (testArrayShort, (Short[]) Serializer.fromBagObject (bagObject));

        Byte testArrayByte[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        bagObject = Serializer.toBagObject (testArrayByte);
        assertArrayEquals (testArrayByte, (Byte[]) Serializer.fromBagObject (bagObject));

        Double testArrayDouble[] = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        bagObject = Serializer.toBagObject (testArrayDouble);
        assertArrayEquals (testArrayDouble, (Double[]) Serializer.fromBagObject (bagObject));

        Float testArrayFloat[] = {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f};
        bagObject = Serializer.toBagObject (testArrayFloat);
        assertArrayEquals (testArrayFloat, (Float[]) Serializer.fromBagObject (bagObject));

        Boolean testArrayBoolean[] = {true, false, true, false, true, false, true, false};
        bagObject = Serializer.toBagObject (testArrayBoolean);
        assertArrayEquals (testArrayBoolean, (Boolean[]) Serializer.fromBagObject (bagObject));

        Character testArrayCharacter[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        bagObject = Serializer.toBagObject (testArrayCharacter);
        assertArrayEquals (testArrayCharacter, (Character[]) Serializer.fromBagObject (bagObject));
    }

    @Test
    public void testPojoArray() {
        TestClassA  testArrayA[] = {
                new TestClassA (1, true, 3.5, "Joe"),
                new TestClassA (2, true, 3.6, "Dave"),
                new TestClassA (3, false, 19.2, "Bret"),
                new TestClassA (4, true, 4.5, "Roxy")
        };
        BagObject bagObject = Serializer.toBagObject (testArrayA);
        TestClassA reconTestArrayA[] = (TestClassA[]) Serializer.fromBagObject (bagObject);
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
            BagObject bogusArray = BagObject.fromJsonString (bogusArrayString);
            Object result = Serializer.fromBagObject (bogusArray);
            AppTest.report (result, null, "Serializer - test bogus array string");
        } catch (IOException exception) {
            AppTest.report (false, true, "An excption is a failure case");
        }
    }
}
