package eccount.util;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.statistical.StatisticalFacetBuilder;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacetBuilder;

/**
 * @author : prayag
 */
public class FilterUtils {


    public static TermsStatsFacetBuilder getTermsStatsFacet(String facetName, String keyField, String valueField, int size) {
        TermsStatsFacetBuilder termsStatsFacet = FacetBuilders.termsStatsFacet(facetName);
        termsStatsFacet.keyField(keyField);
        termsStatsFacet.valueField(valueField);
        termsStatsFacet.size(size);
        termsStatsFacet.order(org.elasticsearch.search.facet.termsstats.TermsStatsFacet.ComparatorType.TOTAL);
        return termsStatsFacet;
    }

    /**
     * get a TermsStatsFacetBuilder based on keyField and valueField
     * <pre>
             "facets" : {
                 "customerGender:eligibilityTrend_6-2013" : {
                     "terms_stats" : {
                         "key_field" : "customerGender",
                         "value_field" : "customerAge",
                         "order" : "total",
                         "size" : 0
                     }
                  }
             }
     * </pre>
     * @param name
     * @param keyField
     * @param valueField
     * @param filterBuilders
     * @return
     */
    public static TermsStatsFacetBuilder getFacet(String name, String keyField, String valueField, FilterBuilder filterBuilders) {
        TermsStatsFacetBuilder facet = getTermsStatsFacet(name, keyField, valueField, QueryUtils.SIZE);
        if(filterBuilders!=null) facet.facetFilter(filterBuilders);
        return facet;
    }

    /**
     *
     * @param facetName
     * @param field
     * @param builder
     * @return
     * <pre>
           "facets" : {
                "totalAmount_stats" : {
                    "statistical" : {
                        "keyField" : "paidAmount"
                    }
                }
            }
     * </pre>
     */
    public static StatisticalFacetBuilder getStatisticalFacet(String facetName, String field, FilterBuilder builder) {
        StatisticalFacetBuilder statisticalFacet = FacetBuilders.statisticalFacet(facetName);
        statisticalFacet.field(field);
        if (builder != null) statisticalFacet.facetFilter(builder);
        return statisticalFacet;
    }
}
