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



package zeus.concepts;

import java.util.*;
import zeus.util.*;
import zeus.concepts.fn.*;
import zeus.actors.ZeusTask;
import zeus.actors.AgentContext;
import zeus.ontology.service.*;

public abstract class Task extends AbstractTask {
   protected Vector constraints = new Vector();
   protected ValueFunction time = ZeusParser.Expression(SystemProps.getProperty("task.default.time"));
   protected ValueFunction cost = ZeusParser.Expression(SystemProps.getProperty("task.default.cost"));

   transient ResolutionContext resolution_context = null;

   public void setTimeFn(String time) {
      ValueFunction fn = ZeusParser.Expression(time);
      if ( fn != null )
         this.time = fn;
   }
   
   
   public void setTimeFn(ValueFunction fn) {
      this.time = fn;
   }
   
   
   public void setCostFn(String cost) {
      ValueFunction fn = ZeusParser.Expression(cost);
      if ( fn != null )
         this.cost = fn;
   }
   
   
   public void setCostFn(ValueFunction fn) {
      this.cost = fn;
   }

   public ValueFunction getTimeFn() { return time; }
   public ValueFunction getCostFn() { return cost; }

   public int getTime() {
      int t = SystemProps.getInt("task.default.time");
      // added by simon 14/2/01 - deal with elaborations
      if (time instanceof ElseFn) { 
        ValueFunction evaledFn = time.evaluationFn(); 
        time = evaledFn; }
        
      if (time instanceof ArithmeticFn) { 
        ValueFunction timef = ((ArithmeticFn)time).evaluationFn(); 
        if (timef instanceof  IntFn) {  
              t = ((IntFn)time).getValue(); 
        } 
        else if (timef instanceof RealFn) { 
            double td = ((RealFn) time).getValue(); 
            Double tconv = new Double (td); 
            t = tconv.intValue(); 
        }
      }
     
      // end of add
      if ( !time.isDeterminate() || !(time instanceof IntFn) ) {
         Core.USER_ERROR("Task " +  name +
            " is improperly defined.\nCannot evaluates its " +
            " duration given required effect.\nCurrent value: \'" +
	    time + "\'\nSetting duration to " + t );
      }
       else {
         t = ((IntFn)time).getValue();
      }
      return t;
   }
   

   public double getCost() {
      
      double c = SystemProps.getDouble("task.default.cost");
          // added by simon 14/2/01 - deal with elaborations
      debug ("cost= " + cost.toString()); 
      Fact [] postConds = getPostconditions(); 
      
      for (int i = 0 ; i<postConds.length; i++) { 
        debug ("post  = " +  postConds[i].toString()); 
      }
      if (cost instanceof ElseFn) { 
        ValueFunction evaledFn = cost.evaluationFn(); 
        debug ("elseed" + evaledFn.toString()); 
        cost = evaledFn; 
        }
        
      if (cost instanceof ArithmeticFn) { 
        ValueFunction costf = ((ArithmeticFn)cost).evaluationFn(); 
        if (costf instanceof  IntFn) {  
              c = ((IntFn)time).getValue(); 
              debug ("arith then int"); 
              return c; 
        } 
        else if (costf instanceof RealFn) { 
         c = ((RealFn) time).getValue(); 
         debug ("arith then real"); 
         return c;    
      }
      }
      // end of add
      if ( !cost.isDeterminate() || !(cost instanceof PrimitiveNumericFn) ) {
         Core.USER_ERROR("Task " +  name +
            " is improperly defined.\nCannot evaluates its " +
            " cost given required effect.\nCurrent value: \'" +
	    cost + "\'\nSetting cost to " + c );
      }
      else {
         c =  ((PrimitiveNumericFn)cost).doubleValue();
      }
      return c;
   }

   public abstract Fact[]            getPostconditions();
   public abstract Fact[]            getPreconditions();
   public abstract ResolutionContext getContext();

   public boolean applyConstraints(Bindings bindings) {
      Bindings local = new Bindings(bindings);

      ResolutionContext context = getContext();
      LogicalFn fn;
      for(int i = 0; i < constraints.size(); i++ ) {
         fn = (LogicalFn)constraints.elementAt(i);
         fn = (LogicalFn)fn.resolve(context,local);
	 if ( fn == null )
	    return false;
	 if ( fn.evaluate() != LogicalFn.TRUE )
            return false;
      }
      return bindings.add(local);
   }

   public void setConstraints(Vector List) {
      constraints.removeAllElements();
      for(int i = 0; i < List.size(); i++)
         constraints.addElement((LogicalFn)List.elementAt(i));
   }
   
   
   public void setConstraints(LogicalFn[] List) {
      constraints.removeAllElements();
      for(int i = 0; i < List.length; i++)
         constraints.addElement(List[i]);

   }
   
   
   public LogicalFn[] getConstraints() {
      LogicalFn[] out = new LogicalFn[constraints.size()];

      for(int i = 0; i < constraints.size(); i++)
         out[i] = (LogicalFn)constraints.elementAt(i);
      return out;
   }
   
   /**
    *This generates the service description from the task stub
    *by using reflection... 
    *@since 1.3
    *@author Simon Thompson
    */
    public String getServiceDesc(AgentContext context) {

      try {
	ProfileRenderer renderer =RendererFactory.getProfileRenderer("DAML-S");
	return renderer.renderProfile(this, context);
      }
      catch(zeus.ontology.service.UnknownRendererTypeException u) {
	return null;
      }
    }
   
  /**
   * Get the instance description from the task.
   */
  public String getInstanceDetails(AgentContext context) {

    try {
      InstanceRenderer renderer =RendererFactory.getInstanceRenderer("DAML-S");
      return renderer.renderInstance(this, context);
    }
    catch(zeus.ontology.service.UnknownRendererTypeException u) {
      return null;
    }
  }

  public String getInstanceRange(AgentContext context) {

    try {
      RangeRenderer renderer = RendererFactory.getRangeRenderer("XSD");
      return renderer.renderRange(this, context);
    }
    catch(zeus.ontology.service.UnknownRendererTypeException u) {
      return null;
    }
  }
  
  public String getProcessModel(AgentContext context) {

    try {
      ProcessRenderer renderer = RendererFactory.getProcessRenderer("DAML-S");
      return renderer.renderProcess(this, context);
    }
    catch(zeus.ontology.service.UnknownRendererTypeException u) {
      return null;
    }
  }

   public void debug (String str) { 
  //  System.out.println("task>> " + str); 
   } 
   
}
