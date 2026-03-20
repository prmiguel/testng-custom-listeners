package com.example.tests;

import org.testng.annotations.Test;

public class SampleTest {

    @Test(groups = {"smoke", "basic"})
    public void testSuccess() {
        System.out.println("SampleTest: testSuccess executed");
        assert true;
    }

    @Test(groups = {"basic"})
    public void testWithLog() {
        System.out.println("SampleTest: testWithLog executed");
    }

    @Test(groups = {"basic", "failure"})
    public void testThatWillFail() {
        System.out.println("SampleTest: testThatWillFail executed - this will fail");
        throw new RuntimeException("Intentional test failure");
    }

    @Test(groups = {"basic"}, dependsOnMethods = "testSuccess")
    public void testDependent() {
        System.out.println("SampleTest: testDependent executed (depends on testSuccess)");
    }
}
