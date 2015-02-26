
package javax.agent;

import java.io.*;

public class Envelope implements Serializable {
    private Identifier receiver;
    private Identifier sender;
    private Object object;

    public Envelope(Identifier receiver, Identifier sender, Object object) {
	    this.sender = sender;
	    this.receiver = receiver;
	    this.object = object;
    }

    public Identifier getReceiver() {
	    return receiver;
    }

    public Identifier getSender() {
	    return sender;
    }

    public Object getObject() {
	    return object;
    }
}

