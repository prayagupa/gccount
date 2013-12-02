package eccount
import eccount.Category
import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.TestFor
 
@TestFor(Category)
class CategorySpec extends Specification {
 
    @Unroll 
    def "search() should return #count results for query '#query'"() {
        given:
            [
                new Category(name: "Fruit"),
                new Category(name: "Liquor"),
            ]*.save()

        expect:
            count == Category.findByName(query)

        where:
            count   | query
            2       | null
            1       | "Liquor"
            1       | "Fruit"
            0       | "Gadget"
    }
 
}
