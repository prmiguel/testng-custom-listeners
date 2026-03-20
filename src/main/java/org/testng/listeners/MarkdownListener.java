package org.testng.listeners;

import org.testng.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class MarkdownListener implements ITestListener, IExecutionListener {

    private StringBuilder reportBuilder = new StringBuilder();

    @Override
    public void onExecutionStart() {
        reportBuilder.append("# Test Execution Report\n\n");
        reportBuilder.append("| Test Name | Parameters | Status | Groups |\n");
        reportBuilder.append("| :--- | :--- | :--- | :--- |\n");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        addResultRow(result, "✅ PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        addResultRow(result, "❌ FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        addResultRow(result, "⚠️ SKIP");
    }

    private void addResultRow(ITestResult result, String status) {
        String name = result.getMethod().getMethodName();
        String groups = Arrays.toString(result.getMethod().getGroups());

        // Handle DataProvider values
        Object[] params = result.getParameters();
        String paramsString = (params.length > 0) ? Arrays.toString(params) : "N/A";

        reportBuilder.append(String.format("| %s | %s | %s | %s |\n",
                name, paramsString, status, groups));
    }

    @Override
    public void onExecutionFinish() {
        try (PrintWriter out = new PrintWriter(new FileWriter("target/TestReport.md"))) {
            out.println(reportBuilder.toString());
            System.out.println("Markdown report generated: TestReport.md");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
