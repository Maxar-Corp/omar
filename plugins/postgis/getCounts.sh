#!/usr/bin/env groovy

def addToClasspath( def jarFile )
{
    this.class.classLoader.rootLoader.addURL( jarFile.toURI()?.toURL() )
}

def baseDir = "${System.env['OMAR_HOME']}/../../plugins/postgis/lib" as File

baseDir.eachFile { file -> addToClasspath( file ) }

//println("Hello world")

//for (a in this.args) {
//  println("Argument: " + a)
//}

if ( args.size() < 1 )
{
	println "Usage: getCounts.sh <dbname>"
	System.exit(-1)
}

import groovy.sql.Sql

def sql = Sql.newInstance(
    "jdbc:postgresql_postGIS:${args[0]}",
    "postgres",
    "postgres",
    "org.postgis.DriverWrapper"
    )


def getTableNames( def sql )
{
    def rs = sql.connection.metaData.getTables(null, null, "%", ["TABLE"] as String[])
    def tableNames = []

    while ( rs.next() )
    {
        tableNames << rs.getString( 3 )
    }

    rs.close()

    return tableNames
}

def tableNames = getTableNames( sql )

tableNames.each { tableName ->
    def txt = "SELECT count(*) FROM ${tableName}".toString()

    sql.eachRow(txt, {
        def count = it.toRowResult()[0]

        println "${tableName}: ${count}"
    } )
}

sql.close()

return 0

