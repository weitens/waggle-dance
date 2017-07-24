/**
 * Copyright (C) 2016-2017 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.bdp.waggledance.mapping.model;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.metastore.api.AddDynamicPartitions;
import org.apache.hadoop.hive.metastore.api.AddPartitionsRequest;
import org.apache.hadoop.hive.metastore.api.AddPartitionsResult;
import org.apache.hadoop.hive.metastore.api.CacheFileMetadataRequest;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.api.CompactionRequest;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.DropConstraintRequest;
import org.apache.hadoop.hive.metastore.api.DropPartitionsRequest;
import org.apache.hadoop.hive.metastore.api.DropPartitionsResult;
import org.apache.hadoop.hive.metastore.api.FireEventRequest;
import org.apache.hadoop.hive.metastore.api.ForeignKeysRequest;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.GrantRevokePrivilegeRequest;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.HiveObjectType;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.LockComponent;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.PartitionsByExprRequest;
import org.apache.hadoop.hive.metastore.api.PartitionsByExprResult;
import org.apache.hadoop.hive.metastore.api.PartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.PrimaryKeysRequest;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.api.SetPartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.TableMeta;
import org.apache.hadoop.hive.metastore.api.TableStatsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import jersey.repackaged.com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseMappingImplTest {

  private final static String DB_NAME = "db";
  private final static String IN_DB_NAME = "in_db";
  private final static String OUT_DB_NAME = "out_db";

  @Mock
  private MetaStoreMapping metastoreMapping;

  private DatabaseMappingImpl databaseMapping;
  private Partition partition;
  private Index index;
  private HiveObjectRef hiveObjectRef;
  private PartitionSpec partitionSpec;
  private Database database;
  private List<Partition> partitions;
  private List<HiveObjectPrivilege> hiveObjectPrivileges;

  @Before
  public void setUp() {
    databaseMapping = new DatabaseMappingImpl(metastoreMapping);
    database = new Database();
    database.setName(DB_NAME);
    partition = new Partition();
    partition.setDbName(DB_NAME);
    partitions = Lists.newArrayList(partition);
    index = new Index();
    index.setDbName(DB_NAME);
    hiveObjectRef = new HiveObjectRef();
    hiveObjectRef.setDbName(DB_NAME);
    hiveObjectRef.setObjectType(HiveObjectType.DATABASE);
    hiveObjectRef.setObjectName(DB_NAME);
    hiveObjectPrivileges = new ArrayList<>();
    HiveObjectPrivilege hiveObjectPrivilege = new HiveObjectPrivilege();
    hiveObjectPrivilege.setHiveObject(hiveObjectRef);
    hiveObjectPrivileges.add(hiveObjectPrivilege);
    partitionSpec = new PartitionSpec();
    partitionSpec.setDbName(DB_NAME);
    when(metastoreMapping.transformInboundDatabaseName(DB_NAME)).thenReturn(IN_DB_NAME);
    when(metastoreMapping.transformOutboundDatabaseName(DB_NAME)).thenReturn(OUT_DB_NAME);
  }

  @Test
  public void transformOutboundTable() throws Exception {
    Table table = new Table();
    table.setDbName(DB_NAME);
    Table result = databaseMapping.transformOutboundTable(table);
    assertThat(result, is(not(sameInstance(table))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundTable() throws Exception {
    Table table = new Table();
    table.setDbName(DB_NAME);
    Table result = databaseMapping.transformInboundTable(table);
    assertThat(result, is(not(sameInstance(table))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundPartition() throws Exception {
    Partition result = databaseMapping.transformOutboundPartition(partition);
    assertThat(result, is(not(sameInstance(partition))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundPartition() throws Exception {
    Partition result = databaseMapping.transformInboundPartition(partition);
    assertThat(result, is(not(sameInstance(partition))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundIndex() throws Exception {
    Index result = databaseMapping.transformOutboundIndex(index);
    assertThat(result, is(not(sameInstance(index))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundIndex() throws Exception {
    Index result = databaseMapping.transformInboundIndex(index);
    assertThat(result, is(not(sameInstance(index))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundFunction() throws Exception {
    Function function = new Function();
    function.setDbName(DB_NAME);
    Function result = databaseMapping.transformOutboundFunction(function);
    assertThat(result, is(not(sameInstance(function))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundHiveObjectRef() throws Exception {
    HiveObjectRef result = databaseMapping.transformInboundHiveObjectRef(hiveObjectRef);
    assertThat(result, is(not(sameInstance(hiveObjectRef))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
    assertThat(result.getObjectName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundHiveObjectRefObjectTypeIsNotDatabase() throws Exception {
    hiveObjectRef.setObjectType(HiveObjectType.TABLE);
    hiveObjectRef.setObjectName("table");
    HiveObjectRef result = databaseMapping.transformInboundHiveObjectRef(hiveObjectRef);
    assertThat(result, is(not(sameInstance(hiveObjectRef))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
    assertThat(result.getObjectName(), is("table"));
  }

  @Test
  public void transformOutboundHiveObjectRef() throws Exception {
    HiveObjectRef result = databaseMapping.transformOutboundHiveObjectRef(hiveObjectRef);
    assertThat(result, is(not(sameInstance(hiveObjectRef))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
    assertThat(result.getObjectName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformOutboundHiveObjectRefObjectTypeIsNotDatabase() throws Exception {
    hiveObjectRef.setObjectType(HiveObjectType.TABLE);
    hiveObjectRef.setObjectName("table");
    HiveObjectRef result = databaseMapping.transformOutboundHiveObjectRef(hiveObjectRef);
    assertThat(result, is(not(sameInstance(hiveObjectRef))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
    assertThat(result.getObjectName(), is("table"));
  }

  @Test
  public void transformOutboundPartitionSpec() throws Exception {
    PartitionSpec result = databaseMapping.transformOutboundPartitionSpec(partitionSpec);
    assertThat(result, is(not(sameInstance(partitionSpec))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundPartitionsStatsRequest() throws Exception {
    PartitionsStatsRequest partitionStatsRequest = new PartitionsStatsRequest();
    partitionStatsRequest.setDbName(DB_NAME);
    PartitionsStatsRequest result = databaseMapping.transformInboundPartitionsStatsRequest(partitionStatsRequest);
    assertThat(result, is(not(sameInstance(partitionStatsRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundTableStatsRequest() throws Exception {
    TableStatsRequest tableStatsRequest = new TableStatsRequest();
    tableStatsRequest.setDbName(DB_NAME);
    TableStatsRequest result = databaseMapping.transformInboundTableStatsRequest(tableStatsRequest);
    assertThat(result, is(not(sameInstance(tableStatsRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundPartitionsByExprRequest() throws Exception {
    PartitionsByExprRequest partitionsByExprRequest = new PartitionsByExprRequest();
    partitionsByExprRequest.setDbName(DB_NAME);
    PartitionsByExprRequest result = databaseMapping.transformInboundPartitionsByExprRequest(partitionsByExprRequest);
    assertThat(result, is(not(sameInstance(partitionsByExprRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundPartitionsByExprResult() throws Exception {
    PartitionsByExprResult partitionsByExprResult = new PartitionsByExprResult();
    partitionsByExprResult.setPartitions(partitions);
    PartitionsByExprResult result = databaseMapping.transformOutboundPartitionsByExprResult(partitionsByExprResult);
    assertThat(result, is(not(sameInstance(partitionsByExprResult))));
    assertPartitions(result.getPartitions(), OUT_DB_NAME);
  }

  @Test
  public void getClient() throws Exception {
    databaseMapping.getClient();
    verify(metastoreMapping).getClient();
  }

  @Test
  public void transformOutboundDatabaseName() throws Exception {
    assertThat(databaseMapping.transformOutboundDatabaseName(DB_NAME), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundDatabaseName() throws Exception {
    assertThat(databaseMapping.transformInboundDatabaseName(DB_NAME), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundDatabase() throws Exception {
    databaseMapping.transformOutboundDatabase(database);
    verify(metastoreMapping).transformOutboundDatabase(database);
  }

  @Test
  public void getDatabasePrefix() throws Exception {
    databaseMapping.getDatabasePrefix();
    verify(metastoreMapping).getDatabasePrefix();
  }

  @Test
  public void getMetastoreMappingName() throws Exception {
    databaseMapping.getMetastoreMappingName();
    verify(metastoreMapping).getMetastoreMappingName();
  }

  @Test
  public void transformInboundCacheFileMetadataRequest() throws Exception {
    CacheFileMetadataRequest cacheFileMetadataRequest = new CacheFileMetadataRequest();
    cacheFileMetadataRequest.setDbName(DB_NAME);
    CacheFileMetadataRequest result = databaseMapping
        .transformInboundCacheFileMetadataRequest(cacheFileMetadataRequest);
    assertThat(result, is(not(sameInstance(cacheFileMetadataRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundFireEventRequest() throws Exception {
    FireEventRequest fireEventRequest = new FireEventRequest();
    fireEventRequest.setDbName(DB_NAME);
    FireEventRequest result = databaseMapping.transformInboundFireEventRequest(fireEventRequest);
    assertThat(result, is(not(sameInstance(fireEventRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundForeignKeysRequest() throws Exception {
    ForeignKeysRequest foreignKeysRequest = new ForeignKeysRequest();
    foreignKeysRequest.setParent_db_name(DB_NAME);
    foreignKeysRequest.setForeign_db_name(DB_NAME);

    ForeignKeysRequest result = databaseMapping.transformInboundForeignKeysRequest(foreignKeysRequest);
    assertThat(result, is(not(sameInstance(foreignKeysRequest))));
    assertThat(result.getParent_db_name(), is(IN_DB_NAME));
    assertThat(result.getForeign_db_name(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundPrimaryKeysRequest() throws Exception {
    PrimaryKeysRequest primaryKeysRequest = new PrimaryKeysRequest();
    primaryKeysRequest.setDb_name(DB_NAME);
    PrimaryKeysRequest result = databaseMapping.transformInboundPrimaryKeysRequest(primaryKeysRequest);
    assertThat(result, is(not(sameInstance(primaryKeysRequest))));
    assertThat(result.getDb_name(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundTableMeta() throws Exception {
    TableMeta tableMeta = new TableMeta();
    tableMeta.setDbName(DB_NAME);
    TableMeta result = databaseMapping.transformOutboundTableMeta(tableMeta);
    assertThat(result, is(not(sameInstance(tableMeta))));
    assertThat(result.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundAddDynamicPartitions() throws Exception {
    AddDynamicPartitions addDynamicPartitions = new AddDynamicPartitions();
    addDynamicPartitions.setDbname(DB_NAME);
    AddDynamicPartitions result = databaseMapping.transformInboundAddDynamicPartitions(addDynamicPartitions);
    assertThat(result, is(not(sameInstance(addDynamicPartitions))));
    assertThat(result.getDbname(), is(IN_DB_NAME));
  }

  @Test
  public void close() throws Exception {
    databaseMapping.close();
    verify(metastoreMapping).close();
  }

  @Test
  public void isAvailable() throws Exception {
    databaseMapping.isAvailable();
    verify(metastoreMapping).isAvailable();
  }

  @Test
  public void checkWritePermissions() throws Exception {
    databaseMapping.checkWritePermissions(DB_NAME);
    verify(metastoreMapping).checkWritePermissions(DB_NAME);
  }

  @Test
  public void transformInboundDropConstraintRequest() throws Exception {
    DropConstraintRequest dropConstraintRequest = new DropConstraintRequest();
    dropConstraintRequest.setDbname(DB_NAME);
    DropConstraintRequest result = databaseMapping.transformInboundDropConstraintRequest(dropConstraintRequest);
    assertThat(result, is(not(sameInstance(dropConstraintRequest))));
    assertThat(result.getDbname(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundAddPartitionsRequest() throws Exception {
    AddPartitionsRequest addPartitionsRequest = new AddPartitionsRequest();
    addPartitionsRequest.setDbName(DB_NAME);
    addPartitionsRequest.setParts(partitions);
    AddPartitionsRequest result = databaseMapping.transformInboundAddPartitionsRequest(addPartitionsRequest);
    assertThat(result, is(not(sameInstance(addPartitionsRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
    assertPartitions(result.getParts(), IN_DB_NAME);
  }

  @Test
  public void transformOutboundAddPartitionsResult() throws Exception {
    AddPartitionsResult addPartitionsResult = new AddPartitionsResult();
    addPartitionsResult.setPartitions(partitions);
    AddPartitionsResult result = databaseMapping.transformOutboundAddPartitionsResult(addPartitionsResult);
    assertThat(result, is(not(sameInstance(addPartitionsResult))));
    assertPartitions(result.getPartitions(), OUT_DB_NAME);
  }

  @Test
  public void transformInboundDropPartitionRequest() throws Exception {
    DropPartitionsRequest dropPartitionsRequest = new DropPartitionsRequest();
    dropPartitionsRequest.setDbName(DB_NAME);
    DropPartitionsRequest result = databaseMapping.transformInboundDropPartitionRequest(dropPartitionsRequest);
    assertThat(result, is(not(sameInstance(dropPartitionsRequest))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transforOutboundDropPartitionsResult() throws Exception {
    DropPartitionsResult dropPartitionsResult = new DropPartitionsResult();
    dropPartitionsResult.setPartitions(partitions);
    DropPartitionsResult result = databaseMapping.transforOutboundDropPartitionsResult(dropPartitionsResult);
    assertThat(result, is(not(sameInstance(dropPartitionsResult))));
    assertPartitions(result.getPartitions(), OUT_DB_NAME);
  }

  @Test
  public void transformOutboundPartitionSpecs() throws Exception {
    List<PartitionSpec> partitionSpecs = new ArrayList<>();
    partitionSpecs.add(partitionSpec);
    List<PartitionSpec> result = databaseMapping.transformOutboundPartitionSpecs(partitionSpecs);
    assertThat(result, is(not(sameInstance(partitionSpecs))));
    PartitionSpec resultSpec = result.get(0);
    assertThat(resultSpec, is(not(sameInstance(partitionSpec))));
    assertThat(resultSpec.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformOutboundIndexes() throws Exception {
    List<Index> indexes = new ArrayList<>();
    indexes.add(index);
    List<Index> result = databaseMapping.transformOutboundIndexes(indexes);
    assertThat(result, is(not(sameInstance(indexes))));
    Index resultIndex = result.get(0);
    assertThat(resultIndex, is(not(sameInstance(index))));
    assertThat(resultIndex.getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundColumnStatistics() throws Exception {
    ColumnStatistics columnStatistics = new ColumnStatistics();
    ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
    statsDesc.setDbName(DB_NAME);
    columnStatistics.setStatsDesc(statsDesc);
    ColumnStatistics result = databaseMapping.transformInboundColumnStatistics(columnStatistics);
    assertThat(result, is(not(sameInstance(columnStatistics))));
    assertThat(result.getStatsDesc(), is(not(sameInstance(columnStatistics.getStatsDesc()))));
    assertThat(result.getStatsDesc().getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundColumnStatistics() throws Exception {
    ColumnStatistics columnStatistics = new ColumnStatistics();
    ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
    statsDesc.setDbName(DB_NAME);
    columnStatistics.setStatsDesc(statsDesc);
    ColumnStatistics result = databaseMapping.transformOutboundColumnStatistics(columnStatistics);
    assertThat(result, is(not(sameInstance(columnStatistics))));
    assertThat(result.getStatsDesc(), is(not(sameInstance(columnStatistics.getStatsDesc()))));
    assertThat(result.getStatsDesc().getDbName(), is(OUT_DB_NAME));
  }

  @Test
  public void transformInboundSetPartitionStatsRequest() throws Exception {
    SetPartitionsStatsRequest setPartitionsStatsRequest = new SetPartitionsStatsRequest();
    ColumnStatistics columnStatistics = new ColumnStatistics();
    ColumnStatisticsDesc statsDesc = new ColumnStatisticsDesc();
    statsDesc.setDbName(DB_NAME);
    columnStatistics.setStatsDesc(statsDesc);
    setPartitionsStatsRequest.setColStats(Lists.newArrayList(columnStatistics));
    ;
    SetPartitionsStatsRequest result = databaseMapping
        .transformInboundSetPartitionStatsRequest(setPartitionsStatsRequest);
    assertThat(result, is(not(sameInstance(setPartitionsStatsRequest))));
    ColumnStatistics resultColStats = result.getColStats().get(0);
    assertThat(resultColStats, is(not(sameInstance(columnStatistics))));
    assertThat(resultColStats.getStatsDesc(), is(not(sameInstance(statsDesc))));
    assertThat(resultColStats.getStatsDesc().getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundSetPartitionStatsRequestNoColumnStats() throws Exception {
    SetPartitionsStatsRequest setPartitionsStatsRequest = new SetPartitionsStatsRequest();
    SetPartitionsStatsRequest result = databaseMapping
        .transformInboundSetPartitionStatsRequest(setPartitionsStatsRequest);
    assertThat(result, is(not(sameInstance(setPartitionsStatsRequest))));
    assertFalse(result.isSetColStats());
  }

  @Test
  public void transformInboundFunction() throws Exception {
    Function function = new Function();
    function.setDbName(DB_NAME);
    Function result = databaseMapping.transformInboundFunction(function);
    assertThat(result, is(not(sameInstance(function))));
    assertThat(result.getDbName(), is(IN_DB_NAME));
  }

  @Test
  public void transformOutboundHiveObjectPrivileges() throws Exception {
    List<HiveObjectPrivilege> result = databaseMapping.transformOutboundHiveObjectPrivileges(hiveObjectPrivileges);
    assertHiveObjectPrivileges(result, OUT_DB_NAME);
  }

  @Test
  public void transformInboundPrivilegeBag() throws Exception {
    PrivilegeBag privilegeBag = new PrivilegeBag();
    privilegeBag.setPrivileges(hiveObjectPrivileges);
    PrivilegeBag result = databaseMapping.transformInboundPrivilegeBag(privilegeBag);
    assertThat(result, is(not(sameInstance(privilegeBag))));
    assertHiveObjectPrivileges(result.getPrivileges(), IN_DB_NAME);
  }

  @Test
  public void transformInboundPrivilegeBagPriviligesNotSet() throws Exception {
    PrivilegeBag privilegeBag = new PrivilegeBag();
    PrivilegeBag result = databaseMapping.transformInboundPrivilegeBag(privilegeBag);
    assertThat(result, is(not(sameInstance(privilegeBag))));
    assertFalse(result.isSetPrivileges());
  }

  @Test
  public void transformInboundGrantRevokePrivilegesRequest() throws Exception {
    GrantRevokePrivilegeRequest grantRevokePrivilegeRequest = new GrantRevokePrivilegeRequest();
    PrivilegeBag privilegeBag = new PrivilegeBag();
    privilegeBag.setPrivileges(hiveObjectPrivileges);
    grantRevokePrivilegeRequest.setPrivileges(privilegeBag);
    GrantRevokePrivilegeRequest result = databaseMapping
        .transformInboundGrantRevokePrivilegesRequest(grantRevokePrivilegeRequest);
    assertThat(result, is(not(sameInstance(grantRevokePrivilegeRequest))));
    PrivilegeBag resultPriviligeBag = result.getPrivileges();
    assertThat(resultPriviligeBag, is(not(sameInstance(privilegeBag))));
    assertHiveObjectPrivileges(resultPriviligeBag.getPrivileges(), IN_DB_NAME);
  }

  @Test
  public void transformInboundGrantRevokePrivilegesRequestPriviligeBagNotSet() throws Exception {
    GrantRevokePrivilegeRequest grantRevokePrivilegeRequest = new GrantRevokePrivilegeRequest();
    GrantRevokePrivilegeRequest result = databaseMapping
        .transformInboundGrantRevokePrivilegesRequest(grantRevokePrivilegeRequest);
    assertThat(result, is(not(sameInstance(grantRevokePrivilegeRequest))));
    assertFalse(result.isSetPrivileges());
  }

  @Test
  public void transformInboundLockRequest() throws Exception {
    LockRequest lockRequest = new LockRequest();
    LockComponent lockComponent = new LockComponent();
    lockComponent.setDbname(DB_NAME);
    List<LockComponent> components = Lists.newArrayList(lockComponent);
    lockRequest.setComponent(components);
    LockRequest result = databaseMapping.transformInboundLockRequest(lockRequest);
    assertThat(result, is(not(sameInstance(lockRequest))));
    List<LockComponent> resultComponents = result.getComponent();
    assertThat(resultComponents, is(not(sameInstance(components))));
    LockComponent resultComponent = resultComponents.get(0);
    assertThat(resultComponent, is(not(sameInstance(lockComponent))));
    assertThat(resultComponent.getDbname(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundLockRequestLockComponentsNotSet() throws Exception {
    LockRequest lockRequest = new LockRequest();
    LockRequest result = databaseMapping.transformInboundLockRequest(lockRequest);
    assertThat(result, is(not(sameInstance(lockRequest))));
    assertFalse(result.isSetComponent());
  }

  @Test
  public void transformInboundCompactionRequest() throws Exception {
    CompactionRequest compactionRequest = new CompactionRequest();
    compactionRequest.setDbname(DB_NAME);
    CompactionRequest result = databaseMapping.transformInboundCompactionRequest(compactionRequest);
    assertThat(result, is(not(sameInstance(compactionRequest))));
    assertThat(result.getDbname(), is(IN_DB_NAME));
  }

  @Test
  public void createDatabase() throws Exception {
    databaseMapping.createDatabase(database);
    verify(metastoreMapping).createDatabase(database);
  }

  @Test
  public void transformInboundDatabase() throws Exception {
    Database result = databaseMapping.transformInboundDatabase(database);
    assertThat(result, is(not(sameInstance(database))));
    assertThat(result.getName(), is(IN_DB_NAME));
  }

  @Test
  public void transformInboundPartitionSpecs() throws Exception {
    List<PartitionSpec> partitionSpecs = Lists.newArrayList(partitionSpec);
    List<PartitionSpec> result = databaseMapping.transformInboundPartitionSpecs(partitionSpecs);
    assertThat(result, is(not(sameInstance(partitionSpecs))));
    PartitionSpec resultSpec = result.get(0);
    assertThat(resultSpec, is(not(sameInstance(partitionSpec))));
    assertThat(resultSpec.getDbName(), is(IN_DB_NAME));
  }

  private void assertHiveObjectPrivileges(List<HiveObjectPrivilege> result, String expectedDatabaseName) {
    assertThat(result, is(not(sameInstance(hiveObjectPrivileges))));
    HiveObjectPrivilege resultPrivilege = result.get(0);
    assertThat(resultPrivilege, is(not(sameInstance(hiveObjectPrivileges.get(0)))));
    HiveObjectRef resultHiveObjectRef = resultPrivilege.getHiveObject();
    assertThat(resultHiveObjectRef, is(not(sameInstance(hiveObjectRef))));
    assertThat(resultHiveObjectRef.getDbName(), is(expectedDatabaseName));
    assertThat(resultHiveObjectRef.getObjectName(), is(expectedDatabaseName));
  }

  private void assertPartitions(List<Partition> partitions, String expectedDatabaseName) {
    assertThat(partitions.size(), is(1));
    Partition partitionResult = partitions.get(0);
    assertThat(partitionResult, is(not(sameInstance(partition))));
    assertThat(partitionResult.getDbName(), is(expectedDatabaseName));
  }

}