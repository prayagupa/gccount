package eccount



import org.junit.*
import grails.test.mixin.*

@TestFor(StallController)
@Mock(Stall)
class StallControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/stall/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.stallInstanceList.size() == 0
        assert model.stallInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.stallInstance != null
    }

    void testSave() {
        controller.save()

        assert model.stallInstance != null
        assert view == '/stall/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/stall/show/1'
        assert controller.flash.message != null
        assert Stall.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/stall/list'

        populateValidParams(params)
        def stall = new Stall(params)

        assert stall.save() != null

        params.id = stall.id

        def model = controller.show()

        assert model.stallInstance == stall
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/stall/list'

        populateValidParams(params)
        def stall = new Stall(params)

        assert stall.save() != null

        params.id = stall.id

        def model = controller.edit()

        assert model.stallInstance == stall
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/stall/list'

        response.reset()

        populateValidParams(params)
        def stall = new Stall(params)

        assert stall.save() != null

        // test invalid parameters in update
        params.id = stall.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/stall/edit"
        assert model.stallInstance != null

        stall.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/stall/show/$stall.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        stall.clearErrors()

        populateValidParams(params)
        params.id = stall.id
        params.version = -1
        controller.update()

        assert view == "/stall/edit"
        assert model.stallInstance != null
        assert model.stallInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/stall/list'

        response.reset()

        populateValidParams(params)
        def stall = new Stall(params)

        assert stall.save() != null
        assert Stall.count() == 1

        params.id = stall.id

        controller.delete()

        assert Stall.count() == 0
        assert Stall.get(stall.id) == null
        assert response.redirectedUrl == '/stall/list'
    }
}
