#!/bin/bash
. ./conf.sh

#################################################
################# mapping #######################
#################################################
#curl -X PUT localhost:9200/gccount/ --data-binary @customer_mapping.json; echo

#echo
#echo "$type mapping created"
#echo

#exit 0
#####################################################
################### document ########################
#####################################################

curl -XPOST "http://localhost:9200/_bulk" --data-binary @customers.json; echo

echo
echo "$typeCustomer document indexed"
echo

