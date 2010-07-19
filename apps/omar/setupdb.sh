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

#psql84 -U postgres -d omardb-2.0-${mode} -f ddl.sql
#psql84 -U postgres -d omardb-2.0-${mode} -f geoms.sql
