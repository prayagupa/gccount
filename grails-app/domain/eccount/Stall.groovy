package eccount
/**
  * @author  : Prayag Upd
  * @created : 9 Dec, 2012
  */
class Stall{
    String name;
    Date created = new Date();
    Boolean active=true;
    
    static hasMany = [items:Item]
    //static mappedBy = [items:"category"]

    //static mapWith="mongo"

    static constraints = {
         name(blank:false)
	 created(display:false)
         active(display:false)
    }
}
