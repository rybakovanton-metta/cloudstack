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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Test fixtures for creating test data objects.
 * Provides simple data structures for testing without heavy dependencies.
 */
public class TestFixtures {
    private final List<Object> createdObjects;
    private boolean cleanedUp;

    /**
     * Creates a new TestFixtures instance.
     */
    public TestFixtures() {
        this.createdObjects = new ArrayList<>();
        this.cleanedUp = false;
    }

    /**
     * Creates a test zone data map with default values.
     *
     * @return Map containing zone data
     */
    public Map<String, Object> createTestZone() {
        Map<String, Object> zone = new HashMap<>();
        zone.put("id", 1L);
        zone.put("name", "test-zone");
        zone.put("description", "Test Zone");
        zone.put("networkType", "Advanced");
        zone.put("securityGroupEnabled", true);
        zone.put("guestCidr", "10.0.0.0/8");
        zone.put("dns1", "8.8.8.8");
        zone.put("dns2", "8.8.4.4");
        zone.put("domain", "test");
        zone.put("internalDns1", "10.0.0.1");
        zone.put("internalDns2", "10.0.0.2");
        zone.put("vlanType", "VLAN");
        zone.put("storageIp", "10.0.0.1");
        createdObjects.add(zone);
        return zone;
    }

    /**
     * Creates a test host data map with default values.
     *
     * @return Map containing host data
     */
    public Map<String, Object> createTestHost() {
        Map<String, Object> host = new HashMap<>();
        host.put("id", 1L);
        host.put("ip", "192.168.1.100");
        host.put("type", "Host");
        host.put("dataCenterId", 1L);
        host.put("hypervisorType", "KVM");
        host.put("hypervisorVersion", "7.0.0");
        createdObjects.add(host);
        return host;
    }

    /**
     * Creates a test network data map with default values.
     *
     * @return Map containing network data
     */
    public Map<String, Object> createTestNetwork() {
        Map<String, Object> network = new HashMap<>();
        network.put("id", 1L);
        network.put("name", "test-network");
        network.put("displayText", "Test Network");
        network.put("accountId", 1L);
        network.put("domainId", 1L);
        network.put("dataCenterId", 1L);
        network.put("networkOfferingId", 1L);
        network.put("guestType", "Isolated");
        network.put("cidr", "10.0.0.0/24");
        network.put("broadcastDomainType", "Vlan");
        network.put("broadcastUri", "vlan://100");
        createdObjects.add(network);
        return network;
    }

    /**
     * Generates a random unique name for test objects.
     *
     * @return random unique name
     */
    public String generateUniqueName() {
        return "test-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generates a random IP address.
     *
     * @return random IP address in 192.168.x.x range
     */
    public String generateRandomIp() {
        int octet3 = (int) (Math.random() * 256);
        int octet4 = (int) (Math.random() * 256);
        return "192.168." + octet3 + "." + octet4;
    }

    /**
     * Generates a random VXLAN ID.
     *
     * @return random VNI between 1000 and 9999
     */
    public int generateRandomVni() {
        return 1000 + (int) (Math.random() * 9000);
    }

    /**
     * Cleans up all created objects.
     */
    public void tearDown() {
        createdObjects.clear();
        cleanedUp = true;
    }

    /**
     * Checks if fixtures have been cleaned up.
     *
     * @return true if cleaned up, false otherwise
     */
    public boolean isCleanedUp() {
        return cleanedUp;
    }

    /**
     * Gets the list of created objects.
     *
     * @return list of created objects
     */
    public List<Object> getCreatedObjects() {
        return new ArrayList<>(createdObjects);
    }
}
