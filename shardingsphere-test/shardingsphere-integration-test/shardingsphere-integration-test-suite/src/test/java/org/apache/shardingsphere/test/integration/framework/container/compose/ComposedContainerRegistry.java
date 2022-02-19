/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.test.integration.framework.container.compose;

import org.apache.shardingsphere.test.integration.framework.container.compose.mode.ClusterComposedContainer;
import org.apache.shardingsphere.test.integration.framework.container.compose.mode.MemoryComposedContainer;
import org.apache.shardingsphere.test.integration.framework.param.model.ParameterizedArray;
import org.testcontainers.lifecycle.Startable;

import java.util.HashMap;
import java.util.Map;

/**
 * Composed container registry.
 */
public final class ComposedContainerRegistry implements AutoCloseable {
    
    private final Map<String, ComposedContainer> composedContainers = new HashMap<>();
    
    /**
     * Get composed container.
     *
     * @param parameterizedArray parameterized array
     * @return composed container
     */
    public ComposedContainer getComposedContainer(final ParameterizedArray parameterizedArray) {
        String key = generateKey(parameterizedArray);
        if (composedContainers.containsKey(key)) {
            return composedContainers.get(key);
        }
        synchronized (composedContainers) {
            if (!composedContainers.containsKey(key)) {
                composedContainers.put(key, createComposedContainer(parameterizedArray));
            }
            return composedContainers.get(key);
        }
    }
    
    private String generateKey(final ParameterizedArray parameterizedArray) {
        return String.join("-", parameterizedArray.getScenario(), parameterizedArray.getAdapter(), parameterizedArray.getDatabaseType().getName());
    }
    
    private ComposedContainer createComposedContainer(final ParameterizedArray parameterizedArray) {
        return isMemoryMode(parameterizedArray) ? new MemoryComposedContainer(parameterizedArray) : new ClusterComposedContainer(parameterizedArray);
    }
    
    private boolean isMemoryMode(final ParameterizedArray parameterizedArray) {
        // TODO cluster mode often throw exception sometimes, issue is #15517
        return true;
//        return "H2".equals(parameterizedArray.getDatabaseType().getName());
    }
    
    @Override
    public void close() {
        composedContainers.values().forEach(Startable::close);
    }
}
