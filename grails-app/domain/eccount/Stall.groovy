package eccount
/**
  * @author  : Prayag Upd
  * @created : 9 Dec, 2012
  */
class Stall{
    String name;
       
    static hasMany = [items:Item]
    //static mappedBy = [items:"stall"]
    User user;
    Date created = new Date();
    Boolean active=true;
    //static mapWith="mongo"

    static constraints = {
         name(blank:false)
         items()
         user(blank:true);
       	 created(display:false)
	 active(display:false)
    }
}
