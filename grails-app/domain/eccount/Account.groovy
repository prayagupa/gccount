package eccount
/**
  * @author  : prayag
  * @created : 13 Dec, 2012
  */
class Account {
    Customer customer;
    Double balance;
    RfidCard rfid;
    Date created = new Date();
    Boolean active=true;

    static constraints = {
       customer(blank:false);
       rfid(blank:false);
       balance(blank:false);
       created(display:false)
       active(display:false)	
    }

    String toString(){
         "${customer.firstName+" "+customer.lastName}+" "+customer.lastName}"
    }
}
