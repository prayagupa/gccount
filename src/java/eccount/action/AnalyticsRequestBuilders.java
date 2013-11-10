package eccount.action;

import eccount.ClientRequest;
import eccount.action.search.AnalyticsRequestBuilder;
import eccount.action.search.TransactionAnalysisRequestBuilder;
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

    static Map<String, AnalyticsRequestBuilder> queryBuilders;

    static {
        queryBuilders = new ConcurrentHashMap<String, AnalyticsRequestBuilder>();
        queryBuilders.put(AnalyticsActionListeners.TRANSACTION, new TransactionAnalysisRequestBuilder());
    }

    public static AnalyticsRequestBuilder getBuilder(String report) {
        return queryBuilders.get(report);
    }

    public static void registerQueryBuilder(String report, AnalyticsRequestBuilder builder) {
        queryBuilders.put(report, builder);
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
            return multiSearchRequestBuilder.add(QueryUtils.buildSearchRequest(client,
                                                                               state.request,
                                                                               state.period() + "From",
                                                                               state.period() + "To",
                                                                               state,
                                                                               filterOnKeyField,
                                                                               state.type()));
        }
    }
}
