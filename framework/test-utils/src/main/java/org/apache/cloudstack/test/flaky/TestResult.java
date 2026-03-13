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
 * Represents the result of a single test execution.
 */
public class TestResult {
    private final boolean passed;
    private final long executionTime;
    private final boolean timeout;
    private final Throwable error;

    /**
     * Creates a new TestResult.
     *
     * @param passed true if test passed
     * @param executionTime execution time in milliseconds
     * @param timeout true if test timed out
     * @param error exception if test failed, null otherwise
     */
    public TestResult(boolean passed, long executionTime, boolean timeout, Throwable error) {
        this.passed = passed;
        this.executionTime = executionTime;
        this.timeout = timeout;
        this.error = error;
    }

    /**
     * Creates a successful test result.
     *
     * @param executionTime execution time in milliseconds
     * @return new TestResult
     */
    public static TestResult success(long executionTime) {
        return new TestResult(true, executionTime, false, null);
    }

    /**
     * Creates a failed test result.
     *
     * @param executionTime execution time in milliseconds
     * @param error the exception that caused the failure
     * @return new TestResult
     */
    public static TestResult failure(long executionTime, Throwable error) {
        return new TestResult(false, executionTime, false, error);
    }

    /**
     * Creates a timeout test result.
     *
     * @param executionTime execution time in milliseconds
     * @return new TestResult
     */
    public static TestResult timeout(long executionTime) {
        return new TestResult(false, executionTime, true, null);
    }

    public boolean isPassed() {
        return passed;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public Throwable getError() {
        return error;
    }
}
