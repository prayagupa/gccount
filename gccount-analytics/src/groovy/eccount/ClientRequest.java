package eccount;

import eccount.report.AbstractAnalyticsActionListener;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilder;

import java.util.Date;
import java.util.Map;

/**
  * State object for reportName requests.
  * @author prayagupd
  */

public class ClientRequest {
    public long time;
    public int requestSize;
    public boolean filterByValue;
    public String keyField;
    public double value;
    public SearchRequest request;
    public boolean repeat = true;           //repeat same query for comparison period
    public XContentBuilder contentBuilder;
    public Map<String, AbstractAnalyticsActionListener.ResponseRecord> recordsMap;
    public Map<String, AbstractAnalyticsActionListener.ResponseRecord> oldRecordsMap;
    public int reportCount = 0;
    public boolean noFilter = true;   //by default no filtering
    public String[] types;            // recordTypes (eg "Customer", "Transaction")
    public boolean findTotal = false; // make this true only when you want to iterate all values to find total
    public boolean preprocess = true;
    public String report;
    public Map<String, FilterBuilder> filterBuilders;

    public ClientRequest(String reportName, String keyField, SearchRequest request, String[] types) {
        this.report = reportName;
        this.keyField = keyField;
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
                         String keyField,
                         boolean filterByValue,
                         double value,
                         SearchRequest request,
                         String[] types) {
        this.report=report;
        this.requestSize = requestSize;
        this.filterByValue = filterByValue;
        this.keyField = keyField;
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
        this.keyField = copy.keyField;
        this.value = copy.value;
        this.request = copy.request;
        this.types = copy.types;
        //add copy non-construct parameters
        this.recordsMap = copy.recordsMap;
        this.oldRecordsMap = copy.oldRecordsMap;
        this.contentBuilder = copy.contentBuilder;
        this.repeat = copy.repeat;
        this.reportCount = copy.reportCount + 1;
        this.time=new Date().getTime();
        this.preprocess = copy.preprocess;
        this.filterBuilders = copy.filterBuilders;
    }

    @Override
    public String toString(){
        return "type :"+(types.length==1?types[0]:types.length)+", reportName Count :"+reportCount;
    }

    public boolean isPaidThrough(){
        return true;
    }

    public String periodTo() {
        return request.get(period() + "To");
    }
    public String periodFrom() {
        return request.get(period() + "From");
    }
}
