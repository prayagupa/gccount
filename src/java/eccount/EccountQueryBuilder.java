package eccount;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;

/**
 *@author prayagupd  
 */
 
public interface EccountQueryBuilder {
	    SearchRequestBuilder query(ElasticRequest request, Client client);
}

