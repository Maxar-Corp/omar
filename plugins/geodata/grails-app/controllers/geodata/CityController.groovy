package geodata

class CityController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [cityInstanceList: City.list(params), cityInstanceTotal: City.count()]
    }

    def create = {
        def cityInstance = new City()
        cityInstance.properties = params
        return [cityInstance: cityInstance]
    }

    def save = {
        def cityInstance = new City(params)
        if (cityInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'city.label', default: 'City'), cityInstance.id])}"
            redirect(action: "show", id: cityInstance.id)
        }
        else {
            render(view: "create", model: [cityInstance: cityInstance])
        }
    }

    def show = {
        def cityInstance = City.get(params.id)
        if (!cityInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'city.label', default: 'City'), params.id])}"
            redirect(action: "list")
        }
        else {
            [cityInstance: cityInstance]
        }
    }

    def edit = {
        def cityInstance = City.get(params.id)
        if (!cityInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'city.label', default: 'City'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [cityInstance: cityInstance]
        }
    }

    def update = {
        def cityInstance = City.get(params.id)
        if (cityInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (cityInstance.version > version) {
                    
                    cityInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'city.label', default: 'City')] as Object[], "Another user has updated this City while you were editing")
                    render(view: "edit", model: [cityInstance: cityInstance])
                    return
                }
            }
            cityInstance.properties = params
            if (!cityInstance.hasErrors() && cityInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'city.label', default: 'City'), cityInstance.id])}"
                redirect(action: "show", id: cityInstance.id)
            }
            else {
                render(view: "edit", model: [cityInstance: cityInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'city.label', default: 'City'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def cityInstance = City.get(params.id)
        if (cityInstance) {
            try {
                cityInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'city.label', default: 'City'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'city.label', default: 'City'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'city.label', default: 'City'), params.id])}"
            redirect(action: "list")
        }
    }
}
