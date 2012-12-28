/**
  * @author : Prayag Upd
  * @since  : 27.12.2012 
  */
import eccount.User;

class BootStrap {

	def init = { servletContext ->
		new User(firstName:"Prayag",
			 middleName:"",			 
			 lastName:"Upd",
			 username:"prayag.upd@gmail.com",
                         password:"123456",
                         active:true).save()
		println("user created");		
	}
	def destroy = {
	}
}
