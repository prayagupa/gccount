package eccount

import org.springframework.dao.DataIntegrityViolationException

class ItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [itemInstanceList: Item.list(params), itemInstanceTotal: Item.count()]
    }

    def create() {
        [itemInstance: new Item(params)]
    }

    def save() {
        def itemInstance = new Item(params)
        if (!itemInstance.save(flush: true)) {
            render(view: "create", model: [itemInstance: itemInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'item.label', default: 'Item'), itemInstance.id])
        redirect(action: "show", id: itemInstance.id)
    }

    def show(Long id) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        [itemInstance: itemInstance]
    }

    def edit(Long id) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        [itemInstance: itemInstance]
    }

    def update(Long id, Long version) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (itemInstance.version > version) {
                itemInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'item.label', default: 'Item')] as Object[],
                          "Another user has updated this Item while you were editing")
                render(view: "edit", model: [itemInstance: itemInstance])
                return
            }
        }

        itemInstance.properties = params

        if (!itemInstance.save(flush: true)) {
            render(view: "edit", model: [itemInstance: itemInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'item.label', default: 'Item'), itemInstance.id])
        redirect(action: "show", id: itemInstance.id)
    }

    def delete(Long id) {
        def itemInstance = Item.get(id)
        if (!itemInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
            return
        }

        try {
            itemInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'item.label', default: 'Item'), id])
            redirect(action: "show", id: id)
        }
    }
}
