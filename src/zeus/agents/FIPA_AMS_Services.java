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
package zeus.agents;
import zeus.actors.*;
import zeus.concepts.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.net.*;
import JADE_SL.*;
import JADE_SL.lang.sl.*;
import JADE_SL.abs.*;
import zeus.util.SystemProps;
import zeus.actors.intrays.*;


/**
 * FIPA_AMS_Services is used by the ACC agent to wrap the Nameserver
 * so that it can be accessed exernally.
 *
 * Change Log
 * ----------
 * 01/03/02 - using JADE_SL parser.
 */
public class FIPA_AMS_Services extends FIPA_Services implements AddressListener{
    
    private Hashtable ams_info = new Hashtable();
    private SLCodec codec = new SLCodec();
    
    public void exec(AgentContext context) {
        this.type = new String("ams");
        
        this.context = context;
     //  System.out.println("trying registration");
        
        registerAlias(this.type);
        AddressBook book = context.getAddressBook(); 
        book.addAddressListener(this); 
        setName();
        setRegister();
        setDeregister();
        setModify();
        setSearch();
        setGet_description();
     
    }
    
    
    
    
    public void setGet_description() {
        MsgHandler handler = context.getMsgHandler();
        String msg_pattern[] = {"receiver",type,"content","\\A(.*)get-description(.*)\\Z"};
        handler.addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern, this, "handleGet_description"));
    }
    
    
    
    /**
     * not the best thing to do...
     */
    private String stripLeadingTrailing(String str) {
        String retVal = str.substring(1,str.length()-1);
        System.err.println("retVal is : " +retVal);
        return retVal;
    }
    
    
    /**
     * this ap-description is hard coded to use the ports in Zeus_ACC_Server
     */
    public void handleGet_description(Performative perf) {
      //  System.out.println("handleGet_description in ams called");
        try {
            String content = perf.getContent();
            Performative agree = new Performative("agree");
            agree.setSender(perf.getReceiver());
            agree.setReceiver(perf.getSender());
            FIPA_AMS_Management_Content agreeCont = ZeusParser.FIPA_AMS_Management_Content(content);
            AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
            //System.out.println("AMS:parsed content fine");
            slcontent.dump();
            
            //    sendAgree(perf);
            
            InetAddress ip = InetAddress.getLocalHost();
            Performative reply = new Performative("inform");
            reply.setSender("ams");
            reply.setReceiver(perf.getSender());
            FIPA_AP_Description desc = new FIPA_AP_Description();
            desc.setDynamic(false);
            desc.setMobility(false);
            desc.setName(SystemProps.getProperty("HAP.address"));
            FIPA_Transport_Profile trans = new FIPA_Transport_Profile();
            FIPA_MTP_Description mtp = new FIPA_MTP_Description();
            mtp.setMTPName("fipa.mts.mtp.iiop.std");
            TransportConfig iiop = SystemProps.getTransport ("FIPA_IIOP_2000"); 
            mtp.addAddress("iiop://" + ip.getHostAddress() +":" + iiop.getPort() +"/acc");
            trans.addMTPDescription(mtp);
            FIPA_MTP_Description mtphttp = new FIPA_MTP_Description();
            mtphttp.setMTPName("fipa.mts.mtp.http.std");
            TransportConfig http = SystemProps.getTransport ("FIPA_HTTP_2000"); 
                  mtphttp.addAddress("http://" + ip.getHostAddress() +":" + http.getPort() +"/acc");
            trans.addMTPDescription(mtphttp);
            desc.setTransportProfile(trans);
            reply.setContent("(result " +stripLeadingTrailing(perf.getContent()) + "(set " + desc.toString() +"))");
            reply.setLanguage("FIPA-SL0");
            reply.setProtocol("FIPA-Request");
            reply.setOntology("FIPA-Agent-Management");
            reply.setConversationId(perf.getConversationId());
            reply.setInReplyTo(perf.getReplyWith());
            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            //System.out.println(reply.toString());
            //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            
            reply.send(context); }
            catch (Exception e) {
              //  e.printStackTrace();
                notUnderstood(perf,"failed");
            }
            
    }
    
    public void informDone(Performative perf) {
        String content = perf.getContent();
        Performative inform = new Performative("inform");
        inform.setSender("ams");
        inform.setReceiver(perf.getSender());
        inform.setLanguage("FIPA-SL0");
        inform.setProtocol("FIPA-Request");
        inform.setOntology("FIPA-Agent-Management");
        inform.setConversationId(perf.getConversationId());
        inform.setInReplyTo(perf.getReplyWith());
        inform.setContent("(done" + content + ")");
        send(inform);
    }
    
    
    /**
     *make an "agree" response in SL
     **/
    public void sendAgree(Performative perf) {
        Performative agree = new Performative("agree");
        agree.setContent( perf.getContent().substring(0,perf.getContent().length() -1)+"true)");
        agree.setLanguage("FIPA-SL0");
        agree.setProtocol("FIPA-Request");
        agree.setOntology("FIPA-Agent-Management");
        agree.setConversationId(perf.getConversationId());
        agree.setInReplyTo(perf.getReplyWith());
        agree.setReceiver(perf.getSender());
        agree.setSender("ams");
        agree.send(context);
    }
    
    /**
     * method for handleing a registration message to the ams
     */
    public void handleRegister(Performative perf) {
        try {
            //System.out.println("new handleRegister in ams called");
            String content = perf.getContent();
            AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
            //System.out.println("AMS:parsed content fine");
            slcontent.dump();
            AbsAgentAction act = (AbsAgentAction) slcontent.getAbsObject("ACTION");
            sendAgree(perf);
            AbsConcept absActor = (AbsConcept) slcontent.getAbsObject("ACTOR");
            AbsAggregate agentAddresses = (AbsAggregate) absActor.getAbsObject("addresses");
            AbsPrimitive agentName = (AbsPrimitive) absActor.getAbsObject("name");
            String addr = SL_Util.makeAddressString(agentName,agentAddresses);
            ams_info.put(addr, makeDescription(act));
            informDone(perf);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public AMS_Description makeDescription(AbsConcept act) {
        try {
            AbsConcept target_descr = (AbsConcept) act.getAbsObject("_SL.UNNAMED0"); // dangerous?
            AbsConcept desName = (AbsConcept) target_descr.getAbsObject("name");
            AbsPrimitive targName = (AbsPrimitive) desName.getAbsObject("name");
            AbsAggregate targAddresses =  (AbsAggregate) desName.getAbsObject("addresses");
            FIPA_AID_Address agent = new FIPA_AID_Address(SL_Util.makeAddressString(targName, targAddresses));
            AbsPrimitive targOwnership = (AbsPrimitive) target_descr.getAbsObject("ownership");
            AbsPrimitive targState = (AbsPrimitive) target_descr.getAbsObject("state");
            AMS_Description desc = new AMS_Description();
            desc.setName(agent);
            if (targOwnership != null) {
            desc.setOwnership(targOwnership.getString());}
            if (targState != null) { 
            desc.setState(targState.getString()); }
            return (desc);      
        }
        catch (Exception e) {
            // probably an empty param
          //  e.printStackTrace(); // surpress error!
            return (null);
        }
        
    }
    
    
    public void  handleDeregister(Performative perf) {
        //System.out.println("handleDeregister in ams called");
        try {
            // parse the content
            String content = perf.getContent();
            String agreeCont = new String(content);
            AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
            AbsAgentAction act = (AbsAgentAction) slcontent.getAbsObject("ACTION");
            // check and see if it is a deregistration message, if it is then agree to register,
            // otherwise send a not understood
            if (act != null) {
                if (act.getTypeName().equalsIgnoreCase("deregister")) {
                    sendAgree(perf);
                }
                else {
                    ;//       simpleReply(perf,UNEXPECTED,"not_understood");
                }
            }
            OntologyDb ont = context.getOntologyDb();
            ResourceDb res = context.getResourceDb();
            Fact agentDesc = ont.getFact(true,"agent");
            String name = new String();
            AbsConcept absActor = (AbsConcept) slcontent.getAbsObject("ACTOR");
            AbsAggregate agentAddresses = (AbsAggregate) absActor.getAbsObject("addresses");
            AbsPrimitive agentName = (AbsPrimitive) absActor.getAbsObject("name");
            String searchAddr = SL_Util.makeAddressString(agentName,agentAddresses);
            Enumeration allAddresses = ams_info.keys();
            while (allAddresses.hasMoreElements()) {
                String current = (String) allAddresses.nextElement();
                if (current.equals(searchAddr)) {
                    ams_info.remove(current);
                    informDone(perf);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }}
        
        
        
        
        
        public void handleModify(Performative perf) {
            //System.out.println("handleModify in ams called");
            try {
                Enumeration members = ams_info.elements();
                String content = perf.getContent();
                FIPA_AMS_Management_Content cont = ZeusParser.FIPA_AMS_Management_Content(content);
                AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
                //System.out.println("AMS:parsed content fine");
                slcontent.dump();
                AMS_Description desc = cont.getDescription();
                Performative agree = new Performative("agree");
                FIPA_AMS_Management_Content agreeCont = ZeusParser.FIPA_AMS_Management_Content(content);
                agreeCont.setResult(true);
                agree.setSender(perf.getReceiver());
                agree.setReceiver(perf.getSender());
                agree.setLanguage("FIPA-SL0");
                agree.setProtocol("FIPA-Request");
                agree.setOntology("FIPA-Agent-Management");
                agree.setContent(agreeCont.toString());
                agree.setConversationId(perf.getConversationId());
                agree.setInReplyTo(perf.getReplyWith());
                agree.send(context);
                Vector matches = new Vector();
                while (members.hasMoreElements()) {
                    debug("attempting a match");
                    AMS_Description member = (AMS_Description) members.nextElement();
                    debug("calling match");
                    boolean matched = desc.match(member);
                    debug("matched = " + String.valueOf(matched));
                    if (matched)
                        matches.addElement(member);
                }
                String retVal = new String("(set ");
                Enumeration allMatch = matches.elements();
                boolean notEmpty = false;
                while (allMatch.hasMoreElements()) {
                    notEmpty = true;
                    retVal += ((AMS_Description) allMatch.nextElement()).toString() +" ";
                }
                retVal += " )";
                Performative inform = new Performative("inform");
                cont.setAction("search");
                if (notEmpty) cont.setResult(retVal);
                inform.setSender(perf.getReceiver());
                inform.setReceiver(perf.getSender());
                inform.setLanguage("FIPA-SL0");
                inform.setProtocol("FIPA-Request");
                inform.setOntology("FIPA-Agent-Management");
                inform.setContent(cont.toString());
                inform.setConversationId(perf.getConversationId());
                inform.setInReplyTo(perf.getReplyWith());
                inform.send(context);
            } catch (Exception e) {
                notUnderstood(perf,"failed");
                e.printStackTrace(); }
                notUnderstood(perf,"(unsupported-act " + perf.getType() +")");
                
        }
        
        
        
        public void handleSearch(Performative perf) {
            try {
                Enumeration members = ams_info.elements();
                String in = perf.getContent();
                AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),in);
                //System.out.println("AMS:parsed content fine");
                slcontent.dump();
                AbsAgentAction act = (AbsAgentAction) slcontent.getAbsObject("ACTION");
                AMS_Description desc = makeDescription(act);
                Performative agree = new Performative("agree");
                sendAgree(perf);
                Vector matches = new Vector();
               
                while (members.hasMoreElements()) {
                    debug("attempting a match");
                    AMS_Description member = (AMS_Description) members.nextElement();
                    debug("calling match");
                    if (desc!=null) { 
                    boolean matched = desc.match(member);
                    debug("matched = " + String.valueOf(matched));
                    if (matched)
                        matches.addElement(member);
                    }
                    else matches.addElement(member); 
                }
                Enumeration allMatch = matches.elements();
                boolean notEmpty = false;
                String retVal = new String();
                while (allMatch.hasMoreElements()) {
                    notEmpty = true;
                    retVal += ((AMS_Description) allMatch.nextElement()).toString() +" ";
                }
                Performative inform = new Performative("inform");
                inform.setSender(perf.getReceiver());
                inform.setReceiver(perf.getSender());
                inform.setLanguage("FIPA-SL0");
                inform.setProtocol("FIPA-Request");
                inform.setOntology("FIPA-Agent-Management");
                String rcontent = new String("((result "+ in.substring(1,in.length()-1) + " (set " );
                rcontent += retVal;
                rcontent+= ")))";
                inform.setContent(rcontent);
                inform.setConversationId(perf.getConversationId());
                inform.setInReplyTo(perf.getReplyWith());
                inform.send(context);
            } catch (Exception e) {
                notUnderstood(perf,"failed");
                e.printStackTrace(); }
                
        }
        
        
        
        public void notUnderstood(Performative inPerf, String reason) {
            Performative outPerf = new Performative("not-understood");
            outPerf.setReceiver(inPerf.getSender());
            outPerf.setSender(inPerf.getReceiver());
            outPerf.setLanguage("FIPA-SL0");
            outPerf.setProtocol("FIPA-Request");
            outPerf.setOntology("FIPA-Agent-Management");
            String content = inPerf.getContent();
            FIPA_AMS_Management_Content cont = ZeusParser.FIPA_AMS_Management_Content(content);
            cont.setResult(reason);
            outPerf.setContent(cont.toString());
            
            
        }
        
        
        void debug(String str) {
            //System.out.println("FIPA_AMS_Services : "+ str);
            ;
        }
        
        public static void main(String argv[]) {
            
            FIPA_AMS_Services serv = new FIPA_AMS_Services();
            serv.debug(serv.stripLeadingTrailing(argv[0]));
        }
        
        public void newAddress(AddressEvent addr) {
            Address currentAddr = addr.getAddress();
            String nameStr = new String();
            InTray serv = context.getMailBox().getInTray(); 
            try { 
                Zeus_ACC_Server server = (Zeus_ACC_Server) serv; 
            if (currentAddr instanceof zeus.concepts.ZeusAddress) {
                String name = currentAddr.getName() +"@"+ zeus.util.SystemProps.getProperty("HAP.address");
                nameStr = new String("(agent-identifier :name ");
                nameStr += name.toString() +" :addresses (sequence ";
                nameStr += server.getResponseAddress(); 
                nameStr +="))";
                // handle nothing else!
                FIPA_AID_Address fAddr = new FIPA_AID_Address(nameStr);
                AMS_Description desc = new AMS_Description();
                desc.setName(fAddr);
                desc.setOwnership("ams@" + SystemProps.getProperty("HAP.address"));
                desc.setState("active");
               // System.out.println("nameStr = " + nameStr); 
                //System.out.println("desc = " + desc.toString()); 
                ams_info.put(nameStr, desc);
            }
            else
                return;
            }catch (Exception e) {
                //e.printStackTrace(); 
                ;
            }
            
        }
        
        public void deleteAddress(AddressEvent addr) {
            
            ;
        }
        
        public void replaceAddress(AddressEvent addr) {
            ;
        }
        
}
