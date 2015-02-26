import zeus.concepts.*; 
import zeus.actors.*; 
import zeus.agents.*; 
import java.util.*;


/**
 Response is the external for the testForward zeus agent, and contains the code that
 sets the agent up to respond to the requests that have been sent to it. 
 */
public class Response implements ZeusExternal{  
    
    
    private AgentContext context = null; 
    
    
    public void exec (AgentContext context) { 
        // put context into a global for use by other methods
        this.context = context; 
        // set up a message rule to detect that the agent has had some mail!
        MsgHandler msg = context.getMsgHandler (); 
        String msg_pattern[] =  { "type", "request", "content", "\\A( retrieve_fact\\Z"};
        msg.addRule(new MessageRule(context.newId("Rule"),msg_pattern,this, "respond"));
    }
    
    
    /** 
        respond is the method that is called by the agent's MsgHandler when the 
        rule that is added in the exec() method of this object fires. 
        */
    public void respond (Performative perf) { 
        Performative reply = new Performative ("inform"); 
        String msgContent = perf.getContent(); 
        StringTokenizer tokens = new StringTokenizer (msgContent,":"); 
        OntologyDb ont = context.getOntologyDb(); 
        try { 
            tokens.nextToken(); // discard the front of the message 
            String factType = tokens.nextToken();
            if (factType.startsWith("type information")) { 
                reply.setReceiver(perf.getReplyTo()); 
                Fact fact = ont.getFact (false,"information"); 
                fact.setValue("retVal","A successful response!"); 
                reply.setContent(fact.toString()); }
                else { 
                    reply = new Performative ("not-understood"); 
                    reply.setReceiver(perf.getReplyTo()); 
                    }
                MailBox mail = context.getMailBox();
                
                mail.sendMsg(reply); 
        }
            catch (Exception e) { 
                e.printStackTrace(); 
                System.out.println("Error in Response, the testForward agent's external");
                System.out.println("Probably a bad message...."); 
            }
    }
    
    
}