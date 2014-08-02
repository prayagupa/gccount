package eccount.config

/**
 * User: prayag
 * Date: 09/11/13
 * Time: 09:50
 */
class AbstractConfManager {
    def q

    public EsCluster getEsCluster(String server){
        def confScript =  AbstractConfManager.classLoader.loadClass(server).newInstance()
        confScript.run()
        def EsServersBuilder builder= new EsServersBuilder()
        def servers = confScript.servers
        servers.delegate = builder
        servers()
        EsCluster clusterConfig = new EsCluster()
        clusterConfig.nodes=builder.serverMaps
        clusterConfig.clusterName = builder.clusterName
        clusterConfig
    }

    def getIndexId(requestMap){
        requestMap!=null?requestMap.get("clientId"):""
    }

    def setIndex(requestMap){
        def clientId=getIndexId(requestMap)
        if ( clientId ) {
            requestMap.put("index_name",clientId)
        } else {
            throw new RuntimeException("Index is not defined.")
        }
    }
}
