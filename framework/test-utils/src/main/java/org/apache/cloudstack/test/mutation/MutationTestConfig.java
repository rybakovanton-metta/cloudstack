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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Configuration for mutation testing with Pitest.
 */
public class MutationTestConfig {
    // Default values
    private static final int DEFAULT_THREADS = 4;
    private static final long DEFAULT_TIMEOUT = 60000;
    private static final double DEFAULT_COVERAGE_THRESHOLD = 80.0;
    private static final List<String> DEFAULT_MUTATORS = Arrays.asList("ALL");

    private int threads;
    private long timeout;
    private double coverageThreshold;
    private List<String> mutators;
    private List<String> targetClasses;
    private List<String> targetTests;
    private List<String> excludedClasses;
    private List<String> excludedMethods;
    private String outputDirectory;
    private List<String> outputFormats;

    /**
     * Creates a new MutationTestConfig with default values.
     */
    public MutationTestConfig() {
        this.threads = DEFAULT_THREADS;
        this.timeout = DEFAULT_TIMEOUT;
        this.coverageThreshold = DEFAULT_COVERAGE_THRESHOLD;
        this.mutators = new ArrayList<>(DEFAULT_MUTATORS);
        this.targetClasses = new ArrayList<>();
        this.targetTests = new ArrayList<>();
        this.excludedClasses = new ArrayList<>();
        this.excludedMethods = new ArrayList<>();
        this.outputDirectory = "target/pit-reports";
        this.outputFormats = Arrays.asList("HTML", "XML");
    }

    /**
     * Creates a MutationTestConfig from a properties file.
     *
     * @param configPath path to the properties file
     */
    public MutationTestConfig(String configPath) {
        this();
        loadFromProperties(configPath);
    }

    /**
     * Loads configuration from a properties file.
     *
     * @param configPath path to the properties file
     */
    public void loadFromProperties(String configPath) {
        Properties props = new Properties();
        File configFile = new File(configPath);

        if (!configFile.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);

            if (props.containsKey("threads")) {
                this.threads = parseIntProperty(props.getProperty("threads"), this.threads);
            }
            if (props.containsKey("timeout")) {
                this.timeout = parseLongProperty(props.getProperty("timeout"), this.timeout);
            }
            if (props.containsKey("coverageThreshold")) {
                this.coverageThreshold = parseDoubleProperty(props.getProperty("coverageThreshold"), this.coverageThreshold);
            }
            if (props.containsKey("mutators")) {
                this.mutators = parseListProperty(props.getProperty("mutators"));
            }
            if (props.containsKey("targetClasses")) {
                this.targetClasses = parseListProperty(props.getProperty("targetClasses"));
            }
            if (props.containsKey("targetTests")) {
                this.targetTests = parseListProperty(props.getProperty("targetTests"));
            }
            if (props.containsKey("excludedClasses")) {
                this.excludedClasses = parseListProperty(props.getProperty("excludedClasses"));
            }
            if (props.containsKey("excludedMethods")) {
                this.excludedMethods = parseListProperty(props.getProperty("excludedMethods"));
            }
            if (props.containsKey("outputDirectory")) {
                this.outputDirectory = props.getProperty("outputDirectory");
            }
            if (props.containsKey("outputFormats")) {
                this.outputFormats = parseListProperty(props.getProperty("outputFormats"));
            }
        } catch (IOException e) {
            // Log error but continue with defaults
        }
    }

    private List<String> parseListProperty(String value) {
        List<String> items = new ArrayList<>();
        if (value == null || value.trim().isEmpty()) {
            return items;
        }

        String[] parts = value.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                items.add(trimmedPart);
            }
        }

        return items;
    }

    private int parseIntProperty(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private long parseLongProperty(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDoubleProperty(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Validates the mutator list.
     *
     * @return true if all mutators are valid, false otherwise
     */
    public boolean validateMutators() {
        List<String> validMutators = Arrays.asList(
            "ALL", "DEFAULTS", "INLINE_CONSTS", "CONDITIONALS",
            "EMPTY_RETURNS", "NULL_RETURNS", "PRIMITIVE_RETURNS",
            "FALSE_RETURNS", "TRUE_RETURNS", "VOID_METHOD_CALLS",
            "REMOVE_CONDITIONALS", "NEGATE_CONDITIONALS",
            "REMOVE_INCREMENTS", "MATH", "BOUNDARY", "INCREMENTS"
        );

        for (String mutator : mutators) {
            if (!validMutators.contains(mutator.toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    // Getters and setters

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public double getCoverageThreshold() {
        return coverageThreshold;
    }

    public void setCoverageThreshold(double coverageThreshold) {
        this.coverageThreshold = coverageThreshold;
    }

    public List<String> getMutators() {
        return new ArrayList<>(mutators);
    }

    public void setMutators(List<String> mutators) {
        this.mutators = new ArrayList<>(mutators);
    }

    public List<String> getTargetClasses() {
        return new ArrayList<>(targetClasses);
    }

    public void setTargetClasses(List<String> targetClasses) {
        this.targetClasses = new ArrayList<>(targetClasses);
    }

    public List<String> getTargetTests() {
        return new ArrayList<>(targetTests);
    }

    public void setTargetTests(List<String> targetTests) {
        this.targetTests = new ArrayList<>(targetTests);
    }

    public List<String> getExcludedClasses() {
        return new ArrayList<>(excludedClasses);
    }

    public void setExcludedClasses(List<String> excludedClasses) {
        this.excludedClasses = new ArrayList<>(excludedClasses);
    }

    public List<String> getExcludedMethods() {
        return new ArrayList<>(excludedMethods);
    }

    public void setExcludedMethods(List<String> excludedMethods) {
        this.excludedMethods = new ArrayList<>(excludedMethods);
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<String> getOutputFormats() {
        return new ArrayList<>(outputFormats);
    }

    public void setOutputFormats(List<String> outputFormats) {
        this.outputFormats = new ArrayList<>(outputFormats);
    }
}
