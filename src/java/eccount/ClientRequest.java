package eccount;

import eccount.SearchRequest;
import eccount.action.AbstractAnalyticsActionListener;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilder;

import java.util.Date;
import java.util.Map;

/**
  * State object for report requests.
  * @author prayagupd
  */

public class ClientRequest {
    public long time;
    public int requestSize;
    public boolean filterByValue;
    public String field;
    public double value;
    public SearchRequest request;
    public boolean repeat = true;           //repeat same query for comparison period
    public XContentBuilder contentBuilder;
    public Map<String, AbstractAnalyticsActionListener.Record> recordIds;
    public Map<String, AbstractAnalyticsActionListener.Record> oldRecordIds;
    public int reportCount = 0;
    public boolean noFilter = true;//by default no filtering
    public String[] types;
    public boolean findTotal = false; // make this true only when you want to iterate all values to find total
    public boolean preprocess = true;
    public String report;
    public Map<String, FilterBuilder> filterBuilders;

    public ClientRequest(String report, String field, SearchRequest request, String[] types) {
        this.report = report;
        this.field = field;
        this.request = request;
        this.types = types;
    }

    public String type() {
        if (reportCount > 0)
            return types[reportCount - 1];
        return "all";
    }

    public String period() {
        if (repeat) return "reporting";
        return "comparison";
    }

    public ClientRequest(String report,
                         int requestSize,
                         String field,
                         boolean filterByValue,
                         double value,
                         SearchRequest request,
                         String[] types) {
        this.report=report;
        this.requestSize = requestSize;
        this.filterByValue = filterByValue;
        this.field = field;
        this.value = value;
        this.request = request;
        this.types = types;
        this.time=new Date().getTime();
    }

    /**
     * Copy constructor that copies everything form  the current state and
     * takes care of new state propagation while forming new listener by updating required state variables accordingly.
     *
     * @param copy
     */
    public ClientRequest(ClientRequest copy) {
        this.report=copy.report;
        this.requestSize = copy.requestSize;
        this.filterByValue = copy.filterByValue;
        this.field = copy.field;
        this.value = copy.value;
        this.request = copy.request;
        this.types = copy.types;
        //add copy non-construct parameters
        this.recordIds = copy.recordIds;
        this.oldRecordIds = copy.oldRecordIds;
        this.contentBuilder = copy.contentBuilder;
        this.repeat = copy.repeat;
        this.reportCount = copy.reportCount + 1;
        this.time=new Date().getTime();
        this.preprocess = copy.preprocess;
        this.filterBuilders = copy.filterBuilders;
    }

    @Override
    public String toString(){
        return "type :"+(types.length==1?types[0]:types.length)+", report Count :"+reportCount;
    }

    public boolean isPaidThrough(){
        return true;
        //return "serviceDate".equals(QueryUtils.parseBasis(request)) && (request.hasParameter("comparisonPaidThrough") || request.hasParameter("reportingPaidThrough"));
    }

    public String periodTo() {
        return request.get(period() + "To");
    }
    public String periodFrom() {
        return request.get(period() + "From");
    }
}
