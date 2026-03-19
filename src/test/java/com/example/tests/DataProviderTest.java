package com.example.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataProviderTest {

    @DataProvider(name = "stringData")
    public Object[][] stringDataProvider() {
        return new Object[][] {
            {"apple", 1},
            {"banana", 2},
            {"cherry", 3}
        };
    }

    @Test(dataProvider = "stringData")
    public void testWithStringData(String fruit, Integer count) {
        System.out.println("DataProviderTest: Testing " + fruit + " with count " + count);
        assert fruit != null;
        assert count > 0;
    }

    @DataProvider(name = "numberData")
    public Object[][] numberDataProvider() {
        return new Object[][] {
            {1, 2, 3},
            {10, 20, 30},
            {100, 200, 300}
        };
    }

    @Test(dataProvider = "numberData")
    public void testWithNumberData(int a, int b, int expectedSum) {
        System.out.println("DataProviderTest: Testing " + a + " + " + b + " = " + expectedSum);
        int actualSum = a + b;
        assert actualSum == expectedSum : "Expected " + expectedSum + " but got " + actualSum;
    }

    @DataProvider(name = "booleanData")
    public Object[][] booleanDataProvider() {
        return new Object[][] {
            {true, true},
            {true, false},
            {false, true},
            {false, false}
        };
    }

    @Test(dataProvider = "booleanData")
    public void testWithBooleanData(boolean a, boolean b) {
        System.out.println("DataProviderTest: Testing booleans " + a + " and " + b);
    }
}
