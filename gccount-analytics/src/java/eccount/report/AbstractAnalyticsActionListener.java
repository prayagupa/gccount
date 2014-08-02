package eccount.report;

import eccount.ClientRequest;
import eccount.report.search.AnalyticsRequestBuilder;
import eccount.util.AmountUtils;
import eccount.util.RequestUtils;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author prayagupd
 */

public class AbstractAnalyticsActionListener implements ActionListener<MultiSearchResponse> {
    public AtomicBoolean processCompleted;

    /**
     * Logger propagated down so as to write correctly to configured ES log
     */
    protected ESLogger logger;
    /**
     * Current state of request in this listener
     */
    protected ClientRequest state;

    /**
     * requested report name
     */
    protected String report;

    /**
     * index for ES command execution
     */
    protected Client client;


    public AbstractAnalyticsActionListener() {
    }

    public AbstractAnalyticsActionListener(ESLogger logger, ClientRequest state, Client client, AtomicBoolean processComplete) {
        this.logger = logger;
        this.state = state;
        this.client = client;
        this.processCompleted = processComplete;
    }

    /**
     * duplicate present listener and progress record execution by default. For example: increment reportCount
     *
     * @param duplicate
     * @return
     * @throws Exception
     */
    public AbstractAnalyticsActionListener newActionListener(AbstractAnalyticsActionListener duplicate) throws Exception {
        return AnalyticsActionListeners.newActionListener(duplicate);
    }

    /**
     * Initializes record map. Defaults to linked hash map for key order maintaining by put order
     *
     * @return
     */
    public static Map<String, ResponseRecord> createRecordMap() {
        return new LinkedHashMap<String, ResponseRecord>();

    }

    /**
     * Makes decision to term the loop
     * The ordered facets output can be neglected below the required value( or SIZE )
     *
     * @param i
     * @param entry
     * @return boolean
     */
    protected boolean termLoop(int i, TermsStatsFacet.Entry entry) {
        return !state.noFilter && (i >= state.requestSize || (state.filterByValue && entry.getTotal() <= state.value));
    }

    /**
     * Initialize the Records from the search response of first query
     * The records are kept by state variable so that the output from other child queries are updated on the same records.
     *
     * @param facetEntry
     */
    protected void initRecord(TermsStatsFacet.Entry facetEntry) {
        ResponseRecord customer = new ResponseRecord();
        customer.Id = facetEntry.getTerm().toString();
        customer.total = facetEntry.getTotal();
        state.recordsMap.put(customer.Id, customer);
    }

    protected void initRequestState() {
        try {
            state.recordsMap = createRecordMap();
            state.contentBuilder = XContentFactory.jsonBuilder();
        } catch (Exception e) {
            onFailure(e);
        }
    }

    /**
     * Process the facet output from the child queries.
     * The output need to be updated on the existing records
     *
     * @param statsFacetEntry
     */
    protected void processEntry(TermsStatsFacet.Entry statsFacetEntry) {
        if (!state.recordsMap.containsKey(statsFacetEntry.getTerm().toString().toString())) {
            return;
        }
        if (state.reportCount == 1) {
            state.recordsMap.get(statsFacetEntry.getTerm().toString().toString()).first = statsFacetEntry.getTotal();
        }
        if (state.reportCount == 2) {
            state.recordsMap.get(statsFacetEntry.getTerm().toString().toString()).second = statsFacetEntry.getTotal();
        }
    }

    protected void processStatsFacetEntry(String prefix, TermsStatsFacet.Entry facetEntry) {
        if (!state.recordsMap.containsKey(facetEntry.getTerm().toString().toString())) {
            return;
        }
        if (state.reportCount == 1) {
            if (prefix.contains("customerCost"))
                state.recordsMap.get(facetEntry.getTerm().toString().toString()).first = facetEntry.getTotal();
            else if (prefix.contains("customerCost"))
                state.recordsMap.get(facetEntry.getTerm().toString().toString()).third = facetEntry.getTotal();
            else
                state.recordsMap.get(facetEntry.getTerm().toString().toString()).fourth = facetEntry.getTotal();
        }
        if (state.reportCount == 2) {
            state.recordsMap.get(facetEntry.getTerm().toString().toString()).second = facetEntry.getTotal();
        }
    }

    /**
     * Called once externally,
     * the recursion inside it is controlled by {@linkplain ClientRequest#repeat} and
     * {@linkplain ClientRequest#types}  and finally by {@linkplain #processChild()} implementation
     *
     * @param multiSearchResponse
     */
    @Override
    public void onResponse(MultiSearchResponse multiSearchResponse) {
        long first = new Date().getTime();
        logger.info("Response : " + multiSearchResponse);

        try {
            if (state.reportCount == 0 && state.repeat) {
                initRequestState();
            }
            processSearchResponse(multiSearchResponse);
            if (isReprocess()) {
                logger.debug("reprocessing abstract...");
                reprocess();
            } else {
                if (isRepeat()) {
                    logger.debug("After reporting period, Now executing for comparison period");
                    processAgain();
                } else { //child will write to response upon completion
                    logger.debug("comparison period executed. Now writing content to response");
                    processChild();
                }
            }
        long last=new Date().getTime();
        logger.info("final response completed at " + (last - first) + " ms");
        } catch (Exception e) {
            onFailure(e);
            logger.error("errr " + e.getMessage());
        }
    }

    /**
     * Process the searchResponse
     * Depending upon the {@link ClientRequest} creates new records or appends to the existing ones.
     *
     * @param multiSearchResponse
     */
    protected void processSearchResponse(MultiSearchResponse multiSearchResponse) {
        for (MultiSearchResponse.Item responseItem : multiSearchResponse.getResponses()) {
            SearchResponse response = responseItem.getResponse();

            int i = 0;
            double totalSum = 0d;
            String prefix = "";

            for (Facet facetEntry : response.getFacets().facets()) {
                TermsStatsFacet termsStatsFacet = (TermsStatsFacet) facetEntry;
                if (termsStatsFacet.getName().contains(":")) {
                    prefix = termsStatsFacet.getName();
                }
                for (TermsStatsFacet.Entry stringEntry : termsStatsFacet.getEntries()) {
                    if (!state.findTotal && termLoop(i, stringEntry)) {
                        break; //avoid all subsequent results
                    }
                    if (state.reportCount == 0) {
                        if (!termLoop(i, stringEntry)) {
                            initRecord(stringEntry);
                        }
                        ++i;
                        if (state.findTotal)
                            totalSum += stringEntry.getTotal();
                        continue;
                    }

                    if (prefix == "")
                        processEntry(stringEntry);
                    else
                        processStatsFacetEntry(prefix, stringEntry);
                }
            }
            if (state.findTotal) {
                addTotalEntry(totalSum);
            }
        }
    }

    protected void processAgain() throws Exception {
        AbstractAnalyticsActionListener childActionListener = newActionListener(this);
        ClientRequest childState = childActionListener.state;
        childState.repeat = false;
        childState.reportCount = 0;
        childState.noFilter = false;//reset filtering

        childState.oldRecordsMap = state.recordsMap;
        childState.recordsMap = createRecordMap();
        //build query for child
        final AnalyticsRequestBuilder builder = AnalyticsRequestBuilders.getBuilder(report);
        logger.debug("This is query at comparision side " + builder.toString());
        final MultiSearchRequestBuilder query = builder.query(childActionListener.state, client);
        query.execute(childActionListener);

    }

    protected void addTotalEntry(double totalSum) {
        ResponseRecord m = new ResponseRecord();
        m.Id = "total";
        m.total = totalSum;
        state.recordsMap.put(m.Id, m);
    }

    /**
     * Checks whether to recursively run for next query.
     * Defaults to "checking the number of types"
     *
     * @return
     */
    protected boolean isReprocess() {
        return state.reportCount < state.types.length && state.types.length > 1;
    }

    /**
     * Create child queries and process them
     * Required when the multiple queries need to be processed for a single period
     *
     * @throws Exception
     */
    protected void reprocess() throws Exception {
        AbstractAnalyticsActionListener child = newActionListener(this);
        //build query for child
        MultiSearchRequestBuilder builder = AnalyticsRequestBuilders.getBuilder(report).query(child.state, client);
        logger.debug("child query " + builder.toString());
        builder.execute(child);
    }

    /**
     * Checks whether to repeat the same queries for another period. Defaults to state.repeat variable
     *
     * @return
     */
    protected boolean isRepeat() {
        return state.repeat;
    }

    /**
     * Creates the output report content and send the response via {@link org.elasticsearch.rest.RestChannel}
     */
    protected void processChild() {
        try {
            //defaults to writing content
            String[] period = RequestUtils.getPeriod(state.request);

            XContentBuilder contentBuilder = state.contentBuilder.startObject();

            if (period.length > 1) {
                writeContent(state.oldRecordsMap, contentBuilder, RequestUtils.REPORTING);
                writeContent(state.recordsMap, contentBuilder, RequestUtils.COMPARISON);
            } else writeContent(state.recordsMap, contentBuilder, RequestUtils.REPORTING);
            contentBuilder.endObject();
            processCompleted.set(true);
            //state.channel.sendResponse(new XContentRestResponse(state.request, OK, state.contentBuilder));
        } catch (Exception e) {
            processCompleted.set(true);
            logger.error("error in writing " + e.getMessage());
            onFailure(e);
        }
    }

    /**
     * Builds content {@link XContentBuilder} for a period using the records
     *
     * @param customerIds      record map holding the report for a period
     * @param contentBuilder
     * @param period
     * @throws Exception
     */
    protected void writeContent(Map<String, ResponseRecord> customerIds, XContentBuilder contentBuilder, String period) throws Exception {
        logger.debug("writeContent");
    }

    @Override
    public void onFailure(Throwable e) {
        try {
            processCompleted.set(true);
            e.printStackTrace();
        } catch (Exception e1) {
            processCompleted.set(true);
            logger.error("Failed to send failure response", e1);
        }
    }

    public void setState(ClientRequest state) {
        this.state = state;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setLogger(ESLogger logger) {
        this.logger = logger;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public ESLogger getLogger() {
        return logger;
    }

    public ClientRequest getState() {
        return state;
    }

    public String getReport() {
        return report;
    }

    public Client getClient() {
        return client;
    }


    protected void render() throws Exception {
        String[] period = RequestUtils.getPeriod(state.request);
        XContentBuilder contentBuilder = state.contentBuilder.startObject();
        writeContent(state.oldRecordsMap, contentBuilder, RequestUtils.REPORTING);
        if (period.length > 1)
            writeContent(state.recordsMap, contentBuilder, RequestUtils.COMPARISON);
        contentBuilder.endObject();
        processCompleted.set(true);
        logger.debug("writing response");
    }



    private static String getConcatenatedString(Set<String> programsSet) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String program : programsSet) {
            stringBuilder.append(program).append(",");
        }

        String program = stringBuilder.toString();
        if (!program.isEmpty())
            program = program.substring(0, program.length() - 1);
        return program;
    }

    /**
     * ResponseRecord
     */
    public static class ResponseRecord {
        public String Id;
        public double total;

        public double first = Double.MIN_VALUE;
        public double second = Double.MIN_VALUE;
        public double third = Double.MIN_VALUE;
        public double fourth = Double.MIN_VALUE;

        public long field_long_1 = -2;

        public String field_string_1;
        public String field_string_2;
        public String field_string_3;
        public String field_string_4;
        public String field_string_5;
        public String field_string_6;

        public int field_int_1;

        Set<String> types = null;

        public void addProgramType(String type) {
            if (types == null)
                types = new HashSet<String>();
            types.add(type);
        }

        public String getProgramTypes() {
            return getConcatenatedString(types);
        }
    }

    public ResponseRecord getRecord(String id) {
        ResponseRecord r = new ResponseRecord();
        r.Id = id;
        return r;
    }

}
