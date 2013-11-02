package eccount.util;

import eccount.SearchRequest;

/**
 * @author : prayagupd
 */

public class RequestUtils {
    public static final String REPORTING="reporting";
    public static final String COMPARISON="comparison";

    public static String[] getPeriod(SearchRequest request){
        StringBuilder builder=new StringBuilder();
        if(request.hasParameter("reportingFrom"))      {
            builder.append(request.get("reportingFrom")).append(":").append(request.get("reportingTo"));
        }
        if(request.hasParameter("comparisonFrom"))  builder.append(",").append(request.get("comparisonFrom")).append(":").append(request.get("comparisonTo"));

        return builder.toString().split(",");
    }
    public static boolean isArrayRequest(String request){
        return (request.startsWith("[")&& request.endsWith("]"));
    }

    public static String[] getArrayRequest(String request){
        String requestParams=isArrayRequest(request)?request.replace("[","").replace("]",""):request;
        return requestParams.split(",");
    }
}

