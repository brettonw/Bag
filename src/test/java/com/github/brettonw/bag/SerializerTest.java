package com.github.brettonw.bag;

import com.github.brettonw.AppTest;
import com.github.brettonw.bag.test.TestClassA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;


public class SerializerTest {
    private static final Logger log = LogManager.getLogger (SerializerTest.class);

    @Test
    public void test() {
        TestClassA testClass = new TestClassA (5, true, 123.0, "pdq");
        BagObject bagObject = Serializer.toBagObject (testClass);
        log.info (bagObject.toString ());

        TestClassA reconClass = (TestClassA) Serializer.fromBagObject (bagObject);
        BagObject reconBagObject = Serializer.toBagObject (reconClass);
        AppTest.report (reconBagObject.toString (),bagObject.toString (), "Serializer test round trip");

        Integer testArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        bagObject = Serializer.toBagObject (testArray);
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

        ArrayList<Integer> arrayList = new ArrayList<> (3);
        arrayList.add(1);
        arrayList.add (3);
        arrayList.add (5);
        bagObject = Serializer.toBagObject (arrayList);
        log.info (bagObject.toString ());
        ArrayList<Integer> reconArrayList = (ArrayList<Integer>) Serializer.fromBagObject (bagObject);
        assertArrayEquals ("Check array list reconstitution", arrayList.toArray (), reconArrayList.toArray ());

        HashMap<String, Integer> hashMap = new HashMap<> (3);
        hashMap.put ("A", 1);
        hashMap.put ("B", 3);
        hashMap.put ("C", 5);
        bagObject = Serializer.toBagObject (hashMap);
        log.info (bagObject.toString ());
        HashMap<String, Integer> reconHashMap = (HashMap<String, Integer>) Serializer.fromBagObject (bagObject);
        assertArrayEquals ("Check hash map reconstitution - keys", hashMap.keySet ().toArray (), reconHashMap.keySet ().toArray ());
        assertArrayEquals ("Check hash map reconstitution - values", hashMap.values ().toArray (), reconHashMap.values ().toArray ());


        log.info ("got here");
    }
}
