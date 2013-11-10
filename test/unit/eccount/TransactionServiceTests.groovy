package eccount

import eccount.config.AbstractConfManager
import grails.test.GrailsUnitTestCase
import grails.test.mixin.*
import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.client.Client

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
//@TestFor(TransactionService)
class TransactionServiceTests extends GrailsUnitTestCase {

    def TransactionService service
    Client client
    final def ESINDEX_NAME = "gccount_test"
    final def ESTYPE_CUSTOMER = "Customer"
    final def ES_MAPPING_CUSTOMER = "Customer"
    final def DOCUMENT_LOCATION_CUSTOMER = "test/resources/es/document/Customer.json"
    final def DOCUMENT_EXPECTED = "test/resources/es/document/CustomerReportExpected.json"

    final def ESTYPE_TRANSACTION = "Transaction"
    final def ES_MAPPING_TRANSACTION = "Transaction"

    def SearchRequest searchRequest = new SearchRequest()
    def JSONObject expected

    @Override
    protected void setUp(){
        printLn("setting up testing documents")
        EsConnector.ENVIRONMENT_TEST = true
        client = EsEmbeddedNode.getClient()

        EsEmbeddedNode.applyIndexTemplate()

        printLn("request to apply testing IndexTemplate")
        EsEmbeddedNode.checkAndCreateIndex(ESINDEX_NAME)

        EsEmbeddedNode.applyMapping(ESINDEX_NAME, ESTYPE_CUSTOMER, ES_MAPPING_CUSTOMER)
        //EsEmbeddedNode.applyMapping(ESINDEX_NAME, ESTYPE_TRANSACTION, ES_MAPPING_TRANSACTION)
        printLn("setting confmanager")
        service = new TransactionService()
        service.confManager = new AbstractConfManager()

        configureParams()

        expected = EsEmbeddedNode.readDocument(DOCUMENT_EXPECTED)
    }

    void testGetSearchResponse() {

        printLn("after setup, preparing to insert " + ESTYPE_CUSTOMER + " document")
        EsEmbeddedNode.insertDocument(DOCUMENT_LOCATION_CUSTOMER, ESINDEX_NAME, ESTYPE_CUSTOMER)

        String requestUrl = "from=2012-06-01" +
                   "&to=2013-05-31" +
                   "&report=transaction" +
                   "&indexName="+ESINDEX_NAME

        service = new TransactionService()
        service.confManager = new AbstractConfManager()
        String actualJson = service.getSearchResponse(searchRequest)
        String expectedJson = ""

        assertEquals(actualJson, expectedJson)
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
