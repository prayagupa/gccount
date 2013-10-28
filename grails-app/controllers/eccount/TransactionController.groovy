package eccount

import org.springframework.dao.DataIntegrityViolationException

class TransactionController {

    def transactionService

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [transactionInstanceList: Transaction.list(params), transactionInstanceTotal: Transaction.count()]
    }

    def create() {
		switch (request.method) {
		case 'GET':
        	[transactionInstance: new Transaction(params)]
			break
		case 'POST':
	        def transactionInstance = new Transaction(params)
	        if (!transactionInstance.save(flush: true)) {
	            render view: 'create', model: [transactionInstance: transactionInstance]
	            return
	        }

			flash.message = message(code: 'default.created.message', args: [message(code: 'transaction.label', default: 'Transaction'), transactionInstance.id])
	        redirect action: 'show', id: transactionInstance.id
			break
		}
    }

    def show() {
        def transactionInstance = Transaction.get(params.id)
        if (!transactionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'transaction.label', default: 'Transaction'), params.id])
            redirect action: 'list'
            return
        }

        [transactionInstance: transactionInstance]
    }

    def edit() {
		switch (request.method) {
		case 'GET':
	        def transactionInstance = Transaction.get(params.id)
	        if (!transactionInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transaction.label', default: 'Transaction'), params.id])
	            redirect action: 'list'
	            return
	        }

	        [transactionInstance: transactionInstance]
			break
		case 'POST':
	        def transactionInstance = Transaction.get(params.id)
	        if (!transactionInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'transaction.label', default: 'Transaction'), params.id])
	            redirect action: 'list'
	            return
	        }

	        if (params.version) {
	            def version = params.version.toLong()
	            if (transactionInstance.version > version) {
	                transactionInstance.errors.rejectValue('version', 'default.optimistic.locking.failure',
	                          [message(code: 'transaction.label', default: 'Transaction')] as Object[],
	                          "Another user has updated this Transaction while you were editing")
	                render view: 'edit', model: [transactionInstance: transactionInstance]
	                return
	            }
	        }

	        transactionInstance.properties = params

	        if (!transactionInstance.save(flush: true)) {
	            render view: 'edit', model: [transactionInstance: transactionInstance]
	            return
	        }

			flash.message = message(code: 'default.updated.message', args: [message(code: 'transaction.label', default: 'Transaction'), transactionInstance.id])
	        redirect action: 'show', id: transactionInstance.id
			break
		}
    }

    def delete() {
        def transactionInstance = Transaction.get(params.id)
        if (!transactionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'transaction.label', default: 'Transaction'), params.id])
            redirect action: 'list'
            return
        }

        try {
            transactionInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'transaction.label', default: 'Transaction'), params.id])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'transaction.label', default: 'Transaction'), params.id])
            redirect action: 'show', id: params.id
        }
    }
    /**
      * list daily sales for a stall
      */	
    def daily() {
       	println "Request : "+request.method
        
         switch(request.method){
             case 'GET':
	     //	[transactionInstance: new Transaction(params)];
                 [transactionInstanceList:null, transactionInstanceTotal:0]
                break;
             case 'POST':
	        params.max = Math.min(params.max ? params.int('max') : 10, 100)
       		def fromDate  = params.fromDate;
		println "fromDate : " + fromDate;
		/*
        		def query = Transaction.where {
				(created <= fromDate)
			}
			def results = query.list(sort:"created")
		*/
	
		//creating criteria
		def trxnCriteria = Transaction.createCriteria()
		def results = trxnCriteria.list {
		    eq("created", fromDate)
		    //TODO
	            //eq("user", user)		
		}
		for(result in results){	
			println "result created : "+result.created
		}
		println "count : "+results.count
		render(view: "daily", model: [salesDate:fromDate, transactionInstanceList: results,transactionInstanceTotal: results.count]);
		//[transactionInstanceList: results, transactionInstanceTotal: results.count]
          }//end of switch
    }//end of daily

     /**
       * list monthly sales for a logged in stall
       */
    def monthly() {
	println request.method
        switch(request.method){
             case 'GET':
		[transactionInstanceList:null, transactionInstanceTotal:0]
                break;
             case 'POST':
        	params.max = Math.min(params.max ? params.int('max') : 10, 100)
	        def fromDate  = params.fromDate
		def toDate    = fromDate+30
		//creating criteria
		def results = Transaction.withCriteria() {
		    between('created', fromDate, toDate)
		}
		println "Count - "+results.count;
		render(view:"monthly", model:[fromDate:fromDate, transactionInstanceList: results, transactionInstanceTotal: results.count])
	 }//end of switch
    }//end of monthly

	
   /**
     * list overall sales
     */
   def anyRange() {
        println request.method
        switch(request.method){
             case 'GET':
                [transactionInstanceList:null, transactionInstanceTotal:0]
                break;
             case 'POST':
                params.max = Math.min(params.max ? params.int('max') : 10, 100)
                def fromDate  = params.fromDate
                def toDate    = params.toDate
                //creating criteria
                def results = Transaction.withCriteria() {
                    between('created', fromDate, toDate)
                }
                println "Count - "+results.count;
                render(view:"anyRange", model:[fromDate:fromDate, toDate: toDate, transactionInstanceList: results, transactionInstanceTotal: results.count])
         }//end of switch
    }//end of anyRange

   def requestES = {
       transactionService.requestES("http://", "transactionCode=TRXN1")
   }
}
