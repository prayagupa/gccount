package eccount.config

/**
 * User: prayag
 * Date: 09/11/13
 * Time: 09:50
 */
class AbstractConfManager {
    def qualifier
    def hBaseQualifier
    def hBaseTables=[]
    public enum ConfigType{
        TABLE,
        WEB_PARAMETER,
        FILE_MAPPING,
        REPORT,
        SUMMARY_DRILLDOWN,
        ES_REPORT;
    }


    public Map<String, Table> getTableConfig(Map<String,String> requestMap) {
        //TODO
        def script = ConfigurationManager.classLoader.loadClass('EccountTables').newInstance()
        script.run()

        def TablesBuilder builder = new TablesBuilder()
        def tables = script.tables
        tables.delegate = builder
        tables()
        runCustomConfiguration(builder.outputTables,ConfigType.TABLE,requestMap)
    }

//    public String  getPassword() {
//        def script = ConfigurationManager.classLoader.loadClass('Passwords').newInstance()
//        script.run()
//
//        def PasswordBuilder builder = new PasswordBuilder()
//        def passwords = script.passwords
//        passwords.delegate = builder
//        passwords()
//        builder.password
//    }

    public EsCluster getClusterConfig(String server){
        def script =  AbstractConfManager.classLoader.loadClass(server).newInstance()
        script.run()
        def ServersBuilder builder= new ServersBuilder()
        def servers = script.servers
        servers.delegate = builder
        servers()
        EsCluster clusterConfig=new EsCluster()
        clusterConfig.nodes=builder.serverMaps
        clusterConfig.clusterName = builder.clusterName
        clusterConfig
    }

    public Index getIndexConfig(Map<String,String> requestMap){
        Index config = new Index()
        def tableName = "NewIndexTable";
        config.tableName=getPrefix(getClientId(requestMap),tableName)+tableName
        config
    }

    public Map<String, WebParam> getWebConfig(Map<String, Table> tables,Map<String,String> requestMap) {

        def script2 = ConfManager.classLoader.loadClass('DasWebParameters').newInstance()
        script2.run()

        def ParamSetsBuilder webBuilder = new ParamSetsBuilder()
        webBuilder.tables = tables
        def parameterSets = script2.parameterSets
        parameterSets.delegate = webBuilder
        parameterSets()

//    System.out.println("This is parameters"+webBuilder.parameterSets.get("seg").parameters);

        runCustomConfiguration(webBuilder.parameterSets,ConfigType.WEB_PARAMETER,requestMap)
    }
//
//    public Map<String, FileMappingConfig> getMappingConfig(Map<String, Table> tables) {
//
//        def script2 = ConfigurationManager.classLoader.loadClass('DasMappings').newInstance()
//        script2.run()
//
//        def FileMappingBuilder mappingBuilder = new FileMappingBuilder()
//        mappingBuilder.tables = tables
//        def parameterSets = script2.mappings
//        parameterSets.delegate = mappingBuilder
//        parameterSets()
//
//        runCustomConf(mappingBuilder.mappings,ConfigType.FILE_MAPPING,null)
//    }
//
//    def Map<String, FileMappingConfig> getMappingConfig() {
//        Map<String, TableConfig> tableConfigs = getTableConfig()
//        getMappingConfig(tableConfigs)
//    }
//
//    public Map<String,ReportConfig> getReportConfig(Map<String,String> requestMap){
//        def script=ConfigurationManager.classLoader.loadClass('DynamicReports').newInstance()
//        script.run()
//        def builder=new ReportSetBuilder()
//        def reportSet=script.reportSet
//        reportSet.delegate=builder
//        reportSet()
//        runCustomConf(builder.reportSet,ConfigType.REPORT,requestMap)
//    }

    public  String getPrefix(String clientId,String tableName){
        if(hBaseTables.contains(tableName)){
            return hBaseQualifier+clientId+"_";
        }
        return ""


    }
    /**
     *Reads in all the table name included in the initial configuration and added the term "test_" to all the tables
     * In case of Configtype TABLE, we go further into its indexes and changes their table name with "test_" appended
     *
     * @param output  the input to the custom configuration that comes from the output of default configuration
     * @param configType  type type of customization to be done on basis of type of configuration sent
     * @return  custom configured Map of the @param output
     *
     *  */
    public Map runCustomConfiguration(Map output,ConfigType configType,Map<String,String> requestMap){
        def clientId=getClientId(requestMap);
        Set outputKeySet = new HashSet()
        output.keySet().each{
            outputKeySet.add(it)
        }
        outputKeySet.each {
            def value = output.get(it)
            if(configType!=ConfigType.SUMMARY_DRILLDOWN){
                def tableQualifier = getPrefix(clientId,value?.tableName)
                value.tableName=tableQualifier+value.tableName
            }
            if(configType==ConfigType.TABLE){
                output.put(value.tableName,value)
            }
            if(configType==ConfigType.REPORT)
                value.tableQualifier=getPrefix(clientId,"SummaryTable")

            if(configType==ConfigType.SUMMARY_DRILLDOWN){
                for( def index : value.indexes.values() ){
                    def tableQualifier = getPrefix(clientId,index.lookupTable)
                    index.lookupTable=tableQualifier+index.lookupTable
                }
            }
        }
        output
    }

    def getClientId(requestMap){
        requestMap!=null?requestMap.get("clientId"):""
    }
    def setIndex(requestMap){
        def clientId=getClientId(requestMap)
        if(clientId){
            requestMap.put("index_name",qualifier+clientId)
        }
        else{
            throw new RuntimeException("clientId not defined")
        }
    }
}
