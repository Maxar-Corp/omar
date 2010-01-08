#!/bin/sh
grails prod drop-postgis-database
grails prod create-postgis-database
grails prod schema-export ./ddl.sql
grails prod run-sql-file ddl.sql
grails prod run-sql-file geoms.sql

