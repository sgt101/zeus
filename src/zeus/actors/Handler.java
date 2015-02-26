package zeus.actors;
import zeus.concepts.Performative; 
public class Handler implements java.lang.Runnable { 
 
 
 private RootNode node = null; 
 private Performative msg = null; 
 
 
  public Handler (RootNode node, Performative msg) { 
    this.node = node; 
    this.msg = msg; 
  }
    
    
 public void run () { 
 //   System.out.println("HANDLING :" + msg.getSender()  + " " + msg.getContent()); 
    node.evaluate(null,msg);
 }
}