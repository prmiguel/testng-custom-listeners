package org.testng.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class TestMarkdownReporterListener implements ITestListener {

    private final Map<String, List<ITestResult>> testResults = new HashMap();

    @Override
    public void onTestSuccess(ITestResult result) {
        addTest(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        addTest(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        addTest(result);
    }

    @Override
    public void onStart(ITestContext context) {
//        writeReport(String.format("\n## Test Run: %s [%s]\n\n", context.getName(), Instant.now().toString()));
    }

    @Override
    public void onFinish(ITestContext context) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<ITestResult>> entry : testResults.entrySet()) {
            sb.append("### ").append(entry.getKey()).append("\n\n");
            List<ITestResult> results = entry.getValue();
            if (!results.isEmpty()) {
                int paramCount = results.get(0).getParameters().length;
                StringBuilder header = new StringBuilder("| Status |");
                for (int i = 0; i < paramCount; i++) {
                    header.append(" Param").append(i + 1).append(" |");
                }
                sb.append(header).append("\n");
                StringBuilder separator = new StringBuilder("| --- |");
                for (int i = 0; i < paramCount; i++) {
                    separator.append(" --- |");
                }
                sb.append(separator).append("\n");
                for (ITestResult result : results) {
                    String status = getStatus(result.getStatus());
                    sb.append("| ").append(status).append(" |");
                    for (Object param : result.getParameters()) {
                        sb.append(" ").append(param).append(" |");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
        writeReport(sb.toString());
    }

    private String getStatus(int status) {
        switch (status) {
            case ITestResult.SUCCESS: return "passed";
            case ITestResult.FAILURE: return "failed";
            case ITestResult.SKIP: return "skipped";
            default: return "unknown";
        }
    }

    private void addTest(ITestResult result) {
        if (!testResults.containsKey(result.getName())) testResults.put(result.getName(), new ArrayList<>());
        testResults.get(result.getName()).add(result);
    }

    private String formatParameters(Object[] parameters) {
        if (parameters == null || parameters.length == 0) return "";
        return String.format("[`%s`] ", Arrays.stream(parameters).map(Object::toString)
                .collect(Collectors.joining("`, `")));
    }

    private void writeReport(String content) {
        try (FileWriter fw = new FileWriter("target/test-report.md", true)) {
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

