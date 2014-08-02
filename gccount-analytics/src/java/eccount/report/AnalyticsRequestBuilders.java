package eccount.report;

import eccount.ClientRequest;
import eccount.report.search.AnalyticsRequestBuilder;
import eccount.report.search.TransactionAnalysisRequestBuilder;
import eccount.util.QueryUtils;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory class for query building for Transaction Analytics
 * User: prayag
 * Date: 02/11/13
 * Time: 00:28
 */
public class AnalyticsRequestBuilders {

    static Map<String, AnalyticsRequestBuilder> requestBuilders;

    static {
        requestBuilders = new ConcurrentHashMap<String, AnalyticsRequestBuilder>();
        requestBuilders.put(AnalyticsActionListeners.TRANSACTION, new TransactionAnalysisRequestBuilder());
    }

    public static AnalyticsRequestBuilder getBuilder(String report) {
        return requestBuilders.get(report);
    }

    public static void registerRequestBuilder(String report, AnalyticsRequestBuilder builder) {
        requestBuilders.put(report, builder);
    }

    public static class DefaultRequestBuilder implements AnalyticsRequestBuilder {

        /**
         * entry point for default request builder
         * @param state
         * @param client
         * @return MultiSearchRequestBuilder
         */
        @Override
        public MultiSearchRequestBuilder query(ClientRequest state, Client client) {
            return executeMultiSearchQuery(state, client);

        }

        protected MultiSearchRequestBuilder executeMultiSearchQuery(ClientRequest state, Client client) {
            MultiSearchRequestBuilder multiSearchRequestBuilder = new MultiSearchRequestBuilder(client);
            //
            FilterBuilder filterOnKeyField = QueryUtils.buildFacetFilter(state.keyField, state.recordsMap.keySet());

            String fromKey = state.period() + "From";
            String toKey   = state.period() + "To";
            return multiSearchRequestBuilder.add(QueryUtils.buildSearchRequest(client,
                                                                               state.request,
                                                                               fromKey,
                                                                               toKey,
                                                                               state,
                                                                               filterOnKeyField,
                                                                               state.type()));
        }
    }
}
