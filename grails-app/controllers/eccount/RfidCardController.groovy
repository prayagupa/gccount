package eccount

import org.springframework.dao.DataIntegrityViolationException

class RfidCardController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rfidCardInstanceList: RfidCard.list(params), rfidCardInstanceTotal: RfidCard.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[rfidCardInstance: new RfidCard(params)]
			break
		case 'POST':
	        def rfidCardInstance = new RfidCard(params)
	        if (!rfidCardInstance.save(flush: true)) {
	            render view: 'create', model: [rfidCardInstance: rfidCardInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), rfidCardInstance.id])
	        redirect action: 'show', id: rfidCardInstance.id
			break
		}
    }

    def show() {
        def rfidCardInstance = RfidCard.get(params.id)
        if (!rfidCardInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), params.id])
            redirect action: 'list'
            return
        }

        [rfidCardInstance: rfidCardInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def rfidCardInstance = RfidCard.get(params.id)
	        if (!rfidCardInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [rfidCardInstance: rfidCardInstance]
			break
		case 'POST':
	        def rfidCardInstance = RfidCard.get(params.id)
	        if (!rfidCardInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (rfidCardInstance.version > version) {
	                rfidCardInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'rfidCard.label', default: 'RfidCard')] as Object[],
	                          "Another user has updated this RfidCard while you were editing")
	                render view: 'edit', model: [rfidCardInstance: rfidCardInstance]
	                return
	            }
	        }

	        rfidCardInstance.properties = params

	        if (!rfidCardInstance.save(flush: true)) {
	            render view: 'edit', model: [rfidCardInstance: rfidCardInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), rfidCardInstance.id])
	        redirect action: 'show', id: rfidCardInstance.id
			break
		}
    }

    def delete() {
        def rfidCardInstance = RfidCard.get(params.id)
        if (!rfidCardInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), params.id])
            redirect action: 'list'
            return
        }

        try {
            rfidCardInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'rfidCard.label', default: 'RfidCard'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
