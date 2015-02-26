package javax.agent;

import java.io.*;

/**
    Implementation of the jas spec.
 */
public class Name implements Serializable {
    public final String separator = "@";
    private String agentName;
    private String domainName;
    private String aid;

 
    public Name(String name) {
	    aid = name;
    }

    public Name(String localname, String domain) {
	    agentName = localname;
	    domainName = domain;
	    aid = agentName + domainName;
    }

    public String toString() {
	return aid;
    }
}
