package eccount.action;


import eccount.ClientRequest;
import eccount.action.AbstractAnalyticsActionListener;
import eccount.action.SearchAnalyticsActionListener;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
  * Factory class for transaction report listeners to do multiple queries in ES to fetch a specific report. 
  * Registered listener has to be
  * sub class of {@linkplain AbstractAnalyticsActionListener}
  *
  * @author: prayagupd
  */

public class AnalyticsActionListeners {
    /**
     * Maps report name to listener
     */
    static Map<String, Class> listeners;
    static final String TRANSACTION = "transaction";

    static {
        listeners = new ConcurrentHashMap<String, Class>();
        listeners.put(TRANSACTION, SearchAnalyticsActionListener.class);
    }

    /**
     * Construct listener from provided state and supporting object
     *
     * @param report
     * @param state
     * @param client
     * @param logger
     * @return
     * @throws Exception
     */
    public static AbstractAnalyticsActionListener newActionListener(String report, ClientRequest state, Client client, ESLogger logger,AtomicBoolean processComplete) throws Exception {
        Class c = listeners.get(report);
        AbstractAnalyticsActionListener listener = (AbstractAnalyticsActionListener) c.newInstance();
        listener.setClient(client);
        listener.setState(state);
        listener.setLogger(logger);
        listener.setReport(report);
        listener.processComplete=processComplete;
        return listener;
    }

    /**
     * Constructs listener as a copy of supplied listener copying state to the new one.
     *  However, the copy constructor of the {@linkplain eccount.ClientRequest}can propagate current state as required.
     *
     * @param copy listener to copy from
     * @return new instance
     * @throws Exception if instance creation fails
     */
    public static AbstractAnalyticsActionListener newActionListener(AbstractAnalyticsActionListener copy) throws Exception {
        return newActionListener(copy.getReport(), new ClientRequest(copy.getState()), copy.getClient(), copy.getLogger(),copy.processComplete);
    }

}
