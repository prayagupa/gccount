#!/bin/sh
<<FACET
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
    "fields" : ["firstName", "lastName"],
    "query" : {
        "match_all" : {  }
    },
    "facets" : {
        "customers" : {
            "terms" : {
                "field" : "firstName",
                "size" : 10
            }
        }
    }
}'
FACET



<<TAGS_FACET
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
    "fields" : ["firstName", "lastName"],
    "query" : {
        "match_all" : {  }
    },
    "facets" : {
        "customers" : {
            "terms" : {
                "field" : "tags",
                "size" : 10
            }
        }
    }
}'
TAGS_FACET

<<BALANCE_STATS
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
 "query" :
  {
       "match_all": {}
  },
  "aggs" : {
     "amount_stats" : {
       "stats" :{
               "field" : "balance"
       }
     }
   }
}'
BALANCE_STATS

<<BALANCE_SUM
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
 "query" :
  {
       "match_all": {}
  },
  "aggs" : {
     "amount_sum" : {
       "sum" :{
               "field" : "balance"
       }
     }
   }
}'
BALANCE_SUM

<<BALANCE_MIN
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
 "query" :
  {
       "match_all": {}
  },
  "aggs" : {
     "amount_min" : {
       "min" :{
               "field" : "balance"
       }
     }
   }
}'
BALANCE_MIN

<<BALANCE_MAX
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
 "query" :
  {
       "match_all": {}
  },
  "aggs" : {
     "amount_max" : {
       "max" :{
               "field" : "balance"
       }
     }
   }
}'
BALANCE_MAX

<<BALANCE_COUNT
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
 "query" :
  {
       "match_all": {}
  },
  "aggs" : {
     "value_count" : {
       "value_count" :{
               "field" : "balance"
       }
     }
   }

}'
BALANCE_COUNT

<<NESTED_AGGS_SUM
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
    "aggs" : {
        "transactions" : {
            "nested" : {
                "path" : "transactions"
            },
            "aggs" : {
                "amount_sum" : { "sum" : { "field" : "transactions.amount" } }
            }
        }
    }

}
'
NESTED_AGGS_SUM

curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d '
{
 "query" :
  {
       "match_all": {}
  },
  "aggs" : {
        "transactions" : {
            "nested" : {
                "path" : "transactions"
            },
            "aggs" : {
                "status" : {
                    "terms" : {
                        "field" : "transactions.status"
                    }
                }
            }
         }
   }
}
'