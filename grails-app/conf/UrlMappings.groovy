/**
  * @author Prayag Upd
  * @since 28.12.2012
  */
class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		//default route for login
		 "/" {
		    controller = "user"
		    action = "login"
 		 }
		// "/"(view:"/index")
		"500"(view:'/error')
	}
}
