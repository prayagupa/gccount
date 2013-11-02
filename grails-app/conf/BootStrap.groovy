/**
  * @author : Prayag Upd
  * @since  : 27.12.2012 
  */
import eccount.Role;
import eccount.UserRole;
import eccount.User;
import eccount.Stall;

class BootStrap {
	def springSecurityService
	
	def init = { servletContext ->
		
		log.info("initialising bootstrap")

		User sandboxUser = new User(firstName:"Prayag",
			 middleName:"",			 
			 lastName:"Upd",
			 username:"prayag.upd@gmail.com",
                         password:"123456",
                         enabled:true,
		         accountExpired:false,
			 accountLocked:false,
			 passwordExpired:false).save()
                         
		
		if(sandboxUser){
			println("sandbox user created")
	         	def role = Role.findByAuthority("ROLE_ADMIN")
			if(role==null){
				role = new Role(authority: "ROLE_ADMIN")
		                role.save(flush: true)
				println("role created")
			}
			
     	                def userrole = new UserRole(user: sandboxUser,role: role)
	                userrole.save(flush: true)

			Stall stall = new Stall(name:"Estonia Food Stall", 
						        user:sandboxUser)
			println("stall created with a user")
		}else{
            log.error("user already exists")
		}
	}
	def destroy = {
	}
}
