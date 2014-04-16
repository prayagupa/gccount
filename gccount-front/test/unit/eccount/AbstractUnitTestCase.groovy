package eccount

import eccount.config.AbstractConfManager
import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.client.Client

/**
 * @author prayagupd
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */

class AbstractUnitTestCase extends GrailsUnitTestCase {

    Client client
    final def ESINDEX_NAME               = "gccount_test"
    final def ES_TYPE_CUSTOMER           = "Customer"
    final def ES_MAPPING_CUSTOMER        = "Customer"
    final def DOCUMENT_LOCATION_CUSTOMER = "test/resources/es/document/Customer.json"
    final def DOCUMENT_EXPECTED          = "test/resources/es/document/CustomerReportExpected.json"

    final def ESTYPE_TRANSACTION         = "Transaction"
    final def ES_MAPPING_TRANSACTION     = "Transaction"

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

    }


    protected HashMap<String, String> configureParams(){
        printLn("setting params")
        def params = [:]
        params.from = "2012-06-01"
        params.reportName = "transaction"
        params.indexName = ESINDEX_NAME

        printLn("setting params")
        return params
    }

    def printLn(message){
        println(this.getClass().getName()+" : "+message)
    }

    def assertValues(JSONObject expectedJson, JSONObject actualJson){
        println(actualJson)
        for (String key : expectedJson.keySet()) {

            JSONObject expectedRow = expectedJson.get(key)
            JSONObject actualRow   = actualJson.get(key)

            for (String field : expectedRow.keySet()) {
                Object expectedValue = expectedRow.get(field)
                Object actualValue   = actualRow.get(field)

                assertEquals(expectedValue, actualValue)
            }
        }
    }
}
