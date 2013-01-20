package eccount
/**
  * @author  : Prayag Upd
  * @created : 4 Dec, 2012
  */
class Item{
    String name;
    Double price;
    Date created = new Date();
    Boolean active=true;
    Category category;
    static belongsTo = Category;

    //static mapWith="mongo"

    static constraints = {
         name(blank:false)
         price(blank:false)
         category(blank:false)
         created(display:false)
         active(display:false)
    } 
    String toString(){
         "${name}"
    }
}
