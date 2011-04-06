#!/bin/sh
ldapsearch -x -D "cn=Administrator,dc=otd,dc=radiantblue,dc=com" -w omarldap -p 389 -h sles11-ldap-server -s base -b "uid=$1,ou=people,dc=otd,dc=radiantblue,dc=com" "objectclass=*"
