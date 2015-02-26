/*
 * AddressEvent.java
 *
 * Created on 18 March 2002, 16:38
 */

package zeus.concepts;

/**
 *
 * @author  thompss
 * @version 
 */
public class AddressEvent extends java.util.EventObject {

    private Address addr = null; 
    
    /** Creates new AddressEvent */
    public AddressEvent(Address addr) {
        super (addr);
        this.addr = addr;
    }
    
    public Address getAddress () { 
     return addr;    
    }

    public void setAddress (Address addr) { 
        this.addr = addr; 
    }
}
