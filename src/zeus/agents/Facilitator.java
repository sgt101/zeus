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
 * public limited company are Copyright 1996-9. All Rights Reserved.
 *
 * THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
 */



/*
 * @(#)Facilitator.java 1.03b
 */

package zeus.agents;

import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.ontology.service.ServiceDescriptionReceiver;

import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.*;
import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.arp.lang.*;
//import com.ibm.icu.text.*;

/**
 * The implementation of the Zeus Facilitator agent. An agent society can have
 * any number of facilitators whose role is to provide an ability-identity
 * look-up service (analogous to the Yellow Pages).
 *
 * It is unlikely that users will need to change or call directly any of the
 * methods of this class.
 */

public class Facilitator extends BasicAgent implements Runnable {
    protected String   ABILITY_KEY;
    public static final String SERVICE_KEY = "ACC_DAMLS_KEY";
    protected long     timeout;
    protected boolean  query_mode = true;
    
    
    public Facilitator(String name, String file, Vector nameservers) {
        this(name,file,nameservers,-1);
    }
    
    public Facilitator(String name, String filename,
    Vector nameservers, long user_timeout) {
        super(SystemProps.getProperty("agent.names.facilitator"),
        name,nameservers);
        
        context.set(new OntologyDb(context.GenSym()));
        new OrganisationDb(context);
        
        OntologyDb db = context.OntologyDb();
        int status = db.openFile(new File(filename));
        if ( (status & OntologyDb.ERROR_MASK) != 0 ) {
            System.err.println("File I/O Error: " + db.getError());
            System.exit(0);
        }
        else if ( (status & OntologyDb.WARNING_MASK) != 0 ) {
            System.err.println("Warning: " + db.getWarning());
        }
        
        query_mode = (user_timeout != 0);
        timeout= (long)(60000*SystemProps.getDouble("facilitator.period.default"));
        if ( user_timeout > 0 )
            timeout = user_timeout;
        
        // Initialise behaviour
        Performative msg;
        Address addr;
        
        ABILITY_KEY = context.newId();
        String key = context.newId();
        String[] pattern1 = { "type", "inform", "in-reply-to", key };
        String[] pattern2 = { "type", "inform", "in-reply-to", ABILITY_KEY,
			      "content", "\\A\\(:fact(.*)\\Z"};

	String[] serviceProfile = {"type", "inform",
				   "content", "\\A.*:serviceProfile.*",
				   "in-reply-to", ABILITY_KEY};
	String[] serviceInstance = {"type", "inform",
				    "content", "\\A.*:serviceInstance.*",
				    "in-reply-to", ABILITY_KEY};
	String[] serviceRange = {"type", "inform",
				 "content", "\\A.*:serviceRange.*",
				 "in-reply-to", ABILITY_KEY};
	String[] processModel = {"type", "inform",
				 "content", "\\A.*:processModel.*",
				 "in-reply-to", ABILITY_KEY};
        
        context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
        pattern1,this,"addressReceived")
        );
        context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
        pattern2,this,"abilityReceived")
        );
        

	Object receiver = new ServiceDescriptionReceiver(context);

	context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
        serviceProfile, receiver,"serviceProfileReceived"));
	context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
        serviceInstance, receiver,"serviceInstanceReceived"));
	context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
        serviceRange, receiver,"serviceRangeReceived"));
	context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
        processModel, receiver,"processModelReceived"));

        for(int i = 0; i < nameservers.size(); i++ ) {
            addr = (Address)nameservers.elementAt(i);
            
            msg = new Performative("subscribe");
            msg.setReceiver(addr.getName());
            msg.setReplyWith(key);
            msg.setContent("log_address");
            context.MailBox().sendMsg(msg);
        }
        if ( query_mode ) new Thread(this).start();
    }
    
    /**
     * addressReceived updates the address book of the facilitator so that
     *it can send requests for service registrations to an agent
     */
    public void addressReceived(Performative msg) {
        Core.DEBUG(2, "addressReceived: " + msg);
        Vector List = ZeusParser.addressList(msg.getContent());
        context.MailBox().add(List);
        advertise(List);
        //ST 050500 1.03b -  retain immediate ability query
        // behavior until more user feedback obtained. (delete this
        // line to cause facilitator to wait until next refresh period
        if ( query_mode ) dispatchRequests(List);
        
    }
    
    /**
     *abilityReceived updates the Zeus internal database of the Facilitator for
     *services that it knows about and can respond to queries about
     */
    public void abilityReceived(Performative msg) {

        Core.DEBUG(2, "abilityReceived: " + msg);
        Vector List = ZeusParser.abilitySpecList(context.OntologyDb(),msg.getContent());
        String agent = msg.getSender();
        context.OrganisationDb().add(agent,List);
        msg.setReceiver("df");
        msg.setContent(msg.getContent());
        msg.setInReplyTo("ACC_FIPA_DF_KEY");
        msg.send(context);
    }
    
    protected void dispatchRequests(Vector v) {
        Performative msg;
        Address addr;
        String me = context.whoami();
        String agent = SystemProps.getProperty("agent.names.agent");
        for(int i = 0; i < v.size(); i++ ) {
            addr = (Address)v.elementAt(i);
            if (!(addr.getName().equals(me)) && addr.getType().equals(agent)) {
                msg = new Performative("query-ref");
                msg.setReceiver(addr.getName());
                msg.setReplyWith(ABILITY_KEY);
                msg.setContent("your_abilities");
                context.MailBox().sendMsg(msg);
            }
        }
    }
    
    protected void advertise(Vector v) {
        Performative msg;
        Address addr;
        String me = context.whoami();
        String agent = SystemProps.getProperty("agent.names.agent");
        for(int i = 0; i < v.size(); i++ ) {
            addr = (Address)v.elementAt(i);
            if (!(addr.getName().equals(me)) && addr.getType().equals(agent)) {
                msg = new Performative("inform");
                msg.setReceiver(addr.getName());
                msg.setContent("isa_facilitator " + me);
                context.MailBox().sendMsg(msg);
            }
        }
    }
    
    public void run() {
        Thread me = Thread.currentThread();
        while( true ) {
            try {
                me.sleep(timeout);
            }
            catch(InterruptedException e) {
            }
            Vector List = context.MailBox().listAddresses();
            dispatchRequests(List);
            me.yield();
        }
    }
    
    public void addAbilityMonitor(AbilityMonitor monitor, long type) {
        context.OrganisationDb().addAbilityMonitor(monitor,type);
    }
    public void addRelationMonitor(RelationMonitor monitor, long type) {
        context.OrganisationDb().addRelationMonitor(monitor,type);
    }
    public void removeAbilityMonitor(AbilityMonitor monitor, long type) {
        context.OrganisationDb().removeAbilityMonitor(monitor,type);
    }
    public void removeRelationMonitor(RelationMonitor monitor, long type) {
        context.OrganisationDb().removeRelationMonitor(monitor,type);
    }
    
    protected static void version() {
        System.err.println("Facilitator version: " +
        SystemProps.getProperty("version.id"));
        System.exit(0);
    }
    protected static void usage() {
        System.err.println("Usage: java zeus.agents.Facilitator <name> " +
        "-o <ontology_file> -s <dns_file>] " +
        "[-t <period>] [-gui ViewerProg] [-e ExternalProg] " +
        "[-debug] [-v] [-h]");
        System.exit(0);
    }
    
    public static void main(String[] arg) {
        // debug classpath problems for lamers.
        // added by simon 21/08/00
        try {
            Class c = Class.forName("java.lang.Object");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Java cannot find java.lang.Object.\n This indicates that the rt.jar file is not in your classpath.\n Ensure that $java_install_dir\\jre\\rt.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}
        try {
            // obscure zeus class picked to try it
            Class c = Class.forName("zeus.gui.help.HelpWindow");
        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Java cannot find a zeus class.\n This indicates that the zeus.jar file is not in your classpath.\n Ensure that zeus_install_dir\\lib\\zeus.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}
 /*   try {
         Class c = Class.forName("gnu.regexp.REException");
    }
    catch (ClassNotFoundException cnfe) {
       System.out.println("Java cannot find a utility object.\n This indicates that the gnu-regexp.jar file is not in your classpath.\n Ensure that $zeus_install_dir\\lib\\gnu-regexp.jar is present in the classpath and then continue");
            cnfe.printStackTrace();}*/
        Vector nameservers = null;
        String dns_file = null;
        String recycle_period = null;
        long timeout = 0;
        String gui = null;
        String external = null;
        String ontology = null;
        
        if ( arg.length < 5 )  usage();
        else
            for( int i = 1; i < arg.length; i++ ) {
                if ( arg[i].equals("-s") && ++i < arg.length )
                    dns_file = arg[i];
                else if ( arg[i].equals("-t") && ++i < arg.length )
                    recycle_period = arg[i];
                else if ( arg[i].equals("-o") && ++i < arg.length )
                    ontology = arg[i];
                else if ( arg[i].equals("-gui") && ++i < arg.length )
                    gui = arg[i];
                else if ( arg[i].equals("-e") && ++i < arg.length )
                    external = arg[i];
                else if ( arg[i].equals("-debug") ) {
                    Core.debug = true;
                    Core.setDebuggerOutputFile(arg[0] + ".log");
                }
                else if ( arg[i].equals("-h") )
                    usage();
                else if ( arg[i].equals("-v") )
                    version();
                else
                    usage();
            }
        
        if ( ontology == null ) {
            System.err.println("Ontology Database file must be specified with -o option");
            usage();
        }
        if ( dns_file == null ) {
            System.err.println("Domain nameserver file must be specified with -s option");
            usage();
        }
        
        try {
            nameservers = ZeusParser.addressList(new FileInputStream(dns_file));
            if ( nameservers == null || nameservers.isEmpty() )
                throw new IOException();
            
            if ( recycle_period != null ) {
                double d = (Double.valueOf(recycle_period)).doubleValue();
                timeout = (long)(d*60000);
            }
            
            Facilitator f = new Facilitator(arg[0],ontology,nameservers,timeout);
            
            Class c;
            AgentContext context = f.getAgentContext();
            
            if ( gui != null ) {
                c = Class.forName(gui);
                BasicAgentUI ui = (BasicAgentUI) c.newInstance();
                context.set(ui);
                ui.set(context);
            }
            
            if ( external != null ) {
                c = Class.forName(external);
                ZeusExternal user_prog = (ZeusExternal)c.newInstance();
                context.set(user_prog);
                user_prog.exec(context);
            }
            
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
