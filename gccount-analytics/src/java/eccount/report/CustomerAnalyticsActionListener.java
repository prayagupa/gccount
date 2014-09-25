package eccount.report;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : prayag
 */
public class CustomerAnalyticsActionListener extends AbstractAnalyticsActionListener {

    @Override
    public void onResponse(MultiSearchResponse response) {
        super.onResponse(response);
    }

    @Override
    protected void processSearchResponse(MultiSearchResponse multiSearchResponse) {
        MultiSearchResponse.Item[] responseItems = multiSearchResponse.getResponses();
        MultiSearchResponse.Item item1 = responseItems[0];
        SearchResponse countResponse = item1.getResponse();
        processCounts(countResponse);

    }


    private void processCounts(SearchResponse response) {
        double customerCount = 0d;
        for (Facet facet : response.getFacets().facets()) {
            //customerCount facet...
            if (facet instanceof  TermsStatsFacet) {
                TermsStatsFacet termFacet = (TermsStatsFacet) facet;
                for (TermsStatsFacet.Entry stringEntry : termFacet.getEntries()) {
                    customerCount += stringEntry.getCount();
                }
            } else {
                StatisticalFacet statisticalFacet = (StatisticalFacet) facet;
                customerCount = statisticalFacet.getCount();
                System.out.print("StatisticalFacet => " + customerCount);
            }
        }

        String metric = "customers";
        String date = "2013-10-28";
        CustomerResponseRecord customersCountRecord = new CustomerResponseRecord();
        customersCountRecord.Id    = "default";

        Map<String, Double> kv = customersCountRecord.getThirdMap(metric, date);
        kv.put("customers", customerCount);
        state.recordsMap.put(customersCountRecord.Id, customersCountRecord);
    }


    @Override
    protected boolean isReprocess() {
        return  false;

    }


    @Override
    protected void writeContent(Map<String, ResponseRecord> recordMap, XContentBuilder contentBuilder, String period) throws Exception {

        contentBuilder.startObject(period);                    //"reporting"
        for (ResponseRecord hpRecord : recordMap.values()) {
            CustomerResponseRecord reportRecord = (CustomerResponseRecord) hpRecord;
            contentBuilder.startObject(reportRecord.Id);       //for default
            for (String metric : reportRecord.superWrapperMap.keySet()){
                Map<String, Map<String, Double>> monthMap = reportRecord.superWrapperMap.get(metric);
                if (metric.equals("default")){
                    contentBuilder.startObject(metric);       //metric name
                }
                for(String month : monthMap.keySet()) {
                    if (metric.equals("default")){
                        contentBuilder.startObject(month);    // month
                    }
                    Map<String, Double> keyValueMap = monthMap.get(month);
                    for (String metricField : keyValueMap.keySet()){
                        contentBuilder.field(metricField, keyValueMap.get(metricField));
                    }
                    if (metric.equals("default")){
                        contentBuilder.endObject();
                    }
                }
                if (metric.equals("default")){
                    contentBuilder.endObject();
                }
            }
            contentBuilder.endObject();
        }
        contentBuilder.endObject();
    }

    protected class CustomerResponseRecord extends ResponseRecord{
        protected Map<String,
                             Map<String,
                                     Map<String,Double>>> superWrapperMap = new HashMap<String, Map<String, Map<String,Double>>>();


        //metric
        public boolean containsFirstKey(String metric) {
            return superWrapperMap.containsKey(metric);
        }

        public void putFirstWrapper(String metric, Map<String, Map<String, Double>> secondWrapper) {
            superWrapperMap.put(metric, secondWrapper);
        }

        protected Map<String, Map<String,Double>> getFirstWrapper(String metric) {
            if (!superWrapperMap.containsKey(metric)) {
                putFirstWrapper(metric, new HashMap<String, Map<String, Double>>());
            }
            return superWrapperMap.get(metric);
        }

        //date wrapper
        public boolean containsSecondKey(String metric, String date) {
            Map<String, Map<String, Double>> subWrapper = getFirstWrapper(metric);
            return subWrapper.containsKey(date);
        }

        // key value
        public void putSecondWrapper(String metric, String date, HashMap<String, Double> metricContents) {
            getFirstWrapper(metric).put(date, metricContents);
        }

        Map<String, Double> getThirdMap(String metric, String date) {
            if (!getFirstWrapper(metric).containsKey(date)) {
                putSecondWrapper(metric, date, new HashMap<String,Double>());
            }
            return getFirstWrapper(metric).get(date);
        }

    }
}
