package org.testng.listeners;

import org.testng.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MarkdownListenerWithChart implements ITestListener, IExecutionListener {

    private StringBuilder tableRows = new StringBuilder();
    private AtomicInteger passed = new AtomicInteger(0);
    private AtomicInteger failed = new AtomicInteger(0);
    private AtomicInteger skipped = new AtomicInteger(0);

    @Override
    public void onExecutionStart() {
        tableRows.append("| Test Name | Status | Groups | Parameters |\n");
        tableRows.append("| :--- | :--- | :--- | :--- |\n");
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

    private void addResultRow(ITestResult result, String status) {
        String name = result.getMethod().getMethodName();
        String groups = Arrays.toString(result.getMethod().getGroups());
        Object[] params = result.getParameters();
        String paramsString = (params.length > 0) ? Arrays.toString(params).replace("|", "\\|") : "N/A";

        tableRows.append(String.format("| %s | %s | %s | %s |\n",
                name, status, groups, paramsString));
    }

    @Override
    public void onExecutionFinish() {
        int p = passed.get();
        int f = failed.get();
        int s = skipped.get();

        StringBuilder finalReport = new StringBuilder();
        finalReport.append("# Test Execution Report\n\n");

        // Mermaid Pie Chart
        finalReport.append("```mermaid\n");
        finalReport.append("---\nconfig:\n  pie:\n    textPosition: 0.5\n");
        finalReport.append("  themeVariables:\n");
        finalReport.append("    pie1: \"#2da44e\"\n"); // GitHub Green
        finalReport.append("    pie2: \"#cf222e\"\n"); // GitHub Red
        finalReport.append("    pie3: \"#8c959f\"\n"); // GitHub Grey
        finalReport.append("---\npie showData\n");
        finalReport.append("    title Test Results Distribution\n");
        finalReport.append(String.format("    \"Passed\" : %d\n", p));
        finalReport.append(String.format("    \"Failed\" : %d\n", f));
        finalReport.append(String.format("    \"Skipped\" : %d\n", s));
        finalReport.append("```\n\n");

        // Summary Stats
        finalReport.append("### Statistics\n");
        finalReport.append(String.format("- **Total:** %d\n- **Passed:** %d\n- **Failed:** %d\n- **Skipped:** %d\n\n",
                (p + f + s), p, f, s));

        finalReport.append("--- \n\n");
        finalReport.append("### Detailed Results\n\n");
        finalReport.append(tableRows.toString());

        try (PrintWriter out = new PrintWriter(new FileWriter("target/TestReportWithChart.md"))) {
            out.println(finalReport.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
