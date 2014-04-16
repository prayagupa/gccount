package eccount

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.ClusterState
import org.elasticsearch.cluster.metadata.IndexMetaData
import org.elasticsearch.cluster.metadata.MappingMetaData
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory

import java.util.concurrent.atomic.AtomicBoolean

/**
 * User: prayagupd
 * Date: 28/10/12
 * Time: 12:43 AM
 */

class EsEmbeddedNode {
    protected static Logger logger = Logger.getLogger(EsEmbeddedNode.class)
    protected static org.elasticsearch.node.Node node
    protected static Client client

    protected static final String ES_SETTINGS_DEF = "settings"
    protected static final String ES_MAPPING_PATH = "test/resources/es/mapping/"
    protected static final String ES_TEMPLATE_NAME = "Template"

    static {
        start();
    }

    private static void start() {
        println 'starting node'
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("node.http.enabled", true)
                .put("path.logs", "target/elasticsearch/logs")
                .put("path.data", "target/elasticsearch/data")
                .put("gateway.type", "none")
                .put("index.store.type", "memory")
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1)
                .put("index.store.fs.memory.enabled", "memory")
                .put("discovery.zen.ping.multicast.enabled", "false")
                .put("cluster.name", "dev_stream")
                .put("path.conf", "test/resources/mapping/conf")
                .put("foreground", "true").build();
        try {
            // This step actually interprets the system properties
            org.elasticsearch.node.NodeBuilder nodeBuilder = org.elasticsearch.node.NodeBuilder.nodeBuilder().settings(settings).loadConfigSettings(false).local(true);
            node = nodeBuilder.build();
            // register a shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    node.close();
                }
            });
            node.start();
            client = node.client();
        } catch (Exception e) {
            throw new IllegalStateException("elasticsearch failed to launch " + e.getMessage(), e);
        }
    }



    public static JSONObject readDocument(String filePath) {

        FileInputStream input = new FileInputStream(new File(filePath).absolutePath);
        DataInputStream inputStream = new DataInputStream(input);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;
        int i = 0;
        StringBuilder jsonStringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if ("".equals(line.trim()) || line.startsWith("#")) {//ignore comments and empty lines
                continue;
            }
            jsonStringBuilder.append(line)


        }
        return new JSONObject(jsonStringBuilder.toString());

    }

    private static void insertInnerJSON(JSONObject data, String fieldName, XContentBuilder jsonContentBuilder) {
        if (fieldName) {
            jsonContentBuilder.startObject(fieldName)
        } else {
            jsonContentBuilder.startObject()
        }
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value instanceof JSONObject) {
                insertInnerJSON((JSONObject) value, key, jsonContentBuilder);
            } else if (value instanceof JSONArray) {
                insertInnerArray((JSONArray) value, key, jsonContentBuilder)
            } else {
                jsonContentBuilder.field(key, value)
            }
        }
        jsonContentBuilder.endObject()
    }


    private static void insertInnerArray(JSONArray data, String fieldName, XContentBuilder jsonBuilder) {
        jsonBuilder.startArray(fieldName)
        for (Object fieldValue : data) {
            if (fieldValue instanceof JSONObject) {
                insertInnerJSON((JSONObject) fieldValue, null, jsonBuilder);
            } else if (fieldValue instanceof JSONArray) {
                insertInnerArray((JSONArray) fieldValue, null, jsonBuilder)
            } else {
                jsonBuilder.value(fieldValue)
            }
        }
        jsonBuilder.endArray()
    }

    public static Client getClient() {
        return client;
    }

    /**
     *
     * @param filePath
     * @param index
     * @param type
     * @return
     */
    public static boolean insertDocument(String filePath, String index, String type) {
        JSONObject data = readDocument(filePath)
        XContentBuilder jsonBuilder;
        Client client = getClient()
        for (String key : data.keySet()) {
            jsonBuilder = XContentFactory.jsonBuilder();
            jsonBuilder.startObject();
            JSONObject value = data.get(key);
            for (String fieldName : value.keySet()) {
                Object fieldValue = value.get(fieldName);
                if (fieldValue instanceof JSONObject) {
                    insertInnerJSON((JSONObject) fieldValue, fieldName, jsonBuilder);
                } else if (fieldValue instanceof JSONArray) {
                    insertInnerArray((JSONArray) fieldValue, fieldName, jsonBuilder)
                } else {
                    jsonBuilder.field(fieldName, fieldValue)
                }
            }
            jsonBuilder.endObject()
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(index, type, String.valueOf(key))
            println("inserted the index:" + String.valueOf(key))
            indexRequestBuilder.setCreate(false).setSource(jsonBuilder);
            indexRequestBuilder.execute().actionGet()
        }
        //need to refresh indices to reflect the insertion made
        client.admin().indices().prepareRefresh().execute().actionGet()
        true
    }

    public static void applyMapping(String index, String type, String location) throws Exception {
        if (logger.isTraceEnabled()) logger.trace("applyMapping(" + index + "," + type + ")");
        println("applying ${type} mapping")

        // create es type when it doesn't exist
        boolean mappingExist = isMappingExist(client, index, type);
        if (!mappingExist) {
            if (logger.isDebugEnabled()) logger.debug("Mapping [" + index + "]/[" + type + "] doesn't exist. Creating it.");
            println "Since mapping does not exist for " + index + ":" + type + " need to create."

            String source = readJsonDefn(location);

            if (source != null) {
                PutMappingRequestBuilder pmrb = client.admin().indices()
                        .preparePutMapping(index)
                        .setType(type);

                if (source != null) {
                    if (logger.isTraceEnabled()) logger.trace("Mapping for [" + index + "]/[" + type + "]=" + source);
                    pmrb.setSource(source);
                }


                println("executing MappingRequest")
                MappingListener mappingListener = new MappingListener(pmrb)

                // Create type and mapping
                Thread thread = new Thread(mappingListener)

                thread.start();
                while (!mappingListener.processComplete.get()) {
                    println("not complete yet. Waiting for 100 ms")
                    Thread.sleep(100);

                }
                println "mapping applied"

            } else {
                if (logger.isDebugEnabled()) logger.debug("No mapping definition for [" + index + "]/[" + type + "]. Ignoring.");
            }
        } else {
            if (logger.isDebugEnabled()) logger.debug("Mapping [" + index + "]/[" + type + "] already exists.");
            println("mapping already exists")
        }
        if (logger.isTraceEnabled()) logger.trace("/applyMapping(" + index + "," + type + ")");
        println("mapping created::")

    }

    public static void applyIndexTemplate() throws Exception {

        println("preparing to apply IndexTemplate")

        // If type does not exist, we create it
        String source = readJsonDefn(ES_TEMPLATE_NAME);
        if (source != null) {
            PutIndexTemplateRequestBuilder pmrb = client.admin().indices()
                    .preparePutTemplate("eccount_template") //dw_template(?)
            pmrb.setSource(source);

            println("executing IndexTemplate")
            // Create type and mapping
            PutIndexTemplateResponse response = pmrb.execute().actionGet();
            if (logger.isTraceEnabled()) logger.trace("/applyIndexTemplate( template )");
            println("IndexTemplate created")
        }

    }

    public static boolean isMappingExist(Client client, String index, String type) {
        ClusterState cs = client.admin().cluster().prepareState().setFilterIndices(index).execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index(index);

        if (imd == null) return false;

        MappingMetaData mdd = imd.mapping(type);

        if (mdd != null) return true;
        return false;
    }

    public static void checkAndCreateIndex(String indexName) {
        if (logger.isDebugEnabled()) logger.debug("checkAndCreateIndex({})", indexName);

        try {
            // check first if index already exists
            if (!isIndexExist(client, indexName)) {
                println("creating index:" + indexName)
                if (logger.isDebugEnabled()) logger.debug("Index {} doesn't exist. Creating it.", indexName);

                CreateIndexRequestBuilder cirb = client.admin().indices().prepareCreate(indexName);

                String source = readJsonDefn(ES_SETTINGS_DEF);
                if (source != null) {
                    if (logger.isTraceEnabled()) logger.trace("Mapping for [{}]={}", indexName, source);
                    cirb.setSettings(source);
                }

                CreateIndexResponse createIndexResponse = cirb.execute().actionGet();
                if (!createIndexResponse.isAcknowledged()) throw new Exception("Could not create index [" + indexName + "].");

            } else {
                println "Index already exist"
            }
            println("Index " + indexName + " created")

            // We create the mapping for the doc type
        } catch (Exception e) {
            logger.warn("checkAndCreateIndex() : Exception raised : {}", e);
            if (logger.isDebugEnabled()) logger.debug("- Exception stacktrace :", e);
            e.printStackTrace()
        }

        if (logger.isDebugEnabled()) logger.debug("/checkAndCreateIndex()");
    }

    public static boolean isIndexExist(Client client, String index) throws Exception {
        return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
    }

    public static boolean isTypeExist(Client client, String index, String type) throws Exception {
        return client.admin().indices().prepareExists(index, type).execute().actionGet().isExists();
    }

    public static String getMapping(String index, String type) {
        ClusterState cs = client.admin().cluster().prepareState().setFilterIndices(index).execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index(index);
        imd.mapping(type).source().string()
    }

    public static String readJsonDefn(String esType) throws Exception {
        return readFileInClasspath(ES_MAPPING_PATH + esType + ".json");
    }

    public static String readFileInClasspath(String url) {
        println "called reading file: " + url
        StringBuffer bufferJSON = new StringBuffer();

        FileInputStream input = new FileInputStream(new File(url).absolutePath);
        DataInputStream inputStream = new DataInputStream(input);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;

        while ((line = br.readLine()) != null) {
            bufferJSON.append(line);
        }
        br.close();
        println("Json read:" + bufferJSON.toString())
        return bufferJSON.toString();
    }

    static class MappingListener implements Runnable {
        @Override
        void run() {
            try {
                requestBuilder.execute(actionListener)
            } catch (Exception e) {
                e.printStackTrace()
                this.processComplete.set(true)
            }
        }

        PutMappingRequestBuilder requestBuilder;
        public AtomicBoolean processComplete;
        PutMappingActionListener actionListener;

        public MappingListener(PutMappingRequestBuilder requestBuilder) {
            this.processComplete = new AtomicBoolean(false);
            actionListener = new PutMappingActionListener(processComplete);
            this.requestBuilder = requestBuilder;
        }

        static class PutMappingActionListener implements ActionListener<PutMappingResponse> {
            public AtomicBoolean processComplete;

            public PutMappingActionListener(AtomicBoolean processComplete) {
                this.processComplete = processComplete;
            }

            void onResponse(PutMappingResponse response) {
                if (response.isAcknowledged()) {
                    println("template successfully applied")
                }
                processComplete.set(true)
            }

            @Override
            void onFailure(Throwable throwable) {
                println("error applying mapping : " + throwable)
                throwable.printStackTrace()
                processComplete.set(true)
            }
        }
    }


}

