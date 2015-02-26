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
 * @(#)AgentContext.java 1.00
 */

package zeus.actors;

import zeus.actors.factories.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.rete.ReteEngine;
import zeus.actors.rtn.*;
import zeus.agents.ZeusExternal;
import zeus.agents.BasicAgent;
import zeus.agents.BasicAgentUI;
import zeus.actors.intrays.*;


/**
    Every agent must have an implementor of this intereface, or they 
    probably won't work.<p>
    The implementors of this interface should be thought of as being directories
    of object references which can be accessed from various parts of the agent to get
    at the other parts. <p> 
    Numerous doubts have been expressed about this strategy, because it promotes tight coupleing 
    between the components of the agent, but moving away from it seems likely to be an extensive 
    peice of work, so it was decided (who am I kidding? I decided) that it would be a good 
    idea to refactor this as an interface to at least promote the possibilitiy that different 
    component sets could be added/used. <p> 
    @see zeus.actors.ZeusAgentContext
    @author Simon Thompson 
    @since 1.1
 
 */

public interface AgentContext 
{
  
  public void setFacilitators( Vector input );
  
  
  public void addFacilitator(String agent);
  
  
  public void removeFacilitator(String agent);


  public void setNameservers(Vector input);
  
  
  public void addNameserver(Address address);
  
  
  public void removeNameserver(String address) ;
  
  /** 
    set the addressBook for this agent
    */
  public void set(AddressBook addressBook);
  
  
  public void set(ProtocolDb protocolDb);


  public void set(MailBox mbox);


  public void set(MsgHandler msgHandler);


  public void set(Engine engine);


  public void set(ReteEngine reteEngine);


  public void set(ExecutionMonitor monitor);

  public void set(Planner planner);


  public void set(OrganisationDb db);


  public void set(TaskDb taskDb);


  public void set(ResourceDb resourceDb);


  public void set(OntologyDb ontologyDb) ;

 /**
    set a reference to the agent object into this context object
    */
  public void set(BasicAgent agent) ;
  

  public void set(BasicAgentUI agentUI);
  
   /**
     the ZeusExternal is the user defined part of the agent that is 
     called by the agent class when it is run
     */
  public void set(ZeusExternal zeusExternal);

    
    
  

  public void set(ExternalDb externalDb);

    /**
         setting the clock is a vital activity for a Zeus agent, basically by getting a Clock that 
         is set to the Agency time (provided by the ANServer) the agent are able to co-ordinate 
         their actions 
         */
  public void set(Clock clock);
  
  


  public double           now();             
  public Time             currentTime();
  public Time             time(long ctm);
  public long             getClockStep();
  
  public String           newId();
  public String           newId(String tag);

    /**
         return the type of agent, originally one of "Nameserver", "Facilitator", "Visualiser",
      "DbProxy" or "Agent" */
  public String           whatami();
  
  /** 
    return the name of the agent
    */
  public String           whoami();

   
  public Hashtable        queryTable();
  public Vector           facilitators();
  public Vector           nameservers();
  public AddressBook      AddressBook();
  public MailBox          MailBox();
  public MsgHandler       MsgHandler();
  public ReteEngine       ReteEngine();
  public Engine           Engine() ;
  public ExecutionMonitor ExecutionMonitor() ;
  public Planner          Planner() ;
  public OrganisationDb   OrganisationDb();
  public TaskDb           TaskDb()  ;
  public ResourceDb       ResourceDb();
  public OntologyDb       OntologyDb() ;
  public ExternalDb       ExternalDb();
  public ProtocolDb       ProtocolDb() ;
  public ZeusExternal     ZeusExternal() ;
  public BasicAgentUI     AgentUI();
  public BasicAgent       Agent() ;
  public GenSym           GenSym();
  public Clock            Clock() ;


  public Hashtable        getQueryTable(); 
  public Vector           getFacilitators();
  public Vector           getNameservers();
  public AddressBook      getAddressBook();
  public MailBox          getMailBox();
  public MsgHandler       getMsgHandler();
  public ReteEngine       getReteEngine();
  public Engine           getEngine();
  public ExecutionMonitor getExecutionMonitor();
  public Planner          getPlanner();
  public OrganisationDb   getOrganisationDb();
  public TaskDb           getTaskDb();
  public ResourceDb       getResourceDb();
  public OntologyDb       getOntologyDb();
  public ExternalDb       getExternalDb();
  public ProtocolDb       getProtocolDb();
  public ZeusExternal     getZeusExternal();
  public BasicAgentUI     getAgentUI();
  public BasicAgent       getAgent();
  public GenSym           getGenSym();
  public Clock            getClock();
  public TransportFactory getTransportFactory(); 
  
  
  public boolean getSharePlan();
  public boolean getExecuteEarliest(); 
  
  public double getAddressBookRefresh(); 
  public double getAddressTimeout ();   
  public double getReplanPeriod (); 
  public double getRegistrationTimeout(); 
  public double getFacilitatorTimeout(); 
  public double getAcceptTimeout(); 
  public double getFacilitatorRefresh(); 
  
  public void setSharePlan(boolean share_plan);
  public void setExecuteEarliest(boolean execute_earliest); 
  
  public void setAddressBookRefresh(double val); 
  public void setAddressTimeout (double val);   
  public void setReplanPeriod (double val); 
  public void setRegistrationTimeout(double val); 
  public void setFacilitatorTimeout(double val); 
  public void setAcceptTimeout(double val); 
  public void setFacilitatorRefresh(double val); 
  
  
  public InTray getInTray(); 
  
  /**
   *  whereAmI should return the deployed address of the agent - 
   *probably implemented as a TCP/IP address ....
   *would be better implemented from a config file
   */
  public String whereAmI(); 
  
 

}
