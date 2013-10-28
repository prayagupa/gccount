package eccount.config

/**
 * User: prayag
 * Date: Apr 27, 2013
 * Time: 2:34:02 AM
 */
class ElasticClusterConfig {
  public static String ES_DEFAULT_CLUSTER_NAME="elasticsearch"

  Map<String, ElasticServerConfig> nodes
  String clusterName
}
