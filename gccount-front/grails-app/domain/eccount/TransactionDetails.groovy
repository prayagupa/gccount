package eccount
/**
  * @author   prayag
  * @created  16 Dec, 2012
  */

class TransactionDetails {
    Transaction transaction;
    Item item;
    Long number;
    
    static constraints = {
 	transaction();
        item();
        number();      
    }
}
