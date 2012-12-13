package eccount
/**
  * @author  : prayag
  * @created : 13 Dec, 2012
  */
class Account {
    Customer customer;
    Double balance;
    //Card rfid;
    static constraints = {
       customer(blank:false);
       //rfid();
       balance(blank:false);
    }
}
