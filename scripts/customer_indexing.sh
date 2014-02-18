type="Customer"

#################################################
################# mapping #######################
#################################################
curl -X PUT localhost:9200/gccount/ --data-binary @customer_mapping.json; echo

echo
echo "$type mapping created"
echo


#####################################################
################### document ########################
#####################################################

curl -XPOST "http://localhost:9200/_bulk" --data-binary @customers.json; echo

echo
echo "$type document indexed"
echo

