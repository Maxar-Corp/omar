package org.ossim.omar

import java.sql.DatabaseMetaData
import java.sql.Connection

//import org.codehaus.groovy.grails.commons.*
//import grails.converters.JSON
public class DbAppender extends org.apache.log4j.AppenderSkeleton

{
  private String sqlStatement = ""
  private def dateFieldList
  protected def tableMapping = [:]
  protected String tableName = ""
  def sql = null
  def modifyParametersClosure = null
  void append(org.apache.log4j.spi.LoggingEvent event)
  {
    if(!sql)
    {
      sql = org.codehaus.groovy.grails.commons.ApplicationHolder.application.mainContext.getBean("sql")
      initializeMappings()
    }
    def params = (grails.converters.JSON.parse(event.message) as Map)
    if(modifyParametersClosure)
    {
      params = modifyParametersClosure(params)
    }
    else
    {
       dateFieldList?.each{field->
         
         def dateTime  = org.ossim.omar.ISO8601DateParser.parseDateTime(params."${field}")
         params."${field}" =  new java.sql.Timestamp(dateTime.millis)
       }
    }
    try
    {
      if(sqlStatement)
      {
        sql.execute(sqlStatement, params)
      }
    }
    catch(Exception e)
    {
       println e
    }
  }
  public void activateOptions()
  {
    this.closed=false
    initializeSqlStatement()

  }

  void initializeSqlStatement()
  {
    sqlStatement = ""
    if(tableMapping.size() > 0)
    {
      def keys = []
      def values = []
      tableMapping.each{k,v->
        keys += ["${k}"]
        values += ["${v}"]
      }

      sqlStatement =  "INSERT INTO ${tableName}("  + keys.join(",") + ") VALUES (" + values.join(",") + ")"
    }
  }
  void initializeMappings()
  {
    try
    {
      dateFieldList = []
      Connection conn = sql.createConnection()
      DatabaseMetaData meta = conn?.getMetaData();
      def rsColumns = meta?.getColumns(null, null, "wms_log", null);
      while (rsColumns?.next())
      {
        switch(rsColumns.getString("TYPE_NAME").toLowerCase())
        {
          case "timestamp":
            def columnName =  rsColumns.getString("COLUMN_NAME").toLowerCase()
            columnName = columnName.replaceAll("_[a-zA-Z]", {value->value[1].toUpperCase()})
            dateFieldList += [columnName]
            break

        }
      }
      conn?.close()
    }
    catch(Exception e)
    {
      println e
    }
  }
  boolean requiresLayout()
  {
    false
  }
  void close()
  {
    this.closed = true;
  }
}
