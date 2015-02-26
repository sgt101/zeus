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



package zeus.concepts;

import java.io.*;
import java.util.*;
import zeus.util.*;

/**
 * FIPAPerformative extends the performative class so that when getReceivers() is
 * called it converts the values in the vector into strings from FIPA_AID_Addresses.
 * This is so that we can store the values and use them to contact FIPA agents,
 * but for the Zeus internal agency we can use Zeus addressing. <p>
 * Also there is a method <i> String getFIPASender () </i> which is used to return
 * an FIPA_AID_Address.
 * <p>
 * @author Simon Thompson
 * @since 1.1
 */

public class FIPAPerformative extends Performative {
    
    FIPA_AID_Address sender = null; //overloaded
    /**
     * received is used to store any "received" elements: these are
     * used to hold a list of agents that have previously had this message
     */
    Vector received = new Vector();
    
    
    public FIPAPerformative(String type ) {
        this.type = type;
    }
    
    
    public FIPAPerformative(Performative perf) {
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
        //if ( (table = perf.getEnvelope()) != null )	setEnvelope(table);
        // if ( (token = perf.getReplyTo()) != null ) setReplyTo(token);
        
    }
    
    
    /**
     * return the address of the receivers as FIPA_AID_Addresses
     */
    public Enumeration getFIPAReceivers() {
        return receivers.elements();
    }
    
    
    /**
     * getReceivers overwrites the getReceivers in Performative to ensure that
     * the correct result is produced when this performative is handled by Zeus
     * internal communications. The receivers are set to string equivalents....
     */
    public Enumeration getReceivers()	     {
        Enumeration allReceivers = receivers.elements();
        Vector tempStore = new Vector();
        String res = null;
        Address addr = null;
        
        while (allReceivers.hasMoreElements()) {
            addr = (Address) allReceivers.nextElement();
            res = addr.getName(); // which should produce a zeus address
            tempStore.addElement(res);
            
        }// end while
        return (tempStore.elements());
    }// end method
    
    
    
    /**
     * produce a performative that can be handled by native zeus parsers
     */
    public Performative performative() {
        return new Performative(this);
    }
    
    
    /**
     * getFIPASender is a bit of a hack, it produces the FIPA_AID_Address in string format which is the sender
     * field  */
    public String getSender_As_FIPA_String() {
        
        return this.sender.toFIPAString();
    }
    
    
    public FIPA_AID_Address getSender_As_FIPA_AID() {
        return this.sender;
    }
    
    
    /**
     * use this method to add any "received's" (stamps that agents that
     * have had the message have put on it) that this
     * message should have
     */
    public void setReceived(FIPA_Received received) {
        this.received.addElement(received);
    }
    
    
    /**
     * get the receiveds as XML: possible compliance issues, what to do
     * with more than one received? My answer here is to build something that
     * replicates the example given by FIPA for one, and to get it to produce
     * a plausable output in the case where there are many
     *
     */
    public String receivedToXML() {
        if (received.isEmpty()) {
            FIPA_Received frec = new FIPA_Received();
            Enumeration allRec = getFIPAReceivers();
            FIPA_AID_Address addr = (FIPA_AID_Address) allRec.nextElement();
            String rec = (String) addr.getAddresses().firstElement();
            frec.setReceivedBy(rec);
            frec.setReceivedDate(FIPA_Date.getDate());
            return frec.toXML(); }
        else {
            String retVal = new String();
            Enumeration allRecs = received.elements();
            while (allRecs.hasMoreElements()) {
                retVal+= ((FIPA_Received)allRecs.nextElement()).toXML();
            }
            
            return retVal;
        }
    }
    
    public void appendContent(String cont) {
        if (this.content == null) 
            this.content = new String(); 
        if (this.content.equals ("null")) { 
            this.content = new String(); 
        }
        if (cont != null && !cont.equals("null")){ 
        this.content += cont;}
    }
    
    
    
    
    
    /**
     * overloaded
     */
    public void setSender(FIPA_AID_Address sender) {
        this.sender = sender;
    }
    
    
    
    /**
     * content brackets may be SL specific
     */
    public String toFIPAString() {
        String str = "(" + type.toUpperCase() + "\n";
        if ( sender != null )
            str += " :sender ( " +  getSender_As_FIPA_String() + " )\n";
        if ( receivers != null && !receivers.isEmpty() ) {
            str += " :receiver (set ";
            Enumeration allRec = getFIPAReceivers();
            while (allRec.hasMoreElements()) {
                FIPA_AID_Address addr = (FIPA_AID_Address) allRec.nextElement();
                String current ="(" + addr.toFIPAString() +")";
                str += current; }
            str += " )\n";
        }
        if ( replyWith != null )
            str += " :reply-with " + replyWith + "\n";
        if ( inReplyTo != null )
            str += " :in-reply-to " + inReplyTo + "\n";
        if ( replyBy != null )
            str += " :reply-by " + replyBy + "\n";
        if ( ontology != null )
            str += " :ontology " + ontology + "\n";
        if ( language != null )
            str += " :language " + language + "\n";
        if ( content != null )
            // try no "'s // brackets may be SL specific
            str += " :content \"" + content  + "\"\n";//" :content \"( "  +Misc.escape(content)  + ")\"\n";
        if ( protocol != null )
            str += " :protocol " + protocol + "\n";
        if ( conversationId != null )
            str += " :conversation-id " + conversationId + "\n";
        if ( replyTo != null )
            str += " :reply-to " + replyTo + "\n";
    /*
        if ( envelope != null && !envelope.isEmpty() ) {
            str += " :envelope (";
            Enumeration enum = envelope.keys();
            String key;
            Object value;
            while( enum.hasMoreElements() ) {
                key = (String)enum.nextElement();
                value = envelope.get(key);
                str += "(" + key + " \"" + Misc.escape(value.toString()) + "\")";
            }*/
        //   str += ")";
        //}
        
        str += ")\n";
        return str;
    }
    
    
    /**
     * receivers are returned as FIPA.AgentID[] so that they can be packed into
     * a message envelope by the FIPA_99_Transport
     */
    public FIPA.AgentID[] getReceiversAgentID() {
        int numberReceivers = receivers.size();
        FIPA.AgentID allReceivers[] = new FIPA.AgentID[numberReceivers];
        Enumeration recs = receivers.elements();
        int count = 0;
        while (recs.hasMoreElements()) {
            allReceivers[count] = ((FIPA_AID_Address) recs.nextElement()).getAgentID();
            count++;
        }
        return (allReceivers);
    }
    
    
    /**
     * sender is returned in a FIPA.AgentID[].
     * In Zeus there is but one sender, whereas the FIPA Spec and the JAS spec
     * seem to think that there can be multiple senders.. I don't know how this
     * can be true, and frankly, it frightens me a bit
     */
    public FIPA.AgentID[] getSenderAgentID() {
        FIPA.AgentID allSenders[] = new FIPA.AgentID[1];
        allSenders[0] = sender.getAgentID();
        return (allSenders);
    }
    
    
    /**
     * encrypted field is added to allow us to add sACL features later
     */
    protected String encrypted = "NO";
    
    
    /**
     * return a String which describes the encryption scheme that is being used
     */
    public String getEncryptionScheme() {
        return encrypted;
    }
    
    
    /**
     * set a string that will be used to flag the encryption mechanism we
     * are using to other agents
     */
    public void setEncryptionDescriptor(String descriptor) {
        encrypted = descriptor;
    }
    
    
    public FIPA_AID_Address getFIPAReceiver() {
        Enumeration allRecs = receivers.elements();
        return (FIPA_AID_Address) allRecs.nextElement();
    }
    
    
    /**
     * Build a javax.agent.Envelope from a fipa address and a string
     */
    public javax.agent.Envelope jasEnvelope(FIPA_AID_Address addr, String thisTarget ) {
        /** System.out.println("target host is : " + addr.toString());
         * javax.agent.Name recName = new javax.agent.Name (addr.getName(),addr.getHost());
         *
         * javax.agent.Name sendName = new javax.agent.Name (this.sender.getName(), this.sender.getHost()); */
        javax.agent.Identifier ireceiver = null;  //new javax.agent.Identifier(recName,thisTarget);
        javax.agent.Identifier isender = null; //new javax.agent.Identifier (sendName, this.sender.getHost());
        javax.agent.Envelope env = new javax.agent.Envelope(ireceiver, isender, this);
        return env;
    }
    
    
    /**
     * spits out the sender as XML
     */
    public String getSenderXML() {
        return sender.toXML();
    }
    
    
    /**
     * spits out all receivers as XML strings
     */
    public String getReceiversXML() {
        Enumeration allReceivers = receivers.elements();
        String retVal = new String();
        while (allReceivers.hasMoreElements()) {
            FIPA_AID_Address currentAddr = (FIPA_AID_Address) allReceivers.nextElement();
            retVal += currentAddr.toXML();
        }
        return retVal;
    }
    
    
    /**
     * returns appropriate encryption descriptor wrapped in XML tags as per
     * XC000084
     */
    public String get_is_EncryptedXML() {
        String retVal = new String("<encrypted>");
        if (getEncryptionScheme().equals("NO")) {
            retVal += "no encryption"; }
        else {
            retVal += getEncryptionScheme(); }
        retVal += "</encrypted>";
        return retVal;
    }// end method
    
    
    
    public FIPA.FipaMessage FipaMessage() {
        FIPA.Envelope fenv = new FIPA.Envelope();
        
        try {
            fenv.to = getReceiversAgentID();
            
        } catch (NullPointerException npe) { ;}
        try {
            fenv.from = getSenderAgentID();
        } catch (NullPointerException npe) {
            fenv.from = new FIPA.AgentID[0];}
        //("null",new String[0], new FIPA.AgentID[0], new FIPA.Property[0]);}
        try {
            fenv.comments = new String("Zeus Agent Building Environment v1.1");
        } catch (NullPointerException npe) { ;}
        
        fenv.payloadLength = -1; // indicates that it is up to the ACC to work this out.
        
        fenv.payloadEncoding = new String("String");
        
        fenv.aclRepresentation = new String("fipa-string-std");
        
        fenv.date = new FIPA.DateTime[0];
        
        fenv.encrypted = new String [1];
        
        fenv.encrypted[0] = getEncryptionScheme();
        
        fenv.intendedReceiver = getReceiversAgentID();
        
        //  fenv.intendedReceiver[0].getAddress
        
        fenv.received = new FIPA.ReceivedObject[0];
        
        fenv.transportBehaviour = new FIPA.Property[0][0];
        
        fenv.userDefinedProperties = new FIPA.Property[0];
        
        FIPA.Envelope envelopes[]  = {fenv};
        
        
        FIPA.FipaMessage message = new FIPA.FipaMessage(envelopes,toFIPAString().getBytes());
        return message;
    }
    
}
// end class
