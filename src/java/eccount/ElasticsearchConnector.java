package eccount;


import eccount.config.ElasticClusterConfig;
import eccount.config.ElasticServerConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static Logger logger = LoggerFactory.getLogger(ElasticsearchConnector.class.getName());
    
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

        System.out.println("----------------------------------------------------------------------------------");
        System.out.println(dt.format(new Date()) + " ElasticSearchConnector.getInstance: Connection attempt to cluster: "+ esClusterName);
        logger.info("", dt.format(new Date()) + " ElasticSearchConnector.getInstance: Connection attempt to cluster: "+ esClusterName);

        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", esClusterName).build();
        TransportClient client = new TransportClient(settings);
        for(ElasticServerConfig serverConfig : clusterConfig.getNodes().values()){
            client.addTransportAddress(new InetSocketTransportAddress(serverConfig.getHostname(),serverConfig.getPort()));
        }
        return client;
    }


}
