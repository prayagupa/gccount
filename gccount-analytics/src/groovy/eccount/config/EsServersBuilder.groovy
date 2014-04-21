package eccount.config

class EsServersBuilder extends BuilderSupport{
		
  def Map<String, EsServer> serverMaps=[:]
  def String clusterName = ""
  EsServer currentServer

  protected void setParent(Object o, Object o1) {}

  protected Object createNode(Object o) {
    switch(o){
      case "server":
        currentServer=new EsServer()
        return o
      default:
        return o
    }
  }

  protected Object createNode(Object o, Object o1) {
    switch(o){
      case "clusterName":
        clusterName=o1;
        return o
      case "hostname":
      case "name":
        currentServer."$o"=o1
        return o
      case "port":
        currentServer."$o"=Integer.parseInt((String)o1)
        return o
      case "httpPort":
        currentServer."$o"=Integer.parseInt((String)o1)
        return o
      default:
        return o
    }
  }

  protected Object createNode(Object o, Map map) {
    return null;
  }

  protected Object createNode(Object o, Map map, Object o1) {
    return null;  
  }

  protected void nodeCompleted(Object parent, Object node) {
    if("server"==node)
      serverMaps.put(currentServer.name, currentServer)
  }
}
