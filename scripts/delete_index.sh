curl -XDELETE 'http://localhost:9200/gccount/Customer/_mapping'
echo "mapping deleted"
echo

curl -XDELETE 'http://localhost:9200/gccount/'
echo "index deleted"
echo

curl -XDELETE 'http://localhost:9200/g/'
echo "index deleted"
echo
