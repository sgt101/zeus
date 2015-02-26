/*
* The contents of this file are subject to the BT "ZEUS" Open Source 
* Licence (L77741), Version 1.0 (the "Licence"); you may not use this file 
* except in compliance with the Licence. You may obtain a copy of the Licence
* from $ZEUS_INSTALL/licence.html or alternatively from
* http://www.labs.bt.com/projects/agents/zeus/licence.htm
* 
* Except as stated in Clause 7 of the Licence, software distributed under the
* Licence is distributed WITHOUT WARRANTY OF ANY KIND, either express or 
* implied. See the Licence for the specific language governing rights and 
* limitations under the Licence.
* 
* The Original Code is within the package zeus.*.
* The Initial Developer of the Original Code is British Telecommunications
* public limited company, whose registered office is at 81 Newgate Street, 
* London, EC1A 7AJ, England. Portions created by British Telecommunications 
* public limited company are Copyright 1996-2001. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



/*
 * @(#)Performative.java 1.00
 */

package zeus.concepts;

import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.actors.*;
import zeus.actors.factories.*; 
import zeus.actors.outtrays.*;

/**
 * The Performative class provides a standard data structure for inter-agent
 * messaging. The message format and available message types have been derived 
 * from the FIPA Agent Communication Language description. <p>
 *
 * This class contains methods that allow the attributes of performative objects
 * to be set and modified, although typically developers will only need to
 * invoke one of the constructors. More information on performatives and the
 * communication mechanism is provided in the Zeus Technical Manual.
 */


public class Performative 
{
   public static final String[] MESSAGE_TYPES = {
      "accept-proposal",
      "agree",
      "cancel",
      "cfp",
      "confirm",
      "disconfirm",
      "failure",
      "inform",
      "inform-if",
      "inform-ref",
      "not-understood",
      "propose",
      "query-if",
      "query-ref",
      "refuse",
      "reject-proposal",
      "request",
      "request-when",
      "request-whenever",
      "subscribe",
      "request-forward"
   };
   
     public static final String[] MESSAGE_TYPES_UPPER = {
      "ACCEPT-PROPOSAL",
      "AGREE",
      "CANCEL",
      "CFP",
      "CONFIRM",
      "DISCONFIRM",
      "FAILURE",
      "INFORM",
      "INFORM-IF",
      "INFORM-REF",
      "NOT-UNDERSTOOD",
      "PROPOSE",
      "QUERY-IF",
      "QUERY-REF",
      "REFUSE",
      "REJECT-PROPOSAL",
      "REQUEST",
      "REQUEST-WHEN",
      "REQUEST-WHENEVER",
      "SUBSCRIBE",
      "REQUEST-FORWARD"
   };

   public static final String[] ATTRIBUTE_TYPES = {
      "type",
      "sender",
      "receiver",
      "content",
      "reply-with",
      "in-reply-to",
      "reply-by",
      "ontology",
      "language",
      "protocol",
      "conversation-id",
      "reply-to"
   };
   // note reply-to has been added to the end if this array 

   private static final int TYPE            = 0;
   private static final int SENDER          = 1;
   private static final int RECEIVER        = 2;
   private static final int CONTENT         = 3;
   private static final int REPLY_WITH      = 4;
   private static final int IN_REPLY_TO     = 5;
   private static final int REPLY_BY        = 6;
   private static final int ONTOLOGY        = 7;
   private static final int LANGUAGE        = 8;
   private static final int PROTOCOL        = 9;
   private static final int CONVERSATION_ID = 10;
   private static final int REPLY_TO        = 11; 

   protected String    type;
   protected String    sender;
   protected HSet      receivers = new HSet();
   protected String    content;
   protected String    replyWith;
   protected String    inReplyTo;
   protected String    replyBy;
   protected String    ontology;
   protected String    language;
   protected String    protocol;
   protected String    conversationId;
   protected Hashtable envelope = new Hashtable();
   protected String    replyTo; 

   public Performative () { 
    }

   public Performative(String type) {
      setType(type);
   }

   public Performative(Performative perf) {
      String  token;
      Hashtable table;
      Enumeration data;

      if ( (token = perf.getType()) != null )		setType(token);
      if ( (token = perf.getSender()) != null )		setSender(token);
      if ( (data  = perf.getReceivers()) != null )	setReceivers(data);
      if ( (token = perf.getReplyWith()) != null )	setReplyWith(token);
      if ( (token = perf.getInReplyTo()) != null )	setInReplyTo(token);
      if ( (token = perf.getReplyBy()) != null )	setReplyBy(token);
      if ( (token = perf.getOntology()) != null )	setOntology(token);
      if ( (token = perf.getLanguage()) != null )	setLanguage(token);
      if ( (token = perf.getContent()) != null )	setContent(token);
      if ( (token = perf.getProtocol()) != null )	setProtocol(token);
      if ( (token = perf.getConversationId()) != null )	setConversationId(token);
      if ( (table = perf.getEnvelope()) != null )	setEnvelope(table);
      if ( (token = perf.getReplyTo()) != null ) setReplyTo(token); 
   }


   public void setType(String type) {
    type = type.trim();
      if ( Misc.member(type,MESSAGE_TYPES) ) {
         this.type = type;
      }
      else if ( Misc.member(type,MESSAGE_TYPES_UPPER) ) {
         this.type = type;
      }
      else {
         Core.ERROR(null,1,"Invalid Performative type: " + type);
      }
   }

   public void setSender(String value){
        sender = value; }
   
   
   public void setReceiver(String value){
        receivers.add(value); }
   
   
   public void setReplyWith(String value){
        replyWith = value; }
   
   
   public void setInReplyTo(String value){
        inReplyTo = value; }
   
   
   public void setReplyBy(String value) { 
        replyBy = value; }
   
   
   public void setReplyTo (String value) { 
        replyTo = value; 
   }


   public void setOntology(String value) {
        ontology = value; }
        
        
   public void setLanguage(String value) { 
        language = value; }
        
        
   public void setContent(String value) {
        content = value; }


   public void setProtocol(String value) {
        protocol = value; }
        
        
   public void setConversationId(String value) { 
        conversationId = value; }



   public void setReceivers(Vector input) {
      receivers.clear();
      Enumeration enum = input.elements();
      while( enum.hasMoreElements() )
         receivers.add(enum.nextElement());
   }
   
   
   public void setReceivers(HSet input) {
      receivers.clear();
      Enumeration enum = input.elements();
      while( enum.hasMoreElements() )
         receivers.add(enum.nextElement());
   }
   
   
   public void setReceivers(Enumeration enum) {
      receivers.clear();
      while( enum.hasMoreElements() )
         receivers.add(enum.nextElement());
   }

   public void setAddress(Address value )  {
      envelope.put("address",value);
   }
   
   
   public void setSendTime(Time value)	   {
      envelope.put("sendTime",value);
   }
   
   
   public void setReceiveTime(Time value)  {
      envelope.put("receiveTime",value);
   }

   public void setEnvelopeItem(String name, Object item) {
      envelope.put(name,item);
   }


   public Object getEnvelopeItem(String name) {
      return envelope.get(name);
   }


   public void setEnvelope(Hashtable input) {
      envelope.clear();
      Enumeration enum = input.keys();
      Object key;
      Object value;
      while( enum.hasMoreElements() ) {
         key = enum.nextElement();
         value = input.get(key);
         envelope.put(key,value);
      }
   }

   public Hashtable getEnvelope() { 
        return envelope; }


   public String getType() { 
        return type; }
        
        
   public String getSender() {
        return sender; }
        
        
   public String getReplyWith(){
        return replyWith; }
        
        
   public String getInReplyTo()	{ 
        return inReplyTo; }
        
        
   public String getReplyBy(){ 
        return replyBy; }
        
        
   public String getReplyTo () {
        return replyTo; 
   }
        
        
   public String getOntology() {
        return ontology; }
        
        
   public String getLanguage() {
        return language; }
        
        
   public String getContent() {
        return content; }
        
        
   public String getProtocol(){
        return protocol; }
        
        
   public String getConversationId() {
        return conversationId; }


   public String getReceiver() {
      Enumeration enum = receivers.elements();
      try {
        return (String) enum.nextElement();}
        catch (Exception e ) { 
            return enum.nextElement().toString(); }
   }
   
   
   public Enumeration getReceivers(){
      return receivers.elements();
   }


   public Address getAddress()	{
      Object item = envelope.get("address");
      if ( item instanceof String ) {
         Address a = ZeusParser.address((String)item);
         envelope.put("address",a);
         return a;
      }
      else
         return (Address)item;
   }
   
   
   public Time getSendTime()	{
      Object item = envelope.get("sendTime");
      if ( item instanceof String ) {
         Time t = new Time((String)item);
         envelope.put("sendTime", t);
         return t;
      }
      else
         return (Time)item;
   }
   
   
   public Time getReceiveTime()	{
      Object item = envelope.get("receiveTime");
      if ( item instanceof String ) {
         Time t = new Time((String)item);
         envelope.put("receiveTime", t);
         return t;
      }
      else
         return (Time)item;
   }


   public boolean isValid() {
      return type != null && sender != null && !receivers.isEmpty();
   }




StringBuffer sb ;

   public final String toString() {
      sb = new StringBuffer(300); 
      sb.append ("(");
      sb.append (type);
      sb.append ("\n");
    //  String str = "(" + type + "\n";

      if ( sender != null ) {
         sb.append (" :sender ");
         sb.append (sender);
         sb.append ("\n");
         }
      //   str += " :sender " + sender + "\n";
      if ( receivers != null && !receivers.isEmpty() ) {
         sb.append (" :receiver " );
         if ( receivers.size() > 1 ){
          sb.append ("(");
          sb.append (Misc.concat(receivers));
          sb.append (")");
          }
         else {
           sb.append ( Misc.concat(receivers));
          }
          sb.append ("\n");
         //str += " :receiver " + s + "\n";
      }
      if ( replyWith != null ) {
         sb.append (" :reply-with ");
         sb.append (replyWith);
         sb.append ("\n");
//         str += " :reply-with " + replyWith + "\n";
        }
      if ( inReplyTo != null ) {
          sb.append (" :in-reply-to ");
          sb.append (inReplyTo);
          sb.append ("\n");
          }
         //str += " :in-reply-to " + inReplyTo + "\n";
      if ( replyBy != null ) {
          sb.append (" :reply-by ");
          sb.append (replyBy);
          sb.append ("\n");
          }
         //str += " :reply-by " + replyBy + "\n";
      if ( ontology != null ) {
          sb.append (" :ontology ");
          sb.append (ontology);
          sb.append ("\n");
        }
  //         str += " :ontology " + ontology + "\n";
      if ( language != null ) {
         sb.append (" :language ");
         sb.append (language);
         sb.append ("\n");
         }
         //str += " :language " + language + "\n";
      if ( content != null ) {
        sb.append (" :content ");
        sb.append ("\"");
        String temp = Misc.escape (content); 
        sb.append (temp);
        temp = null; 
        sb.append ("\"");
        sb.append ("\n");
        }
         //str += " :content " + "\"" + Misc.escape(content) + "\"" + "\n";
      if ( protocol != null ) {
          sb.append (" :protocol ");
          sb.append (protocol);
          sb.append ("\n");
      }
       //  str += " :protocol " + protocol + "\n";
      if ( conversationId != null ) {
         sb.append (" :conversation-id ");
         sb.append (conversationId);
         sb.append ("\n");
         }
         //str += " :conversation-id " + conversationId + "\n";
      if ( replyTo != null ) {
          sb.append (" :reply-to ");
          sb.append (replyTo);
          sb.append ("\n");
        }
         //str += " :reply-to " + replyTo + "\n";

      if ( envelope != null && !envelope.isEmpty() ) {
         sb.append(" :envelope (");
         Enumeration enum = envelope.keys();
         String key;
         Object value;
         while( enum.hasMoreElements() ) {
            key = (String)enum.nextElement();
            value = envelope.get(key);
            sb.append("(");
            sb.append(key);
            sb.append(" \"");
            sb.append ( Misc.escape(value.toString()));
            sb.append ("\")");
         }
         sb.append(")");
      }

      sb.append(")\n");
      return sb.toString();
   }

   
   public Object getAttribute(String attribute) {
      switch( Misc.whichPosition(attribute,ATTRIBUTE_TYPES) ) {
         case TYPE:
              return type;
         case SENDER:
              return sender;
         case RECEIVER:
              return getReceiver();
         case CONTENT:
              return content;
         case REPLY_WITH:
              return replyWith;
         case IN_REPLY_TO:
              return inReplyTo;
         case REPLY_BY:
              return replyBy;
         case ONTOLOGY:
              return ontology;
         case LANGUAGE:
              return language;
         case PROTOCOL:
              return protocol;
         case CONVERSATION_ID:
              return conversationId;
         case REPLY_TO: 
              return replyTo; 
         default:
              return envelope.get(attribute);
      }
   }
   public void setAttribute(String attribute, Object value) {
      switch( Misc.whichPosition(attribute,ATTRIBUTE_TYPES) ) {
         case TYPE:
              setType((String)value);
              break;
         case SENDER:
              setSender((String)value);
              break;
         case RECEIVER:
              setReceiver((String)value);
              break;
         case CONTENT:
              setContent((String)value);
              break;
         case REPLY_WITH:
              setReplyWith((String)value);
              break;
         case IN_REPLY_TO:
              setInReplyTo((String)value);
              break;
         case REPLY_BY:
              setReplyBy((String)value);
              break;
         case ONTOLOGY:
              setOntology((String)value);
              break;
         case LANGUAGE:
              setLanguage((String)value);
              break;
         case PROTOCOL:
              setProtocol((String)value);
              break;
         case CONVERSATION_ID:
              setConversationId((String)value);
              break;
         case REPLY_TO:
              setReplyTo ((String) value); 
              break;
         
         default:
              envelope.put(attribute,value);
              break;
      }
   }

    public void send (AgentContext context) { 
        TransportFactory factory = context.getTransportFactory(); 
        try {
            OutTray out = factory.getTransport (this.getReceiver()); 
          //  System.out.println("out =" + out.toString()); 
            
              context.MailBox().sendMsg(this);
              // this way causes the mailbox to update the guis
           // out.send((Performative)this); 
            }
            catch (Exception e) { 
                e.printStackTrace(); 
            }
        /*
            catch (UnsuitableMessageException ume) { 
                System.out.println("wrong type of address - can't send this performative to that address"); 
                System.out.println("Probably you are sending a Zeus performative to a FIPA agent directly "); 
                System.out.println("which won't work, instead use the ACC proxy mechanism"); 
            }
            } // watch out, nested exception about (sorry). 
            catch (TransportUnsupportedException tue) { 
                System.out.println("Address of receiver is not a known format for this agent architecture"); 
            }*/
    }


}
