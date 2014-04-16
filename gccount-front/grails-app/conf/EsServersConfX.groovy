/**
  * elastic server config
  */

servers = {

    clusterName("elasticsearch")

    server(){
        bean->
            name("Node1")
            hostname("localhost")
            port("9300")
            httpPort("9200")
    }
}
