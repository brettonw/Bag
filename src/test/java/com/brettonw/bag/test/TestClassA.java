package com.brettonw.bag.test;

public class TestClassA {
    public Integer x;
    public boolean y;
    public double z;
    public String abc;
    public TestClassB sub;
    public TestEnumXYZ xyz;

    private TestClassA () {
        x = 3;
    }

    public TestClassA (int x, boolean y, double z, String abc, TestEnumXYZ xyz) {
        this.x = x; this.y = y; this.z = z;
        this.abc = abc;
        sub = new TestClassB (x + 2, x + 1000, (float) z / 2.0f);
        this.xyz = xyz;
    }
}
