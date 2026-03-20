package com.example.tests;

import org.testng.SkipException;
import org.testng.annotations.Test;

public class SkippedTest {

    @Test(enabled = false, groups = {"skipped", "wip"})
    public void testSkippedByDefault() {
        System.out.println("SkippedTest: testSkippedByDefault executed");
    }

    @Test(groups = {"basic"})
    public void testWillBeSkipped() {
        System.out.println("SkippedTest: testWillBeSkipped executed");
        throw new SkipException("Skipping this test intentionally");
    }
}
