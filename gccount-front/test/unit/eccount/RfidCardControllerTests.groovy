package eccount



import org.junit.*
import grails.test.mixin.*

@TestFor(RfidCardController)
@Mock(RfidCard)
class RfidCardControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/rfidCard/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.rfidCardInstanceList.size() == 0
        assert model.rfidCardInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.rfidCardInstance != null
    }

    void testSave() {
        controller.save()

        assert model.rfidCardInstance != null
        assert view == '/rfidCard/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/rfidCard/show/1'
        assert controller.flash.message != null
        assert RfidCard.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/rfidCard/list'

        populateValidParams(params)
        def rfidCard = new RfidCard(params)

        assert rfidCard.save() != null

        params.id = rfidCard.id

        def model = controller.show()

        assert model.rfidCardInstance == rfidCard
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/rfidCard/list'

        populateValidParams(params)
        def rfidCard = new RfidCard(params)

        assert rfidCard.save() != null

        params.id = rfidCard.id

        def model = controller.edit()

        assert model.rfidCardInstance == rfidCard
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/rfidCard/list'

        response.reset()

        populateValidParams(params)
        def rfidCard = new RfidCard(params)

        assert rfidCard.save() != null

        // test invalid parameters in update
        params.id = rfidCard.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/rfidCard/edit"
        assert model.rfidCardInstance != null

        rfidCard.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/rfidCard/show/$rfidCard.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        rfidCard.clearErrors()

        populateValidParams(params)
        params.id = rfidCard.id
        params.version = -1
        controller.update()

        assert view == "/rfidCard/edit"
        assert model.rfidCardInstance != null
        assert model.rfidCardInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/rfidCard/list'

        response.reset()

        populateValidParams(params)
        def rfidCard = new RfidCard(params)

        assert rfidCard.save() != null
        assert RfidCard.count() == 1

        params.id = rfidCard.id

        controller.delete()

        assert RfidCard.count() == 0
        assert RfidCard.get(rfidCard.id) == null
        assert response.redirectedUrl == '/rfidCard/list'
    }
}
