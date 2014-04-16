package eccount

import eccount.config.AbstractConfManager
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.client.Client

/**
 * @author prayagupd
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
//@TestFor(TransactionService)
class TransactionServiceTests extends AbstractUnitTestCase {

    def TransactionService service
    Client client

    def SearchRequest searchRequest  = new SearchRequest()
    def JSONObject expectedJson

    @Override
    protected void setUp(){
        super.setUp()

        printLn("setting confmanager")
        service = new TransactionService()
        service.confManager = new AbstractConfManager()

        expectedJson = EsEmbeddedNode.readDocument(DOCUMENT_EXPECTED)
    }

    void testGetSearchResponse() {
        SearchRequest searchRequest  = new SearchRequest()
        searchRequest.requestParams = configureParams()
        printLn("after setup, preparing to insert " + ES_TYPE_CUSTOMER + " document")
        EsEmbeddedNode.insertDocument(DOCUMENT_LOCATION_CUSTOMER, ESINDEX_NAME, ES_TYPE_CUSTOMER)

        String requestUrl = "from=2012-06-01" +
                   "&to=2013-05-31" +
                   "&report=transaction" +
                   "&indexName="+ESINDEX_NAME

        service = new TransactionService()
        service.confManager = new AbstractConfManager()
        String jsonString = service.getSearchResponse(searchRequest)
	    JSONObject actualJson = JSON.parse(jsonString)

        assertValues(expectedJson, actualJson)
    }

    def printLn(message){
        println(this.getClass().getName()+" : "+message)
    }


}
