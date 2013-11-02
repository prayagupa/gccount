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
    Map<String, String> posAggregator;

    @Override
    public void onResponse(MultiSearchResponse response) {
        super.onResponse(response);
    }

    @Override
    protected void processResponse(MultiSearchResponse multiSearchResponse) {
        MultiSearchResponse.Item[] responseItems = multiSearchResponse.getResponses();

        MultiSearchResponse.Item item1 = responseItems[0];
        MultiSearchResponse.Item item2 = responseItems[1];
        MultiSearchResponse.Item item3 = responseItems[2];


        SearchResponse response1 = item1.getResponse();
        processMedicalAmountByPOS(response1);

        SearchResponse response2 = item2.getResponse();
        processCounts(response2);

        SearchResponse response3 = item3.getResponse();
        processPharmacyAmount(response3);

    }


    private void processWorkersCompClaimsPaid(SearchResponse response) {

        double totalClaimsPaid=0;
        StatisticalFacet facet = response.getFacets().facet("workersCompensationClaimsPaid_stats");
        totalClaimsPaid = facet.getTotal();
        Record workersCompClaimsPaid = new Record();
        workersCompClaimsPaid.Id = "totalWorkersCompClaimsPaid";
        workersCompClaimsPaid.first = AmountUtils.getAmount(totalClaimsPaid);
        state.recordIds.put(workersCompClaimsPaid.Id, workersCompClaimsPaid);

    }

    /**
     * Aggregate the medical amount by PlaceOfService Aggregator
     * @param response
     */
    private void processMedicalAmountByPOS(SearchResponse response) {

        URL url=SearchAnalyticsActionListener.class.getClassLoader().getResource("Resources/pOSAggregator.csv");

        //posAggregator = FileUtils.readFile(url.getFile(), new FileUtils.POSAggregatorParser());
        Map<String, Object> summary = new SummableMap<String, Object>();
        summary.put("Inpatient", 0l);
        summary.put("Outpatient", 0l);
        summary.put("Office", 0l);

        for (Facet entry : response.getFacets().facets()) {
            TermsStatsFacet termFacet = (TermsStatsFacet) entry;
            for (TermsStatsFacet.Entry stringEntry : termFacet.getEntries()) {
                String pos = posAggregator.get(stringEntry.getTerm().toString());
                summary.put(pos, AmountUtils.getLong(stringEntry.getTotal()));
            }
        }
        long total_medical = 0l;
        for (Map.Entry<String, Object> posAmountEnty : summary.entrySet()) {
            Record r = new Record();
            r.Id = posAmountEnty.getKey();
            r.first = AmountUtils.getAmount((Long) posAmountEnty.getValue());
            total_medical += (Long) posAmountEnty.getValue();
            state.recordIds.put(r.Id, r);
        }
        Record total = new Record();
        total.Id = "totalMedicalPaidAmount";
        total.first = AmountUtils.getAmount(total_medical);
        state.recordIds.put(total.Id, total);
    }

    private void processCounts(SearchResponse response) {
        //memberMonths variables...
        long memberMonths = 0l;

        //member/subscriber count variables...
        long memberCount = 0l;
        long subscriberCount=0l;

        for (Facet entry : response.getFacets().facets()) {
            //memberMonths facet...
            if (entry.getName().startsWith("memberMonths")) {
                TermsStatsFacet termsStatsFacet = (TermsStatsFacet) entry;
                for (TermsStatsFacet.Entry stringEntry : termsStatsFacet.getEntries()) {
                    memberMonths += stringEntry.getCount();
                }
            }

            //memberCount facet...
            else if (entry.getName().equalsIgnoreCase("memberCount")) {
                TermsStatsFacet termFacet = (TermsStatsFacet) entry;
                for (TermsStatsFacet.Entry stringEntry : termFacet.getEntries()) {
                    memberCount += stringEntry.getCount();
                }
            }
            //subscriberCount facet...
            else if (entry.getName().equalsIgnoreCase("subscriberCount")) {
                TermsStatsFacet termFacet = (TermsStatsFacet) entry;
                for (TermsStatsFacet.Entry stringEntry : termFacet.getEntries()) {
                    subscriberCount += stringEntry.getCount();
                }
            }
        }

        Record _memberMonths = new Record();
        _memberMonths.Id = "memberMonths";
        _memberMonths.first = memberMonths;
        state.recordIds.put(_memberMonths.Id, _memberMonths);


        Record _memberCount = new Record();
        _memberCount.Id = "members";
        _memberCount.first = memberCount;
        state.recordIds.put(_memberCount.Id, _memberCount);

        Record _subscriberCount=new Record();
        _subscriberCount.Id="subscribers";
        _subscriberCount.first=subscriberCount;
        state.recordIds.put(_subscriberCount.Id,_subscriberCount);
    }

    private void processPharmacyAmount(SearchResponse response){
        long pharmacyAmount=0l;
        for(Facet entry:response.getFacets().facets()){
            StatisticalFacet statFacet=(StatisticalFacet)entry;
            pharmacyAmount+=AmountUtils.getLong(statFacet.getTotal());
        }
        Record _pharmacyAmount=new Record();
        _pharmacyAmount.Id="totalPharmacyPaidAmount";
        _pharmacyAmount.first=AmountUtils.getAmount(pharmacyAmount);
        state.recordIds.put(_pharmacyAmount.Id,_pharmacyAmount);
    }



    @Override
    protected boolean doReprocess() {
        if (state.request.hasParameter("isWrc")){
            return (state.reportCount < 3);
        }  else {
            return (state.reportCount < 2);
        }
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
