// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.cloudstack.test.flaky;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Report containing flaky test detection results.
 */
public class FlakyTestReport {
    private final List<FlakyTestInfo> flakyTests;
    private final int totalTests;
    private final int passedTests;

    /**
     * Creates a new FlakyTestReport.
     *
     * @param flakyTests list of detected flaky tests
     * @param totalTests total number of tests run
     * @param passedTests number of tests that passed
     */
    public FlakyTestReport(List<FlakyTestInfo> flakyTests, int totalTests, int passedTests) {
        this.flakyTests = new ArrayList<>(flakyTests);
        this.totalTests = totalTests;
        this.passedTests = passedTests;
    }

    public List<FlakyTestInfo> getFlakyTests() {
        return new ArrayList<>(flakyTests);
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public int getFlakyCount() {
        return flakyTests.size();
    }

    public double getFlakyPercentage() {
        if (totalTests == 0) {
            return 0.0;
        }
        return (flakyTests.size() * 100.0) / totalTests;
    }

    /**
     * Generates a JSON representation of the report.
     *
     * @return JSON string
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"totalTests\": ").append(totalTests).append(",\n");
        json.append("  \"passedTests\": ").append(passedTests).append(",\n");
        json.append("  \"flakyCount\": ").append(getFlakyCount()).append(",\n");
        json.append("  \"flakyPercentage\": ").append(formatDecimal(getFlakyPercentage(), 2)).append(",\n");
        json.append("  \"flakyTests\": [\n");

        for (int i = 0; i < flakyTests.size(); i++) {
            FlakyTestInfo info = flakyTests.get(i);
            json.append("    {\n");
            json.append("      \"testClass\": \"").append(escapeJson(info.getTestClass())).append("\",\n");
            json.append("      \"testMethod\": \"").append(escapeJson(info.getTestMethod())).append("\",\n");
            json.append("      \"failureRate\": ").append(formatDecimal(info.getFailureRate(), 4)).append(",\n");
            json.append("      \"totalRuns\": ").append(info.getTotalRuns()).append(",\n");
            json.append("      \"failureCount\": ").append(info.getFailureCount()).append("\n");
            json.append("    }");
            if (i < flakyTests.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}");
        return json.toString();
    }

    /**
     * Generates an HTML representation of the report.
     *
     * @return HTML string
     */
    public String toHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("  <title>Flaky Test Report</title>\n");
        html.append("  <style>\n");
        html.append("    body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("    table { border-collapse: collapse; width: 100%; }\n");
        html.append("    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("    th { background-color: #4CAF50; color: white; }\n");
        html.append("    tr:nth-child(even) { background-color: #f2f2f2; }\n");
        html.append("    .flaky { color: red; font-weight: bold; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("  <h1>Flaky Test Report</h1>\n");
        html.append("  <h2>Summary</h2>\n");
        html.append("  <ul>\n");
        html.append("    <li>Total Tests: ").append(totalTests).append("</li>\n");
        html.append("    <li>Passed Tests: ").append(passedTests).append("</li>\n");
        html.append("    <li>Flaky Tests: <span class=\"flaky\">").append(getFlakyCount()).append("</span></li>\n");
        html.append("    <li>Flaky Percentage: <span class=\"flaky\">").append(String.format("%.2f%%", getFlakyPercentage())).append("</span></li>\n");
        html.append("  </ul>\n");

        if (!flakyTests.isEmpty()) {
            html.append("  <h2>Flaky Tests</h2>\n");
            html.append("  <table>\n");
            html.append("    <tr>\n");
            html.append("      <th>Test Class</th>\n");
            html.append("      <th>Test Method</th>\n");
            html.append("      <th>Failure Rate</th>\n");
            html.append("      <th>Total Runs</th>\n");
            html.append("      <th>Failures</th>\n");
            html.append("    </tr>\n");

            for (FlakyTestInfo info : flakyTests) {
                html.append("    <tr>\n");
                html.append("      <td>").append(escapeHtml(info.getTestClass())).append("</td>\n");
                html.append("      <td>").append(escapeHtml(info.getTestMethod())).append("</td>\n");
                html.append("      <td class=\"flaky\">").append(String.format("%.2f%%", info.getFailureRate() * 100)).append("</td>\n");
                html.append("      <td>").append(info.getTotalRuns()).append("</td>\n");
                html.append("      <td class=\"flaky\">").append(info.getFailureCount()).append("</td>\n");
                html.append("    </tr>\n");
            }

            html.append("  </table>\n");
        }

        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }

    private String formatDecimal(double value, int precision) {
        String formatPattern = "%." + precision + "f";
        return String.format(Locale.US, formatPattern, value);
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
