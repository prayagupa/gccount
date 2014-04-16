package eccount

import org.springframework.dao.DataIntegrityViolationException

class CustomerController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [customerInstanceList: Customer.list(params), customerInstanceTotal: Customer.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[customerInstance: new Customer(params)]
			break
		case 'POST':
	        def customerInstance = new Customer(params)
	        if (!customerInstance.save(flush: true)) {
	            render view: 'create', model: [customerInstance: customerInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'customer.label', default: 'Customer'), customerInstance.id])
	        redirect action: 'show', id: customerInstance.id
			break
		}
    }

    def show() {
        def customerInstance = Customer.get(params.id)
        if (!customerInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect action: 'list'
            return
        }

        [customerInstance: customerInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def customerInstance = Customer.get(params.id)
	        if (!customerInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [customerInstance: customerInstance]
			break
		case 'POST':
	        def customerInstance = Customer.get(params.id)
	        if (!customerInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (customerInstance.version > version) {
	                customerInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'customer.label', default: 'Customer')] as Object[],
	                          "Another user has updated this Customer while you were editing")
	                render view: 'edit', model: [customerInstance: customerInstance]
	                return
	            }
	        }

	        customerInstance.properties = params

	        if (!customerInstance.save(flush: true)) {
	            render view: 'edit', model: [customerInstance: customerInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'customer.label', default: 'Customer'), customerInstance.id])
	        redirect action: 'show', id: customerInstance.id
			break
		}
    }

    def delete() {
        def customerInstance = Customer.get(params.id)
        if (!customerInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect action: 'list'
            return
        }

        try {
            customerInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'customer.label', default: 'Customer'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
