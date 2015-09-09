/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.testing.fixtures.deploy.hive;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by akonopko on 02.02.15.
 */
public class HiveTableDeployerTest {

    @Test
    public void testHiveTableDeployer() throws Exception {
        HiveTableDeployer deployer = new HiveTableDeployer(null, "tableName", null, null);
        List<String> columns = Lists.newArrayList();
        List<String> partColumns = Lists.newArrayList();
        Statement statement = mock(Statement.class);

        ResultSet res = mock(ResultSet.class);
        doReturn(res).when(statement).executeQuery("DESCRIBE mockedDatabase.tableName");

        String[] row1 = new String[] {"tstamp", "bigint" };
        String[] row2 = new String[] {"language", "string"};
        String[] row3 = new String[] {"year", "int"};
        String[] row4 = new String[] {null, null};
        String[] row5 = new String[] {"# Partition Information", null};
        String[] row6 = new String[] {"# col_name", "data_type"};
        String[] row7 = new String[] {null, null};
        String[] row8 = new String[] {"year", "int"};

        when(res.getString(1)).thenReturn(row1[0]).thenReturn(row2[0]).thenReturn(row3[0]).thenReturn(row4[0]).thenReturn(row5[0]).thenReturn(row6[0]).thenReturn(row7[0]).thenReturn(row8[0]).thenReturn(row1[0]);
        when(res.getString(2)).thenReturn(row1[1]).thenReturn(row2[1]).thenReturn(row3[1]).thenReturn(row4[1]).thenReturn(row5[1]).thenReturn(row6[1]).thenReturn(row7[1]).thenReturn(row8[1]).thenReturn(row1[1]);
        when(res.next()).thenReturn(true, true, true, true, true, true, true, true, false);

        deployer.parseTableDefinition(columns, partColumns, "mockedDatabase", statement);

        Assert.assertEquals(Lists.newArrayList("tstamp bigint", "language string", "year int"), columns);
        Assert.assertEquals(Lists.newArrayList("year"), partColumns);
    }

    @Test
    public void testHiveTableDeployerNoPartitions() throws Exception {
        HiveTableDeployer deployer = new HiveTableDeployer(null, "tableName", null, null);
        List<String> columns = Lists.newArrayList();
        List<String> partColumns = Lists.newArrayList();
        Statement statement = mock(Statement.class);

        ResultSet res = mock(ResultSet.class);
        doReturn(res).when(statement).executeQuery("DESCRIBE mockedDatabase.tableName");

        String[] row1 = new String[] {"tstamp", "bigint" };
        String[] row2 = new String[] {"language", "string"};
        String[] row3 = new String[] {"year", "int"};

        when(res.getString(1)).thenReturn(row1[0]).thenReturn(row2[0]).thenReturn(row3[0]);
        when(res.getString(2)).thenReturn(row1[1]).thenReturn(row2[1]).thenReturn(row3[1]);
        when(res.next()).thenReturn(true, true, true, false);

        deployer.parseTableDefinition(columns, partColumns, "mockedDatabase", statement);

        Assert.assertEquals(Lists.newArrayList("tstamp bigint", "language string", "year int"), columns);
        Assert.assertTrue(partColumns.isEmpty());
    }

}
