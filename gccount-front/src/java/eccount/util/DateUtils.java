package eccount.util;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Months;
import org.joda.time.Years;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    private final static DateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat usDateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private final static TimeZone timeZone = TimeZone.getTimeZone("GMT");
    private final static DateTimeZone dateTimeZone = DateTimeZone.forID("GMT");
    private final static SimpleDateFormat monthYearformatter = new SimpleDateFormat("MMM-yyyy");

    public static void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        DateTimeZone.setDefault(DateTimeZone.forID("GMT"));
    }

    /**
     * Method returns Month and year in Numeric format for given long date
     * @param time
     * @return
     */
    public static String getMonthAndYearForDate (long time) {
        String date = sqlDateFormat.format(time);
        String[] split = date.split("-");
        return split[1] +"-"+ split[0];
    }

    /**
     * Method returns mid day of the month for given long dateformat
     * @param time
     * @return
     */
    public static String getMidDate (long time) {
        String date = sqlDateFormat.format(time);
        String[] split = date.split("-");
        return split[0] + "-" + split[1] + "-15";
    }

    public static long getTimeFromDateWrtTimeZone(String dateStr) {
        DateFormat formatter;
        sqlDateFormat.setTimeZone(timeZone);
        usDateFormat.setTimeZone(timeZone);
        String[] parts = dateStr.split("-");
        if (parts[0].length() > 2)
            formatter = sqlDateFormat;
        else
            formatter = usDateFormat;
        if (parts.length == 2) { //contains only MM-YYYY
            dateStr = parts[1] + "-" + parts[0] + "-" + "01";
            formatter = sqlDateFormat;
        }
        formatter.setTimeZone(timeZone); // change the time zone of this formatter
        Date date = null;
        try {
            date = formatter.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return Long.MIN_VALUE;
        }
    }

    public static Calendar getCalendar(String dateStr) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(getTimeFromDateWrtTimeZone(dateStr));
        return calendar;
    }
    public static String getMonthForDate(String dateStr){
        Calendar cal=getCalendar(dateStr);
        return cal.get(Calendar.YEAR)+"-"+String.format("%02d",cal.get(Calendar.MONTH)+1)+"-15";
    }

    public static Calendar getCalendar(long time) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public static long getLastDateOfMonth(String dateString) {
        long date = getTimeFromDateWrtTimeZone(dateString);
        Calendar cal = getCalendar(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static long getFirstDateOfMonth(String dateString) {
        long date = getTimeFromDateWrtTimeZone(dateString);
        Calendar cal = getCalendar(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static DateTime getDateTime(String dateString) {
        long date = getTimeFromDateWrtTimeZone(dateString);
        return getDateTime(date);
    }

    public static DateTime getDateTime(long date) {
        return new DateTime(date, dateTimeZone);
    }

    public static Map<Long, Long> getAllMonths(String from, String to) {
        Map<Long, Long> ranges = new TreeMap<Long, Long>();
        DateTime startMonth = getDateTime(from).dayOfMonth().withMinimumValue();
        DateTime endMonth = getDateTime(to).dayOfMonth().withMaximumValue();
        Months months = Months.monthsBetween(startMonth, endMonth);
        for (int i = 0; i <= months.getMonths(); i++) {
            ranges.put(getMinDate(startMonth), getMaxDate(startMonth));
            startMonth = startMonth.plusMonths(1);
        }
        return ranges;
    }

    public static long getMaxDate(DateTime date) {
        return date.dayOfMonth().withMaximumValue().getMillis();
    }

    public static long getMinDate(DateTime date) {
        return date.dayOfMonth().withMinimumValue().getMillis();
    }

    public static String getStringForDate(Date time) {
        return sqlDateFormat.format(time);
    }

    public static String getMonthYearForDate(long date) {
        return monthYearformatter.format(date);
    }

    public static class MonthRange {
        public String start;
        public String end;
        public String quarterYear;

        public MonthRange(int start, int year, MonthRangeType range) {
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.set(year, start * range.getRange(), 1);
            this.start = getStringForDate(calendar.getTime());

            calendar.set(year, start * range.getRange() + range.getRange() - 1, 1);
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            this.end = getStringForDate(calendar.getTime());
            this.quarterYear = range == MonthRangeType.EACH_MONTH ? getMonthYearForDate(calendar.getTimeInMillis()) : "Q" + (start + 1) + "-" + year;

        }

        public enum MonthRangeType {
            EACH_MONTH(1), QUARTER(3), HALF_YEARLY(6), YEARLY(12);

            int range;

            MonthRangeType(int range) {
                this.range = range;
            }

            public int getRange() {
                return this.range;
            }
        }
    }

    public static class MonthRangeDate implements Iterator<MonthRange> {

        Calendar dateFrom;
        Calendar dateTo;
        int startYear;
        int endYear;
        int startQuarter;
        int endQuarter;
        MonthRange.MonthRangeType rangeType;

        public MonthRangeDate(String dateFrom, String dateTo, MonthRange.MonthRangeType rangeType) {
            this.dateFrom = getCalendar(getTimeFromDateWrtTimeZone(dateFrom));
            this.dateTo = getCalendar(getTimeFromDateWrtTimeZone(dateTo));
            startQuarter = this.dateFrom.get(Calendar.MONTH) / rangeType.getRange();
            endQuarter = this.dateTo.get(Calendar.MONTH) / rangeType.getRange();
            startYear = this.dateFrom.get(Calendar.YEAR);
            endYear = this.dateTo.get(Calendar.YEAR);
            this.rangeType = rangeType;
        }

        @Override
        public boolean hasNext() {
            if (startYear > endYear) return false;
            if (startQuarter > endQuarter && startYear == endYear) return false;
            return true;
        }

        @Override
        public MonthRange next() {
            MonthRange range = new MonthRange(startQuarter, startYear, rangeType);
            int val = startQuarter + 1;
            startYear = startYear + val / (12 / rangeType.getRange());
            startQuarter = val % (12 / rangeType.getRange());
            return range;
        }

        @Override
        public void remove() {

        }
    }

    public static String getAdjustedFromDate(String fromDate,String  toDate) {
        fromDate = getPeriodMonth(fromDate);
        toDate = getPeriodMonth(toDate);

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateTime dateTime_fromDate = new DateTime(df.parse(fromDate).getTime());
            DateTime dateTime_toDate = new DateTime(df.parse(toDate).getTime());
            if (getYears(dateTime_fromDate, dateTime_toDate) >= 1) {
                dateTime_fromDate = dateTime_toDate.minusYears(1);
            }
            return df.format(new Date(dateTime_fromDate.getMillis()));
        } catch (Exception ex) {
            throw new RuntimeException("Dates could not be parsed fromDate: " + fromDate + " toDate: " + toDate);
        }
    }

    public static int getYears(DateTime date1,DateTime date2) {
        return Years.yearsBetween(date1, date2).getYears();
    }
    public static String getPeriodMonth(String period) {
        String[] tokens = period.split("-");
        return tokens[0]+"-"+tokens[1]+"-15";
    }

    public static String getStartingDateOfMonth(String period) {
           String[] tokens = period.split("-");
           return tokens[0]+"-"+tokens[1]+"-01";
       }

    /**
     * This method returns the set of all months between fromDate to toDate,date format: yyyy-MM-dd format
     * @param fromDate
     * @param toDate
     * @return set of months
     */
    public static Set<String> getMonthsBetween(String fromDate, String toDate) {
            Set<String> monthsSet = new HashSet<String>();

            Map<Long, Long> monthMap = DateUtils.getAllMonths(fromDate, toDate);
            for (Long month : monthMap.values()) {
                Date dt = new Date(month);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
               monthsSet.add(dateFormat.format(dt));

            }

            return monthsSet;
        }


}
