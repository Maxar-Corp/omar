package org.ossim.omar

//import org.codehaus.groovy.grails.commons.*
//import grails.converters.JSON
public class DbAppender extends org.apache.log4j.AppenderSkeleton

{
  private String sqlStatement = ""
  protected def tableMapping = [:]
  protected String tableName = ""
  def sql = null
  def modifyParametersClosure
  def shell
  void append(org.apache.log4j.spi.LoggingEvent event)
  {
    if(!sql)
    {
      sql = org.codehaus.groovy.grails.commons.ApplicationHolder.application.mainContext.getBean("sql")
      shell = new GroovyShell();
    }
    def params = shell.evaluate (event.message)
    def newParams = modifyParametersClosure(params)
    try
    {
      if(sqlStatement)
      {
        sql.execute(sqlStatement, newParams)
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
  boolean requiresLayout()
  {
    false
  }
  void close()
  {
    this.closed = true;
  }
}
