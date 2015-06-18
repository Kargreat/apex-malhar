/**
 * Copyright (c) 2015 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.contrib.hbase;

import java.util.List;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import com.datatorrent.contrib.common.FieldValueGenerator;
import com.datatorrent.contrib.common.FieldValueGenerator.FieldValueHandler;
import com.datatorrent.contrib.common.TableInfo;
import com.datatorrent.lib.util.PojoUtils;
import com.datatorrent.lib.util.PojoUtils.Getter;

/**
 * 
 * @displayName HBase Put Output
 * @category Database
 * @tags hbase put, output operator, Pojo
 */
public class HBasePojoPutOperator extends AbstractHBasePutOutputOperator<Object>
{
  private static final long serialVersionUID = 3241368443399294019L;

  private TableInfo<HBaseFieldInfo> tableInfo;

  private transient FieldValueGenerator<HBaseFieldInfo> fieldValueGenerator;

  private transient Getter<Object, String> rowGetter;
  private transient  HBaseFieldValueHandler valueHandler = new HBaseFieldValueHandler();
      
  @Override
  public Put operationPut(Object obj)
  {
    final List<HBaseFieldInfo> fieldsInfo = tableInfo.getFieldsInfo();
    if (fieldValueGenerator == null) {
      fieldValueGenerator = FieldValueGenerator.getFieldValueGenerator(obj.getClass(), fieldsInfo);
    }
    if (rowGetter == null) {
      // use string as row id
      rowGetter = PojoUtils.createGetter(obj.getClass(), tableInfo.getRowOrIdExpression(), String.class);
    }

    valueHandler.newRecord( Bytes.toBytes(rowGetter.get(obj) ) );
    fieldValueGenerator.handleFieldsValue(obj, valueHandler );
    return valueHandler.getPut();
  }

  /**
   * HBase table information
   */
  public TableInfo<HBaseFieldInfo> getTableInfo()
  {
    return tableInfo;
  }

  /**
   * HBase table information
   */
  public void setTableInfo(TableInfo<HBaseFieldInfo> tableInfo)
  {
    this.tableInfo = tableInfo;
  }

  
  public static class HBaseFieldValueHandler implements FieldValueHandler<HBaseFieldInfo>
  {
    private Put put;
    public HBaseFieldValueHandler()
    {
    }

    public void newRecord( byte[] rowId )
    {
      put = new Put( rowId ); 
    }

    @Override
    public void handleFieldValue(HBaseFieldInfo fieldInfo, Object value)
    {
      put.add(Bytes.toBytes(fieldInfo.getFamilyName()), Bytes.toBytes(fieldInfo.getColumnName()), fieldInfo.toBytes( value ));
    }
    
    public Put getPut()
    {
      return put;
    }

  }
}
