package eccount.report.search;

import eccount.ClientRequest;

import eccount.SearchRequest;
import eccount.report.AnalyticsRequestBuilders;
import eccount.util.DateUtils;
import eccount.util.FilterUtils;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.search.facet.statistical.StatisticalFacetBuilder;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacetBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAnalysisRequestBuilder extends AnalyticsRequestBuilders.DefaultRequestBuilder {

    Logger logger = LoggerFactory.getLogger(TransactionAnalysisRequestBuilder.class.getName());

    public final String ESTYPE_CUSTOMER_SEARCH = "CustomerSearch";
    public final String ESTYPE_CUSTOMER        = "Customer";
    public final String ESTYPE_TRANSACTION     = "Transaction";

    public static String FIELD_BALANCE     = "balance";
    public static String BALANCE_AGGSNAME = "balance_stats";

    @Override
    protected MultiSearchRequestBuilder executeMultiSearchQuery(ClientRequest state, Client client) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = new MultiSearchRequestBuilder(client);

        SearchRequestBuilder builder  = prepareAggs(state, client);
        //multiSearchRequestBuilder.add(countRequestBuilder);
        multiSearchRequestBuilder.add(builder);
        System.out.println("paidAmountRequestBuilder = "+builder);
        return multiSearchRequestBuilder;
    }

    private SearchRequestBuilder prepareAggs(ClientRequest clientRequest, Client client) {
        SearchRequestBuilder searchRequestBuilder = buildEndSearch(client, clientRequest.request);
        searchRequestBuilder.setTypes(ESTYPE_CUSTOMER);
        searchRequestBuilder.addFacet(FilterUtils.getStatisticalFacet(BALANCE_AGGSNAME, FIELD_BALANCE, null));
        searchRequestBuilder.addField(FIELD_BALANCE);
        return searchRequestBuilder;
    }


    private SearchRequestBuilder prepareSearchRequestBuilder(ClientRequest state, Client esClient) {
        SearchRequest request = state.request;
        String index = request.hasParameter("clientId") ? request.get("clientId") : "XXX";

        SearchRequestBuilder builder = esClient.prepareSearch(index);
        builder.setSearchType(SearchType.COUNT);
        builder.setFrom(0).setSize(1).setExplain(false);
        builder.setTypes(ESTYPE_CUSTOMER_SEARCH);
        return builder;
    }

    public SearchRequestBuilder buildEndSearch(Client client, final SearchRequest request) {
        String index                 = request.hasParameter("clientId") ? request.get("clientId") : "gccount";
        SearchRequestBuilder builder = client.prepareSearch(index);
        builder.setSearchType(SearchType.QUERY_THEN_FETCH);
        return builder;
    }
}

