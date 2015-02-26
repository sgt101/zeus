
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
/** 
    FIPA_AMS_Services is used by the ACC agent to wrap the Nameserver 
    so that it can be accessed exernally. 
    */
public class FIPA_Services { 
   
    protected AgentContext context = null; 
    protected String name = null; 
    protected String host = null;
    protected String type = null; 
    
    
       
          /**
          set the agent using this to handle messages that are ".*receiver df@hap.*content (register"
        by calling the handleRegister method in this class
        */
    public void setRegister () { 
        MsgHandler handler = context.getMsgHandler();
	    String msg_pattern[] = {"receiver",type,"content","\\A(.*)\\((\\s*)register(.*)\\Z"};
	    handler.addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern, this, "handleRegister"));
	}
	
	
	
	
	
	public void setDeregister () { 
	    MsgHandler handler = context.getMsgHandler();
	    String msg_pattern[] = {"receiver",type,"content","\\A(.*)deregister(.*)\\Z"};
	    handler.addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern, this, "handleDeregister"));
	}
	
	
	
	
	public void setModify () { 
	    MsgHandler handler = context.getMsgHandler();
	    String msg_pattern[] = {"receiver",type,"content","\\A(.*)modify(.*)\\Z"};
	    handler.addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern, this, "handleModify"));
	}
	
		
		
	public void setSearch() { 
	    MsgHandler handler = context.getMsgHandler();
	    String msg_pattern[] = {"receiver",type,"content","\\A(.*)search(.*)\\Z"};
	    handler.addRule(new MessageRuleImpl(context.newId("Rule"),msg_pattern, this, "handleSearch"));
	}
	
		
	    /**
        set the name of the agent to df@host - if the host has been set, else
        set it to df@localhost
        */
    public void setName () { 
        name = new String (this.type + "@"); 
        if (host == null) { 
            try {
                InetAddress ip = InetAddress.getLocalHost();
                String localhost = ip.getHostAddress();
                name += localhost; }
                catch (Exception e) { 
                    System.out.println("network configuration problems in " + type); 
                    System.out.println("Exception thrown : " ); 
                    e.printStackTrace(); 
                    System.out.println("setting name to " + type + "@127.0.0.1"); 
                    name += "127.0.0.1"; }
            } else {
                name += host; 
            }
            
    }
    
    
    /**
        set the name of the df to some arbitary value @param name
        This method should not normally be used.
        */
    public void setName (String name) { 
        this.name = name; 
    }
    
    
    /**
        set the hap part of the df@hap name of the df to some value 
        other than the default df@localhost (where local host is the ip address of 
        this machine
    */
    public void setHost (String host) { 
        this.host = host; 
    }
	
	
	 /**
        send a registration to the nameservers that we are using. <br>
        @param sender is the name of the alias to use
        */
    protected void registerAlias (String name) {
     // String name = address.getName();
      MailBox mbox = context.getMailBox();
      AddressBook addressBook = context.getAddressBook();
      for(int i = 0; i < context.nameservers().size(); i++ ) {
        Address addr = (Address)context.nameservers().elementAt(i);
        addressBook.add(addr);
        Performative msg = new Performative("request");
        msg.setReceiver(addr.getName());
        msg.setReplyWith(name);
        msg.setContent("register");
        msg.setSender (name);
        mbox.sendMsg(msg);
        System.out.println("tried to send " + msg.toString());
    }
        System.out.println("finished attempted registration"); 
  }


public void send (Performative perf){ 
        MailBox mbox = context.getMailBox(); 
        mbox.sendMsg(perf); 
    }
    

}