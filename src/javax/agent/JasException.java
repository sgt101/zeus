
package javax.agent;

/**
 * The JasException is the base of exceptions
 * defined in the JAS(Java Agent Services) API.
 */

public class JasException extends Exception {
    private Exception exp;

    public JasException() {
	super();
    }

    public JasException(String msg) {
	super(msg);
    }

    /**
     * Sets the original exception.
     */
    public void setException(Exception exp) {
	this.exp = exp;
    }

    /**
     * Returns the exception that caused this exception
     * if it is stored, otherwise null.
     */
    public Exception getException() {
	return exp;
    }
}
