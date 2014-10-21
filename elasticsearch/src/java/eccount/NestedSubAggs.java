package enterprises.es.support;

import com.deerwalk.das.commonutils.DateUtils;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : prayagupd
 * @desc : NestedSubAggs
 * @date : 6/24/14
 */
public final class NestedSubAggs {

    public static enum AggsType {
        SUM,
        AVG,
        TERMS,
        DATE_RANGE,
        COUNT,
        FILTER_TERMS,
        FILTER_TERM,
        MISSING_FILTER,
        NOT_MISSING_FILTER,
        AND_FILTER
    }

    private Logger logger = LoggerFactory.getLogger(NestedSubAggs.class);
    public String[] name;
    public String[] field;
    public String[] value;
    public AggsType[] aggsType;
    public AggsType zygoteAggs;
    public Map<String, NestedSubAggs> nestedSubAggsMap = new LinkedHashMap<String, NestedSubAggs>();
    public AbstractAggregationBuilder aggregationBuilder;
    public AndFilterBuilder andFilter = new AndFilterBuilder();

    public NestedSubAggs aggregations() {
        return new NestedSubAggs();
    }

    public NestedSubAggs withName(String[] name) {
        this.name = name;
        return this;
    }

    public NestedSubAggs onField(String[] field) {
        this.field = field;
        return this;
    }

    public NestedSubAggs withValue(String[] value) {
        this.value = value;
        return this;
    }

    public NestedSubAggs ofAggsType(AggsType[] aggsType) {
        this.aggsType = aggsType;
        return this;
    }

    public NestedSubAggs ofAggsType(AggsType aggsType, AggsType[] aggsTypes) {
        this.zygoteAggs = aggsType;
        this.aggsType = aggsTypes;
        return this;
    }

    public NestedSubAggs build(HashMap<String, HashMap<String, String>> map, String flag) {
        buildDate(map, flag);
        return this;
    }

    public NestedSubAggs build(final String repfromDate, final String reptoDate, String flag) {

        HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>(){{ put(repfromDate,
                new HashMap<String,
                String>(){{put(repfromDate, reptoDate);}});}};
        buildDate(map, flag);
        return this;
    }

    private void buildDate(HashMap<String, HashMap<String, String>> map, String flag) {
        String repfromDate = "";
        String reptoDate = "";
        for (Map.Entry<String, HashMap<String, String>> keyMonthRow : map.entrySet()) {
            final String fromMonthFormatted = keyMonthRow.getKey();
            final HashMap<String, String> monthMap = keyMonthRow.getValue();
            for (Map.Entry<String, String> month : monthMap.entrySet()){
                repfromDate = month.getKey();
                reptoDate = month.getValue();
            }
        }
        if(flag.equals("trend")){
            this.aggregationBuilder = (DateRangeBuilder) dateRangeAggs(this.name[0], this.field[0]);
            for (Map.Entry<Long, Long> month : DateUtils.getPreceedingMonth(repfromDate).entrySet()) {
                final String strFromMonth = DateUtils.getStringDate(month.getKey());
                final String strToMonth = DateUtils.getStringDate(month.getValue() + TimeUnit.DAYS.toMillis(1));
                final String fromMonthFormatted = DateUtils.getMonthYearForDate(DateUtils.getTimeFromDateWrtTimeZone(strFromMonth));
                ((DateRangeBuilder) this.aggregationBuilder).addRange(fromMonthFormatted, strFromMonth, strToMonth);
            }

            for (Map.Entry<Long, Long> month : DateUtils.getAllMonths(repfromDate, reptoDate).entrySet()) {
                final String strFromMonth = DateUtils.getStringDate(month.getKey());
                final String strToMonth = DateUtils.getStringDate(month.getValue() + TimeUnit.DAYS.toMillis(1));
                final String fromMonthFormatted = DateUtils.getMonthYearForDate(DateUtils.getTimeFromDateWrtTimeZone(strFromMonth));
                ((DateRangeBuilder) this.aggregationBuilder).addRange(fromMonthFormatted, strFromMonth, strToMonth);
            }
        } else if ("yearly".equals(flag)){
            this.aggregationBuilder = (DateRangeBuilder) dateRangeAggs(this.name[0], this.field[0]);
            final String strToMonth = DateUtils.getStringDate(DateUtils.getTimeFromDateWrtTimeZone(reptoDate) + + TimeUnit
                    .DAYS.toMillis(1));
            final String fromMonthFormatted = DateUtils.getMonthYearForDate(DateUtils.getTimeFromDateWrtTimeZone
                    (repfromDate));
            ((DateRangeBuilder) this.aggregationBuilder).addRange(fromMonthFormatted, repfromDate, strToMonth);

        } else if ("map".equals(flag)){
            this.aggregationBuilder = (DateRangeBuilder) dateRangeAggs(this.name[0], this.field[0]);
            for (Map.Entry<String, HashMap<String, String>> keyMonthRow : map.entrySet()) {
                final String fromMonthFormatted = keyMonthRow.getKey();
                final HashMap<String, String> monthMap = keyMonthRow.getValue();
                for (Map.Entry<String, String> month : monthMap.entrySet()){
                        final String strFromMonth = month.getKey();
                        final String strToMonth = DateUtils.getStringDate(DateUtils.getTimeFromDateWrtTimeZone(month.getValue())
                            + TimeUnit.DAYS.toMillis(1));
                    logger.debug(fromMonthFormatted + " : " + strFromMonth + "=>" + strToMonth);
                    ((DateRangeBuilder) this.aggregationBuilder).addRange(fromMonthFormatted, strFromMonth, strToMonth);
                }
            }
        }else {
            this.aggregationBuilder = (DateRangeBuilder) dateRangeAggs(this.name[0], this.field[0]);
            for (Map.Entry<Long, Long> month : DateUtils.getAllMonths(repfromDate, reptoDate).entrySet()) {
                final String strFromMonth = DateUtils.getStringDate(month.getKey());
                final String strToMonth = DateUtils.getStringDate(month.getValue() + TimeUnit.DAYS.toMillis(1));
                final String fromMonthFormatted = DateUtils.getMonthYearForDate(DateUtils.getTimeFromDateWrtTimeZone(strFromMonth));
                ((DateRangeBuilder) this.aggregationBuilder).addRange(fromMonthFormatted, strFromMonth, strToMonth);
            }
        }
        logger.debug(this.aggsType + " aggregationBuilder => " + this.aggregationBuilder);
    }

    public NestedSubAggs build() {
        if (AggsType.AND_FILTER.equals(this.zygoteAggs)){
            this.andFilter  = new AndFilterBuilder();
            for(int index = 0 ; index < field.length ; index ++){
                logger.debug("this.field.length => " + this.aggsType.length);
                if(this.aggsType[index].equals(AggsType.NOT_MISSING_FILTER)){
                    this.andFilter.add(FilterBuilders.notFilter(FilterBuilders.missingFilter(this.field[index])));
                } else if(this.aggsType[index].equals(AggsType.FILTER_TERM)){
                    this.andFilter.add(FilterBuilders.termFilter(this.field[index], this.value[index]));
                } else if(this.aggsType[index].equals(AggsType.FILTER_TERMS)){
                    this.andFilter.add(FilterBuilders.termsFilter(this.field[index], this.value[index]));
                }
            }
            this.aggregationBuilder = andFilterAggs(this.name[0], this.andFilter);
            return this;
        } else {
            if (this.aggsType[0].equals(AggsType.COUNT)) {
                AbstractAggregationBuilder abstractAggregationBuilder = countAggs(this.name[0], this.field[0]);
                this.aggregationBuilder = abstractAggregationBuilder;
            } else if (this.aggsType[0].equals(AggsType.SUM)) {
                this.aggregationBuilder = sumAggs(this.name[0], this.field[0]);
            } else if (this.aggsType[0].equals(AggsType.TERMS)){
                this.aggregationBuilder = termsAgg(this.name[0], this.field[0]);
            } else if(this.aggsType[0].equals(AggsType.MISSING_FILTER)){
                this.aggregationBuilder = filterAggs(this.name[0], FilterBuilders.missingFilter(this.field[0]));
            } else if(this.aggsType[0].equals(AggsType.NOT_MISSING_FILTER)){
                this.aggregationBuilder = filterAggs(this.name[0], FilterBuilders.notFilter(FilterBuilders.missingFilter(this.field[0])));
            } else if(this.aggsType[0].equals(AggsType.AVG)){
                this.aggregationBuilder = avgAggs(this.name[0], this.field[0]);
            } else if (this.aggsType[0].equals(AggsType.FILTER_TERM)){
                this.aggregationBuilder = filterAggs(this.name[0], FilterBuilders.termFilter(this.field[0], this.value[0]));
            } else if (this.aggsType[0].equals(AggsType.FILTER_TERMS)){
                this.aggregationBuilder = filterAggs(this.name[0], FilterBuilders.termsFilter(this.field[0], this.value[0]));
            }
            logger.debug(this.aggsType + " aggregationBuilder => " + this.aggregationBuilder);
            return this;
        }

    }

    public NestedSubAggs generate() {
        logger.debug("NestedSubAggs size => {}", this.nestedSubAggsMap.size());
        for (Map.Entry<String, NestedSubAggs> aggs : this.nestedSubAggsMap.entrySet()) {
            logger.debug("Mapped NestedSubAggs for " + this.name[0] + " " + " => " + aggs.getValue());
            ((AggregationBuilder) this.aggregationBuilder).subAggregation(aggs.getValue().aggregationBuilder);
        }

        return this;
    }

    public NestedSubAggs hasNestedSubAggs(NestedSubAggs nestedSubAggs) {
        logger.debug("hasNestedSubAggs => " + nestedSubAggs.field[0]);
        this.nestedSubAggsMap.put(nestedSubAggs.name[0], nestedSubAggs);
        return this;
    }

    public AbstractAggregationBuilder countAggs(String aggsName, String fieldName) {
        logger.debug("Aggs Name => " + aggsName + " ; Field Name => " + fieldName);
        return AggregationBuilders.count(aggsName).field(fieldName);
    }

    public AbstractAggregationBuilder dateRangeAggs(String aggsName, String fieldName) {
        return AggregationBuilders.dateRange(aggsName).field(fieldName);

    }

    public AggregationBuilder termsAgg(String aggsName, String field) {
        return AggregationBuilders.terms(aggsName).field(field).size(0);
    }

    public AggregationBuilder filterAggs(String name, FilterBuilder filter) {
        return AggregationBuilders.filter(name).filter(filter);
    }

    public AggregationBuilder andFilterAggs(String name, AndFilterBuilder filters){
        return AggregationBuilders.filter(name).filter(filters);
    }

    public AbstractAggregationBuilder sumAggs(String aggsName, String fieldName) {
        return AggregationBuilders.sum(aggsName).field(fieldName);
    }

    public AbstractAggregationBuilder avgAggs(String aggsName, String fieldName) {
        return AggregationBuilders.avg(aggsName).field(fieldName);
    }

    public String toString() {
        return this.aggsType[0] + " aggregating on " + this.field[0] + "";
    }

}
