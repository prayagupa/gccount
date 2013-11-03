package eccount.action;

import eccount.ClientRequest;
import eccount.action.search.AnalyticsRequestBuilder;
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
 * @author: prayagupd
 */

public class AbstractAnalyticsActionListener implements ActionListener<MultiSearchResponse> {
    public AtomicBoolean processComplete;

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
        this.processComplete = processComplete;
    }

    /**
     * copy present listener and progress record execution by default. For example: increment reportCount
     *
     * @param copy
     * @return
     * @throws Exception
     */
    public AbstractAnalyticsActionListener newActionListener(AbstractAnalyticsActionListener copy) throws Exception {
        return AnalyticsActionListeners.newActionListener(copy);
    }

    /**
     * Initializes record map. Defaults to linked hash map for key order maintaining by put order
     *
     * @return
     */
    public static Map<String, Record> createRecordMap() {
        return new LinkedHashMap<String, Record>();

    }

    /**
     * Makes decision to term the loop
     * The ordered facets output can be neglected below the required value or SIZE
     *
     * @param i
     * @param entry
     * @return
     */
    protected boolean termLoop(int i, TermsStatsFacet.Entry entry) {
        return !state.noFilter && (i >= state.requestSize || (state.filterByValue && entry.getTotal() <= state.value));
    }

    /**
     * Initialize the Records from the search response of first query
     * The records are kept by state variable so that the output from other child queries are updated on the same records.
     *
     * @param entry
     */
    protected void initRecord(TermsStatsFacet.Entry entry) {
        Record m = new Record();
        m.Id = entry.getTerm().toString();
        m.total = entry.getTotal();
        state.recordIds.put(m.Id, m);
    }

    protected void initState() {
        try {
            state.recordIds = createRecordMap();
            state.contentBuilder = XContentFactory.jsonBuilder();
        } catch (Exception e) {
            onFailure(e);
        }
    }

    /**
     * Process the facet output from the child queries.
     * The output need to be updated on the existing records
     *
     * @param stringEntry
     */
    protected void processEntry(TermsStatsFacet.Entry stringEntry) {
        if (!state.recordIds.containsKey(stringEntry.getTerm().toString().toString())) {
            return;
        }
        if (state.reportCount == 1) {
            state.recordIds.get(stringEntry.getTerm().toString().toString()).first = stringEntry.getTotal();
        }
        if (state.reportCount == 2) {
            state.recordIds.get(stringEntry.getTerm().toString().toString()).second = stringEntry.getTotal();
        }
    }

    protected void processEntry(String prefix, TermsStatsFacet.Entry stringEntry) {
        if (!state.recordIds.containsKey(stringEntry.getTerm().toString().toString())) {
            return;
        }
        if (state.reportCount == 1) {
            if (prefix.contains("customerCost"))
                state.recordIds.get(stringEntry.getTerm().toString().toString()).first = stringEntry.getTotal();
            else if (prefix.contains("customerCost"))
                state.recordIds.get(stringEntry.getTerm().toString().toString()).third = stringEntry.getTotal();
            else
                state.recordIds.get(stringEntry.getTerm().toString().toString()).fourth = stringEntry.getTotal();
        }
        if (state.reportCount == 2) {
            state.recordIds.get(stringEntry.getTerm().toString().toString()).second = stringEntry.getTotal();
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
                initState();
            }
            processSearchResponse(multiSearchResponse);
            if (doReprocess()) {
                logger.debug("reprocessing abstract...");
                reprocess();
            } else {
                if (repeat()) {
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

            for (Facet entry : response.getFacets().facets()) {
                TermsStatsFacet termFacet = (TermsStatsFacet) entry;
                if (termFacet.getName().contains(":")) {
                    prefix = termFacet.getName();
                }
                for (TermsStatsFacet.Entry stringEntry : termFacet.getEntries()) {
                    if (!state.findTotal && termLoop(i, stringEntry)) {
                        break;//avoid all subsequent results
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
                        processEntry(prefix, stringEntry);
                }
            }
            if (state.findTotal) {
                addTotalEntry(totalSum);
            }
        }
    }

    protected void processAgain() throws Exception {
        AbstractAnalyticsActionListener child = newActionListener(this);
        ClientRequest childState = child.state;
        childState.repeat = false;
        childState.reportCount = 0;
        childState.noFilter = false;//reset filtering

        childState.oldRecordIds = state.recordIds;
        childState.recordIds = createRecordMap();
        //build query for child
        final AnalyticsRequestBuilder builder = AnalyticsRequestBuilders.getBuilder(report);
        logger.debug("This is query at comparision side " + builder.toString());
        final MultiSearchRequestBuilder query = builder.query(child.state, client);
        query.execute(child);

    }

    protected void addTotalEntry(double totalSum) {
        Record m = new Record();
        m.Id = "total";
        m.total = totalSum;
        state.recordIds.put(m.Id, m);
    }

    /**
     * Checks whether to recursively run for next query. Defaults to checking the number of types
     *
     * @return
     */
    protected boolean doReprocess() {
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
    protected boolean repeat() {
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
                writeContent(state.oldRecordIds, contentBuilder, RequestUtils.REPORTING);
                writeContent(state.recordIds, contentBuilder, RequestUtils.COMPARISON);
            } else writeContent(state.recordIds, contentBuilder, RequestUtils.REPORTING);
            contentBuilder.endObject();
            processComplete.set(true);
            //state.channel.sendResponse(new XContentRestResponse(state.request, OK, state.contentBuilder));
        } catch (Exception e) {
            processComplete.set(true);
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
    protected void writeContent(Map<String, Record> customerIds, XContentBuilder contentBuilder, String period) throws Exception {
        contentBuilder.startArray(period);
        String first = state.types[0].toLowerCase();
        String second = "";
        if (state.types.length > 1) {
            second = state.types[1].toLowerCase();
        }
        long first_total = 0l;
        long second_total = 0l;

        for (Record customer : customerIds.values()) {

            contentBuilder.startObject();
            contentBuilder.field(state.keyField, customer.Id);
            first_total += AmountUtils.getLong(customer.first);
            contentBuilder.field(first, AmountUtils.getAmount(customer.first));
            if (!second.isEmpty()) {
                second_total += AmountUtils.getLong(customer.second);
                contentBuilder.field(second, AmountUtils.getAmount(customer.second));
            }
            contentBuilder.field("total", AmountUtils.getAmount(customer.total));
            if (customer.field_string_1 != null) {
                contentBuilder.field("primaryPaymentCodeDesc", customer.field_string_1);
            }
            if (customer.field_long_1 > -1)
                contentBuilder.field("riskScore", AmountUtils.getAmount(customer.field_long_1));
            if (customer.field_string_2 != null) {
                contentBuilder.field("relationshipId", customer.field_string_2);
            }
            if (customer.field_string_3 != null) {
                contentBuilder.field("customerGender", customer.field_string_3);
            }
            if (customer.field_string_4 != null) {
                contentBuilder.field("currentStatus", customer.field_string_4);
            }
            if (customer.field_string_5 != null) {
                contentBuilder.field("groupId", customer.field_string_5);
            }
            if (customer.field_string_6 != null) {
                contentBuilder.field("unblindCustomerId", customer.field_string_6);
            }
            if (customer.field_int_1 >= 0) {
                contentBuilder.field("customerAge", customer.field_int_1);
            }
            if (customer.types != null && !customer.getProgramTypes().isEmpty())
                contentBuilder.field("program_type", customer.getProgramTypes());
            contentBuilder.endObject();
        }
        contentBuilder.startObject();
        contentBuilder.field(first, AmountUtils.getAmount(first_total));
        if (!second.isEmpty()) contentBuilder.field(second, AmountUtils.getAmount(second_total));
        contentBuilder.field("total", AmountUtils.getAmount(first_total + second_total));
        contentBuilder.field("countOfCustomer", customerIds.size());
        contentBuilder.endObject();
        contentBuilder.endArray();
        logger.debug("content generated");
    }

    @Override
    public void onFailure(Throwable e) {
        try {
            processComplete.set(true);
            e.printStackTrace();
        } catch (Exception e1) {
            processComplete.set(true);
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
        writeContent(state.oldRecordIds, contentBuilder, RequestUtils.REPORTING);
        if (period.length > 1)
            writeContent(state.recordIds, contentBuilder, RequestUtils.COMPARISON);
        contentBuilder.endObject();
        processComplete.set(true);
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
     * Record
     */
    public static class Record {
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

    public Record getRecord(String id) {
        Record r = new Record();
        r.Id = id;
        return r;
    }

}
