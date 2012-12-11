package eccount
/**
  * @author  : Prayag Upd
  * @created : 4 Dec, 2012
  */
class Item{
    String name;
    Date created = new Date();
    Boolean active=true;
    Category category;
    static belongsTo = Category;

    //static mapWith="mongo"

    static constraints = {
         created(display:false)
         active(display:false)
    }
}
