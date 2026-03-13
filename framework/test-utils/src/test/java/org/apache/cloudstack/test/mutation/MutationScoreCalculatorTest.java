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

package org.apache.cloudstack.test.mutation;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for MutationScoreCalculator.
 * These tests should fail initially (RED phase) until implementation is complete.
 */
public class MutationScoreCalculatorTest {

    private MutationScoreCalculator calculator;

    @Test
    public void shouldCalculateMutationScore_accurately() {
        // Given 100 mutants: 80 killed, 10 survived, 10 no coverage
        calculator = new MutationScoreCalculator();

        double score = calculator.calculate(80, 10, 10);

        Assert.assertEquals("Mutation score should be 88.89% (killed/(killed+survived))", 88.89, score, 0.01);
    }

    @Test
    public void shouldHandleZeroMutations_whenNoMutants() {
        // Given no mutants
        calculator = new MutationScoreCalculator();

        double score = calculator.calculate(0, 0, 0);

        Assert.assertEquals("Score should be 0 when no mutants", 0.0, score, 0.01);
    }

    @Test
    public void shouldCalculateLineCoverage_accurately() {
        // Given 100 lines: 80 covered, 20 not covered
        calculator = new MutationScoreCalculator();

        double coverage = calculator.calculateLineCoverage(80, 20);

        Assert.assertEquals("Line coverage should be 80%", 80.0, coverage, 0.01);
    }

    @Test
    public void shouldCalculateBranchCoverage_accurately() {
        // Given 50 branches: 40 covered, 10 not covered
        calculator = new MutationScoreCalculator();

        double coverage = calculator.calculateBranchCoverage(40, 10);

        Assert.assertEquals("Branch coverage should be 80%", 80.0, coverage, 0.01);
    }
}
