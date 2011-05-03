import groovy.sql.Sql

Ant.property(environment: "env")

grailsHome = Ant.project.properties."environment.GRAILS_HOME"

def loadConfig()
{
  def dataSourceFile = "${basedir}/grails-app/conf/DataSource.groovy" as File
  def applicationPropertiesFile = "${basedir}/application.properties" as File
  def configSlurper = new ConfigSlurper(grailsEnv)
  def applicationProperties = new Properties()

  applicationProperties.load(applicationPropertiesFile.newReader())
  configSlurper.binding = [appVersion: applicationProperties['app.version'] ?: '']

  def config = configSlurper.parse(dataSourceFile.toURL())

  return config
}

target(main: "Add a Geometry column to a table") {

  //def config = new ConfigSlurper(grailsEnv).parse(new File("${basedir}/grails-app/conf/DataSource.groovy").toURL())
  def config = loadConfig()

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

  Ant.input(addProperty: "srid.name", message: "Please enter the srid:", defaultvalue: "4326")
  def srid = Ant.antProject.properties."srid.name".toInteger()

  def typeNames = [
      "POINT", "MULTIPOINT",
      "LINESTRING", "MULTILINESTRING",
      "POLYGON", "MULTIPOLYGON",
      "CIRCULARSTRING", "COMPOUNDCURVE", "MULTICURVE",
      "CURVEPOLYGON", "MULTISURFACE",
      "GEOMETRY", "GEOMETRYCOLLECTION",
      "POINTM", "MULTIPOINTM",
      "LINESTRINGM", "MULTILINESTRINGM",
      "POLYGONM", "MULTIPOLYGONM",
      "CIRCULARSTRINGM", "COMPOUNDCURVEM", "MULTICURVEM",
      "CURVEPOLYGONM", "MULTISURFACEM",
      "GEOMETRYCOLLECTIONM"
  ].sort()

  def typeMap = [:]

  typeNames.eachWithIndex {name, index ->
    typeMap[index + 1] = name
  }

  def typeMessage = "Please enter the geometry type:\n\n"

  typeMap.each {k, v ->
    typeMessage += "\t${k}: ${v}\n"
  }

  typeMessage += "\n"

  Ant.input(
      addProperty: "type.id",
      message: typeMessage,
      validargs: typeMap.keySet().sort().join(",")
  )

  def typeIndex = Ant.antProject.properties."type.id" as Integer
  def typeName = typeNames[typeIndex - 1]

  Ant.input(
      addProperty: "dimension.num",
      message: "Please enter the number of dimensions:",
      validargs: ["2", "3"].join(","),
      defaultvalue: "2"
  )

  def dimenions = Ant.antProject.properties."dimension.num".toInteger()


  String[] sqlStmts = [
      "ALTER TABLE ${tableName} DROP COLUMN ${columnName};" as String,
      "SELECT AddGeometryColumn( '${tableName}', '${columnName}', ${srid}, '${typeName}', ${dimenions} );" as String,
      "CREATE INDEX  ${tableName}_${columnName}_idx ON ${tableName} USING GIST ( ${columnName} GIST_GEOMETRY_OPS );" as String,
      "VACUUM ANALYZE ${tableName} ( ${columnName} );" as String
  ]

  def sql = Sql.newInstance(
      config.dataSource.url,
      config.dataSource.username,
      config.dataSource.password,
      config.dataSource.driverClassName
  )

  sqlStmts.each {sqlText ->
    try
    {
      sql.execute(sqlText)
    }
    catch (Exception e)
    {
      println sqlText
      println e.message
    }
  }

  sql.close()
}

setDefaultTarget(main)
