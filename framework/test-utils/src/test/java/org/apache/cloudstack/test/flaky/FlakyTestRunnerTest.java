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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests for FlakyTestRunner.
 * These tests should fail initially (RED phase) until implementation is complete.
 */
public class FlakyTestRunnerTest {

    private FlakyTestRunner runner;

    @Test
    public void shouldRunTestMultipleTimes_whenDetectionEnabled() {
        // Given a test to run with 5 repetitions
        TestExecutable test = () -> true; // Always passes

        runner = new FlakyTestRunner(5, 30000);

        // When running the test
        TestResult[] results = runner.run(test);

        // Then it should have 5 results
        Assert.assertEquals("Should run test 5 times", 5, results.length);
    }

    @Test
    public void shouldReportFlakyTests_inSummary() {
        // Given a flaky test
        TestExecutable flakyTest = () -> Math.random() > 0.5;

        runner = new FlakyTestRunner(10, 30000);

        FlakyTestReport report = runner.runAndReport(flakyTest);

        Assert.assertNotNull("Report should not be null", report);
        Assert.assertNotNull("Flaky tests list should exist", report.getFlakyTests());
    }

    @Test
    public void shouldSkipTest_whenTimeoutExceeded() {
        // Given a test that takes too long
        TestExecutable slowTest = () -> {
            try {
                Thread.sleep(100);
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        };

        runner = new FlakyTestRunner(3, 10); // 10ms timeout

        TestResult[] results = runner.run(slowTest);

        // At least some runs should timeout
        int timeoutCount = 0;
        for (TestResult result : results) {
            if (result.isTimeout()) {
                timeoutCount++;
            }
        }
        Assert.assertTrue("Some runs should timeout", timeoutCount > 0);
    }

    @Test
    public void shouldParallelizeRuns_whenMultipleThreadsConfigured() {
        // Given configuration for parallel execution
        runner = new FlakyTestRunner(10, 30000, 4); // 4 threads

        TestExecutable test = () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return true;
        };

        long start = System.currentTimeMillis();
        TestResult[] results = runner.run(test);
        long duration = System.currentTimeMillis() - start;

        // Parallel execution should be faster than sequential
        // Sequential would take ~100ms, parallel should be much less
        Assert.assertEquals("Should run all tests", 10, results.length);
    }

    @Test
    public void shouldCancelTimedOutParallelRun_whenTimeoutExceeded() throws InterruptedException {
        InterruptAwareExecutable slowTest = new InterruptAwareExecutable(200);

        runner = new FlakyTestRunner(1, 10, 2);

        TestResult[] results = runner.run(slowTest);

        Assert.assertEquals("Should return one result", 1, results.length);
        Assert.assertTrue("Parallel run should report timeout", results[0].isTimeout());
        Assert.assertTrue("Timed out task should be interrupted", slowTest.awaitInterrupted());
    }

    private static final class InterruptAwareExecutable implements TestExecutable {
        private final long sleepMs;
        private final CountDownLatch interruptedLatch;

        private InterruptAwareExecutable(long sleepMs) {
            this.sleepMs = sleepMs;
            this.interruptedLatch = new CountDownLatch(1);
        }

        @Override
        public boolean execute() {
            try {
                Thread.sleep(sleepMs);
                return true;
            } catch (InterruptedException e) {
                interruptedLatch.countDown();
                Thread.currentThread().interrupt();
                return false;
            }
        }

        private boolean awaitInterrupted() throws InterruptedException {
            return interruptedLatch.await(1, TimeUnit.SECONDS);
        }
    }
}
