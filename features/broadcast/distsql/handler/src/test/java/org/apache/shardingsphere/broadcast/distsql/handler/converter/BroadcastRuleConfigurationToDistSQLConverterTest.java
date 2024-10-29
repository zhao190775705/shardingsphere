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

package org.apache.shardingsphere.broadcast.distsql.handler.converter;

import org.apache.shardingsphere.broadcast.config.BroadcastRuleConfiguration;
import org.apache.shardingsphere.distsql.handler.engine.query.ral.convert.RuleConfigurationToDistSQLConverter;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BroadcastRuleConfigurationToDistSQLConverterTest {
    
    @SuppressWarnings("unchecked")
    private final RuleConfigurationToDistSQLConverter<BroadcastRuleConfiguration> converter = TypedSPILoader.getService(RuleConfigurationToDistSQLConverter.class, BroadcastRuleConfiguration.class);
    
    @Test
    void assertConvert() {
        BroadcastRuleConfiguration ruleConfig = mock(BroadcastRuleConfiguration.class);
        when(ruleConfig.getTables()).thenReturn(Arrays.asList("t_province", "t_city"));
        assertThat(converter.convert(ruleConfig), is("CREATE BROADCAST TABLE RULE t_province,t_city;"));
    }
}
