package org.testng.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HtmlTestReporterListener implements ITestListener {
    private final List<TestResult> testResults = new ArrayList<>();
    private String suiteName = "";
    private String startTime = "";
    private final String reportPath = "target/test-report.html";

    @Override
    public void onStart(ITestContext context) {
        suiteName = suiteName.isEmpty() ? context.getName() : String.format("%s, %s", suiteName, context.getName());
        startTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(
                Instant.ofEpochMilli(System.currentTimeMillis()).atZone(TimeZone.getDefault().toZoneId()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testResults.add(new TestResult(result.getName(), getParameters(result), "passed", getExecutionTime(result), getThrowableMessage(result)));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        testResults.add(new TestResult(result.getName(), getParameters(result), "failed", getExecutionTime(result), getThrowableMessage(result)));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testResults.add(new TestResult(result.getName(), getParameters(result), "skipped", getExecutionTime(result), getThrowableMessage(result)));
    }

    @Override
    public void onFinish(ITestContext context) {
        generateReport();
    }

    private String getParameters(ITestResult result) {
        Object[] params = result.getParameters();
        if (params == null || params.length == 0) return "";
        return Arrays.stream(params).map(Object::toString).collect(Collectors.joining(", "));
    }

    private long getExecutionTime(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }

    private String getThrowableMessage(ITestResult result) {
        Throwable t = result.getThrowable();
        return t != null ? t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName() : "";
    }

    private void generateReport() {
        long passed = testResults.stream().filter(r -> "passed".equals(r.status)).count();
        long failed = testResults.stream().filter(r -> "failed".equals(r.status)).count();
        long skipped = testResults.stream().filter(r -> "skipped".equals(r.status)).count();
        long total = testResults.size();
        long totalTime = testResults.stream().mapToLong(r -> r.executionTime).sum();
        int passPercent = total > 0 ? (int)(passed * 100 / total) : 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(reportPath))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang=\"en\">");
            writer.println("<head>");
            writer.println("    <meta charset=\"UTF-8\">");
            writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            writer.println("    <title>Test Report - " + escapeHtml(suiteName) + "</title>");
            writer.println("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js\"></script>");
            writer.println("    <style>");
            writer.println("        * { box-sizing: border-box; margin: 0; padding: 0; }");
            writer.println("        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif; background: #f5f7fa; color: #333; padding: 20px; }");
            writer.println("        .container { max-width: 1400px; margin: 0 auto; }");
            writer.println("        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3); }");
            writer.println("        .header h1 { font-size: 28px; margin-bottom: 8px; }");
            writer.println("        .header .meta { opacity: 0.9; font-size: 14px; }");
            writer.println("        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 24px; }");
            writer.println("        .stat-card { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); text-align: center; transition: transform 0.2s; }");
            writer.println("        .stat-card:hover { transform: translateY(-2px); }");
            writer.println("        .stat-card .value { font-size: 36px; font-weight: 700; margin-bottom: 4px; }");
            writer.println("        .stat-card .label { color: #666; font-size: 14px; text-transform: uppercase; letter-spacing: 0.5px; }");
            writer.println("        .stat-card.total .value { color: #667eea; }");
            writer.println("        .stat-card.passed .value { color: #10b981; }");
            writer.println("        .stat-card.failed .value { color: #ef4444; }");
            writer.println("        .stat-card.skipped .value { color: #f59e0b; }");
            writer.println("        .stat-card.time .value { color: #6366f1; font-size: 28px; }");
            writer.println("        .charts-row { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 24px; }");
            writer.println("        @media (max-width: 900px) { .charts-row { grid-template-columns: 1fr; } }");
            writer.println("        .chart-container { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }");
            writer.println("        .chart-container h3 { margin-bottom: 16px; color: #444; font-size: 16px; }");
            writer.println("        .chart-wrapper { position: relative; height: 250px; }");
            writer.println("        .test-table-container { background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); overflow: hidden; }");
            writer.println("        .test-table-header { padding: 20px 24px; border-bottom: 1px solid #eee; }");
            writer.println("        .test-table-header h3 { color: #444; }");
            writer.println("        .test-table { width: 100%; border-collapse: collapse; }");
            writer.println("        .test-table th { background: #f8fafc; padding: 12px 16px; text-align: left; font-weight: 600; color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid #e2e8f0; }");
            writer.println("        .test-table td { padding: 14px 16px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }");
            writer.println("        .test-table tr:hover { background: #f8fafc; }");
            writer.println("        .test-table tr:last-child td { border-bottom: none; }");
            writer.println("        .status-icon { display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; border-radius: 50%; font-size: 14px; font-weight: bold; }");
            writer.println("        .status-passed { background: #d1fae5; color: #10b981; }");
            writer.println("        .status-failed { background: #fee2e2; color: #ef4444; }");
            writer.println("        .status-skipped { background: #fef3c7; color: #f59e0b; }");
            writer.println("        .duration { color: #64748b; font-family: 'Monaco', 'Menlo', monospace; font-size: 13px; }");
            writer.println("        .error-msg { color: #ef4444; max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }");
            writer.println("        .filter-bar { padding: 16px 24px; background: #f8fafc; border-bottom: 1px solid #e2e8f0; display: flex; gap: 12px; flex-wrap: wrap; }");
            writer.println("        .filter-btn { padding: 8px 16px; border: 1px solid #e2e8f0; background: white; border-radius: 6px; cursor: pointer; font-size: 13px; transition: all 0.2s; }");
            writer.println("        .filter-btn:hover { border-color: #667eea; color: #667eea; }");
            writer.println("        .filter-btn.active { background: #667eea; color: white; border-color: #667eea; }");
            writer.println("        .footer { text-align: center; padding: 24px; color: #94a3b8; font-size: 13px; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("    <div class=\"container\">");
            writer.println("        <div class=\"header\">");
            writer.println("            <h1>" + escapeHtml(suiteName) + "</h1>");
            writer.println("            <div class=\"meta\">Started: " + startTime + "</div>");
            writer.println("        </div>");
            writer.println("        <div class=\"stats-grid\">");
            writer.println("            <div class=\"stat-card total\">");
            writer.println("                <div class=\"value\">" + total + "</div>");
            writer.println("                <div class=\"label\">Total Tests</div>");
            writer.println("            </div>");
            writer.println("            <div class=\"stat-card passed\">");
            writer.println("                <div class=\"value\">" + passed + "</div>");
            writer.println("                <div class=\"label\">Passed</div>");
            writer.println("            </div>");
            writer.println("            <div class=\"stat-card failed\">");
            writer.println("                <div class=\"value\">" + failed + "</div>");
            writer.println("                <div class=\"label\">Failed</div>");
            writer.println("            </div>");
            writer.println("            <div class=\"stat-card skipped\">");
            writer.println("                <div class=\"value\">" + skipped + "</div>");
            writer.println("                <div class=\"label\">Skipped</div>");
            writer.println("            </div>");
            writer.println("            <div class=\"stat-card time\">");
            writer.println("                <div class=\"value\">" + formatDuration(totalTime) + "</div>");
            writer.println("                <div class=\"label\">Total Time</div>");
            writer.println("            </div>");
            writer.println("        </div>");
            writer.println("        <div class=\"charts-row\">");
            writer.println("            <div class=\"chart-container\">");
            writer.println("                <h3>Test Results Distribution</h3>");
            writer.println("                <div class=\"chart-wrapper\">");
            writer.println("                    <canvas id=\"pieChart\"></canvas>");
            writer.println("                </div>");
            writer.println("            </div>");
            writer.println("            <div class=\"chart-container\">");
            writer.println("                <h3>Pass Rate</h3>");
            writer.println("                <div class=\"chart-wrapper\">");
            writer.println("                    <canvas id=\"doughnutChart\"></canvas>");
            writer.println("                </div>");
            writer.println("            </div>");
            writer.println("        </div>");
            writer.println("        <div class=\"test-table-container\">");
            writer.println("            <div class=\"test-table-header\">");
            writer.println("                <h3>Test Details</h3>");
            writer.println("            </div>");
            writer.println("            <div class=\"filter-bar\">");
            writer.println("                <button class=\"filter-btn active\" data-filter=\"all\">All (" + total + ")</button>");
            writer.println("                <button class=\"filter-btn\" data-filter=\"passed\">Passed (" + passed + ")</button>");
            writer.println("                <button class=\"filter-btn\" data-filter=\"failed\">Failed (" + failed + ")</button>");
            writer.println("                <button class=\"filter-btn\" data-filter=\"skipped\">Skipped (" + skipped + ")</button>");
            writer.println("            </div>");
            writer.println("            <table class=\"test-table\">");
            writer.println("                <thead>");
            writer.println("                    <tr>");
            writer.println("                        <th style=\"width:50px\">Status</th>");
            writer.println("                        <th>Test Name</th>");
            writer.println("                        <th>Parameters</th>");
            writer.println("                        <th>Duration</th>");
            writer.println("                        <th>Result</th>");
            writer.println("                        <th>Error Message</th>");
            writer.println("                    </tr>");
            writer.println("                </thead>");
            writer.println("                <tbody id=\"testTableBody\">");
            writer.print(generateTestRows());
            writer.println("                </tbody>");
            writer.println("            </table>");
            writer.println("        </div>");
            writer.println("        <div class=\"footer\">");
            writer.println("            Generated by HtmlTestReporterListener | TestNG Custom Listeners");
            writer.println("        </div>");
            writer.println("    </div>");
            writer.println("    <script>");
            writer.println("        const passed = " + passed + ";");
            writer.println("        const failed = " + failed + ";");
            writer.println("        const skipped = " + skipped + ";");
            writer.println("        const passRate = " + passPercent + ";");
            writer.println("        new Chart(document.getElementById('pieChart'), {");
            writer.println("            type: 'pie',");
            writer.println("            data: { labels: ['Passed', 'Failed', 'Skipped'], datasets: [{ data: [passed, failed, skipped], backgroundColor: ['#10b981', '#ef4444', '#f59e0b'], borderWidth: 0 }] },");
            writer.println("            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }");
            writer.println("        });");
            writer.println("        new Chart(document.getElementById('doughnutChart'), {");
            writer.println("            type: 'doughnut',");
            writer.println("            data: { labels: ['Passed', 'Remaining'], datasets: [{ data: [passRate, 100 - passRate], backgroundColor: ['#10b981', '#e2e8f0'], borderWidth: 0 }] },");
            writer.println("            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' }, tooltip: { callbacks: { label: function(ctx) { return ctx.label + ': ' + ctx.raw + '%'; } } } } }");
            writer.println("        });");
            writer.println("        document.querySelectorAll('.filter-btn').forEach(btn => {");
            writer.println("            btn.addEventListener('click', function() {");
            writer.println("                document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));");
            writer.println("                this.classList.add('active');");
            writer.println("                const filter = this.dataset.filter;");
            writer.println("                document.querySelectorAll('#testTableBody tr').forEach(row => {");
            writer.println("                    const status = row.querySelector('td:last-child').previousElementSibling.textContent.toLowerCase();");
            writer.println("                    row.style.display = (filter === 'all' || status === filter) ? '' : 'none';");
            writer.println("                });");
            writer.println("            });");
            writer.println("        });");
            writer.println("    </script>");
            writer.println("</body>");
            writer.println("</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateTestRows() {
        StringBuilder sb = new StringBuilder();
        for (TestResult result : testResults) {
            String statusClass = result.status.equals("passed") ? "status-passed" :
                                  result.status.equals("failed") ? "status-failed" : "status-skipped";
            String statusIcon = result.status.equals("passed") ? "&check;" :
                               result.status.equals("failed") ? "&times;" : "&bull;";
            sb.append("                    <tr>\n");
            sb.append("                        <td><span class=\"status-icon ").append(statusClass).append("\">").append(statusIcon).append("</span></td>\n");
            sb.append("                        <td>").append(escapeHtml(result.name)).append("</td>\n");
            sb.append("                        <td>").append(escapeHtml(result.parameters)).append("</td>\n");
            sb.append("                        <td class=\"duration\">").append(formatDuration(result.executionTime)).append("</td>\n");
            sb.append("                        <td class=\"").append(statusClass).append("\">").append(result.status.toUpperCase()).append("</td>\n");
            sb.append("                        <td class=\"error-msg\">").append(escapeHtml(result.errorMessage)).append("</td>\n");
            sb.append("                    </tr>\n");
        }
        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private String formatDuration(long ms) {
        if (ms < 1000) return ms + " ms";
        return String.format("%.2f s", ms / 1000.0);
    }

    private static class TestResult {
        String name;
        String parameters;
        String status;
        long executionTime;
        String errorMessage;

        TestResult(String name, String parameters, String status, long executionTime, String errorMessage) {
            this.name = name;
            this.parameters = parameters;
            this.status = status;
            this.executionTime = executionTime;
            this.errorMessage = errorMessage;
        }
    }
}
