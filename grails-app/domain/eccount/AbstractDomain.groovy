package eccount
/**
  * @author : Prayag Upd
  */
abstract class AbstractDomain {
    Date created = new Date();
    Date lastModified = new Date();

    static constraints = {
          created(display:false)
          lastModified(display:false)
    }
}
