#!/bin/bash
 
EXPECTED_ARGS=0
E_BADARGS=65
MYSQL=`which mysql`
DATABASE_NAME="cashless" #$1
 
Q1="CREATE DATABASE IF NOT EXISTS $DATABASE_NAME;"
#Q2="GRANT ALL ON *.* TO '$2'@'localhost' IDENTIFIED BY '$3';"
#Q3="FLUSH PRIVILEGES;"
SQL="${Q1}"
 
if [ $# -ne $EXPECTED_ARGS ]
then
  echo "Usage: $0 dbname"
  exit $E_BADARGS
fi
 
$MYSQL -uroot -p -e "$SQL"

echo "DATABASE $DATABASE_NAME CONFIGURED"
