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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for MutationTestConfig.
 * These tests should fail initially (RED phase) until implementation is complete.
 */
public class MutationTestConfigTest {

    @Test
    public void shouldLoadDefaults_whenNoConfigProvided() {
        // When creating config with no parameters
        MutationTestConfig config = new MutationTestConfig();

        // Then defaults should be applied
        Assert.assertEquals("Default threads should be 4", 4, config.getThreads());
        Assert.assertEquals("Default timeout should be 60000", 60000, config.getTimeout());
        Assert.assertNotNull("Default mutators should not be null", config.getMutators());
    }

    @Test
    public void shouldLoadFromProperties_whenConfigFileProvided() {
        Path configPath = createConfigFile(
            "threads=8\n"
                + "timeout=45000\n"
                + "coverageThreshold=85.5\n"
                + "mutators=ALL, CONDITIONALS\n"
                + "targetClasses=org.apache.cloudstack.test.*, org.apache.cloudstack.utils.*\n"
                + "targetTests=org.apache.cloudstack.test.*Test\n"
                + "excludedClasses=org.apache.cloudstack.test.internal.*\n"
                + "excludedMethods=hashCode,toString\n"
                + "outputDirectory=target/custom-pit-reports\n"
                + "outputFormats=HTML, XML\n"
        );

        MutationTestConfig config = new MutationTestConfig(configPath.toString());

        Assert.assertEquals("Threads should be loaded from properties", 8, config.getThreads());
        Assert.assertEquals("Timeout should be loaded from properties", 45000, config.getTimeout());
        Assert.assertEquals("Coverage threshold should be loaded from properties", 85.5, config.getCoverageThreshold(), 0.01);
        Assert.assertEquals("Mutators should be trimmed and loaded", Arrays.asList("ALL", "CONDITIONALS"), config.getMutators());
        Assert.assertEquals("Target classes should be loaded", Arrays.asList("org.apache.cloudstack.test.*", "org.apache.cloudstack.utils.*"), config.getTargetClasses());
        Assert.assertEquals("Target tests should be loaded", Arrays.asList("org.apache.cloudstack.test.*Test"), config.getTargetTests());
        Assert.assertEquals("Excluded classes should be loaded", Arrays.asList("org.apache.cloudstack.test.internal.*"), config.getExcludedClasses());
        Assert.assertEquals("Excluded methods should be loaded", Arrays.asList("hashCode", "toString"), config.getExcludedMethods());
        Assert.assertEquals("Output directory should be loaded", "target/custom-pit-reports", config.getOutputDirectory());
        Assert.assertEquals("Output formats should be loaded", Arrays.asList("HTML", "XML"), config.getOutputFormats());
    }

    @Test
    public void shouldValidateMutatorList_whenConfigured() {
        // Given a list of mutators
        List<String> mutators = Arrays.asList("ALL", "CONDITIONALS", "INLINE_CONSTS");

        MutationTestConfig config = new MutationTestConfig();
        config.setMutators(mutators);

        Assert.assertEquals("Should have 3 mutators", 3, config.getMutators().size());
    }

    @Test
    public void shouldCalculateCoverageThreshold_accurately() {
        // Given a config with 80% threshold
        MutationTestConfig config = new MutationTestConfig();
        config.setCoverageThreshold(80.0);

        Assert.assertEquals("Threshold should be 80%", 80.0, config.getCoverageThreshold(), 0.01);
    }

    private Path createConfigFile(String content) {
        try {
            Path configPath = Files.createTempFile("pitest-config", ".properties");
            Files.write(configPath, content.getBytes(StandardCharsets.UTF_8));
            configPath.toFile().deleteOnExit();
            return configPath;
        } catch (IOException e) {
            throw new AssertionError("Failed to create temporary config file", e);
        }
    }
}
