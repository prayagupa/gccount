#!/bin/bash
. ./conf.sh

curl -XDELETE "http://localhost:9200/$index/$typeCustomer/_mapping"
echo "customer mapping deleted"

curl -XDELETE "http://localhost:9200/gccount/Customer/"
echo "customer deleted"
echo

curl -XDELETE 'http://localhost:9200/gccount/'
echo "gccount index deleted"
echo

#curl -XDELETE 'http://localhost:9200/g/'
#echo "index alias deleted"
#echo
