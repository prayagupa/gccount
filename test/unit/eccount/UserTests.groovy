package eccount



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(User)
class UserTests {

    void testCreateUser() {
       User user = new User(firstName:"Prayag")	
	   assertEquals "Prayag", user.firstName
    }
}
