## http://stackoverflow.com/a/10360039/432903

<<FUZZY
curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d'
{
   "query": {
      "fuzzy": {
         "firstName": "Prayag fictional"
      }
   },
   "aggs": {
      "distinct_lastNames": {
         "terms": {
            "field": "lastName"
         }
      }
   }
}'
FUZZY

curl -X POST "http://localhost:9200/gccount/Customer/_search?pretty=true" -d'
{
   "query": {
    "fuzzy_like_this_field" : {
        "tags" : {
            "like_text" : "birthday",
            "max_query_terms" : 12
        }
    }
   },
   "aggs": {
      "distinct_lastNames": {
         "terms": {
            "field": "lastName"
         }
      }
   }
}'

