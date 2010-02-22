import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

import groovy.sql.Sql

Ant.property(environment: "env")

grailsHome = Ant.project.properties."environment.GRAILS_HOME"

target('default': "The description of the script goes here!") {
  doStuff()
}

target(doStuff: "The implementation task") {
  def config = new ConfigSlurper(grailsEnv).parse(new File("${basedir}/grails-app/conf/DataSource.groovy").toURL())

  /*
  println "${config.dataSource.driverClassName}"
  println "${config.dataSource.username}"
  println "${config.dataSource.password}"
  println "${config.dataSource.url}"
  */

  Ant.input(addProperty: "table.name", message: "Please enter the table name:")
  def tableName = Ant.antProject.properties."table.name"

  Ant.input(addProperty: "column.name", message: "Please enter the column name:")
  def columnName = Ant.antProject.properties."column.name"

  def sqlText = """
  SELECT DropGeometryColumn( '${tableName}', '${columnName}' );
  """ as String

  // DROP INDEX ${tableName}_${columnName}_idx;
  // println sqlText

  def sql = Sql.newInstance(
      config.dataSource.url,
      config.dataSource.username,
      config.dataSource.password,
      config.dataSource.driverClassName
  )

  sql.execute(sqlText)
  sql.close()
}