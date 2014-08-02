package eccount

import eccount.report.AbstractAnalyticsActionListener
import eccount.report.AnalyticsActionListeners
import eccount.report.AnalyticsRequestBuilders
import eccount.config.AbstractConfManager
import eccount.config.EsCluster
import eccount.config.EsServer
import org.elasticsearch.action.search.MultiSearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.logging.ESLogger
import org.elasticsearch.common.logging.Loggers
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.atomic.AtomicBoolean

/*
 * @author : prayagupd
 * @created : 24 Dec, 2012
*/

class TransactionService {

    Client esClient
    Settings settings
    ESLogger logger
    Logger log = LoggerFactory.getLogger(TransactionService.class.getName())

    @Autowired
    private AbstractConfManager confManager

    AbstractConfManager getConfManager() {
        return confManager
    }
    void setConfManager(AbstractConfManager confManager) {
        this.confManager = confManager
    }

    def getDailyTrxns() {
		def fromDate  = new Date(); 
		def trxnCriteria = Transaction.createCriteria()
		def results = trxnCriteria.list {
		    eq("created", fromDate)
		}
    }//end of dailyTrxns

    /**
     * get es results
     * @param searchRequest
     * @return
     */
   def getSearchResponse(SearchRequest searchRequest) {
		final String clustername = !confManager.getEsCluster("EsServersConfig").getClusterName().isEmpty() ? confManager.getEsCluster("EsServersConfig").getClusterName() : "elasticsearch"
        settings               = ImmutableSettings.settingsBuilder().put("cluster.name", clustername).build();
        esClient               = EsConnector.getClient(getDefaultCluster())

        AtomicBoolean processFlag = new AtomicBoolean(false)
        String reportName         = searchRequest.hasParameter("reportName") ? searchRequest.get("reportName") : "transaction"
        final String keyField     = searchRequest.hasParameter("keyField")   ? searchRequest.get("keyField")   : "customerId"

        AbstractAnalyticsActionListener analyticsActionListenerBasedOnReportName = newActionListener(keyField, 
                                                                                                     searchRequest, 
                                                                                                     reportName, 
                                                                                                     processFlag)
        // in following case, by default DefaultRequestBuilder#query(request, esClient) will be executed
        MultiSearchRequestBuilder multiSearchRequestBuilder = AnalyticsRequestBuilders.getBuilder(reportName)
                                                                                      .query(analyticsActionListenerBasedOnReportName.state, esClient)
        try {
            Thread thread = new Thread(new RequestBuilderExecutor(multiSearchRequestBuilder, analyticsActionListenerBasedOnReportName))
            thread.start()

            while (!analyticsActionListenerBasedOnReportName.processCompleted.get()) {
                Thread.currentThread().sleep(100)
            }
        } catch (Exception e) {
            analyticsActionListenerBasedOnReportName.processCompleted.set(true)
            e.printStackTrace()
        }
        return analyticsActionListenerBasedOnReportName.state.contentBuilder?analyticsActionListenerBasedOnReportName.state.contentBuilder.bytes().toUtf8():""
   }

   def getDefaultCluster(){
	   def cluster = confManager.getEsCluster("EsServersConfig")
       cluster
   }

    /**
      * static object for executing @{SearchRequestBuilder}
      * and stimulating respective @{ActionListener} to handle @{SearchResponse}s
      */
    static class RequestBuilderExecutor implements Runnable {
        MultiSearchRequestBuilder builder;
        AbstractAnalyticsActionListener actionListener;

        public RequestBuilderExecutor(MultiSearchRequestBuilder builder, AbstractAnalyticsActionListener listener) {
            this.builder        = builder;
            this.actionListener = listener;
        }

        @Override
        public void run() {
            try {
                builder.execute(actionListener);
            } catch (Exception e) {
                actionListener.processCompleted.set(true);
                e.printStackTrace();
            }
        }
    }

    protected AbstractAnalyticsActionListener newActionListener(String keyField,
                                                                SearchRequest requestParams,
                                                                String reportName,
                                                                AtomicBoolean processComplete) throws Exception {
        this.logger = Loggers.getLogger(AbstractAnalyticsActionListener.class.getName(), settings);
        ClientRequest state = new ClientRequest(reportName, keyField, requestParams, null);
        state.noFilter = false;
        AbstractAnalyticsActionListener actionListener = AnalyticsActionListeners.newActionListener(reportName,
                                                                                                    state,
                                                                                                    esClient,
                                                                                                    logger,
                                                                                                    processComplete);
        return actionListener;
    }



}
