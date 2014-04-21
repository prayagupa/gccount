package eccount.action.search;

import eccount.ClientRequest;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;

/**
 * @author prayagupd
 */
 
public interface AnalyticsRequestBuilder {
    MultiSearchRequestBuilder query(ClientRequest request, Client client);
}

