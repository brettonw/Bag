package com.brettonw.bag;

import com.brettonw.AppTest;
import org.junit.Test;

public class SortKeyTest {

    @Test
    public void testSortKeyDefaultConstructor () {
        SortKey a = new SortKey ();
        a.setKey ("Hello");

        AppTest.report (a.getKey (), "Hello", "Test getKey");

        a.setType (SortType.NUMERIC);
        AppTest.report (a.getType (), SortType.NUMERIC, "Test getType");
        a.setType (SortType.ALPHABETIC);
        AppTest.report (a.getType (), SortType.ALPHABETIC, "Test getType");

        a.setOrder (SortOrder.ASCENDING);
        AppTest.report (a.getOrder (), SortOrder.ASCENDING, "Test getOrder");
        a.setOrder (SortOrder.DESCENDING);
        AppTest.report (a.getOrder (), SortOrder.DESCENDING, "Test getOrder");
    }

    @Test
    public void testSortKeyConstructor () {
        SortKey a = new SortKey ("Hello", SortType.NUMERIC, SortOrder.ASCENDING);

        AppTest.report (a.getKey (), "Hello", "Test getKey");
        AppTest.report (a.getType (), SortType.NUMERIC, "Test getType");
        AppTest.report (a.getOrder (), SortOrder.ASCENDING, "Test getOrder");
    }

    @Test
    public void testSortKeyBagObject () {
        SortKey a = new SortKey (new BagObject ()
                .put (SortKey.KEY, "Hello")
        );

        AppTest.report (a.getKey (), "Hello", "Test getKey");
        AppTest.report (a.getType (), SortKey.DEFAULT_TYPE, "Test getType");
        AppTest.report (a.getOrder (), SortKey.DEFAULT_ORDER, "Test getOrder");

        a = new SortKey (new BagObject ()
                .put (SortKey.KEY, "Hello")
                .put (SortKey.TYPE, SortType.ALPHABETIC)
        );

        AppTest.report (a.getKey (), "Hello", "Test getKey");
        AppTest.report (a.getType (), SortType.ALPHABETIC, "Test getType");
        AppTest.report (a.getOrder (), SortKey.DEFAULT_ORDER, "Test getOrder");

        a = new SortKey (new BagObject ()
                .put (SortKey.KEY, "Hello")
                .put (SortKey.ORDER, SortOrder.DESCENDING)
        );

        AppTest.report (a.getKey (), "Hello", "Test getKey");
        AppTest.report (a.getType (), SortType.ALPHABETIC, "Test getType");
        AppTest.report (a.getOrder (), SortOrder.DESCENDING, "Test getOrder");
    }

    @Test
    public void testSortKeyCompare () {
        SortKey
        a = new SortKey (null, SortType.ALPHABETIC, SortOrder.ASCENDING);
        AppTest.report (a.compare ("10", "10"), 0, "Test compare");
        AppTest.report (a.compare ("10", "2") < 0, true, "Test compare");

        a = new SortKey (null, SortType.ALPHABETIC, SortOrder.DESCENDING);
        AppTest.report (a.compare ("10", "10"), 0, "Test compare");
        AppTest.report (a.compare ("10", "2") > 0, true, "Test compare");

        a = new SortKey (null, SortType.NUMERIC, SortOrder.ASCENDING);
        AppTest.report (a.compare ("10", "10"), 0, "Test compare");
        AppTest.report (a.compare ("10", "2") > 0, true, "Test compare");

        a = new SortKey (null, SortType.NUMERIC, SortOrder.DESCENDING);
        AppTest.report (a.compare ("10", "10"), 0, "Test compare");
        AppTest.report (a.compare ("10", "2") < 0, true, "Test compare");
    }
}
