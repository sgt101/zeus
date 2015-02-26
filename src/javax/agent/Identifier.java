
package javax.agent;

import java.io.*;

public class Identifier implements Serializable {
    private Name name;
    private String location;

    public Identifier(Name name, String location) {
	    this.location = location;
	    this.name = name; 
    }

    public String getLocation() {
	    return location;
    }

    public String toString() {
	    return location;
    }
    
    public Name getName () { 
        return name; 
    }
    
        
}
