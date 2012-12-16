package eccount
/**
  * @author  : Prayag Upd
  * @created : 16 Dec, 2012
  */
class RfidCard {
    String identifier;
    Date created = new Date();
    Boolean active=true;

    static constraints = {
         identifier(blank:false)
         created(display:false)
         active(display:false)
    }
}
