package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

public class SelectKeyTest {

    @Test
    public void testSelectKey () {
        SelectKey
        selectKey = new SelectKey ("Abc", "Def");
        AppTest.report (selectKey.getType (), SelectKey.DEFAULT_TYPE, "Test getType and default constructor");
        AppTest.report (selectKey.select ("Hello"), null, "Test select of value not in set");
        AppTest.report (selectKey.select ("Hello", () -> "junk"), "junk", "Test select value not in set with supplier");
        AppTest.report (selectKey.select ("Abc", () -> "junk"), "Abc", "Test select of value in the set with an unused supplier");
        AppTest.report (selectKey.select ("Abc"), "Abc", "Test select of value in the set with default supplier (null)");

        selectKey = new SelectKey (SelectType.INCLUDE, "Abc", "Def");
        AppTest.report (selectKey.getType (), SelectKey.DEFAULT_TYPE, "Test getType and default constructor");
        AppTest.report (selectKey.select ("Hello"), null, "Test select of value not in set");
        AppTest.report (selectKey.select ("Hello", () -> "junk"), "junk", "Test select value not in set with supplier");
        AppTest.report (selectKey.select ("Abc", () -> "junk"), "Abc", "Test select of value in the set with an unused supplier");
        AppTest.report (selectKey.select ("Abc"), "Abc", "Test select of value in the set with default supplier (null)");
    }

    @Test
    public void testDefaultConstructor () {
        SelectKey selectKey = new SelectKey ();
        AppTest.report (selectKey.getType (), SelectKey.DEFAULT_TYPE, "Test getType and default constructor");
        AppTest.report (selectKey.select ("Hello"), null, "Test select on empty set");
        AppTest.report (selectKey.setType (SelectType.EXCLUDE).getType (), SelectType.EXCLUDE, "Test getType and default constructor");
        AppTest.report (selectKey.select (null, () -> "junk"), "junk", "Test of notFound on null select");
    }

    @Test
    public void testBagConstructors () {
        BagArray bagArray = new BagArray ().add ("Abc").add ("Def");
        BagObject bagObject = new BagObject ().put (SelectKey.KEYS_KEY, bagArray);
        SelectKey
        selectKey = new SelectKey (bagObject);
        AppTest.report (selectKey.getType (), SelectType.INCLUDE, "Test getType and default constructor");
        AppTest.report (selectKey.select ("Hello"), null, "Test select of value not in select key");
        AppTest.report (selectKey.select ("Abc"), "Abc", "Test select of value in select key");
        AppTest.report (selectKey.setType (SelectType.EXCLUDE).getType (), SelectType.EXCLUDE, "Test getType and default constructor");

        selectKey = new SelectKey (bagArray);
        AppTest.report (selectKey.getType (), SelectKey.DEFAULT_TYPE, "Test getType and default constructor");
        AppTest.report (selectKey.select ("Hello"), null, "Test select of value not in select key");
        AppTest.report (selectKey.select ("Abc"), "Abc", "Test select of value in select key");
        AppTest.report (selectKey.setType (SelectType.EXCLUDE).getType (), SelectType.EXCLUDE, "Test getType and default constructor");
    }

    @Test
    public void testSelectExclude () {
        BagArray bagArray = new BagArray ().add ("Abc").add ("Def");
        BagObject bagObject = new BagObject ().put (SelectKey.TYPE_KEY, SelectType.EXCLUDE).put (SelectKey.KEYS_KEY, bagArray);
        SelectKey selectKey = new SelectKey (bagObject);
        AppTest.report (selectKey.getType (), SelectType.EXCLUDE, "Test getType");
        AppTest.report (selectKey.select ("Hello"), "Hello", "Test select (exclude) of value not in select key");
        AppTest.report (selectKey.select ("Abc"), null, "Test select (exclude) of value in select key");
    }

    @Test
    public void testSelectExcludeConstructor () {
        SelectKey selectKey = new SelectKey (SelectType.EXCLUDE, "Hello");
        AppTest.report (selectKey.getType (), SelectType.EXCLUDE, "Test getType");
        AppTest.report (selectKey.select ("Hello"), null, "Test select (exclude) of value in select key");
        AppTest.report (selectKey.select ("Bye Bye"), "Bye Bye", "Test select (exclude) of value not in select key");
    }
}
