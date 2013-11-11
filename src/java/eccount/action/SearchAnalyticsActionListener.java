package eccount.action;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;

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
    protected void processSearchResponse(MultiSearchResponse multiSearchResponse) {
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

        ResponseRecord _customersCount = new ResponseRecord();
        _customersCount.Id = "customers";
        _customersCount.first = customerCount;
        state.recordsMap.put(_customersCount.Id, _customersCount);
    }


    @Override
    protected boolean isReprocess() {
        return  false;

    }


    @Override
    protected void writeContent(Map<String, ResponseRecord> customerIds, XContentBuilder contentBuilder, String period) throws Exception {
        contentBuilder.startArray(period);
        contentBuilder.startObject();
        for (ResponseRecord responseRecord : customerIds.values()) {
            if (responseRecord.Id == null)
                contentBuilder.field("recordId", "null");
            else
                contentBuilder.field(responseRecord.Id, responseRecord.first);
        }
        contentBuilder.endObject();
        contentBuilder.endArray();
        logger.debug("content generated");
    }

}
