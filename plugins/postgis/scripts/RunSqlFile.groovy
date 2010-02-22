includeTargets << grailsScript("Init")

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

  def fileName

  if ( !args )
  {
    Ant.input(addProperty: "file.name", message: "Please enter the name of the file to run:")
    fileName = Ant.antProject.properties."file.name"
  }
  else
  {
    fileName = args
  }

  if ( (fileName as File)?.exists() )
  {
    new AntBuilder().sql(
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
