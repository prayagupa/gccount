package eccount

class User{
    String username;
    String password;
    Boolean active=true;
    static constraints = {
         active(display:false)
    }
}
