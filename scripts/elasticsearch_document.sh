
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
                     }
                 } 
              }
}'

echo "mapping created"

curl -X POST "http://localhost:9200/gccount/Customer" -d '{"customerId" : "01",   "firstName" : "Prayag", "middleName" : "", "lastName" : "Upd", "balance":100.90, "created" : "2013-10-28 00:00:00"}'

echo "document indexed"
