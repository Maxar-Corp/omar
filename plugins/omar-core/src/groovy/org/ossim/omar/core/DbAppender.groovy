package org.ossim.omar.core

import java.sql.DatabaseMetaData
import java.sql.Connection

import org.apache.commons.lang.StringUtils

import org.ossim.omar.core.ISO8601DateParser

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.lang.StringUtils
import java.sql.Timestamp

public class DbAppender
{
  def sqlStatement
  def dateFieldList
  def castFieldList
  def tableMapping
  def tableName
  def sql
  def modifyParametersClosure
  def sqlParams

  void append(def params)
  {
    if ( !sql )
    {
      sql = ApplicationHolder.application.mainContext.getBean("sql")
      initializeSqlStatement()
      initializeMappings()
      println sqlStatement
    }

    sqlParams = [:]

    tableMapping.each { k, v ->
      def propertyName = computePropertyName(k)
      if ( params[propertyName] )
      {
        sqlParams[propertyName] = params[propertyName]
      }
    }

    castFieldList?.each {  castField ->
      if ( castField.cast && castField.field )
      {
        sqlParams[castField.field] = castField.cast(sqlParams[castField.field])
      }
    }

    dateFieldList?.each {field ->

      def dateTime = ISO8601DateParser.parseDateTime(sqlParams[field])
      sqlParams[field] = new Timestamp(dateTime.millis)
    }

    if ( modifyParametersClosure )
    {
      sqlParams = modifyParametersClosure(sqlParams)
    }

    println sqlParams

    try
    {
      if ( sqlStatement )
      {
        sql.execute(sqlStatement, sqlParams)
      }
    }
    catch (Exception e)
    {
      println e
    }
  }

  void initializeSqlStatement()
  {
    sqlStatement = ""
    if ( tableMapping.size() > 0 )
    {
      def keys = []
      def values = []
      tableMapping.each {k, v ->
        keys += ["${k}"]
        values += ["${v}"]
      }

      sqlStatement = "INSERT INTO ${tableName} ( ${keys.join(', ')} ) VALUES ( ${values.join(", ")} )"
    }
  }

  def computePropertyName(def columnName)
  {
    def x = columnName?.split('_')

    if ( x.size() > 1 )
    {
      def y = x[0]

      y += x[1..-1].collect { StringUtils.capitalize(it) }.join('')
      x = y
    }
    else
    {
      x = x.first()
    }

    return x
  }


  void initializeMappings()
  {
    try
    {
      dateFieldList = []
      Connection conn = sql.createConnection()
      DatabaseMetaData meta = conn?.getMetaData();
      def rsColumns = meta?.getColumns(null, null, tableName, null);

      while ( rsColumns?.next() )
      {
        def columnName = rsColumns.getString("COLUMN_NAME").toLowerCase()
        columnName = columnName.replaceAll("_[a-zA-Z]", {value -> value[1].toUpperCase()})
        switch ( rsColumns.getString("TYPE_NAME").toLowerCase() )
        {
        case "timestamp":
          dateFieldList += [columnName]
          castFieldList << [field: columnName, cast: {value ->
            def dateTime = ISO8601DateParser.parseDateTime(sqlParams[columnName])
            new java.sql.Timestamp(dateTime.millis)
          }]
          break
        case "int8":
          castFieldList << [field: columnName, cast: {value ->
            Long.parseLong(value)
          }]
          break;
        case "float8":
          castFieldList << [field: columnName, cast: {value ->
            if ( value instanceof String )
              Double.parseDouble(value)
            else value
          }]
          break;
        default:
          //castFieldList << [field:columnName, cast:{value->value}]
          //    println rsColumns.getString("COLUMN_NAME").toLowerCase()+ ", " +
          //            rsColumns.getString("TYPE_NAME").toLowerCase()

          break
        }
      }
      conn?.close()
    }
    catch (Exception e)
    {
      println e
    }
  }
}
