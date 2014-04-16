package eccount

import org.springframework.dao.DataIntegrityViolationException

class ItemController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [itemInstanceList: Item.list(params), itemInstanceTotal: Item.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[itemInstance: new Item(params)]
			break
		case 'POST':
	        def itemInstance = new Item(params)
	        if (!itemInstance.save(flush: true)) {
	            render view: 'create', model: [itemInstance: itemInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'item.label', default: 'Item'), itemInstance.id])
	        redirect action: 'show', id: itemInstance.id
			break
		}
    }

    def show() {
        def itemInstance = Item.get(params.id)
        if (!itemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), params.id])
            redirect action: 'list'
            return
        }

        [itemInstance: itemInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def itemInstance = Item.get(params.id)
	        if (!itemInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [itemInstance: itemInstance]
			break
		case 'POST':
	        def itemInstance = Item.get(params.id)
	        if (!itemInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (itemInstance.version > version) {
	                itemInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'item.label', default: 'Item')] as Object[],
	                          "Another user has updated this Item while you were editing")
	                render view: 'edit', model: [itemInstance: itemInstance]
	                return
	            }
	        }

	        itemInstance.properties = params

	        if (!itemInstance.save(flush: true)) {
	            render view: 'edit', model: [itemInstance: itemInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'item.label', default: 'Item'), itemInstance.id])
	        redirect action: 'show', id: itemInstance.id
			break
		}
    }

    def delete() {
        def itemInstance = Item.get(params.id)
        if (!itemInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), params.id])
            redirect action: 'list'
            return
        }

        try {
            itemInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'item.label', default: 'Item'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'item.label', default: 'Item'), params.id])
            redirect action: 'show', id: params.id
        }
    }
}
