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

package org.apache.cloudstack.test.fixtures;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Tests for TestFixtures.
 */
public class TestFixturesTest {

    private TestFixtures fixtures;

    @Test
    public void shouldCreateTestZone_withDefaultValues() {
        // When creating a test zone
        fixtures = new TestFixtures();
        Map<String, Object> zone = fixtures.createTestZone();

        // Then it should have default values
        Assert.assertNotNull("Zone should not be null", zone);
        Assert.assertEquals("Zone name should be test-zone", "test-zone", zone.get("name"));
        Assert.assertEquals("Zone network type should be Advanced", "Advanced", zone.get("networkType"));
    }

    @Test
    public void shouldCreateTestHost_withDefaultValues() {
        // When creating a test host
        fixtures = new TestFixtures();
        Map<String, Object> host = fixtures.createTestHost();

        Assert.assertNotNull("Host should not be null", host);
        Assert.assertEquals("Host IP should be set", "192.168.1.100", host.get("ip"));
    }

    @Test
    public void shouldCreateTestNetwork_withDefaultValues() {
        // When creating a test network
        fixtures = new TestFixtures();
        Map<String, Object> network = fixtures.createTestNetwork();

        Assert.assertNotNull("Network should not be null", network);
        Assert.assertEquals("Network name should be test-network", "test-network", network.get("name"));
    }

    @Test
    public void shouldCleanup_resources_whenTeardownCalled() {
        // Given created fixtures
        fixtures = new TestFixtures();
        fixtures.createTestZone();
        fixtures.createTestHost();

        // When teardown is called
        fixtures.tearDown();

        // Then resources should be cleaned up
        Assert.assertTrue("Fixtures should be cleaned up", fixtures.isCleanedUp());
    }

    @Test
    public void shouldGenerateUniqueName_whenCalled() {
        // When generating unique names
        fixtures = new TestFixtures();
        String name1 = fixtures.generateUniqueName();
        String name2 = fixtures.generateUniqueName();

        // Then names should be different and start with "test-"
        Assert.assertNotNull("Name should not be null", name1);
        Assert.assertTrue("Name should start with test-", name1.startsWith("test-"));
        Assert.assertNotEquals("Names should be unique", name1, name2);
    }

    @Test
    public void shouldGenerateRandomIp_whenCalled() {
        // When generating random IPs
        fixtures = new TestFixtures();
        String ip = fixtures.generateRandomIp();

        // Then IP should be in 192.168.x.x format
        Assert.assertNotNull("IP should not be null", ip);
        Assert.assertTrue("IP should start with 192.168", ip.startsWith("192.168"));
    }

    @Test
    public void shouldGenerateRandomVni_whenCalled() {
        // When generating random VNIs
        fixtures = new TestFixtures();
        int vni = fixtures.generateRandomVni();

        // Then VNI should be in range 1000-9999
        Assert.assertTrue("VNI should be >= 1000", vni >= 1000);
        Assert.assertTrue("VNI should be <= 9999", vni <= 9999);
    }
}
