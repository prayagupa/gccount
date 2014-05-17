boot(){
./create_index.sh
./customer_create_mapping.sh
./customer_indexing.sh
}

flush(){
./delete_index.sh
}

boot
#flush
