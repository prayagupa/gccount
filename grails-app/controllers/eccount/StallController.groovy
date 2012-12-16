package eccount

import org.springframework.dao.DataIntegrityViolationException

class StallController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [stallInstanceList: Stall.list(params), stallInstanceTotal: Stall.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[stallInstance: new Stall(params)]
			break
		case 'POST':
	        def stallInstance = new Stall(params)
	        if (!stallInstance.save(flush: true)) {
	            render view: 'create', model: [stallInstance: stallInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'stall.label', default: 'Stall'), stallInstance.id])
	        redirect action: 'show', id: stallInstance.id
			break
		}
    }

    def show() {
        def stallInstance = Stall.get(params.id)
        if (!stallInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'stall.label', default: 'Stall'), params.id])
            redirect action: 'list'
            return
        }

        [stallInstance: stallInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def stallInstance = Stall.get(params.id)
	        if (!stallInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'stall.label', default: 'Stall'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [stallInstance: stallInstance]
			break
		case 'POST':
	        def stallInstance = Stall.get(params.id)
	        if (!stallInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'stall.label', default: 'Stall'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (stallInstance.version > version) {
	                stallInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'stall.label', default: 'Stall')] as Object[],
	                          "Another user has updated this Stall while you were editing")
	                render view: 'edit', model: [stallInstance: stallInstance]
	                return
	            }
	        }

	        stallInstance.properties = params

	        if (!stallInstance.save(flush: true)) {
	            render view: 'edit', model: [stallInstance: stallInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'stall.label', default: 'Stall'), stallInstance.id])
	        redirect action: 'show', id: stallInstance.id
			break
		}
    }

    def delete() {
        def stallInstance = Stall.get(params.id)
        if (!stallInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'stall.label', default: 'Stall'), params.id])
            redirect action: 'list'
            return
        }

        try {
            stallInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'stall.label', default: 'Stall'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'stall.label', default: 'Stall'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
