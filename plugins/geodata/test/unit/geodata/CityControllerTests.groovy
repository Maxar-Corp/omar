package geodata



import org.junit.*
import grails.test.mixin.*

@TestFor(CityController)
@Mock(City)
class CityControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/city/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.cityInstanceList.size() == 0
        assert model.cityInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.cityInstance != null
    }

    void testSave() {
        controller.save()

        assert model.cityInstance != null
        assert view == '/city/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/city/show/1'
        assert controller.flash.message != null
        assert City.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/city/list'


        populateValidParams(params)
        def city = new City(params)

        assert city.save() != null

        params.id = city.id

        def model = controller.show()

        assert model.cityInstance == city
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/city/list'


        populateValidParams(params)
        def city = new City(params)

        assert city.save() != null

        params.id = city.id

        def model = controller.edit()

        assert model.cityInstance == city
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/city/list'

        response.reset()


        populateValidParams(params)
        def city = new City(params)

        assert city.save() != null

        // test invalid parameters in update
        params.id = city.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/city/edit"
        assert model.cityInstance != null

        city.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/city/show/$city.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        city.clearErrors()

        populateValidParams(params)
        params.id = city.id
        params.version = -1
        controller.update()

        assert view == "/city/edit"
        assert model.cityInstance != null
        assert model.cityInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/city/list'

        response.reset()

        populateValidParams(params)
        def city = new City(params)

        assert city.save() != null
        assert City.count() == 1

        params.id = city.id

        controller.delete()

        assert City.count() == 0
        assert City.get(city.id) == null
        assert response.redirectedUrl == '/city/list'
    }
}
