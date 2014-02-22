<<MATCH_ALL
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
  {
    "query" : { 
          "match_all": {} 
     }
  }'
MATCH_ALL

curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
    "size": 10,
    "from": 0,
    "query": { 
     "nested" : {
        "path" : "transactions",
        "query" : {
            "bool" : {
                "must" : [
                    {
                        "match" : {"transactions.transactionId" : "00XX00"}
                    }
                ]
            }
        }
     }
    }
}'


<<FILTERED
{
  "size": 100,
  "from": 0,
  "query": {
    "filtered": {
      "query": {
        "match_all": {}
      },
      "filter": {
        "nested": {
          "path": "transactions",
          "filter": {
            "bool": {
              "must": [
                {
                  "term": {
                    "transactions.transactionId": "00XX00"
                  }
                }
              ]
            }
          },
          "_cache": true
        }
      }
    }
  }
}
FILTERED
