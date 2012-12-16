package eccount
/**
  * @author   prayag
  * @created  13 Dec, 2012
  */
class Transaction {
    //Customer customer;
    //static belongsTo = Customer;
    Account account;
    static belongsTo = Account;
    Double amount;
    static hasMany  = [transactionDetails:TransactionDetails];
    static mappedBy = [transactionDetails:"transaction"]
    Date created = new Date();
    User approvedBy;
    
    static constraints = {
           account(blank:false);
           amount(blank:false);
	   transactionDetails();
           created();
           approvedBy(blank:false);
    }
    String toString(){
         "${account.rfid}"
    }
}
