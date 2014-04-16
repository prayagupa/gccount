package eccount



import org.junit.*
import grails.test.mixin.*

@TestFor(ItemController)
@Mock(Item)
class ItemControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/item/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.itemInstanceList.size() == 0
        assert model.itemInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.itemInstance != null
    }

    void testSave() {
        controller.save()

        assert model.itemInstance != null
        assert view == '/item/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/item/show/1'
        assert controller.flash.message != null
        assert Item.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/item/list'

        populateValidParams(params)
        def item = new Item(params)

        assert item.save() != null

        params.id = item.id

        def model = controller.show()

        assert model.itemInstance == item
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/item/list'

        populateValidParams(params)
        def item = new Item(params)

        assert item.save() != null

        params.id = item.id

        def model = controller.edit()

        assert model.itemInstance == item
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/item/list'

        response.reset()

        populateValidParams(params)
        def item = new Item(params)

        assert item.save() != null

        // test invalid parameters in update
        params.id = item.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/item/edit"
        assert model.itemInstance != null

        item.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/item/show/$item.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        item.clearErrors()

        populateValidParams(params)
        params.id = item.id
        params.version = -1
        controller.update()

        assert view == "/item/edit"
        assert model.itemInstance != null
        assert model.itemInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/item/list'

        response.reset()

        populateValidParams(params)
        def item = new Item(params)

        assert item.save() != null
        assert Item.count() == 1

        params.id = item.id

        controller.delete()

        assert Item.count() == 0
        assert Item.get(item.id) == null
        assert response.redirectedUrl == '/item/list'
    }
}
