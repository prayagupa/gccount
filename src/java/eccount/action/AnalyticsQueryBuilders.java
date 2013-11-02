package eccount.action;

import eccount.ClientRequest;
import eccount.action.search.AnalyticsQueryBuilder;
import eccount.action.search.TransactionAnalysisQueryBuilder;
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
public class AnalyticsQueryBuilders {

    static Map<String, AnalyticsQueryBuilder> queryBuilders;

    static {
        queryBuilders = new ConcurrentHashMap<String, AnalyticsQueryBuilder>();
        queryBuilders.put(AnalyticsActionListeners.TRANSACTION, new TransactionAnalysisQueryBuilder());
    }

    public static AnalyticsQueryBuilder getBuilder(String report) {
        return queryBuilders.get(report);
    }

    public static void registerQueryBuilder(String report, AnalyticsQueryBuilder builder) {
        queryBuilders.put(report, builder);
    }

    public static class DefaultQueryBuilder implements AnalyticsQueryBuilder {
        @Override
        public MultiSearchRequestBuilder query(ClientRequest state, Client client) {
            return executeMultiSearchQuery(state, client);

        }

        protected MultiSearchRequestBuilder executeMultiSearchQuery(ClientRequest state, Client client) {
            MultiSearchRequestBuilder multiSearchRequestBuilder = new MultiSearchRequestBuilder(client);
            FilterBuilder filter = QueryUtils.buildFacetFilter(state.field, state.recordIds.keySet());
            return multiSearchRequestBuilder.add(QueryUtils.buildSearchRequest(client,
                                                                               state.request,
                                                                               state.period() + "From",
                                                                               state.period() + "To",
                                                                               state,
                                                                               filter,
                                                                               state.type()));
        }
    }
}
