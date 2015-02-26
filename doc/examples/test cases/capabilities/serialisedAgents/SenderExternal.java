
import zeus.util.*;
import zeus.actors.*; 
import zeus.concepts.*; 
import zeus.agents.*; 

import java.io.*;

public class SenderExternal implements ZeusExternal,Serializable { 
    
 /**   
    public void runMe () {
        BasicAgent me = context.getAgent();
        me.notifyMonitors(CREATE); 
    }
    */
  //  AgentContext context; 
    
    public void exec (AgentContext context) { 
      //  this.context = context;

                Performative msg = new Performative("inform");
                msg.setReceiver("Receiver");
          
                try {
                     System.out.println("writeAgent = " + writeAgentToString()); 
                     String localAgentString = (Misc.escape(writeAgentToString()) );
                     String unescaped = Misc.unescape (localAgentString); 
                     System.out.println("unescaped = " + unescaped); 
                     System.out.println ("trying to unserialise"); 
                     Object ret = readAgentFromString(unescaped); 
                     System.out.println("ret = " + ret.toString()); 
                     
                     msg.setContent(localAgentString);
                     System.out.println ("PeerGUI localAgentString = " + localAgentString);
                  }
                catch (java.io.IOException ioe) {
                System.err.println ("IO Exception " + ioe);
                }
                catch (java.lang.ClassNotFoundException cnfe) {
                       cnfe.printStackTrace(); 
                }
                    
               context.MailBox().sendMsg(msg);
                System.out.println ("localAgentString sent");
             /*   try {
                    Thread.sleep(100); 
                }
                catch (Exception e) { 
                    ;}
              //      exit(0); */


        }

        
   
    
    /**
     * Serialize object to a String.
     *
     * @author      John Shepherdson.
     * @date        04/05/2001.
     * @param       N/A.
     * @return      String containing the agent.
     * @exception   IOException
     * @see         java.io.ByteArrayOutputStream, java.io.ObjectOutputStream
     */
    public String writeAgentToString () throws java.io.IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream stream = new ObjectOutputStream(out);
      stream.writeObject(this);
      stream.flush();
      stream.close ();
      return out.toString();
    }


/**
     * De-serialize object from a String.
     * Modified by Simon to return an Object, not a LocalAgent for testing
     *
     * @author      John Shepherdson.
     * @date        04/05/2001.
     * @param       String containing the agent.
     * @return      Instance of Object
     * @exception   ClassNotFoundException, StreamCorruptedException, IOException
     * @see         java.io.ByteArrayInputStream, java.io.ObjectInputStream
     */
    public static Object readAgentFromString (String agentString)
      throws java.lang.ClassNotFoundException,
      java.io.StreamCorruptedException, java.io.IOException
      {
        byte [] buf = agentString.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        ObjectInputStream stream = new ObjectInputStream(in);
        Object agent = stream.readObject();
        return agent;
    }

    
    
}

