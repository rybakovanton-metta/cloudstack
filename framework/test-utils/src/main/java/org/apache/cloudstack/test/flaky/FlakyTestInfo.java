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
 * Information about a flaky test.
 */
public class FlakyTestInfo {
    private final String testClass;
    private final String testMethod;
    private final double failureRate;
    private final int totalRuns;
    private final int failureCount;

    /**
     * Creates a new FlakyTestInfo.
     *
     * @param testClass the test class name
     * @param testMethod the test method name
     * @param failureRate the failure rate (0.0-1.0)
     * @param totalRuns total number of test runs
     * @param failureCount number of failures
     */
    public FlakyTestInfo(String testClass, String testMethod, double failureRate, int totalRuns, int failureCount) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.failureRate = failureRate;
        this.totalRuns = totalRuns;
        this.failureCount = failureCount;
    }

    public String getTestClass() {
        return testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public double getFailureRate() {
        return failureRate;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public int getFailureCount() {
        return failureCount;
    }

    @Override
    public String toString() {
        return String.format("%s.%s (failure rate: %.2f%%, %d/%d failures)",
            testClass, testMethod, failureRate * 100, failureCount, totalRuns);
    }
}
