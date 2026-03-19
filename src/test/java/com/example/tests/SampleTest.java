package com.example.tests;

import org.testng.annotations.Test;

public class SampleTest {

    @Test
    public void testSuccess() {
        System.out.println("SampleTest: testSuccess executed");
        assert true;
    }

    @Test
    public void testWithLog() {
        System.out.println("SampleTest: testWithLog executed");
    }

    @Test
    public void testThatWillFail() {
        System.out.println("SampleTest: testThatWillFail executed - this will fail");
        throw new RuntimeException("Intentional test failure");
    }

    @Test(dependsOnMethods = "testSuccess")
    public void testDependent() {
        System.out.println("SampleTest: testDependent executed (depends on testSuccess)");
    }
}
