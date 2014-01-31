type="Customer"

#################################################
################# mapping #######################
#################################################
curl -X PUT localhost:9200/gccount/Customer/_mapping -d '{
    "Customer" : { "properties"  : { 
                    "customerId" : { "type":"String" }, 
                    "firstName"  : { "type":"String" }, 
                    "middleName" : { "type":"String" }, 
                    "lastName"   : { "type":"String" }, 
                    "balance"   : { "type":"Double" }, 
                    "created": {
                         "type" : "date",
                         "format" : "yyyy-MM-dd HH:mm:ss"
                     },
		     "transactions": {
                       "type": "nested",
                       "include_in_root": true,
                       "properties": {
                          "transactionId": {
                             "type" : "string"
                           },                           
                          "createdDate": {
                             "type" : "date",
                             "format" : "dateOptionalTime"
                          },
                          "amount": {
                             "type" : "Double"
                          },
                          "status": {
                              "type" : "string"
                          }
                      } 
	           }
              }
      }
}'

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

