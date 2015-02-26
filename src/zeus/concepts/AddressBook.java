/*
 * The contents of this file are subject to the BT "ZEUS" Open Source
 * Licence (L77741), Version 1.0 (the "Licence"); you may not use this file
 * except in compliance with the Licence. You may obtain a copy of the Licence
 * from $ZEUS_INSTALL/licence.html or alternatively from
 * http://www.labs.bt.com/projects/agents/zeus/licence.htm
 *
 * Except as stated in Clause 7 of the Licence, software distributed under the
 * Licence is distributed WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing rights and
 * limitations under the Licence.
 *
 * The Original Code is within the package zeus.*.
 * The Initial Developer of the Original Code is British Telecommunications
 * public limited company, whose registered office is at 81 Newgate Street,
 * London, EC1A 7AJ, England. Portions created by British Telecommunications
 * public limited company are Copyright 1996-9. All Rights Reserved.
 *
 * THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
 */



package zeus.concepts;

import java.util.*;


public class AddressBook extends Hashtable {
    
    private Vector listeners = new Vector();
    
    // Default Constructor: public AddressBook()
    
    public boolean add(Address addr) {
        Address obj;
        put(addr.getName(), addr); // simplified for expediency
        try {
            Iterator allListeners = listeners.iterator();
            while (allListeners.hasNext()) {
                AddressListener current = (AddressListener) allListeners.next();
                AddressEvent event = new AddressEvent(addr);
                current.newAddress(event);
            }}
            catch (Exception e) {
                e.printStackTrace(); }
                
                return true;
    }
    
    public void replace(Address addr) {
        this.put(addr.getName(), addr);
        try {
            Iterator allListeners = listeners.iterator();
            while (allListeners.hasNext()) {
                AddressListener current = (AddressListener) allListeners.next();
                AddressEvent event = new AddressEvent(addr);
                current.replaceAddress(event);
            }}
            catch (Exception e) {
                e.printStackTrace(); }
    }
    
    public void del(Address addr) {
        this.remove(addr.getName());
        try {
            Iterator allListeners = listeners.iterator();
            while (allListeners.hasNext()) {
                AddressListener current = (AddressListener) allListeners.next();
                AddressEvent event = new AddressEvent(addr);
                current.deleteAddress(event);
            }}
            catch (Exception e) {
                e.printStackTrace(); }
    }
    
    public void del(String name) {
        this.remove(name)  ;
    }
    
    public Address lookup( String name ) {
        return (Address) this.get(name);
    }
    
    public void addAddressListener(AddressListener listen) {
        listeners.addElement(listen);
    }
    
    public void removeAddressListener(AddressListener listen) {
        listeners.removeElement(listen);
    }
}
