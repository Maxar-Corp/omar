#!/usr/bin/env groovy


@Grapes([
    @GrabConfig(systemClassLoader=true),
    @Grab(group='postgresql', module='postgresql', version='8.4-702.jdbc4')
])

import groovy.sql.Sql


def cli = new CliBuilder(usage: 'checkFiles.sh [full path to omar server] [database url connection]')
def options = cli.parse(args)
if(!options)
{
   return 1
}
def arguments = options.arguments();
if(arguments.size() != 2)
{
   cli.usage()
   return 0
}
def omarAppServerUrl = arguments[0]
def database = arguments[1]
def db = [url:"jdbc:postgresql:${database}", user:'postgres', password:'postgres', driver:'org.postgresql.Driver']
def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
println "Omar url to use: " + omarAppServerUrl
println "Database Server connection string: " + db
sql.eachRow("SELECT name FROM raster_file") { row ->
	def file = new File(row.name)

	if ( ! file.exists() )
	{
	//	println "removing file ${row.name}"
		def output = "curl --data filename=${row.name} ${omarAppServerUrl}/dataManager/removeRaster".execute();
                println output.text //output.consumeProcessOutput()
		output.waitFor()
	}
}
 
sql.close()
