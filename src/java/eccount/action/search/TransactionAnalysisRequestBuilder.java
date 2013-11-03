package eccount.action.search;

import eccount.ClientRequest;

import eccount.SearchRequest;
import eccount.action.AnalyticsRequestBuilders;
import eccount.util.DateUtils;
import eccount.util.FilterUtils;
import eccount.util.QueryUtils;
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

    @Override
    protected MultiSearchRequestBuilder executeMultiSearchQuery(ClientRequest state, Client client) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = new MultiSearchRequestBuilder(client);

        SearchRequestBuilder servicewiseAmountRequestBuilder = QueryUtils.buildSearchRequest(client,
                                                                     state.request,
                                                                     state.period() + "From",
                                                                     state.period() + "To",
                                                                     state,
                                                                     null,
                                                                     "Customer");
        SearchRequestBuilder countRequestBuilder = prepareCountFacets(state, client);
        SearchRequestBuilder builder3 = query2(state, client);

        multiSearchRequestBuilder.add(servicewiseAmountRequestBuilder);
        multiSearchRequestBuilder.add(countRequestBuilder);
        multiSearchRequestBuilder.add(builder3);
        System.out.println("multiSearchRequestBuilder="+multiSearchRequestBuilder);
        return multiSearchRequestBuilder;
    }


    private SearchRequestBuilder prepareCountFacets(ClientRequest state, Client client) {
        SearchRequestBuilder searchRequestBuilder = prepareSearchRequestBuilder(state, client);
        addCustomerMonthsFacet(searchRequestBuilder, state);
        addCustomerCountFacet(searchRequestBuilder, state);
        addSubscriberCountFacet(searchRequestBuilder, state);
        return searchRequestBuilder;
    }


    private SearchRequestBuilder query2(ClientRequest state, Client client) {
        SearchRequestBuilder builder3 = QueryUtils.prepareRequest(client,
                                                                  state.request,
                                                                  state.period() + "From",
                                                                  state.period() + "To",
                                                                  state, null,
                                                                  "Transaction");
        StatisticalFacetBuilder transactionAmountFacet = FilterUtils.getStatisticalFacet("totalAmount_stats", "paidAmount", null);
        builder3.addFacet(transactionAmountFacet);
        builder3.addField("paidAmount");
        return builder3;
    }

    private void addCustomerMonthsFacet(SearchRequestBuilder builder, ClientRequest state) {
        for (String month : DateUtils.getMonthsBetween(state.periodFrom(), state.periodTo())) {
            String facetName = "customerMonths:" + month;
            builder.addFacet(termsStatsFacetBuilder(state, month, facetName));
        }
    }

    private void addSubscriberCountFacet(SearchRequestBuilder builder, ClientRequest state) {
        builder.addFacet(termsStatsFacetBuilder(state, state.periodTo(), "subscriberCount"));
    }

    private void addCustomerCountFacet(SearchRequestBuilder builder, ClientRequest state) {
        builder.addFacet(termsStatsFacetBuilder(state, state.periodTo(), "memberCount"));
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


    private TermsStatsFacetBuilder termsStatsFacetBuilder(ClientRequest state, String month, String facetName) {
        Long longDate = DateUtils.getTimeFromDateWrtTimeZone(month);
        AndFilterBuilder andFilter = null;
        Long periodTo = DateUtils.getTimeFromDateWrtTimeZone(state.periodTo());
        return FilterUtils.getFacet(facetName, "searchKey", "valueField", andFilter);
    }
}

