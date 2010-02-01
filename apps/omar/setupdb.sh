#!/bin/sh

case "$1" in
dev)
export mode="dev"
;;
test)
export mode="test"
;;
*)
export mode="prod"
;;
esac

grails ${mode} drop-postgis-database
grails ${mode} create-postgis-database
grails ${mode} schema-export ./ddl.sql
grails ${mode} run-sql-file ddl.sql
grails ${mode} run-sql-file geoms.sql

