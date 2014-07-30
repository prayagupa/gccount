apply es mapping
--------------------

start elasticsearch

```bash
cd scripts
```

create index/ apply mapping/ index documents with following command
```
./bootstrap.sh
```

or, do it stepwise with following commands, line 2, 3, 4

```bash
1 boot(){                                                                                             
2 ./create_index.sh                                                                                   
3 ./customer_create_mapping.sh                                                                        
4 ./customer_indexing.sh                                                                              
5 } 
```

es report analytics
==============
hit `http://localhost:8483/gccount/transaction/transactionAnalytics?indexName=gccount&reportName=transaction`

