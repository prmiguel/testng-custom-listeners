package org.testng.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SimpleMarkdownReporterListener implements ITestListener {
    @Override
    public void onTestSuccess(ITestResult result) {
        writeReport(String.format("✅ %s %s- Passed\n <br>", result.getName(), formatParameters(result.getParameters())));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        writeReport(String.format("❌ %s %s- Failed\n <br>", result.getName(), formatParameters(result.getParameters())));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        writeReport(String.format("⚪ %s %s- Skipped\n <br>", result.getName(), formatParameters(result.getParameters())));
    }

    @Override
    public void onStart(ITestContext context) {
        writeReport(String.format("\n## Test Run: %s [%s]\n\n", context.getName(), Instant.now().toString()));
    }

    @Override
    public void onFinish(ITestContext context) {
        writeReport("\n---");
    }

    private String formatParameters(Object[] parameters) {
        if (parameters == null || parameters.length == 0) return "";
        return String.format("[`%s`] ", Arrays.stream(parameters).map(Object::toString)
                .collect(Collectors.joining("`, `")));
    }

    private void writeReport(String content) {
        try (FileWriter fw = new FileWriter("target/simple-report.md", true)) {
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

