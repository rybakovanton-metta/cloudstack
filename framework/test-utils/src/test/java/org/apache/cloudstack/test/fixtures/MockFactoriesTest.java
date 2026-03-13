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
import org.mockito.Mockito;

/**
 * Tests for MockFactories.
 */
public class MockFactoriesTest {

    private MockFactories factory;

    // Simple interface for testing mock creation
    interface TestService {
        String getValue(String key);
        void setValue(String key, String value);
    }

    @Test
    public void shouldCreateMockDAO_whenRequested() {
        // When creating a mock DAO
        factory = new MockFactories();
        TestService dao = factory.createMockDAO(TestService.class);

        // Then it should be a valid mock
        Assert.assertNotNull("DAO mock should not be null", dao);
        Mockito.verifyNoInteractions(dao); // Should be a fresh mock
    }

    @Test
    public void shouldCreateMockService_whenRequested() {
        // When creating a mock service
        factory = new MockFactories();
        TestService service = factory.createMockService(TestService.class);

        Assert.assertNotNull("Service mock should not be null", service);
    }

    @Test
    public void shouldVerifyAllMocks_whenTeardownCalled() {
        // Given created mocks
        factory = new MockFactories();
        TestService dao = factory.createMockDAO(TestService.class);

        // When teardown is called
        factory.tearDown();

        // Then all mocks should be verified (no unexpected interactions)
        Assert.assertTrue("Factory should be in verified state", factory.isVerified());
    }

    @Test
    public void shouldResetMocks_whenResetCalled() {
        // Given a mock with interactions
        factory = new MockFactories();
        TestService dao = factory.createMockDAO(TestService.class);
        Mockito.when(dao.getValue("test")).thenReturn("value");

        // When reset is called
        factory.reset();

        // Then mock should be reset
        Mockito.verifyNoInteractions(dao);
    }
}
