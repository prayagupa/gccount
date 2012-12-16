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
    Date created = new Date();   
    User approvedBy;

    static constraints = {
           account(blank:false);
           amount(blank:false);
           created();
           approvedBy(blank:false);
    }
}
