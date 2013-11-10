package eccount.action;


import eccount.ClientRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
  * Factory class for transaction reportName actionListeners to do multiple queries in ES to fetch a specific reportName.
  * Registered listener has to be
  * sub class of {@linkplain AbstractAnalyticsActionListener}
  *
  * @author: prayagupd
  */

public class AnalyticsActionListeners {
    /**
     * Maps reportName name to listener
     */
    static Map<String, Class> actionListeners;
    static final String TRANSACTION = "transaction";

    static {
        actionListeners = new ConcurrentHashMap<String, Class>();
        actionListeners.put(TRANSACTION, SearchAnalyticsActionListener.class);
    }


    /**
     *  Constructs actionListener as a duplicate of supplied actionListener copying state to the new one.
     *  However, the duplicate constructor of the {@linkplain eccount.ClientRequest}can propagate current state as required.
     *
     * @param duplicate listener to duplicate from
     * @return new instance
     * @throws Exception if instance creation fails
     */
    public static AbstractAnalyticsActionListener newActionListener(AbstractAnalyticsActionListener duplicate) throws Exception {
        return newActionListener(duplicate.getReport(),
                                 new ClientRequest(duplicate.getState()),
                                 duplicate.getClient(),
                                 duplicate.getLogger(),
                                 duplicate.processCompleted);
    }


    /**
     * Construct listener from provided state and supporting object
     *
     * @param reportName
     * @param requestState
     * @param esClient
     * @param logger
     * @return
     * @throws Exception
     */
    public static AbstractAnalyticsActionListener newActionListener(String reportName,
                                                                    ClientRequest requestState,
                                                                    Client esClient,
                                                                    ESLogger logger,
                                                                    AtomicBoolean processCompleted) throws Exception {
        Class clazz = actionListeners.get(reportName);
        AbstractAnalyticsActionListener actionListener = (AbstractAnalyticsActionListener) clazz.newInstance();
        actionListener.setClient(esClient);
        actionListener.setState(requestState);
        actionListener.setLogger(logger);
        actionListener.setReport(reportName);
        actionListener.processCompleted= processCompleted;
        return actionListener;
    }
}
