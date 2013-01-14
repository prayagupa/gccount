package eccount
/**
  * @author : Prayag Upd
  * @since  : 28.12.2012
  */
class UserService {
      
    /**
      * create user
      * @param params
      */
    User create(String params) {
         def user = new User(params)
         if(user.save()) return user
         else throw new UserException("User couldn't be saved.")
    }
    /**
      * user exception handler
      */
    class UserException extends RuntimeException{
         String message
         User user
    }
}
