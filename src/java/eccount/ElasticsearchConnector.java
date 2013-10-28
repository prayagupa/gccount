package eccount;


import eccount.config.ElasticClusterConfig;
import eccount.config.ElasticServerConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: prayagupd
 * Date: Oct 28, 2013
 * Time: 3:10:01 AM
 */
public class ElasticsearchConnector {

    static Client client = null;
    static String clusterName;
    static boolean initializedClient = false;
    public static boolean isForTest=false;
    static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    static Map<String,Client> clusterClients=new HashMap<String, Client>();
    //Logger logger
    
    public static synchronized Client getClient(ElasticClusterConfig clusterConfig) {
        if (!initializedClient || !clusterName.equals(clusterConfig.getClusterName())) {
            client = getInstance(clusterConfig);
            clusterName = clusterConfig.getClusterName();
            initializedClient = true;
        }
        return client;
    }

    private static Client getInstance(ElasticClusterConfig clusterConfig) {
        if(clusterConfig.getNodes().size()==0){
            throw new RuntimeException("No servers specified. Refer to ServersConfig.");
        }
        String esClusterName = !clusterConfig.getClusterName().isEmpty()?clusterConfig.getClusterName():ElasticClusterConfig.ES_DEFAULT_CLUSTER_NAME;

        //log.info(dt.format(new Date()) + " ElasticSearchConnector.getInstance: Connection attempt to cluster: "+ esClusterName);

        Settings settings = ImmutableSettings.settingsBuilder()
                        .put("cluster.name", esClusterName).build();
        TransportClient client = new TransportClient(settings);
        for(ElasticServerConfig serverConfig : clusterConfig.getNodes().values()){
            System.out.println(dt.format(new Date()) + " ElasticSearchConnectionManager.getInstance: Adding cluster node: "+serverConfig.getName()+" HostName: "+serverConfig.getHostname()+" Port: "+serverConfig.getPort());
            client.addTransportAddress(new InetSocketTransportAddress(serverConfig.getHostname(),serverConfig.getPort()));
        }
        return client;
    }

    public static synchronized Client getClientWithDynamicCluster(ElasticClusterConfig clusterConfig) {
        String clusterName=clusterConfig.getClusterName();
        if (!clusterClients.containsKey(clusterName)||clusterClients.get(clusterName)==null) {

            clusterClients.put(clusterName, getInstance(clusterConfig));

        }
        return clusterClients.get(clusterName);
    }

}
