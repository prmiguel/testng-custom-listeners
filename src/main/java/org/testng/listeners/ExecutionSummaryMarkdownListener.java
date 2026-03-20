package org.testng.listeners;

import org.testng.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionSummaryMarkdownListener implements ITestListener, IExecutionListener {

    private StringBuilder reportBuilder = new StringBuilder();
    private AtomicInteger passed = new AtomicInteger(0);
    private AtomicInteger failed = new AtomicInteger(0);
    private AtomicInteger skipped = new AtomicInteger(0);

    @Override
    public void onExecutionStart() {
        reportBuilder.append("# Test Execution Report\n\n");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passed.incrementAndGet();
        addResultRow(result, "✅ PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed.incrementAndGet();
        addResultRow(result, "❌ FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skipped.incrementAndGet();
        addResultRow(result, "⚠️ SKIP");
    }

    private StringBuilder tableRows = new StringBuilder("| Test Name | Status | Groups | Parameters |\n| :--- | :--- | :--- | :--- |\n");

    private void addResultRow(ITestResult result, String status) {
        String name = result.getMethod().getMethodName();
        String groups = Arrays.toString(result.getMethod().getGroups());
        Object[] params = result.getParameters();
        String paramsString = (params.length > 0) ? Arrays.toString(params) : "N/A";

        tableRows.append(String.format("| %s | %s | %s | %s |\n",
                name, status, groups, paramsString));
    }

    @Override
    public void onExecutionFinish() {
        int total = passed.get() + failed.get() + skipped.get();
        double passRate = (total > 0) ? (passed.get() * 100.0 / total) : 0;

        // Build Summary Section
        reportBuilder.append("## Execution Summary\n\n");
        reportBuilder.append(String.format("- **Total Tests:** `%d`\n", total));
        reportBuilder.append(String.format("- **Passed:** `%d`\n", passed.get()));
        reportBuilder.append(String.format("- **Failed:** `%d`\n", failed.get()));
        reportBuilder.append(String.format("- **Skipped:** `%d`\n", skipped.get()));
        reportBuilder.append(String.format("- **Pass Rate:** `%.2f%%`\n\n", passRate));

        reportBuilder.append("--- \n\n");
        reportBuilder.append("## Detailed Results\n\n");
        reportBuilder.append(tableRows.toString());

        try (PrintWriter out = new PrintWriter(new FileWriter("target/TestSummaryReport.md"))) {
            out.println(reportBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
