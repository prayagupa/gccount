curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
  {
    "query" : { 
          "match_all": {} 
     }
  }'
