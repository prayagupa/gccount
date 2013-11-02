package eccount

import eccount.action.AbstractAnalyticsActionListener
import eccount.action.AnalyticsActionListeners
import eccount.action.AnalyticsQueryBuilders
import eccount.config.ElasticClusterConfig
import eccount.config.ElasticServerConfig
import org.elasticsearch.action.search.MultiSearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings

import java.util.concurrent.atomic.AtomicBoolean

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

    /**
     * get es results
     * @param request
     * @return
     */
   def getResults(SearchRequest request){
        String esClusterName   = ElasticClusterConfig.ES_DEFAULT_CLUSTER_NAME;
        Settings settings      = ImmutableSettings.settingsBuilder().put("cluster.name", esClusterName).build();
        Client esClient        = ElasticsearchConnector.getClient(getDefaultCluster())
        log.info("Client : "+esClient)

        AtomicBoolean process = new AtomicBoolean(false)
        String report         = request.hasParameter("report") ? request.get("report") : "transaction"
        final String field    = request.hasParameter("keyField") ? request.get("keyField") : "customerId"

        AbstractAnalyticsActionListener analyticsActionListener = newActionListener(field, request, report, process)
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
        return analyticsActionListener.state.contentBuilder?analyticsActionListener.state.contentBuilder.bytes().toUtf8():""
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

    protected AbstractAnalyticsActionListener newActionListener(String field, SearchRequest request, String report, AtomicBoolean processComplete) throws Exception {
        ClientRequest state = new ClientRequest(report, field, request, null);
        state.noFilter = false;
        AbstractAnalyticsActionListener actionListener = AnalyticsActionListeners.newActionListener(report, state, client, logger, processComplete);
        return actionListener;
    }



}
