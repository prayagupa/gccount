package eccount.action.search;

import eccount.ClientRequest;

import eccount.SearchRequest;
import eccount.action.AnalyticsQueryBuilders;
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

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAnalysisQueryBuilder extends AnalyticsQueryBuilders.DefaultQueryBuilder {

    Logger logger = LoggerFactory.getLogger(TransactionAnalysisQueryBuilder.class.getName());

    @Override
    protected MultiSearchRequestBuilder executeMultiSearchQuery(ClientRequest state, Client client) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = new MultiSearchRequestBuilder(client);

        SearchRequestBuilder builder1 = QueryUtils.buildSearchRequest(client,
                                                                     state.request,
                                                                     state.period() + "From",
                                                                     state.period() + "To",
                                                                     state,
                                                                     null,
                                                                     "Customer");
        SearchRequestBuilder builder2 = query1(state, client);
        SearchRequestBuilder builder3 = query2(state, client);

        multiSearchRequestBuilder.add(builder1);
        multiSearchRequestBuilder.add(builder2);
        multiSearchRequestBuilder.add(builder3);
        System.out.println(multiSearchRequestBuilder);
        return multiSearchRequestBuilder;
    }


    private SearchRequestBuilder query1(ClientRequest state, Client client) {
        SearchRequestBuilder builder2 = prepareSearchRequestBuilder(state, client);
        addMemberMonthsFacet(builder2, state);
        addMemberCountFacet(builder2, state);
        addSubscriberCountFacet(builder2, state);
        return builder2;
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

    private void addMemberMonthsFacet(SearchRequestBuilder builder, ClientRequest state) {
        for (String month : DateUtils.getMonthsBetween(state.periodFrom(), state.periodTo())) {
            String facetName = "memberMonths:" + month;
            builder.addFacet(facetBuilder(state, month, facetName));
        }
    }

    private void addSubscriberCountFacet(SearchRequestBuilder builder, ClientRequest state) {
        builder.addFacet(facetBuilder(state, state.periodTo(), "subscriberCount"));
    }

    private void addMemberCountFacet(SearchRequestBuilder builder, ClientRequest state) {
        builder.addFacet(facetBuilder(state, state.periodTo(), "memberCount"));
    }

    private SearchRequestBuilder prepareSearchRequestBuilder(ClientRequest state, Client client) {
        SearchRequest request = state.request;
        String index = request.hasParameter("clientId") ? request.get("clientId") : "XXX";

        SearchRequestBuilder builder = client.prepareSearch(index);
        builder.setSearchType(SearchType.COUNT);
        builder.setFrom(0).setSize(1).setExplain(false);
        builder.setTypes("CustomerSearch");
        return builder;
    }


    private TermsStatsFacetBuilder facetBuilder(ClientRequest state, String month, String facetName) {
        Long longDate = DateUtils.getTimeFromDateWrtTimeZone(month);
        AndFilterBuilder andFilter = null;
        Long periodTo = DateUtils.getTimeFromDateWrtTimeZone(state.periodTo());
        return FilterUtils.getFacet(facetName, "searchKey", "valueField", andFilter);
    }
}

