import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

Ant.property(environment: "env")

grailsHome = Ant.project.properties."environment.GRAILS_HOME"

target('default': "The description of the script goes here!") {
  doStuff()
}

target(doStuff: "The implementation task") {
  def config = new ConfigSlurper(grailsEnv).parse(new File("${basedir}/grails-app/conf/DataSource.groovy").toURL())

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