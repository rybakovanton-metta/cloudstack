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

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating and managing Mockito mocks.
 */
public class MockFactories {
    private final List<Object> mocks;
    private boolean verified;
    private boolean reset;

    /**
     * Creates a new MockFactories instance.
     */
    public MockFactories() {
        this.mocks = new ArrayList<>();
        this.verified = false;
        this.reset = false;
    }

    /**
     * Creates a mock DAO instance.
     *
     * @param daoClass the DAO class to mock
     * @param <T> the DAO type
     * @return mock DAO instance
     */
    public <T> T createMockDAO(Class<T> daoClass) {
        T mock = Mockito.mock(daoClass);
        mocks.add(mock);
        return mock;
    }

    /**
     * Creates a mock service instance.
     *
     * @param serviceClass the service class to mock
     * @param <T> the service type
     * @return mock service instance
     */
    public <T> T createMockService(Class<T> serviceClass) {
        T mock = Mockito.mock(serviceClass);
        mocks.add(mock);
        return mock;
    }

    /**
     * Creates a spy (partial mock) of an existing object.
     *
     * @param realObject the real object to spy on
     * @param <T> the object type
     * @return spied object
     */
    public <T> T createSpy(T realObject) {
        T spy = Mockito.spy(realObject);
        mocks.add(spy);
        return spy;
    }

    /**
     * Creates a mock with the given default answer.
     *
     * @param clazz the class to mock
     * @param defaultAnswer the default answer for unstubbed methods
     * @param <T> the type
     * @return mock instance
     */
    public <T> T createMockWithAnswer(Class<T> clazz, Answer<?> defaultAnswer) {
        T mock = Mockito.mock(clazz, defaultAnswer);
        mocks.add(mock);
        return mock;
    }

    /**
     * Verifies all mocks have been interacted with as expected.
     */
    public void verifyAll() {
        for (Object mock : mocks) {
            Mockito.verifyNoMoreInteractions(mock);
        }
        verified = true;
    }

    /**
     * Resets all mocks to their initial state.
     */
    public void reset() {
        for (Object mock : mocks) {
            Mockito.reset(mock);
        }
        reset = true;
        verified = false;
    }

    /**
     * Clears all mock interactions but keeps the mocks.
     */
    public void clear() {
        for (Object mock : mocks) {
            Mockito.clearInvocations(mock);
        }
        verified = false;
    }

    /**
     * Checks if all mocks have been verified.
     *
     * @return true if verified, false otherwise
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Checks if mocks have been reset.
     *
     * @return true if reset, false otherwise
     */
    public boolean isReset() {
        return reset;
    }

    /**
     * Gets the number of mocks created.
     *
     * @return number of mocks
     */
    public int getMockCount() {
        return mocks.size();
    }

    /**
     * Tears down all mocks.
     */
    public void tearDown() {
        verifyAll();
        mocks.clear();
    }
}
