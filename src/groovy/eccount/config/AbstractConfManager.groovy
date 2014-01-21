package eccount.config

/**
 * User: prayag
 * Date: 09/11/13
 * Time: 09:50
 */
class AbstractConfManager {
    def qualifier

    public EsCluster getClusterConfig(String server){
        def script =  AbstractConfManager.classLoader.loadClass(server).newInstance()
        script.run()
        def ServersBuilder builder= new ServersBuilder()
        def servers = script.servers
        servers.delegate = builder
        servers()
        EsCluster clusterConfig=new EsCluster()
        clusterConfig.nodes=builder.serverMaps
        clusterConfig.clusterName = builder.clusterName
        clusterConfig
    }

    def getClientId(requestMap){
        requestMap!=null?requestMap.get("clientId"):""
    }
    def setIndex(requestMap){
        def clientId=getClientId(requestMap)
        if(clientId){
            requestMap.put("index_name",clientId)
        }
        else{
            throw new RuntimeException("ClientId is not defined.")
        }
    }
}
