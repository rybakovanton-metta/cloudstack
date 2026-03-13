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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for FlakyTestReport.
 * These tests should fail initially (RED phase) until implementation is complete.
 */
public class FlakyTestReportTest {

    @Test
    public void shouldGenerateJsonReport_whenRequested() {
        // Given a report with some flaky tests
        List<FlakyTestInfo> flakyTests = Arrays.asList(
            new FlakyTestInfo("com.example.Test1", "testMethod", 0.2, 10, 2)
        );
        FlakyTestReport report = new FlakyTestReport(flakyTests, 100, 1);

        String json = report.toJson();

        Assert.assertNotNull("JSON should not be null", json);
        Assert.assertTrue("JSON should contain flakyTests", json.contains("flakyTests"));
    }

    @Test
    public void shouldGenerateHtmlReport_whenRequested() {
        // Given a report
        List<FlakyTestInfo> flakyTests = Arrays.asList(
            new FlakyTestInfo("com.example.Test1", "testMethod", 0.2, 10, 2)
        );
        FlakyTestReport report = new FlakyTestReport(flakyTests, 100, 1);

        String html = report.toHtml();

        Assert.assertNotNull("HTML should not be null", html);
        Assert.assertTrue("HTML should contain table", html.contains("<table>"));
    }

    @Test
    public void shouldIncludeFlakyTestDetails_inReport() {
        // Given a flaky test with specific details
        List<FlakyTestInfo> flakyTests = Arrays.asList(
            new FlakyTestInfo("com.example.Test1", "testMethod", 0.2, 10, 2)
        );
        FlakyTestReport report = new FlakyTestReport(flakyTests, 100, 1);

        Assert.assertEquals("Should have 1 flaky test", 1, report.getFlakyTests().size());
        Assert.assertEquals("Class name should match", "com.example.Test1", report.getFlakyTests().get(0).getTestClass());
        Assert.assertEquals("Method name should match", "testMethod", report.getFlakyTests().get(0).getTestMethod());
    }

    @Test
    public void shouldCalculateSummaryStatistics_accurately() {
        // Given a report with known numbers
        List<FlakyTestInfo> flakyTests = Arrays.asList(
            new FlakyTestInfo("com.example.Test1", "testMethod", 0.2, 10, 2)
        );
        FlakyTestReport report = new FlakyTestReport(flakyTests, 100, 1);

        Assert.assertEquals("Total tests should be 100", 100, report.getTotalTests());
        Assert.assertEquals("Flaky count should be 1", 1, report.getFlakyCount());
        Assert.assertEquals("Flaky percentage should be 1%", 1.0, report.getFlakyPercentage(), 0.01);
    }
}
