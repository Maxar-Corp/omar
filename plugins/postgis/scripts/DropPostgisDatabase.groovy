import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

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

target(main: "Drop the PostGIS database instance") {
  //def config = new ConfigSlurper(grailsEnv).parse(new File("${basedir}/grails-app/conf/DataSource.groovy").toURL())
  def config = loadConfig()

  def databaseName = config.dataSource.url.split(":")[-1]
  def pgHome = Ant.antProject.properties."env.PG_HOME"
  def postgisHome = Ant.antProject.properties."env.PG_HOME"

  if ( !pgHome )
  {
    System.err.println("PG_HOME environment not set")
    System.exit(-1)
  }

  if ( !postgisHome )
  {
    System.err.println("POSTGIS_HOME environment not set")
    System.exit(-1)
  }



  Ant.exec(executable: "${pgHome}/bin/dropdb")
      {
        arg(value: "-U")
        arg(value: "${config.dataSource.username}")
        arg(value: "${databaseName}")
      }

}

setDefaultTarget(main)
