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

/**
 * Detects flaky tests based on execution results.
 * A test is considered flaky if it fails intermittently (not consistently passing or failing).
 */
public class FlakyTestDetector {

    private final int repetitions;
    private final double failureThreshold;

    /**
     * Creates a new FlakyTestDetector.
     *
     * @param repetitions number of times to run each test
     * @param failureThreshold failure rate threshold above which a test is considered flaky (0.0-1.0)
     */
    public FlakyTestDetector(int repetitions, double failureThreshold) {
        this.repetitions = repetitions;
        this.failureThreshold = failureThreshold;
    }

    /**
     * Detects if a test is flaky based on execution results.
     *
     * @param results array of test results (1 = pass, 0 = fail)
     * @return true if the test is flaky, false otherwise
     */
    public boolean detect(int[] results) {
        double failureRate = calculateFailureRate(results);
        return failureRate > failureThreshold && failureRate < 1.0;
    }

    /**
     * Calculates the failure rate from test results.
     *
     * @param results array of test results (1 = pass, 0 = fail)
     * @return failure rate as a decimal (0.0-1.0)
     */
    public double calculateFailureRate(int[] results) {
        if (results == null || results.length == 0) {
            return 0.0;
        }

        int failures = 0;
        for (int result : results) {
            if (result == 0) {
                failures++;
            }
        }

        return (double) failures / results.length;
    }

    /**
     * Checks if a test execution exceeded the timeout.
     *
     * @param executionTime actual execution time in milliseconds
     * @param timeout maximum allowed execution time in milliseconds
     * @return true if execution exceeded timeout, false otherwise
     */
    public boolean isTimeout(long executionTime, long timeout) {
        return executionTime > timeout;
    }

    /**
     * Gets the number of repetitions configured.
     *
     * @return number of repetitions
     */
    public int getRepetitions() {
        return repetitions;
    }

    /**
     * Gets the failure threshold configured.
     *
     * @return failure threshold as a decimal
     */
    public double getFailureThreshold() {
        return failureThreshold;
    }
}
