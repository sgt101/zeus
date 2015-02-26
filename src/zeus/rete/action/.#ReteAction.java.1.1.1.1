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


package zeus.rete.action;
import zeus.rete.*;
import zeus.actors.*; 
import zeus.concepts.*;

/** 
    superclass  of the class that is called by all the ReteEngine handlers.
    There is one abstract method here - executeAction (Action,Info) that needs to 
    be implemented<p> 
    @author Simon Thompson
    @since 1.1
    */
public abstract class ReteAction implements BasicAction {
    
    /**
        context is stored by the superclass constructor for 
        access by inhereted classes 
        */
    protected AgentContext context = null;
    
    
    /** 
        engine is the reference to the ReteEngine, in some uses of the 
        zeus.ReteEngine it is anticipated that no agent will be present
        */
    protected ReteEngine engine = null; 
    
    
    /**
        conflictHandler is the reference to the zeus.rete.ConflictHandler object
        which (I think) decides which rule or action to fire next
        */
    protected ConflictSet conflictHandler = null; 
    
    
    /** 
        ontologyDb is the reference to the ontology we are working in, again, this
        package protected slot is provided because we need to remember to use case 
        for Rete where it is not in an agent persay. <br>
        @see getOntology() 
        */
    protected OntologyDb ontologyDb; 
        
    
    
    /** 
        Lame () class constructor forced by my compiler (I have no idea either...),
        anyway, does nothing.
        */
      //  public ReteAction () { ;} 
   
    
    
    /** 
        this method is used to provide the Action objects with "actuators",
        by which I mean some object references that allow the Action implementor 
        to actually do something that effects the state of the agent<p>
        @param context - our old friend the AgentContext interface gives us a reference
        to the class which is implementing the agent's body. 
        @param conflictHandler - reference to the ConflictSet object 
        @author Simon Thompson
        @since 1.1
        */
    public void setActuators (ConflictSet conflictHandler, AgentContext context) { 
        this.conflictHandler = conflictHandler;
        this.context = context; 
    }
    
    
        /** 
        this method is used to provide the Action objects with "actuators",
        by which I mean some object references that allow the Action implementor 
        to actually do something. In this case we are being called from a context that 
        has a null context - so no agent, but a ReteEngine instead. <p> 
        @param engine - the ReteEngine that we are using 
        @param conflictHandler - reference to the ConflictSet object 
        @author Simon Thompson
        @since 1.1
        */
    public void setActuators (ConflictSet conflictHandler, ReteEngine engine) { 
        this.conflictHandler = conflictHandler;
        this.engine = engine; 
    }
    
    
    /**
        return the ontology ref, if the ontologyDb slot is null then
        get the current agent ontology, otherwise get the current ontology
       <p>
        Issues: <br>
        ------<br>
        If the context object and the rete object are both null should this then 
         throw an exception - nice, but makes harder to use...
        */
    public OntologyDb getOntologyDb()  { 
        if (ontologyDb == null)
                return context.getOntologyDb();
            else 
                return ontologyDb; 
    }
    
    
    /**     
        executeAction is a abstract method to be defined by implementing classes. <p>
        This method will be called when the rete algorithm decides that this is 
        the right action to fire right now.
        */
    public abstract void executeAction (Action a, Info info);
    
    
    /** 
     *instantiated to return false, override this to produce service descriptions for rules
     */
    public String getServiceDescription(String language) {
        return null; 
    }
    
    
}