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
 *java
 * THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
 */
package zeus.agents;
import zeus.actors.*;
import zeus.concepts.*;
import zeus.util.GenSym;
import java.net.*;
import java.util.*;
import JADE_SL.*;
import JADE_SL.lang.sl.*;
import JADE_SL.abs.*;
import JADE_SL.onto.basic.*;
import zeus.util.*;

/**
 * FIPA_DF_Services is used by the ACC agent to wrap the Facilitator
 * so that it can be accessed exernally.
 *
 *This class was tested against the Agentcities test suit, http://leap.crm-paris.com/agentcities/Services/Testsuite.jsp
 *
 */
public class FIPA_DF_Services extends FIPA_Services {
    
    private String NOT_AUTHORISED = "not authorised";
    private String UNEXPECTED = "zeus didn't expect that";
    private GenSym rand = new GenSym("df");
    
    // fear not, hack was just used to test the class, I left it in incase I wanted to try some more tests...
    String hack = new String(" (set (df-agent-description :name (agent-identifier :name pingagent@adastralcity.agentcities.net) :services (set (service-description :name ping :type ping_acl_alpha_v1.0 ))  :protocol FIPA-Request  :ontology Agentcities   :language Ping   :ownership \\\"Simon\\\")   ");
    
    // registered_agents is used to store all normal agents
    private Hashtable registered_agents = new Hashtable();
    
    // registered_dfs is used to store all the dfs that have registered themselves
    private Hashtable registered_dfs = new Hashtable();
    private SLCodec codec = new SLCodec();
    String ABILITY_KEY = "ACC_FIPA_DF_KEY";
    String SERVICE_KEY = "ACC_DAMLS_KEY";
    public void exec(AgentContext context) {
        this.context = context;
        this.type = new String("df");
        debug("trying registration");
        registerAlias(this.type);
        setName();
        setRegister();
        setDeregister();
        setModify();
        setSearch();
        
      /*  String[] pattern2 = { "type", "inform","in-reply-to", ABILITY_KEY };
        context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"), pattern2,this,"abilityReceived") );*/
        String[] serviceDesc = {"type","inform", "content","\\A(.*)daml(.*)\\Z","in-reply-to",SERVICE_KEY};
        context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"), serviceDesc,this,"serviceDescriptionReceived"));
    }
    
    
    
   
    
    private String getServiceName(Performative msg) {
        
        String type;
        
        String description = msg.getContent();
        String serviceDir = SystemProps.getProperty("http_root");
        
        if(description.indexOf("<service:serviceProfile ") > 0) {
            type = "Profile";
        }
        else if(description.indexOf("<service:Service") > 0 &&
        description.indexOf("<service:presents") > 0) {
            type = "Instance";
        }
        else {
            System.out.println("Unknown type of description");
            return ("unknown");
        }
        
        int nameBegin = description.indexOf("service:serviceProfile ");
        nameBegin = description.indexOf("rdf:ID", nameBegin);
        nameBegin = description.indexOf("\"", nameBegin) + 1;
        
        int nameEnd = description.indexOf("\"", nameBegin);
        
        String name = description.substring(nameBegin, nameEnd);
        return name;
        
    }
    
    /**
     *this is a message that is likely to have the name of the task in it
     */
    public void serviceDescriptionReceived(Performative perf) {
        System.out.println("GOT SERVICE DESCRIPTION!!!!");
        //    Vector List = ZeusParser.abilitySpecList(context.OntologyDb(),perf.getContent());
        String agent = perf.getSender();
        DF_Description desc = new DF_Description();
        Vector language = new Vector();
        language.addElement("FIPA-SL");
        language.addElement("Zeus");
        desc.setLanguage(language);
        Vector protocols = new Vector();
        protocols.addElement("FIPA-Inform-Protocol");
        protocols.addElement("FIPA-Request-Protocol");
        protocols.addElement("FIPA-Iterated-ContractNet-Protocol");
        desc.setProtocol(protocols);
        Vector ontos = new Vector();
        ontos.addElement("agentcities");
        desc.setOntology(ontos);
        Address addr = context.getMailBox().getAddress();
        String host = addr.getHost(); 
        desc.setName(new FIPA_AID_Address (perf.getSender(), host  ));
        FIPA_Service_Description service = new FIPA_Service_Description();
        service.setOwnership(agent);
        service.setType("Backward-Chain-Task");
        String sname = getServiceName(perf);
        System.out.println("sname = " + sname);
        if (!sname.equals("Profile")) {
            service.setName(sname);
            protocols = new Vector();
            protocols.addElement("FIPA-Iterated-ContractNet-Protocol");
            service.setProtocol(protocols);
            service.setLanguage(language);
            service.setOntology(ontos);
            Vector props = new Vector();
            String content = perf.getContent();
            //classification, domain, scope
            String classification = new String(); 
            String domain = "Leisure";
            String scope = "Local"; 
            if (sname == "BookTable") classification ="Restaurant"; 
            if (sname == "BookTicket") classification = "Entertainment"; 
            if (sname == "BookRoom") classification = "Hotel";
            service.setAgentcitiesProperties(classification,domain,scope);
            if (!inServiceList(agent+sname)) {
                System.out.println("sname " + sname +" not in service list");
                desc.addService(service);
                addServiceList(agent+sname);
                System.out.println("sname "+ sname +" added to service list");
                String key = agent+sname;
                registered_agents.put(key,desc);
            }
            else{
                System.out.println("sname " + sname + "was in service list");

            }
        }
        
    }
        Hashtable serviceList = new Hashtable();
        
        private void addServiceList(String name) {
            
            serviceList.put(name,name);
        }
        
        
        private boolean inServiceList(String name) {
            return (serviceList.containsValue(name));
        }
        
        /**
         *the problem is to get the name of the task and to register it
         *here
         *Perhaps we this should be triggered by the second message?
         */
        public void abilityReceived(Performative perf) {
            System.out.println("ADDING SERVICES");
            Vector List = ZeusParser.abilitySpecList(context.OntologyDb(),perf.getContent());
            String agent = perf.getSender();
            DF_Description desc = new DF_Description();
            Vector language = new Vector();
            language.addElement("FIPA-SL");
            language.addElement("Zeus");
            desc.setLanguage(language);
            Vector protocols = new Vector();
            protocols.addElement("FIPA-Inform-Protocol");
            protocols.addElement("FIPA-Request-Protocol");
            protocols.addElement("FIPA-Iterated-ContractNet-Protocol");
            desc.setProtocol(protocols);
            Vector ontos = new Vector();
            ontos.addElement("agentcities");
            desc.setOntology(ontos);
            
            FIPA_Service_Description service = new FIPA_Service_Description();
            service.setOwnership(agent);
            service.setType("Backward-Chain-Task");
            service.setName(rand.newId(perf.getSender()));
            protocols = new Vector();
            protocols.addElement("FIPA-Iterated-ContractNet-Protocol");
            service.setProtocol(protocols);
            service.setLanguage(language);
            service.setOntology(ontos);
            Vector props = new Vector();
            String content = perf.getContent();
            AbilitySpec spec = ZeusParser.abilitySpec(context.getOntologyDb(), content);
            props.addElement(spec.getFact().toSL());
            
            //   props.addElement(perf.getContent());
            service.setAgentcitiesProperties("none","none","none");
            desc.addService(service);
            
            String key = rand.newId();
            registered_agents.put(key,desc);
        }
        
        
        /**
         * method for handleing a registration message to the df
         */
        public void handleRegister(Performative perf) {
            try {
                // parse the content
                String content = perf.getContent();
                String agreeCont = new String(content);
                AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
                AbsAgentAction act = (AbsAgentAction) slcontent.getAbsObject("ACTION");
                // check and see if it is a registration message, if it is then agree to register,
                // otherwise send a not understood
                if (act != null) {
                    if (act.getTypeName().equalsIgnoreCase("register")) {
                        sendAgree(perf);
                    }
                    else {
                        simpleReply(perf,UNEXPECTED,"not_understood");
                    }
                }
                AbsConcept absActor = (AbsConcept) slcontent.getAbsObject("ACTOR");
                AbsAggregate agentAddresses = (AbsAggregate) absActor.getAbsObject("addresses");
                AbsPrimitive agentName = (AbsPrimitive) absActor.getAbsObject("name");
                String addr = SL_Util.makeAddressString(agentName,agentAddresses);
                boolean proceed = checkAuthority("df", agentName, agentAddresses, perf);
                if (!proceed) {
                    simpleReply(perf, NOT_AUTHORISED,"refuse");
                }
                //rand.newId("reg");
                if (agentName.getString().equals("df")) {
                    registered_dfs.put(addr, makeDescription(act));
                }
                else {
                    registered_agents.put(addr, makeDescription(act));}
                informDone(perf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
        
        
        
        public DF_Description makeDescription(AbsConcept act) {
            try {
                AbsConcept target_descr = (AbsConcept) act.getAbsObject("_SL.UNNAMED0"); // dangerous?
                AbsConcept desName = (AbsConcept) target_descr.getAbsObject("name");
                AbsPrimitive targName = (AbsPrimitive) desName.getAbsObject("name");
                AbsAggregate targAddresses =  (AbsAggregate) desName.getAbsObject("addresses");
                FIPA_AID_Address agent = new FIPA_AID_Address(SL_Util.makeAddressString(targName, targAddresses));
                AbsAggregate targLanguage = (AbsAggregate) target_descr.getAbsObject("language");
                AbsAggregate targProtocol = (AbsAggregate) target_descr.getAbsObject("protocol");
                AbsAggregate targOntology = (AbsAggregate) target_descr.getAbsObject("ontology");
                AbsAggregate targServices = (AbsAggregate) target_descr.getAbsObject("services");
                DF_Description desc = new DF_Description();
                desc.setName(agent);
                desc.setLanguage(SL_Util.makeVector(targLanguage));
                desc.setProtocol(SL_Util.makeVector(targProtocol));
                desc.setOntology(SL_Util.makeVector(targOntology));
                //may be more than one service...
                Iterator iter = (targServices).iterator();
                while (iter.hasNext()) {
                    AbsConcept current = (AbsConcept) iter.next();
                    FIPA_Service_Description service = new FIPA_Service_Description(current);
                    desc.addService(service);
                }
                return (desc);}
            catch (Exception e) {
                // probably an empty param
                return (null);
            }
            
        }
        
        
        
        public boolean checkAuthority(String agent, AbsPrimitive attempter, AbsAggregate attempterAddresses, Performative perf) {
            return true; // v. poor!
        }
        
        
        public void handleDeregister(Performative perf) {
            debug("handleDeregister in df called");
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
                        simpleReply(perf,UNEXPECTED,"not_understood");
                    }
                }
                String name = new String();
                AbsConcept absActor = (AbsConcept) slcontent.getAbsObject("ACTOR");
                AbsAggregate agentAddresses = (AbsAggregate) absActor.getAbsObject("addresses");
                AbsPrimitive agentName = (AbsPrimitive) absActor.getAbsObject("name");
                boolean proceed = checkAuthority("df", agentName, agentAddresses, perf);
                if (!proceed) {
                    simpleReply(perf, NOT_AUTHORISED,"refuse");
                }
                String searchAddr = SL_Util.makeAddressString(agentName,agentAddresses);
                Enumeration allAddresses = registered_agents.keys();
                while (allAddresses.hasMoreElements()) {
                    String current = (String) allAddresses.nextElement();
                    if (current.equals(searchAddr)) {
                        registered_agents.remove(current);
                        informDone(perf);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
        
        
        
        
        public void handleModify(Performative perf) {
            debug("handleModify in df called");
            try {
                // always banned.
                String content = perf.getContent();
                String agreeCont = new String(content);
                AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
                AbsAgentAction act = (AbsAgentAction) slcontent.getAbsObject("ACTION");
                if (act != null) {
                    if (act.getTypeName().equalsIgnoreCase("modify")) {
                        AbsConcept absActor = (AbsConcept) slcontent.getAbsObject("ACTOR");
                        AbsAggregate agentAddresses = (AbsAggregate) absActor.getAbsObject("addresses");
                        AbsPrimitive agentName = (AbsPrimitive) absActor.getAbsObject("name");
                        String searchAddr = SL_Util.makeAddressString(agentName,agentAddresses);
                        Enumeration allAddresses = registered_agents.keys();
                        while (allAddresses.hasMoreElements()) {
                            String current = (String) allAddresses.nextElement();
                            if (current.equals(searchAddr)) {
                                registered_agents.remove(current);
                                registered_agents.put(searchAddr, makeDescription(act));
                                informDone(perf);
                                return;
                            }
                        }}
                    else {
                        simpleReply(perf,UNEXPECTED,"not_understood");
                    }
                }
            } catch (Exception e ) {
                e.printStackTrace();
            }
        }
        
        
        
        
        public void handleSearch(Performative perf) {
            try {
                debug("In handle search");
                Enumeration members = registered_agents.elements();
                String content = perf.getContent();
                debug("content = " + content);
                AbsContentElement slcontent = codec.decode(SLOntology.getInstance(),content);
                AbsAgentAction act = (AbsAgentAction) slcontent.getAbsObject("ACTION");
                // should check policy
                if (act != null) {
                    System.out.println("not a null action");
                    if (act.getTypeName().equalsIgnoreCase("search")) {
                        System.out.println("action is search");
                        sendAgree(perf);
                        // agree not sent in initail version
                    }
                    else {
                        System.out.println("was not a search!");
                        simpleReply(perf,UNEXPECTED,"not_understood");
                    }
                }
                else {
                    System.out.println("was a null action!"); }
                if (!registered_dfs.isEmpty()) {
                    Enumeration allDfs = registered_dfs.keys();
                    while (allDfs.hasMoreElements()) {
                        // set thread timeout
                        // set handling rules
                        // go, go
                    }
                }
                DF_Description toMatch = makeDescription(act);
                String results = getDescriptions(toMatch);
                Performative inform = new Performative("inform");
                inform.setSender("df");
                inform.setReceiver(perf.getSender());
                inform.setLanguage("FIPA-SL0");
                inform.setProtocol("FIPA-Request");
                inform.setOntology("FIPA-Agent-Management");
                inform.setConversationId(perf.getConversationId());
                inform.setInReplyTo(perf.getReplyWith());
                String in = perf.getContent();
                String rcontent = new String("((result "+ in.substring(1,in.length()-1) + " (set " );
                rcontent += results;
                rcontent+= ")))";
                inform.setContent(rcontent);
                inform.send(context);
            }
            catch (Exception e) {
                e.printStackTrace() ;// for now
            }
        }
        
        /**
         *returns a string that contains the df_descriptions that are matches for this df_description.
         */
        public String getDescriptions(DF_Description toMatch) {
            Enumeration allDescriptions = registered_agents.elements();
            String results = new String();
            // case one : the description is constrained
            if (toMatch != null) {
                while (allDescriptions.hasMoreElements()) {
                    DF_Description desc = (DF_Description) allDescriptions.nextElement();
                    if (toMatch.match(desc)) {
                        results+=desc.toString() +" ";
                    }}
            }
            // no constraints : avoid a null pointer
            else {
                while (allDescriptions.hasMoreElements()) {
                    DF_Description desc = (DF_Description) allDescriptions.nextElement();
                    results+=desc.toString() +" ";
                }
            }
            return results;
        }
        
        
        
        
        public String agentDescToSL(Fact fact) {
            return new String();
        }
        
        
        public void informDone(Performative perf) {
            String content = perf.getContent();
            Performative inform = new Performative("inform");
            inform.setSender("df");
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
         *  simpleReply lets you reply to a message using the result convention of FIPA
         * what I mean is: in FIPA there are alot of messages like "refuse original reason"
         * or "not_understood original reason", and this method lets you send them
         * pretty simply
         **/
        public void simpleReply(Performative inPerf, String reason, String replyType) {
            Performative outPerf = new Performative(replyType);
            outPerf.setReceiver(inPerf.getSender());
            outPerf.setSender("df");
            outPerf.setLanguage("FIPA-SL0");
            outPerf.setProtocol("FIPA-Request");
            outPerf.setOntology("FIPA-Agent-Management");
            outPerf.setConversationId(inPerf.getConversationId());
            outPerf.setInReplyTo(inPerf.getReplyWith());
            String content = inPerf.getContent();
            
            // needs rewriting with SLParser
            FIPA_DF_Management_Content cont = ZeusParser.FIPA_DF_Management_Content(content);
            cont.setResult(reason);
            outPerf.setContent(cont.toString());
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
            agree.setSender("df");
            agree.send(context);
        }
        
        
        
        void debug(String str) {
            System.out.println(str);
        }
        
        
        public static void main(String param[]) {
            Performative perf = new Performative("request");
            perf.setContent( "((action      (agent-identifier        :name df@foo.com        :addresses (sequence iiop://foo.com/acc))      (search        (df-agent-description          :ontology (set meeting-scheduler)          :language (set FIPA-SL0 KIF)          :services (set  (service-description   :name profiling     :type meeting-scheduler-service)))        (search-constraints          :min-depth 2))))");
            perf.setSender("Ion");
            perf.setReceiver("me");
            FIPA_DF_Services df = new FIPA_DF_Services();
            df.handleSearch(perf);
            
        }
    }
