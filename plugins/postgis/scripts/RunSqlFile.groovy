includeTargets << grailsScript("Init")
includeTargets << grailsScript("Package")
includeTargets << grailsScript("Bootstrap")

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

target(main: "Execute an SQL file") {
  depends(parseArguments)
  //def config = new ConfigSlurper(grailsEnv).parse(new File("${basedir}/grails-app/conf/DataSource.groovy").toURL())
  def config = loadConfig()

  /*
  println "${config.dataSource.driverClassName}"
  println "${config.dataSource.username}"
  println "${config.dataSource.password}"
  println "${config.dataSource.url}"
  */

  def fileName
  def databaseName = config.dataSource.url.split(":")[-1]

  def pgHome = Ant.antProject.properties."env.PG_HOME"
  if ( !pgHome )
  {
    System.err.println("PG_HOME environment not set")
    System.exit(-1)
  }
 
  if ( !argsMap?.params )
  {
    Ant.input(addProperty: "file.name", message: "Please enter the name of the file to run:")
    fileName = Ant.antProject.properties."file.name"
  }
  else
  {
    fileName = argsMap?.params[0]
  }

  if ( (fileName as File)?.exists() )
  {
    /*
    Ant.exec(executable: "${pgHome}/bin/psql")
        {
          arg(value: "-U")
          arg(value: "${config.dataSource.username}")
          arg(value: "-d")
          arg(value: "${databaseName}")
          arg(value: "-f")
          arg(value: "${fileName}")
        }
        */

    Ant.sql(
        driver: config.dataSource.driverClassName,
        url: config.dataSource.url,
        userid: config.dataSource.username,
        password: config.dataSource.password,
        src: fileName,
        onerror: "continue",
        autocommit: true
    )
    
  }
  else
  {
    System.err.println("Cannot find file: ${fileName}")
    System.exit(-1)
  }
}

setDefaultTarget(main)
