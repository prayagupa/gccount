indexName="gccount"
curl -XPUT 'http://localhost:9200/gccount/'
echo
echo "$indexName created."
echo

curl -XPOST 'http://localhost:9200/_aliases' -d '
{
    "actions" : [
        { "add" : { "index" : "gccount", "alias" : "g" } }
    ]
}'

echo "alias 'g' created"
echo
