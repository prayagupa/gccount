package eccount

import eccount.config.AbstractConfManager
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.client.Client

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
//@TestFor(TransactionService)
class TransactionServiceTests extends GrailsUnitTestCase {

    def TransactionService service
    Client client
    final def ESINDEX_NAME               = "gccount_test"
    final def ES_TYPE_CUSTOMER           = "Customer"
    final def ES_MAPPING_CUSTOMER        = "Customer"
    final def DOCUMENT_LOCATION_CUSTOMER = "test/resources/es/document/Customer.json"
    final def DOCUMENT_EXPECTED          = "test/resources/es/document/CustomerReportExpected.json"

    final def ESTYPE_TRANSACTION         = "Transaction"
    final def ES_MAPPING_TRANSACTION     = "Transaction"

    def SearchRequest searchRequest  = new SearchRequest()
    def JSONObject expectedJson

    @Override
    protected void setUp(){
        printLn("setting up testing documents")
        EsConnector.ENVIRONMENT_TEST = true
        client = EsEmbeddedNode.getClient()

        EsEmbeddedNode.applyIndexTemplate()

        printLn("request to apply testing IndexTemplate")
        EsEmbeddedNode.checkAndCreateIndex(ESINDEX_NAME)

        EsEmbeddedNode.applyMapping(ESINDEX_NAME, ES_TYPE_CUSTOMER, ES_MAPPING_CUSTOMER)
        //EsEmbeddedNode.applyMapping(ESINDEX_NAME, ESTYPE_TRANSACTION, ES_MAPPING_TRANSACTION)
        printLn("setting confmanager")
        service = new TransactionService()
        service.confManager = new AbstractConfManager()

        configureParams()

        expectedJson = EsEmbeddedNode.readDocument(DOCUMENT_EXPECTED)
    }

    void testGetSearchResponse() {

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

        for (String key : expectedJson.keySet()) {

            JSONObject expectedRow = expectedJson.get(key)
            JSONObject actualRow   = expectedJson.get(key)

            for (String field : expectedRow.keySet()) {
                Object expectedValue = expectedRow.get(field)
                Object actualValue   = actualRow.get(field)

                assertEquals(expectedValue, actualValue)
            }
        }
    }


    protected void configureParams(){
        printLn("setting params")
        def params = [:]
        params.from = "2012-06-01"
        params.reportName = "transaction"
        params.indexName = ESINDEX_NAME

        printLn("setting params")
        searchRequest.requestParams = params
    }

    def printLn(message){
        println(this.getClass().getName()+" : "+message)
    }


}
