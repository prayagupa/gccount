package eccount
/**
  * @author  : Prayag Upd
  * @created : 9 Nov, 2012
  */
class User{
    String username;
    String password;
    Boolean active=true;
    
    //static mapWith="mongo"
    
    static constraints = {
         password(password:true)
         active(display:false)
    }
}
