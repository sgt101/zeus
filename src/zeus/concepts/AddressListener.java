/*
 * AddressListener.java
 *
 * Created on 18 March 2002, 16:35
 */

package zeus.concepts;

/**
 *
 * @author  thompss
 * @version 
 */
public interface AddressListener {

    public void newAddress (AddressEvent addr); 
    
    public void deleteAddress (AddressEvent addr); 
    
    public void replaceAddress (AddressEvent addr); 
    
}

