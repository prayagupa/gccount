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

    //static mapWith="mongo"

    static constraints = {
         active(display:false)
    }
}
