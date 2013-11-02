package eccount.action;

import eccount.util.AmountUtils;
import eccount.util.SummableMap;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;

import java.net.URL;
import java.util.Map;

/**
 * @author : prayag
 */
public class SearchAnalyticsActionListener extends AbstractAnalyticsActionListener {

    @Override
    public void onResponse(MultiSearchResponse response) {
        super.onResponse(response);
    }

    @Override
    protected void processResponse(MultiSearchResponse multiSearchResponse) {
        MultiSearchResponse.Item[] responseItems = multiSearchResponse.getResponses();
        MultiSearchResponse.Item item1 = responseItems[0];
        SearchResponse response1 = item1.getResponse();
        processCounts(response1);

    }


    private void processCounts(SearchResponse response) {

        long customerCount = 0l;

        for (Facet entry : response.getFacets().facets()) {


            //customerCount facet...
            if (entry.getName().equalsIgnoreCase("customerCount")) {
                TermsStatsFacet termFacet = (TermsStatsFacet) entry;
                for (TermsStatsFacet.Entry stringEntry : termFacet.getEntries()) {
                    customerCount += stringEntry.getCount();
                }
            }
        }


        Record _memberCount = new Record();
        _memberCount.Id = "customers";
        _memberCount.first = customerCount;
        state.recordIds.put(_memberCount.Id, _memberCount);
    }


    @Override
    protected boolean doReprocess() {
        return  false;

    }


    @Override
    protected void writeContent(Map<String, Record> records, XContentBuilder contentBuilder, String period) throws Exception {
        contentBuilder.startArray(period);
        contentBuilder.startObject();
        for (Record record : records.values()) {
            if (record.Id == null)
                contentBuilder.field("recordId", "null");
            else
                contentBuilder.field(record.Id, record.first);
        }
        contentBuilder.endObject();
        contentBuilder.endArray();
        logger.debug("content generated");
    }

}
