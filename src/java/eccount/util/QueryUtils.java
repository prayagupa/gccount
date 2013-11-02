package eccount.util;

import eccount.ClientRequest;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

import java.util.Set;
import  eccount.SearchRequest;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacetBuilder;

/**
 * @author : prayag
 * Date: 02/11/13
 * Time: 00:43
 */
public class QueryUtils {
    public static final String DEFAULT_DATE_UPPER_END = "2013-12-31";
    public static final int FIXED_DAY_OF_MONTH=15;
    public static int SIZE = 0;

    public static FilterBuilder buildFacetFilter(String field, Set<String> values) {
        String[] terms = new String[values.size()];
        int i = 0;
        for (String term : values) {
            terms[i++] = term;
        }

        return buildFacetFilter(field, terms);
    }
    public static FilterBuilder buildFacetFilter(String field, String[] terms){
        return FilterBuilders.termsFilter(field, terms);
    }


    public static SearchRequestBuilder buildSearchRequest(Client client,
                                                          final SearchRequest request,
                                                          String paramFrom,
                                                          String paramTo,
                                                          ClientRequest state,
                                                          FilterBuilder filter,
                                                          String... types) {
        String dateRangeFrom = request.hasParameter(paramFrom) ? request.get(paramFrom) : "2010-11-01";
        String dateRangeTo = request.hasParameter(paramTo) ? request.get(paramTo) : DEFAULT_DATE_UPPER_END;

        String reportingBasis = request.hasParameter("reportingBasis") ? request.get("reportingBasis") : "serviceDate";
        String field_ = reportingBasis.equalsIgnoreCase("serviceDate") ? "serviceDate" : "paidDate";

        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(field_);
        rangeFilter.from(dateRangeFrom);
        rangeFilter.to(dateRangeTo);

        String index = request.hasParameter("clientId") ? request.get("clientId") : "0005";
        SearchRequestBuilder builder = client.prepareSearch(index);
        builder.setSearchType(SearchType.QUERY_THEN_FETCH);

        AndFilterBuilder andFilter = new AndFilterBuilder();

        final String field = request.hasParameter("keyField") ? request.get("keyField") : "customerId";
        final String valueField = request.hasParameter("valueField") ? request.get("valueField") : "paidAmount";
        TermsStatsFacetBuilder facet;
        if(RequestUtils.isArrayRequest(valueField)){
            String[] valueFields=RequestUtils.getArrayRequest(valueField);
            for(String s: valueFields){
                //facet= FilterUtils.getFacet(s+"_stats", field, s, SIZE);
                facet= null;
                builder.addFacet(facet);
            }
        }else{
            builder.addFacet(getTermsStatFacet(request,null,filter));
            builder.addField("total");
        }

        builder.addField(field);
        return builder;
    }

    public static void setSize(SearchRequestBuilder builder) {
        builder.setFrom(0).setSize(1).setExplain(false);
    }

    public static TermsStatsFacetBuilder getTermsStatFacet(final SearchRequest request, String facetName, FilterBuilder filter){
        final String field = getKeyField(request);
        final String valueField = request.hasParameter("valueField") ? request.get("valueField") : "paidAmount";
        TermsStatsFacetBuilder facet = null;
        if ((facetName=="")||(facetName==null)){
            facetName = "amount_stats";
        }
        facet = FilterUtils.getFacet(facetName, field, valueField, SIZE);
        if(filter!=null)
            facet.facetFilter(filter);

        return facet;
    }

    public static String getKeyField(SearchRequest request) {
        return request.hasParameter("keyField") ? request.get("keyField") : "customerId";
    }


    public static SearchRequestBuilder prepareRequest(Client client,
                                                      final SearchRequest request,
                                                      String paramFrom,
                                                      String paramTo,
                                                      ClientRequest state,
                                                      FilterBuilder filter,
                                                      String... types) {
        SearchRequestBuilder builder = prepareSearchQuery(client, request, paramFrom, paramTo, state, filter, types);
        setSize(builder);

        return builder;
    }

    public static SearchRequestBuilder prepareSearchQuery(Client client,
                                                          SearchRequest request,
                                                          String paramFrom,
                                                          String paramTo,
                                                          ClientRequest state,
                                                          FilterBuilder filter,
                                                          String[] types) {
        String dateRangeFrom = request.hasParameter(paramFrom) ? request.get(paramFrom) : "2010-11-01";
        String dateRangeTo = request.hasParameter(paramTo) ? request.get(paramTo) : DEFAULT_DATE_UPPER_END;
        return buildQuery(client,state,parseBasis(request),dateRangeFrom,dateRangeTo,state.isPaidThrough(),filter,types);

    }

    public static String parseBasis(SearchRequest request) {
        String reportingBasis = request.hasParameter("reportingBasis") ? request.get("reportingBasis") : "serviceDate";
        return reportingBasis.equalsIgnoreCase("serviceDate") ? "serviceDate" : "paidDate";
    }

    public static SearchRequestBuilder buildQuery(Client client,ClientRequest state,String field,Object from,Object to,boolean toFilterPaidThrough,FilterBuilder filter,String... types){
        SearchRequest request=state.request;
        SearchRequestBuilder builder = buildEndSearch(client, request);

        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(field);
        rangeFilter.from(from);
        rangeFilter.to(to);

        setSize(builder);
        return builder;
    }

    public static SearchRequestBuilder buildEndSearch(Client client, final SearchRequest request) {
        String index = request.hasParameter("clientId") ? request.get("clientId") : "0005";
        SearchRequestBuilder builder = client.prepareSearch(index);
        builder.setSearchType(SearchType.QUERY_THEN_FETCH);
        return builder;
    }
}
