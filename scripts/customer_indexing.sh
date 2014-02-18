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

#curl -X POST "http://localhost:9200/gccount/Customer" -d '{"customerId" : "01",   "firstName" : "Prayag", "middleName" : "", "lastName" : "Upd", "balance":100.90, "created" : "2013-10-28 00:00:00"}'

curl -XPOST "http://localhost:9200/_bulk" --data-binary @customers.json; echo

echo
echo "$type document indexed"
echo

