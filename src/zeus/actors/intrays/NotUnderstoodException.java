package zeus.actors.intrays;

public class NotUnderstoodException extends Exception {
    
    private String reason = null; 
    
    public NotUnderstoodException (String reason) { 
        super(); 
        this.reason = reason; 
    }
    
    
    public String getReason () { 
        return reason; 
    }
    
}