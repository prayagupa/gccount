package eccount

import eccount.config.ElasticClusterConfig
import eccount.config.ElasticServerConfig
import org.elasticsearch.client.Client
import org.elasticsearch.common.netty.handler.codec.http.DefaultHttpRequest
import org.elasticsearch.common.netty.handler.codec.http.HttpMethod
import org.elasticsearch.common.netty.handler.codec.http.HttpRequest
import org.elasticsearch.common.netty.handler.codec.http.HttpVersion
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.http.netty.NettyHttpRequest
import org.elasticsearch.rest.RestRequest
import org.springframework.beans.factory.annotation.Autowired

/*
 * @author : prayag upd
 * @created : 24 Dec, 2012
*/
class TransactionService {

    def getDailyTrxns() {
		def fromDate  = new Date(); 
		def trxnCriteria = Transaction.createCriteria()
		def results = trxnCriteria.list {
		    eq("created", fromDate)
		}
        }//end of dailyTrxns
  
   def requestES(String url, String requestParams){
        HttpVersion version  = new HttpVersion("http", 2, 1)
        HttpMethod method    = new HttpMethod("GET")
        HttpRequest req      = new DefaultHttpRequest(version, method, url + "?" + requestParams);
        RestRequest restReq  = new NettyHttpRequest(req);

        String esClusterName = ElasticClusterConfig.ES_DEFAULT_CLUSTER_NAME;

        Settings settings    = ImmutableSettings.settingsBuilder().put("cluster.name", esClusterName).build();
        Client client        = ElasticsearchConnector.getClient(getDefaultCluster())

        println "Client : "+client

        //FIXME
        //XContentBuilder content = new TrxnAnalytics().requestES(restReq, client, settings)
        //return content?content.bytes().toUtf8():"";
   }

   def getDefaultCluster(){
       def server = new ElasticServerConfig(name :"Node1",
                                            hostname :"localhost",
                                            port : 9300,
                                            httpPort : 9200)
       def cluster   = new ElasticClusterConfig()
       cluster.nodes = ["Node1":server]
       cluster.clusterName  = "elasticsearch"
       cluster
   }
}
