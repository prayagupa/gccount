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
