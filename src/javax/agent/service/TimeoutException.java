
package javax.agent.service;

import javax.agent.*;

/**
 * Thrown when the time has passed over a specifed time-out.
 */
public class TimeoutException extends JasException {
    /**
     * Creates a TimeoutException instance with no messsages.
     */
    public TimeoutException() {
	super();
    }

    /**
     * Creates a TimeoutException instance with the specified message.
     */
    public TimeoutException(String msg) {
	super(msg);
    }

    /**
     * Creates a TimeoutException instance with specifying the time in milliseconds.
     */
    public TimeoutException(long millis) {
	this((new Long(millis)).toString());
    }
}
