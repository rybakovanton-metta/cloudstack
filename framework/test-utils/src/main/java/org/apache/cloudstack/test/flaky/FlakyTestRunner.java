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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Runs tests multiple times to detect flaky tests.
 */
public class FlakyTestRunner {

    private final int repetitions;
    private final long timeoutMs;
    private final int threadCount;
    private final FlakyTestDetector detector;

    /**
     * Creates a new FlakyTestRunner.
     *
     * @param repetitions number of times to run each test
     * @param timeoutMs timeout for each test run in milliseconds
     */
    public FlakyTestRunner(int repetitions, long timeoutMs) {
        this(repetitions, timeoutMs, 1);
    }

    /**
     * Creates a new FlakyTestRunner with thread configuration.
     *
     * @param repetitions number of times to run each test
     * @param timeoutMs timeout for each test run in milliseconds
     * @param threadCount number of threads for parallel execution
     */
    public FlakyTestRunner(int repetitions, long timeoutMs, int threadCount) {
        this.repetitions = repetitions;
        this.timeoutMs = timeoutMs;
        this.threadCount = threadCount;
        this.detector = new FlakyTestDetector(repetitions, 0.1);
    }

    /**
     * Runs a test multiple times and returns the results.
     *
     * @param test the test to run
     * @return array of test results
     */
    public TestResult[] run(TestExecutable test) {
        List<TestResult> results = new ArrayList<>();

        if (threadCount > 1) {
            results = runParallel(test);
        } else {
            results = runSequential(test);
        }

        return results.toArray(new TestResult[0]);
    }

    /**
     * Runs a test and generates a flaky test report.
     *
     * @param test the test to run
     * @return FlakyTestReport with results
     */
    public FlakyTestReport runAndReport(TestExecutable test) {
        TestResult[] results = run(test);
        int[] passFailResults = new int[results.length];
        int passCount = 0;

        for (int i = 0; i < results.length; i++) {
            passFailResults[i] = results[i].isPassed() ? 1 : 0;
            if (results[i].isPassed()) {
                passCount++;
            }
        }

        boolean isFlaky = detector.detect(passFailResults);
        double failureRate = detector.calculateFailureRate(passFailResults);

        List<FlakyTestInfo> flakyTests = new ArrayList<>();
        if (isFlaky) {
            flakyTests.add(new FlakyTestInfo(
                "UnknownTest",
                "unknownMethod",
                failureRate,
                results.length,
                results.length - passCount
            ));
        }

        return new FlakyTestReport(flakyTests, results.length, passCount);
    }

    private List<TestResult> runSequential(TestExecutable test) {
        List<TestResult> results = new ArrayList<>();

        for (int i = 0; i < repetitions; i++) {
            long start = System.currentTimeMillis();
            try {
                boolean passed = test.execute();
                long duration = System.currentTimeMillis() - start;

                if (detector.isTimeout(duration, timeoutMs)) {
                    results.add(TestResult.timeout(duration));
                } else {
                    results.add(passed ? TestResult.success(duration)
                                       : TestResult.failure(duration, null));
                }
            } catch (Throwable e) {
                long duration = System.currentTimeMillis() - start;
                results.add(TestResult.failure(duration, e));
            }
        }

        return results;
    }

    private List<TestResult> runParallel(TestExecutable test) {
        List<TestResult> results = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<TestResult>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < repetitions; i++) {
                futures.add(executor.submit(createTask(test)));
            }

            for (Future<TestResult> future : futures) {
                results.add(resolveFutureResult(future));
            }
        } finally {
            executor.shutdownNow();
        }

        return results;
    }

    private TestResult resolveFutureResult(Future<TestResult> future) {
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return TestResult.timeout(timeoutMs);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            }
            return TestResult.failure(timeoutMs, cause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TestResult.failure(timeoutMs, e);
        }
    }

    private Callable<TestResult> createTask(TestExecutable test) {
        return () -> {
            long start = System.currentTimeMillis();
            try {
                boolean passed = test.execute();
                long duration = System.currentTimeMillis() - start;

                if (detector.isTimeout(duration, timeoutMs)) {
                    return TestResult.timeout(duration);
                }
                return passed ? TestResult.success(duration)
                              : TestResult.failure(duration, null);
            } catch (Throwable e) {
                long duration = System.currentTimeMillis() - start;
                return TestResult.failure(duration, e);
            }
        };
    }

    public int getRepetitions() {
        return repetitions;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
