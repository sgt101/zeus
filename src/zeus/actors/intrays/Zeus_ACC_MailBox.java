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


package zeus.actors.intrays;
import zeus.actors.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.event.*;
import java.util.*;
import zeus.actors.factories.*;
import zeus.actors.outtrays.*;
// the fact that I am importing *everything* makes me think that this is 
// in the wrong package...

/**
    zeus.actors.fipa.FIPA_Mailbox extends the normal zeus.actors.MailBox.
    In fact it is almost the same. The only changes are that the PostMan[] postman
    and Server server objects are overwritten with FIPA_PostMan and FIPA_Server 
    instances. Since these are functionally equivalent to PostMan and Server 
    everything else can be let be. 
    */ 
public class Zeus_ACC_MailBox extends zeus.actors.MailBox { 
  

  
  private FIPA_AddressBook addressBook;
  protected Queue       fipaOut = new Queue("fipaOut");
  protected Zeus_ACC_Server fipaServer; 
  
  public Zeus_ACC_MailBox(AgentContext context) {
    //super (context); 
    Assert.notNull(context);
    this.context = context;
    context.set(this);

    Address addr;
    Performative msg;

    // setup event-monitor db
    for(int i = 0; i < eventMonitor.length; i++ )
       eventMonitor[i] = new HSet();
    addressBook = new FIPA_AddressBook();
    context.set(addressBook);
    // HERE is the FIPA_Server 

    fipaServer = new Zeus_ACC_Server(context,this);

    server = new Server(context,this,inMail);
    myAddress = server.getAddress();
    

    // HERE is the FIPA_PostMan
    postman = new PostMan[3];
    // normal postmen..
    postman[0] = new PostMan(this,outMail,ccMail,myAddress);
    // this one is not a FIPA_PostMan, cause is is just
    // sending mail to the visualiser!
    postman[1] = new PostMan(this,ccMail,myAddress);
    // knock
    // knock! Ha Ha HA .
   
    postman[2] = new FIPA_PostMan (this, fipaOut,ccMail, myAddress); 
    // Register with Name Servers
    String key = context.newId();
    String[] pattern = { "type", "inform", "in-reply-to", key };

    context.MsgHandler().addRule(new MessageRuleImpl(context.newId("Rule"),
	       pattern,MessageActionImpl.EXECUTE_ONCE,this,"register"));

    for(int i = 0; i < context.nameservers().size(); i++ ) {
       addr = (Address)context.nameservers().elementAt(i);
       context.AddressBook().add(addr);

       msg = new Performative("request");
       msg.setReceiver(addr.getName());
       msg.setReplyWith(key);
       msg.setContent("register");
       sendMsg(msg);
    }   
 
  		//$$ zeus_ACC_MailBox1.move(0,0);
}
  
  
  public FIPA_PostMan getFIPA_PostMan () { 
    return (FIPA_PostMan) this.postman[2]; 
  }
  
  
  public Zeus_ACC_Server getZeus_ACC_Server() { 
    return fipaServer;
  }
  
  
  public InTray getInTray () { 
    return fipaServer; 
  }
  
  
  public boolean is_FIPA (String agent_address_string) {
        if (addressBook.lookupFIPA(agent_address_string)!=null) { 
            return true; 
        }
        return false; 
        // should return true if this address is a FIPA address
  }
  
  
  /**
    lookup the address of a FIPA agent from the name that is being
    used for it in the Zeus agency. 
    */
  public FIPA_AID_Address FIPA_Lookup(String agent_address_string){
    return (addressBook.lookupAlias(agent_address_string)); 
  }
  
  
 public String addressSought(String agent) {
    // First we should clear 'asTable' of entries older than a predefined
    // age so that our agent should query known nameservers for the
    // receiver's address. This way, a receiver that went off-line and
    // later comes online would be found.

    String name;
    KeyValue data;
    double now = context.now();
    Enumeration enum = asTable.keys();
    while( enum.hasMoreElements() ) {
      name = (String) enum.nextElement();
      data = (KeyValue)asTable.get(name);
      if ( now-data.value >= context.getAddressBookRefresh() ) { // LL 040500 1.03b 
        asTable.remove(name);
         context.MsgHandler().removeRule(data.key);
      }
    }
    data = (KeyValue)asTable.get(agent);

    if (data == null) {
      // try contacting known nameservers to find agent's address
      Performative query;
      Address addr;

      String key = context.newId();
      String[] pattern = { "type", "inform", "in-reply-to", key };
      context.MsgHandler().addRule(new MessageRuleImpl(key,pattern,
         MessageActionImpl.EXECUTE_ONCE,this,"addressReceived")
      );
      for(int i = 0; i < context.nameservers().size(); i++ ) {

         addr = (Address)context.nameservers().elementAt(i);
         query = new Performative("query-ref");
         query.setReceiver(addr.getName());
         query.setReplyWith(key);
         query.setContent("address_of " + agent);
         sendMsg(query);
      }

      // add receiver to list of agents whose addresses
      // are being looked for
      now = context.now();
      if ( !context.nameservers().isEmpty() ) {
         now += context.getAddressTimeout();
         asTable.put(agent,new KeyValue(key,now));
         return key;
      }
      else {
         return null;
      }
    }

    else if ( data.value > context.now()){
      return data.key;}
    else{
      return null;}
  }

  public void addressReceived(Performative msg) {
    String key = msg.getInReplyTo();
    Address  address = ZeusParser.address(msg.getContent());
    add(address);
    asTable.remove(address.getName());
    for(int i = 0; i < postman.length; i++ )
       postman[i].addressReceived(key);
  }   
    
	
}