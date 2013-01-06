package eccount
/**
  * @author  : Prayag Upd
  * @created : 9 Nov, 2012
  */
class User{
    String firstName;
    String middleName;
    String lastName;
    String username;
    String password;
    Boolean active=true;
    Date created = new Date();
    //static mapWith="mongo"
    
    static constraints = {
         firstName(blank:false)
         middleName(blank:true)
         lastName(blank:false)
	 username(unique:true)
         password(password:true)
         active(display:false)
         created(display:false)
    }
    String toString(){
         "${firstName+" "+middleName+" "+lastName}";
    }
}
