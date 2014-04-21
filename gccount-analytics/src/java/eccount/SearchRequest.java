package eccount;

import java.util.HashMap;

/**
 * User: prayagupd
 */
public class SearchRequest {

    public HashMap<String, String> requestParams;

    public boolean hasParameter(String _param) {
        return requestParams.containsKey(_param) && !requestParams.get(_param).isEmpty();
    }

    public String  get(String _param) {
        return requestParams.get(_param);
    }
}

