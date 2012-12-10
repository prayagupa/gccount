package eccount
/**
  * @author  : Prayag Upd
  * @created : 4 Dec, 2012
  */
class Category{
    String name;
    Date created = new Date();
    Boolean active=true;
    
    static hasMany = [items:Item]
    static mappedBy = [items:"category"]

    //
    //static mapWith="mongo"

    static constraints = {
         created(display:false)
         active(display:false)
    }
}
