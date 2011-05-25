package org.ossim.omar

import java.sql.DatabaseMetaData
import java.sql.Connection

/**
 *  Example log4j:
 * 
 *     appender new org.ossim.omar.DbAppender(  name: "wmsLoggingAppender",
 *           threshold: org.apache.log4j.Level.INFO,
 *           tableMapping: [width:":width", height: ":height", layers:":layers", styles:":styles",
 *                          format: ":format", request:":request", bbox:":bbox", internal_time:":internalTime",
 *                          render_time:":renderTime", total_time:":totalTime", start_date:":startDate",
 *                          end_date:":endDate", user_name:":userName", ip:":ip", url:":url", mean_gsd:":meanGsd",
 *                          geometry: "ST_GeomFromText(:geometry, 4326)"],
 *           tableName: "wms_log"
 *           )
 *
 *
 * Now to log the filter:
 *
 *      info wmsLoggingAppender: 'grails.app.service.org.ossim.omar.WmsLogService',
 *                                additivity: true
 *
 * There is a default massaging of the data type that can handle dates but if you want to do your own massaging
 * there is an attribute called:
 *
 *  modifyParametersClosure
 *
 *     appender new org.ossim.omar.DbAppender(  name: "wmsLoggingAppender",
 *           threshold: org.apache.log4j.Level.INFO,
 *           tableMapping: [width:":width", height: ":height", layers:":layers", styles:":styles",
 *                          format: ":format", request:":request", bbox:":bbox", internal_time:":internalTime",
 *                          render_time:":renderTime", total_time:":totalTime", start_date:":startDate",
 *                          end_date:":endDate", user_name:":userName", ip:":ip", url:":url", mean_gsd:":meanGsd",
 *                          geometry: "ST_GeomFromText(:geometry, 4326)"],
 *           tableName: "wms_log"
 *           modifyParametersClosure: {map->
 *                        // add any map adjustements here
 *                        // this is the routed message in a HashMap format that will be passed
 *                        // to the sql insert command
 *                        map // return result
 *                     }
 *    
 *           )
 */
public class DbAppender extends org.apache.log4j.AppenderSkeleton

{
    private String sqlStatement = ""
    private def dateFieldList
    private def castFieldList = [[:]]
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
      castFieldList?.each{  castField->
            params."${castField.field}" = castField.cast(params."${castField.field}")
      }
   //   dateFieldList?.each{field->

    //     def dateTime  = org.ossim.omar.ISO8601DateParser.parseDateTime(params."${field}")
    //     params."${field}" =  new java.sql.Timestamp(dateTime.millis)
    //   }
    if(modifyParametersClosure)
    {
      //params = modifyParametersClosure(params)
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
            castFieldList << [field:columnName, cast:{value->
                                      def dateTime = org.ossim.omar.ISO8601DateParser.parseDateTime(params."${field}")
                                      new java.sql.Timestamp(dateTime.millis)
                                      }]
            break
            default:
               // println rsColumns.getString("COLUMN_NAME").toLowerCase()+ ", " +
               //         rsColumns.getString("TYPE_NAME").toLowerCase()

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
