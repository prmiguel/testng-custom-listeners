package org.testng.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CustomTestListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        logTestInfo(result, "PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logTestInfo(result, "FAILED");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logTestInfo(result, "SKIPPED");
    }

    private void logTestInfo(ITestResult result, String status) {
        String methodName = result.getMethod().getMethodName();
        String params = formatParameters(result.getParameters());

        System.out.println("=================================================");
        System.out.println("Test Method : " + methodName);
        System.out.println("Parameters  : " + params);
        System.out.println("Status      : " + status);
        System.out.println("=================================================");
    }

    private String formatParameters(Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return "(none)";
        }
        return IntStream.range(0, parameters.length)
                .mapToObj(i -> "param[" + i + "] = " + formatValue(parameters[i]))
                .collect(Collectors.joining(", "));
    }

    private String formatValue(Object value) {
        if (value == null) return "null";
        if (value.getClass().isArray()) return Arrays.deepToString(new Object[]{value});
        return value.toString();
    }

    // -- Unused lifecycle methods (optional to override) --
    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}
}