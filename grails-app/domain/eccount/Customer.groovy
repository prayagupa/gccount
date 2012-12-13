package eccount
/**
  * @author  : Prayag Upd
  * @created : 9 Dec, 2012
  */

class Customer{
    String firstName;
    String middleName;
    String lastName;	
    Boolean active=true;
    Date created = new Date();
    //static mapWith="mongo"

    static constraints = {
         firstName(blank:false);
         middleName(blank:true);
         lastName(blank:false);
         active(display:false)
         created(blank:false);
    }
}
