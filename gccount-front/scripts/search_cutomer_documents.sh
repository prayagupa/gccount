<<MATCH_ALL_QUERY
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
  {
    "query" : { 
          "match_all": {} 
     }
  }'
MATCH_ALL_QUERY

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
                        "match" : {"transactions.transactionId" : "1"}
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
                    "transactions.transactionId": "1"
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


<<NESTED_FILTER_EX
{
  "fields": ["transactions"], 
  "query": {
    "filtered": {
      "query": {
        "match_all": {}
      },
      "filter": {
        "nested": {
          "path": "transactions",
          "filter": {
            "and": {
              "filters": [
                {
                  "range": {
                    "transactions.serviceDate": {
                      "from": "2013-02-01",
                      "to": "2014-01-31",
                      "include_lower": true,
                      "include_upper": true
                    }
                  }
                },
                {
                  "range": {
                    "transactions.paidDate": {
                      "from": "2013-02-01",
                      "to": "2014-01-31",
                      "include_lower": true,
                      "include_upper": true
                    }
                  }
                },
                {
                  "term": {
                    "transactions.type": "7"
                  }
                }
              ]
            }
          }
        }
      }
    }
  }
}
NESTED_FILTER_EX


<<AGGREGATOR
POST http://localhost:9200/gccount/Customer/_search
{
   "size": 0, 
   "aggregations": {
      "firstNameAggregator": {
         "terms": {
            "field": "firstName",
            "order": {
               "balance_avg": "desc"
            }
         },
         "aggregations": {
            "balance_avg": {
               "avg": {
                  "field": "balance"
               }
            }
         }
      }
   }
}
AGGREGATOR

