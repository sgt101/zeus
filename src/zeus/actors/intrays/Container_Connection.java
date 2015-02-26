package zeus.actors.intrays;

import zeus.util.Queue;
import zeus.concepts.*;

public class Container_Connection implements Runnable {
	
Queue que = null; 

 public Container_Connection (Queue msgQ) { 
   que = msgQ; 
 }
		

 public void message (Performative perf) {
	que.enqueue (perf); 
	}

 public void run() {
        ;
        }
	

}