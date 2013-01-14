package eccount
/*
 * @author : prayag upd
 * @created : 24 Dec, 2012
*/
class TransactionService {

    def getDailyTrxns() {
		def fromDate  = new Date(); 
		def trxnCriteria = Transaction.createCriteria()
		def results = trxnCriteria.list {
		    eq("created", fromDate)
		}
        }//end of dailyTrxns
}
