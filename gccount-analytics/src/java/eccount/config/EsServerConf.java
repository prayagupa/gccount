package eccount.config;

/**
 * User: prayag
 * Date: 09/11/13
 * Time: 10:49
 */
public class EsServerConf {
    public static String CURRENT_CLUSTER = "EsServersConfX";    //located at grails-app/conf/


    public static final String clusterX="EsServersConfX";
    public static final String clusterY="EsServersConfY";

    public static String swapCluster(String currentCluster) {
        String targetCluster;
        if (currentCluster.equals(EsServerConf.clusterX)) targetCluster= EsServerConf.clusterY;
        else targetCluster= EsServerConf.clusterX;
        return targetCluster;
    }
}
