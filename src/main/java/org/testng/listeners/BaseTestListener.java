package org.testng.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class BaseTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[BaseTestListener] Test starting: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[BaseTestListener] Test succeeded: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[BaseTestListener] Test failed: " + result.getMethod().getMethodName());
        System.out.println("[BaseTestListener] Failure reason: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[BaseTestListener] Test skipped: " + result.getMethod().getMethodName());
    }
}
