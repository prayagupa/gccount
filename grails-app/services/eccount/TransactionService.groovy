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
 * @author : prayagupd
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
        Client esClient        = ElasticsearchConnector.getClient(getDefaultCluster())

        log.info("Client : "+esClient)

        //FIXME
        AtomicBoolean process = new AtomicBoolean(false)
        AbstractAnalyticsActionListener analyticsActionListener = newActionListener(field, request, report, types, process)
        MultiSearchRequestBuilder builder = AnalyticsQueryBuilders.getBuilder(report).query(analyticsActionListener.state, esClient)
        try {
            Thread thread = new Thread(new BuilderExecutor(builder, analyticsActionListener))
            thread.start()

            while (!analyticsActionListener.processComplete.get()) {
                Thread.currentThread().sleep(100)
            }
        } catch (Exception e) {
            analyticsActionListener.processComplete.set(true)
            e.printStackTrace()
        }
        return analyticsActionListeiner.state.contentBuilder?analyticsActionListener.state.contentBuilder.bytes().toUtf8():""
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

    /**
      * static object for executing @{SearchRequestBuilder}
      * and stimulating respective @{ActionListener} to handle @{SearchResponse}s
      */
    static class BuilderExecutor implements Runnable {
        MultiSearchRequestBuilder builder;
        AbstractAnalyticsActionListener actionListener;

        public BuilderExecutor(MultiSearchRequestBuilder builder, AbstractAnalyticsActionListener listener) {
            this.builder = builder;
            this.actionListener = listener;
        }

        @Override
        public void run() {
            try {
                builder.execute(actionListener);
            } catch (Exception e) {
                actionListener.processComplete.set(true);
                e.printStackTrace();
            }
        }
    }

}
