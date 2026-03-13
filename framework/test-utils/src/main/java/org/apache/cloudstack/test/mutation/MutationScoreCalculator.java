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

/**
 * Calculates mutation testing scores and coverage metrics.
 */
public class MutationScoreCalculator {

    /**
     * Calculates the mutation score.
     *
     * @param killed number of killed mutants
     * @param survived number of survived mutants
     * @param notCovered number of not covered mutants
     * @return mutation score as a percentage (0-100)
     */
    public double calculate(int killed, int survived, int notCovered) {
        if (killed + survived == 0) {
            return 0.0;
        }
        return (killed * 100.0) / (killed + survived);
    }

    /**
     * Calculates line coverage percentage.
     *
     * @param coveredLines number of covered lines
     * @param missedLines number of missed lines
     * @return line coverage as a percentage (0-100)
     */
    public double calculateLineCoverage(int coveredLines, int missedLines) {
        if (coveredLines + missedLines == 0) {
            return 0.0;
        }
        return (coveredLines * 100.0) / (coveredLines + missedLines);
    }

    /**
     * Calculates branch coverage percentage.
     *
     * @param coveredBranches number of covered branches
     * @param missedBranches number of missed branches
     * @return branch coverage as a percentage (0-100)
     */
    public double calculateBranchCoverage(int coveredBranches, int missedBranches) {
        if (coveredBranches + missedBranches == 0) {
            return 0.0;
        }
        return (coveredBranches * 100.0) / (coveredBranches + missedBranches);
    }

    /**
     * Calculates method coverage percentage.
     *
     * @param coveredMethods number of covered methods
     * @param missedMethods number of missed methods
     * @return method coverage as a percentage (0-100)
     */
    public double calculateMethodCoverage(int coveredMethods, int missedMethods) {
        if (coveredMethods + missedMethods == 0) {
            return 0.0;
        }
        return (coveredMethods * 100.0) / (coveredMethods + missedMethods);
    }

    /**
     * Creates a summary report of mutation testing results.
     *
     * @param killed number of killed mutants
     * @param survived number of survived mutants
     * @param notCovered number of not covered mutants
     * @param coveredLines number of covered lines
     * @param missedLines number of missed lines
     * @return formatted summary string
     */
    public String createSummary(int killed, int survived, int notCovered,
                                 int coveredLines, int missedLines) {
        double mutationScore = calculate(killed, survived, notCovered);
        double lineCoverage = calculateLineCoverage(coveredLines, missedLines);

        StringBuilder summary = new StringBuilder();
        summary.append("=== Mutation Testing Summary ===\n");
        summary.append("Mutants:\n");
        summary.append("  Killed: ").append(killed).append("\n");
        summary.append("  Survived: ").append(survived).append("\n");
        summary.append("  Not Covered: ").append(notCovered).append("\n");
        summary.append("  Mutation Score: ").append(String.format("%.2f%%", mutationScore)).append("\n");
        summary.append("\n");
        summary.append("Coverage:\n");
        summary.append("  Line Coverage: ").append(String.format("%.2f%%", lineCoverage)).append("\n");
        summary.append("  Covered Lines: ").append(coveredLines).append("\n");
        summary.append("  Missed Lines: ").append(missedLines).append("\n");

        return summary.toString();
    }

    /**
     * Checks if mutation score meets the threshold.
     *
     * @param killed number of killed mutants
     * @param survived number of survived mutants
     * @param threshold minimum required mutation score
     * @return true if threshold is met, false otherwise
     */
    public boolean meetsThreshold(int killed, int survived, double threshold) {
        return calculate(killed, survived, 0) >= threshold;
    }
}
