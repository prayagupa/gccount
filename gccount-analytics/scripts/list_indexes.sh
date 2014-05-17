#!/bin/bash
. ./conf.sh

curl -XGET $host/_aliases?pretty=true
echo;
