curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
  {
    "query" : { 
          "match_all": {} 
     }
  }'


curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
  {
   "filtered" : {
    "query" : { 
          "match_all": {} 
     }, 
     "filter" : {
          "nested" : {
            "filter" : {
             "term" : {
                   "transactions.transactionId":"00xx00"
             }
            },
           "path" : "transactions"
          }
     }
   }
  }'
