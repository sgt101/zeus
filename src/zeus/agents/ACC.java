/*
 * The contents of this file are subject to the BT "ZEUS" Open Source
 * Licence (L77741), Version 1.0 (the "Licence"); you may not use this file
 * except in compliance with the Licence. You may obtain a copy of the Licence
 * from $ZEUS_INSTALL/licence.html or alternatively from
 * http://www.labs.bt.com/projects/agents/zeus/licence.htm
 *
 * Except as stated in Clause 7 of the Licence, software distributed und the
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

package zeus.agents;

import zeus.actors.outtrays.*;
import zeus.actors.factories.*;
import zeus.actors.intrays.*;
import java.util.*;
import java.io.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.agents.*;

import zeus.actors.rtn.Engine;
import zeus.rete.ReteEngine;

import javax.xml.bind.*;
import javax.xml.marshal.*;
import zeus.concepts.xmlobject.acc.Contacts;
import zeus.concepts.xmlobject.acc.Contact;


/**
 * ACC is a Zeus agent that has been set up to act as a FIPA ACC.
 * This means that the ACC agent is the point of contact between agents in
 * a Zeus naming domain and agents in a FIPA naming domain
 */
public class ACC implements ZeusExternal {
    
    protected static void version() {
        System.err.println("ZeusAgent - ACC version: 1.2");
        // System.exit(0);
    }
    
    
    protected static void usage() {
        System.err.println("Usage: java ACC -s <dns_file> [-gui ViewerProg] ] [-debug] [-fipaNames fipNameFileName] [-transports transportConfigFile");
        System.exit(0);
    }
    
    
    public static void main(String[] arg) {
        String name        = "ACC";
        Vector cmdLineFact = new Vector(10);
        
        BasicAgent agent;
        String external = null;
        String dns_file = null;
        String resource = null;
        String gui = null;
        String ontology_file = null;
        Vector nameservers = null;
        Bindings b;
        FileInputStream stream = null;
        ZeusExternal user_prog = null;
        String fipaNames = "contacts.xml";
        String transports = null;
        String filename = null;
        
        
        for( int j = 0; j < arg.length; j++ ) {
            if ( arg[j].equals("-s") &&  ++j < arg.length )
                dns_file = arg[j];
            if (arg[j].equals("-gui") && ++j < arg.length)
                gui = arg[j];
            if (arg[j].equals("-fipaNames") && ++j < arg.length) {
                fipaNames = arg[j];
            }
            if (arg[j].equals("-transports") && ++j < arg.length)
                transports = arg[j];
            if (arg[j].equals("-o") && ++j <arg.length)
                filename = arg[j];
            else if ( arg[j].equals("-debug") ) {
                Core.debug = true;
                Core.setDebuggerOutputFile("ACC.log");
            }
            
        }
        
        b = new Bindings(name);
        
        
        if ( dns_file == null ) {
            System.err.println("Domain nameserver file must be specified with -s option");
            usage();
        }
        
        try {
            nameservers = ZeusParser.addressList(new FileInputStream(dns_file));
            System.out.println ("namerservers = " + nameservers); 
            if ( nameservers == null || nameservers.isEmpty() )
                throw new IOException();
            
            ACC acc = new ACC();
            if (transports != null) {
                acc.initialiseTransports(transports);}
            
            agent = new ACCAgent(name,name,nameservers);
            
            AgentContext context = agent.getAgentContext();
            // context.set(new Clock(0,0));
            OntologyDb db = new OntologyDb(context.GenSym());
            context.set(db);
            int status = 0;
            if (filename == null) {
                status = db.openFile(new File("ACC.ont"));}
            else
                status = db.openFile(new File(filename));
            new OrganisationDb(context);
            new ResourceDb(context);
            new Engine(context);
            new TaskDb(context);
            // 4 && 40 are arbitary
            new Planner(context,4,40);
            new ExecutionMonitor(context);
            
            
            new ProtocolDb(context);
            //new MsgHandler(context);
            
/*
         Initialising Extensions
 */
            Class c;
            
            if ( resource != null ) {
                c = Class.forName(resource);
                ExternalDb oracle = (ExternalDb) c.newInstance();
                context.set(oracle);
                oracle.set(context);
            }
            if ( gui != null ) {
                c = Class.forName(gui);
                ZeusAgentUI ui = (ZeusAgentUI)c.newInstance();
                context.set(ui);
                ui.set(context);
            }
            
/*
         Initialising ProtocolDb
 */
            ProtocolInfo info;
/*
         Initialising OrganisationalDb
 */
            AbilityDbItem item;
            
            
            // ACC has no external program
            // instead call the functions in this class
            
            
            context.set(acc);
            acc.exec(context);
            
            
            
            if (fipaNames != null ) {
                System.out.println("attempting to register names");  
                acc.fipaNames = fipaNames;
                acc.loadFIPAAliases(); 
            }
            
            
            // ACC is used as a wrapper for df - could implement a separate DF agent?
            FIPA_DF_Services fdf = new FIPA_DF_Services();
            System.out.println("calling fdf");
            fdf.exec(context);
            
            // ACC is used as a wrapper for ams - could implement a AMS agent?
            FIPA_AMS_Services fams = new FIPA_AMS_Services();
            System.out.println("calling fams");
            fams.exec(context);
        }
        catch (Exception e) {
            e.printStackTrace(); }
    }
    
    
    String fipaNames = null;  // Name of XML file - set as parameter to main method
    AgentContext context = null;
    MsgHandler msg = null;
    
    public void exec(AgentContext context) {
        this.context = context;
        //   FIPA_MailBox mbox = new FIPA_MailBox(context);
        
        // context.set(mbox);
        msg = context.MsgHandler();
        setMessageRules();
        
    }
    
    
    public void setMessageRules() {
        String FIPA_forward_request[] = {"type", "request", "content",  "\\A(action acc(\\s*)(forward (\\s*)(.*)))\\Z"};
        msg.addRule(new MessageRuleImpl(context.newId("Rule"), FIPA_forward_request, this, "forward_message"));
        String FIPA_inform_new_contact[] = {"type", "inform", "content", "\\A(<contact\\s)(.*)(/>)\\Z"};
        msg.addRule(new MessageRuleImpl(context.newId("Rule"), FIPA_inform_new_contact, this, "add_new_contact"));
    }
    
    
    public void forward_message(Performative perf) {
        System.out.println(perf.toString());
    }
    
    public File contactsDb = null;
    public Contacts contactsRoot = new Contacts();
    
    /*
     * Loads the FIPA aliases from file into ACC's address book.
     * Converts the XML document into Java objects, all accessible
     * via the "root" object of type zeus.concepts.xmlobject.acc.Contacts
     */
    protected synchronized void loadFIPAAliases() {
        List allContacts = null;
        try {
            contactsDb = new File (fipaNames);            
            // Build the Java objects from the file
            buildTree();       
            // Get java.util.List of all Contact objects
            allContacts = contactsRoot.getList();                  
        }
        catch (Exception e) {
            e.printStackTrace(); 
        }

        if (allContacts != null) {

            Zeus_ACC_MailBox mbox = (Zeus_ACC_MailBox) context.getMailBox();
            Zeus_ACC_Server server = mbox.getZeus_ACC_Server();            

            // Iterate through the List set each FIPA alias
            for (ListIterator i = allContacts.listIterator(); i.hasNext();  ) {
                Contact contact = (Contact)i.next();
                System.out.println(contact.toString());
                String ZEUSName = contact.getZEUSName();  
                FIPA_AID_Address FIPAAddress = new FIPA_AID_Address(contact.getFIPAAddress());
                System.out.println("ZEUS Name = " + ZEUSName + ",  FIPA Address = " + FIPAAddress.toString());
               /* try {
                    Thread.sleep(500);
                    System.out.println("Sleeping...");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }*/
                server.setFIPAAlias(ZEUSName,FIPAAddress);
            }
        }
        else {
            System.out.println("Failed to load FIPA aliases into address book");
        }
    }
    
    /*
     * Save all known FIPA Aliases to the file
     * The root element Java object and the File object are globals and so no
     * parameters are passes into this method
     */
    protected synchronized void saveFIPAAliases() {
        try {
            // Before doing unmarshalling to XML, must validate the data.
            contactsRoot.validate();        
            // Marshall to XML
            marshalTree(); 
            System.out.println("FIPA Aliases file saved successfully!");
        }
        catch (Exception e) {
            System.out.println("Error saving FIPA Aliases to file");
            e.printStackTrace();
        }
    }
    
    
    public void add_new_contact(Performative perf) {
        // Initialise the reply message - the 'type' and 'content' of message will 
        // depend on the success of the unmarshal operation below
        Performative p = new Performative();
        p.setInReplyTo(perf.getReplyWith());
        p.setReceiver(perf.getSender());
        
        Contact newContact = new Contact();
        String msgContent = perf.getContent();
        System.out.println("Msg content: " + msgContent);
        try {
            // Convert the XML msg content into its corresponding Java object
            newContact = newContact.unmarshal(new ByteArrayInputStream(msgContent.getBytes()));
            // Add the Contact object to the root object (which contains a list of Contact objects)
            // Replace existing entry - if a contact exists with same alias
            List existingContacts = contactsRoot.getList();
            for (ListIterator i = existingContacts.listIterator(); i.hasNext(); ) {
                Contact c = (Contact)i.next();
                if (c.getZEUSName().equalsIgnoreCase(newContact.getZEUSName())) {
                    // USe iterator's remove() method to avoid ConcurrentModificationException
                    i.remove();            
                }
            }
            existingContacts.add(newContact);
            // Now save the updated root object to the XML file
            saveFIPAAliases();
            // Now reload the file's contents into the ACC's address book
            loadFIPAAliases();
            // DONE!  Complete the reply message
            p.setType("inform");
            p.setContent("((done (" + msgContent + ")))");
        }
        catch (Exception e) {
            System.out.println("Error unmarshalling XML msg content to Java object");
            e.printStackTrace();
            // FAILED! Complete the reply message
            p.setType("failure");
            p.setContent(msgContent);
        }  
        // Send the reply
        p.send(context);            
    }    
    
    
    /**
     * call the methods necessary to set up the agents transports using a different set of ports.
     *
     */
    protected  void initialiseTransports(String transports) {
        String thisLine;
        String input = new String();
        try {
            FileInputStream fileStream = new FileInputStream(transports);
            DataInputStream toReadFrom = new DataInputStream(fileStream);
            while(toReadFrom.available() > 0) {
                System.out.println("reading");
                thisLine = toReadFrom.readLine();
                input += "\n" + thisLine;
                System.out.println(thisLine);
                
            }
            System.out.println(input);
            
        }
        catch (Exception e) {
            e.printStackTrace(); }
            //  System.out.println(input);
            
            StringHashtable allTransports = ZeusParser.Transports(input);
            Enumeration keys = allTransports.keys();
            while(keys.hasMoreElements()) {
                System.out.println("key  = " + (String) keys.nextElement());
            }
            SystemProps.setTransports(allTransports);
            
    }
    
    
    
    
    /** Traverses the XML document and creates the Java classes 
     * to represent it 
     */
    private synchronized void buildTree() 
    throws Exception {        
        FileInputStream fIn = new FileInputStream (contactsDb);        
        try {
            contactsRoot = contactsRoot.unmarshal (fIn);
        } finally {
            fIn.close();
        }      
    }    
    
    
    /** Writes XML content from the Java classes */
    private synchronized void marshalTree() 
    throws Exception {        
        FileOutputStream fOut = new FileOutputStream (contactsDb);        
        try {
            contactsRoot.marshal(fOut);
        } finally {
            fOut.close();
        }
    }
    
}
