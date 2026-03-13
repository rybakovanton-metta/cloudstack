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

/**
 * Tests for FlakyTestDetector.
 * These tests should fail initially (RED phase) until implementation is complete.
 */
public class FlakyTestDetectorTest {

    private FlakyTestDetector detector;

    @Test
    public void shouldDetectFlakyTest_whenTestFailsIntermittently() {
        // Given a test that passes 7 out of 10 times (30% failure rate)
        int[] results = {1, 1, 0, 1, 1, 1, 0, 1, 1, 0}; // 1=pass, 0=fail

        detector = new FlakyTestDetector(10, 0.1);

        // When detecting flakiness
        boolean isFlaky = detector.detect(results);

        // Then it should be marked as flaky (>10% failure rate)
        Assert.assertTrue("Test with 30% failure rate should be flaky", isFlaky);
    }

    @Test
    public void shouldMarkStableTest_whenAlwaysPasses() {
        // Given a test that always passes
        int[] results = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        detector = new FlakyTestDetector(10, 0.1);

        boolean isFlaky = detector.detect(results);

        Assert.assertFalse("Test with 0% failure rate should not be flaky", isFlaky);
    }

    @Test
    public void shouldMarkConsistentlyFailingTest_whenAlwaysFails() {
        // Given a test that always fails (consistently failing, not flaky)
        int[] results = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        detector = new FlakyTestDetector(10, 0.1);

        boolean isFlaky = detector.detect(results);

        // A test that always fails is NOT flaky - it's consistently broken
        // Flaky tests are those that fail intermittently
        Assert.assertFalse("Test with 100% failure rate is consistently failing, not flaky", isFlaky);
    }

    @Test
    public void shouldCalculateFailureRate_accurately() {
        // Given a test with exactly 20% failure rate
        int[] results = {1, 1, 1, 1, 0, 1, 1, 1, 1, 0};

        detector = new FlakyTestDetector(10, 0.1);

        double failureRate = detector.calculateFailureRate(results);

        Assert.assertEquals("Failure rate should be 20%", 0.2, failureRate, 0.001);
    }

    @Test
    public void shouldHandleTimeout_whenTestExceedsLimit() {
        // Given a test execution that times out
        long executionTime = 60000; // 60 seconds
        long timeout = 30000; // 30 seconds

        detector = new FlakyTestDetector(10, 0.1);

        boolean isTimeout = detector.isTimeout(executionTime, timeout);

        Assert.assertTrue("Execution exceeding timeout should be flagged", isTimeout);
    }
}
