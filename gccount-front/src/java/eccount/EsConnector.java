package eccount;


import eccount.config.EsCluster;
import eccount.config.EsServer;
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
public class EsConnector {

    static Client client = null;
    static String clusterName;
    static boolean initializedClient = false;
    public static boolean ENVIRONMENT_TEST =false;
    static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    static Map<String,Client> clusterClients=new HashMap<String, Client>();

    static Logger logger = LoggerFactory.getLogger(EsConnector.class.getName());
    
    public static synchronized Client getClient(EsCluster clusterConfig) {
        if(ENVIRONMENT_TEST){
            return EsEmbeddedNode.getClient();
        }

        if (!initializedClient || !clusterName.equals(clusterConfig.getClusterName())) {
            client = getInstance(clusterConfig);
            clusterName = clusterConfig.getClusterName();
            initializedClient = true;
        }
        return client;
    }

    private static Client getInstance(EsCluster clusterConfig) {

        if(clusterConfig.getNodes().size()==0){
            throw new RuntimeException("No servers specified. Refer to ServersConfig.");
        }
        String esClusterName = !clusterConfig.getClusterName().isEmpty()?clusterConfig.getClusterName(): EsCluster.ES_DEFAULT_CLUSTER_NAME;

        //System.out.println("----------------------------------------------------------------------------------");
        //System.out.println(dt.format(new Date()) + " ElasticSearchConnector.getInstance: Connection attempt to cluster: "+ esClusterName);
        System.out.println("println logger EC =>" + logger.getName());
        logger.info("", dt.format(new Date()) + " ElasticSearchConnector.getInstance: Connection attempt to cluster: "+ esClusterName);

        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", esClusterName).build();
        TransportClient client = new TransportClient(settings);
        for(EsServer serverConfig : clusterConfig.getNodes().values()){
            client.addTransportAddress(new InetSocketTransportAddress(serverConfig.getHostname(),serverConfig.getPort()));
        }
        return client;
    }


}
