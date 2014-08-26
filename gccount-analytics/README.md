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

If you already have 'gccount' index and you want to delete it along with mapping, fire following command 

```bash
./delete_index.sh
```


Before connecting to es cluster from application, change config file at `grails-app/conf/EsServersConfig.groovy`

eg, 
```bash
    1 /**                                                                                                                                             
    2   * elastic server config                                                                           
    3   */                                                                                                
    4                                                                                                     
    5 servers = {                                                                                         
    6                                                                                                     
~   7     clusterName("gccount")                                                                      
    8                                                                                                     
    9     server(){                                                                                       
   10         bean->                                                                                      
   11             name("Node1")                                                                           
   12             hostname("localhost")                                                                   
   13             port("9300")                                                                            
   14             httpPort("9200")                                                                        
   15     }                                                                                               
   16 } 
```

es report analytics
==============
hit `http://localhost:8483/gccount/transaction/transactionAnalytics?indexName=gccount&reportName=transaction`

