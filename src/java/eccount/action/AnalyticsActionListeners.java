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
     * Construct listener from provided state and supporting object
     *
     * @param reportName
     * @param state
     * @param client
     * @param logger
     * @return
     * @throws Exception
     */
    public static AbstractAnalyticsActionListener newActionListener(String reportName,
                                                                    ClientRequest state,
                                                                    Client client,
                                                                    ESLogger logger,
                                                                    AtomicBoolean processComplete) throws Exception {
        Class c = actionListeners.get(reportName);
        AbstractAnalyticsActionListener actionListener = (AbstractAnalyticsActionListener) c.newInstance();
        actionListener.setClient(client);
        actionListener.setState(state);
        actionListener.setLogger(logger);
        actionListener.setReport(reportName);
        actionListener.processComplete=processComplete;
        return actionListener;
    }

    /**
     *  Constructs actionListener as a copy of supplied actionListener copying state to the new one.
     *  However, the copy constructor of the {@linkplain eccount.ClientRequest}can propagate current state as required.
     *
     * @param copy listener to copy from
     * @return new instance
     * @throws Exception if instance creation fails
     */
    public static AbstractAnalyticsActionListener newActionListener(AbstractAnalyticsActionListener copy) throws Exception {
        return newActionListener(copy.getReport(),
                                 new ClientRequest(copy.getState()),
                                 copy.getClient(),
                                 copy.getLogger(),
                                 copy.processComplete);
    }

}
