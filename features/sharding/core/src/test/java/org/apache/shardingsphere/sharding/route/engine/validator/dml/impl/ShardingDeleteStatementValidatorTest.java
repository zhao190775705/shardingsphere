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

package org.apache.shardingsphere.sharding.route.engine.validator.dml.impl;

import org.apache.shardingsphere.infra.binder.context.statement.dml.DeleteStatementContext;
import org.apache.shardingsphere.infra.config.props.ConfigurationProperties;
import org.apache.shardingsphere.infra.database.core.DefaultDatabase;
import org.apache.shardingsphere.infra.hint.HintValueContext;
import org.apache.shardingsphere.infra.metadata.database.ShardingSphereDatabase;
import org.apache.shardingsphere.sharding.exception.syntax.DMLWithMultipleShardingTablesException;
import org.apache.shardingsphere.sharding.rule.ShardingRule;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.DeleteMultiTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.TableNameSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.dml.DeleteStatement;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;
import org.apache.shardingsphere.sql.parser.statement.mysql.dml.MySQLDeleteStatement;
import org.apache.shardingsphere.sql.parser.statement.oracle.dml.OracleDeleteStatement;
import org.apache.shardingsphere.sql.parser.statement.postgresql.dml.PostgreSQLDeleteStatement;
import org.apache.shardingsphere.sql.parser.statement.sql92.dml.SQL92DeleteStatement;
import org.apache.shardingsphere.sql.parser.statement.sqlserver.dml.SQLServerDeleteStatement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShardingDeleteStatementValidatorTest {
    
    @Mock
    private ShardingRule shardingRule;
    
    @Test
    void assertPreValidateWhenDeleteMultiTablesForMySQL() {
        assertThrows(DMLWithMultipleShardingTablesException.class, () -> assertPreValidateWhenDeleteMultiTables(new MySQLDeleteStatement()));
    }
    
    @Test
    void assertPreValidateWhenDeleteMultiTablesForOracle() {
        assertThrows(DMLWithMultipleShardingTablesException.class, () -> assertPreValidateWhenDeleteMultiTables(new OracleDeleteStatement()));
    }
    
    @Test
    void assertPreValidateWhenDeleteMultiTablesForPostgreSQL() {
        assertThrows(DMLWithMultipleShardingTablesException.class, () -> assertPreValidateWhenDeleteMultiTables(new PostgreSQLDeleteStatement()));
    }
    
    @Test
    void assertPreValidateWhenDeleteMultiTablesForSQL92() {
        assertThrows(DMLWithMultipleShardingTablesException.class, () -> assertPreValidateWhenDeleteMultiTables(new SQL92DeleteStatement()));
    }
    
    @Test
    void assertPreValidateWhenDeleteMultiTablesForSQLServer() {
        assertThrows(DMLWithMultipleShardingTablesException.class, () -> assertPreValidateWhenDeleteMultiTables(new SQLServerDeleteStatement()));
    }
    
    private void assertPreValidateWhenDeleteMultiTables(final DeleteStatement sqlStatement) {
        DeleteMultiTableSegment tableSegment = new DeleteMultiTableSegment();
        tableSegment.getActualDeleteTables().add(new SimpleTableSegment(new TableNameSegment(0, 0, new IdentifierValue("user"))));
        tableSegment.getActualDeleteTables().add(new SimpleTableSegment(new TableNameSegment(0, 0, new IdentifierValue("order"))));
        tableSegment.getActualDeleteTables().add(new SimpleTableSegment(new TableNameSegment(0, 0, new IdentifierValue("order_item"))));
        sqlStatement.setTable(tableSegment);
        Collection<String> tableNames = new HashSet<>(Arrays.asList("user", "order", "order_item"));
        when(shardingRule.isAllShardingTables(tableNames)).thenReturn(false);
        when(shardingRule.containsShardingTable(tableNames)).thenReturn(true);
        ShardingSphereDatabase database = mock(ShardingSphereDatabase.class);
        DeleteStatementContext sqlStatementContext = new DeleteStatementContext(sqlStatement, DefaultDatabase.LOGIC_NAME);
        new ShardingDeleteStatementValidator().preValidate(shardingRule, sqlStatementContext, mock(HintValueContext.class), Collections.emptyList(), database, mock(ConfigurationProperties.class));
    }
}
