package org.ossim.omar

//import org.codehaus.groovy.grails.commons.*
//import grails.converters.JSON
public class DbAppender extends org.apache.log4j.AppenderSkeleton

{
  protected String sqlStatement = ""
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
      sql.execute(sqlStatement, newParams)
    }
    catch(Exception e)
    {
       println e
    }
  }
  public void activateOptions()
  {
    this.closed=false
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
