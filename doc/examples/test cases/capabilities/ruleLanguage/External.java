

import zeus.actors.*;
import zeus.agents.*;
import zeus.concepts.*;
import zeus.actors.event.*;

public class External implements ZeusExternal,MessageMonitor, Runnable {

  int received = 0;
  int sent = 0;
  boolean killed = false;
  SpeedFrontEnd frontend;

  public void exec (AgentContext context) {
    frontend = new SpeedFrontEnd();
    MailBox mbox = context.getMailBox();
    mbox.addMessageMonitor (this,MailBox.QUEUE);

    Thread nt = new Thread(this);
    nt.start();
  }


 /**
        this is the thread that updates the GUI
        */
    public void run (){
        int count = 0;
        while (!killed) {
        try {
                Thread.sleep(1000);  }
                catch (Exception e) {
                    e.printStackTrace(); }
            count++;
            frontend.setNumberReceived (received);
            frontend.setNumberSent (sent);
            frontend.setNumberReceivedPerSecond (received/count);
            frontend.setNumberSentPerSecond (sent/count);
        }
        }


   public void messageReceivedEvent(MessageEvent event) {
    received++;
      }

   public void messageQueuedEvent(MessageEvent event){
    ;
   }

   public void messageDispatchedEvent(MessageEvent event){
    ;
    }

   public void messageNotDispatchedEvent(MessageEvent event){
    ;
    }


}