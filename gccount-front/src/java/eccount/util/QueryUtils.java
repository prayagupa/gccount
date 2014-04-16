package eccount.util;

import eccount.ClientRequest;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;

import java.util.Set;
import eccount.SearchRequest;
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
    public static String CUSTOMER_FIELD = "customerId";
    public static String BALANCE_FIELD  = "balance";

    /**
     *
     * @param keyField
     * @param values
     * @return
     * <pre>
     *     {
                "terms" : {
                  "keyField" : [ values ]
                }
           }
     * </pre>
     */
    public static FilterBuilder buildFacetFilter(String keyField, Set<String> values) {
        String[] terms = new String[values.size()];
        int i = 0;
        for (String term : values) {
            terms[i++] = term;
        }

        return buildFacetFilter(keyField, terms);
    }
    public static FilterBuilder buildFacetFilter(String field, String[] terms){
        return FilterBuilders.termsFilter(field, terms);
    }

    /**
     *
     * @param client
     * @param request
     * @param paramFrom
     * @param paramTo
     * @param state
     * @param filter
     * @param esTypes
     * @return
     * <pre>
     *     {
            "range" : {
                    "field" : {
                        "from" : "2012-06-01",
                        "to"   : "2013-05-31",
                        "include_lower" : true,
                        "include_upper" : true
                    }
                }
           }
     * </pre>
     */
    public static SearchRequestBuilder buildSearchRequest(Client client,
                                                          final SearchRequest request,
                                                          String paramFrom,
                                                          String paramTo,
                                                          ClientRequest state,
                                                          FilterBuilder filter,
                                                          String... esTypes) {
        String index = request.hasParameter("clientId") ? request.get("clientId") : "gccount";
        SearchRequestBuilder requestBuilder = client.prepareSearch(index);
        requestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);

        //getPeriodRange
        String dateRangeFrom = request.hasParameter(paramFrom) ? request.get(paramFrom) : "2010-11-01";
        String dateRangeTo   = request.hasParameter(paramTo)   ? request.get(paramTo)   : DEFAULT_DATE_UPPER_END;

        String field_         = "created";
        RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter(field_);
        rangeFilter.from(dateRangeFrom);
        rangeFilter.to(dateRangeTo);
        AndFilterBuilder andFilter = new AndFilterBuilder();
        andFilter.add(rangeFilter);

        requestBuilder.setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), andFilter));
        requestBuilder.setTypes(esTypes);

        final String typeField      = request.hasParameter("keyField")   ? request.get("keyField")   : CUSTOMER_FIELD;
        final String valueField     = request.hasParameter("valueField") ? request.get("valueField") : BALANCE_FIELD;
        TermsStatsFacetBuilder termsStatsFacet;

        if(RequestUtils.isArrayRequest(valueField)){
            String[] valueFields=RequestUtils.getArrayRequest(valueField);
            for(String valueField_ : valueFields){
                termsStatsFacet= FilterUtils.getTermsStatsFacet(valueField_ + "_stats", typeField, valueField_, SIZE);
                requestBuilder.addFacet(termsStatsFacet);
            }
        }else{

            /**
             *
             * "amount_stats" : {
                     "terms_stats" : {
                         "key_field" : "customerId",
                         "value_field" : "paidAmount",
                         "order" : "total",
                         "size" : 0
                     }
                 }
             */
            requestBuilder.addFacet(getTermsStatsFacet(request, null, filter));
            requestBuilder.addField("total"); //"fields" : [ "total"],
        }

        requestBuilder.addField(typeField); //"fields" : ["customerId" ],
        return requestBuilder;
    }

    public static void setSize(SearchRequestBuilder builder) {
        builder.setFrom(0).setSize(1).setExplain(false);
    }

    /**
     *
     * @param request
     * @param facetName
     * @param filter
     * @return   "facetName" : {
                        "terms_stats" : {
                            "key_field" : "customerId",
                            "value_field" : "paidAmount",
                            "order" : "total",
                            "size" : 0
                        }
                  }
     *
     */
    public static TermsStatsFacetBuilder getTermsStatsFacet(final SearchRequest request, String facetName, FilterBuilder filter){
        final String typeField = getKeyField(request);
        final String valueField = request.hasParameter("valueField") ? request.get("valueField") : "paidAmount";
        TermsStatsFacetBuilder termsStatsFacet = null;
        if ((facetName=="")||(facetName==null)){
            facetName = "amount_stats";
        }
        termsStatsFacet = FilterUtils.getTermsStatsFacet(facetName, typeField, valueField, SIZE);
        if(filter!=null)
            termsStatsFacet.facetFilter(filter);

        return termsStatsFacet;
    }

    public static String getKeyField(SearchRequest request) {
        return request.hasParameter("keyField") ? request.get("keyField") : "customerId";
    }

    /**
     *
     * @param client
     * @param request
     * @param paramFrom
     * @param paramTo
     * @param state
     * @param filter
     * @param esTypes
     * @return
     */
    public static SearchRequestBuilder prepareRequestBuilder(Client client,
                                                             final SearchRequest request,
                                                             String paramFrom,
                                                             String paramTo,
                                                             ClientRequest state,
                                                             FilterBuilder filter,
                                                             String... esTypes) {
        SearchRequestBuilder builder = prepareSearchQuery(client, request, paramFrom, paramTo, state, filter, esTypes);
        setSize(builder);
        return builder;
    }

    public static SearchRequestBuilder prepareSearchQuery(Client client,
                                                          SearchRequest request,
                                                          String paramFrom,
                                                          String paramTo,
                                                          ClientRequest state,
                                                          FilterBuilder filter,
                                                          String[] esTypes) {
        String dateRangeFrom = request.hasParameter(paramFrom) ? request.get(paramFrom) : "2010-11-01";
        String dateRangeTo   = request.hasParameter(paramTo)   ? request.get(paramTo)   : DEFAULT_DATE_UPPER_END;
        return buildQuery(client, state, parseBasis(request), dateRangeFrom, dateRangeTo, state.isPaidThrough(), filter, esTypes);

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
        String index                 = request.hasParameter("clientId") ? request.get("clientId") : "gccount";
        SearchRequestBuilder builder = client.prepareSearch(index);
        builder.setSearchType(SearchType.QUERY_THEN_FETCH);
        return builder;
    }
}
