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

/** 
 *this interface is instantiatied by a number of different types of task, and 
 *is used to store user defined parameters and info that are entered into the 
 *task at design time using the Zeus Agent Generator tool
 *CHANGE LOG
 *20/9/02 added a description field to let users put in a description of the task 
 *for some use cases; this information is used by the service description system
 *I have added some feilds and client methods to the class to collect service 
 *description information, I think that this is the right place to do it because
 *while the Zeus reasoner only looks at plan atoms (primitive tasks) in an
 *open environment the ability to write service descriptions for reactive 
 *tasks may be useful, so this interface provides the common root that the two 
 *subclasses can inheret the functionality from. 
 */
public abstract class AbstractTask {

   public static final int PRIMITIVE = 0;
   public static final int SUMMARY   = 1;
   public static final int BEHAVIOUR = 2;
   public static final int SCRIPT    = 3;

  private List restrictions;

  public AbstractTask() {
    restrictions = new Vector();
  }

   static String[] TaskTypes = {
      "Primitive", "Summary", "Rulebase", "PlanScript"
   };

   public static int getType(String typeName) {
      return Misc.whichPosition(typeName,TaskTypes);
   }
   public static String getTypeName(int type) {
      return TaskTypes[type];
   }

   protected String name = SystemProps.getProperty("task.default.name");
   protected int    type = PRIMITIVE;

   public void setName(String name)  {
      Assert.notNull(name);
      Assert.notFalse( !name.equals("") );
      this.name = name;
   }

   public String getName()     { return name; }
   public int    getType()     { return type; }
   public String getTypeName() { return TaskTypes[type]; }

   public boolean isPrimitive() { return type == PRIMITIVE; }
   public boolean isSummary()   { return type == SUMMARY;   }
   public boolean isBehaviour() { return type == BEHAVIOUR; }
   public boolean isScript()    { return type == SCRIPT;    }

   public AbstractTask duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public String pprint() {
      return pprint(0);
   }

   public abstract AbstractTask duplicate(DuplicationTable table);
   public abstract boolean      isValid();
   public abstract String       pprint(int sp);
   public abstract boolean      resolve(Bindings bindings);
   private String textInfo = ""; 
   private String phoneInfo = "";
   private String faxInfo = ""; 
   private String emailInfo = "";
   private String physicalInfo = "";
   private String geoInfo = "";
   
   public void setTextInfo (String in) {
       this.textInfo = in; 
   }
   
   
   public String getTextInfo() {
       return this.textInfo;
   }
   
   public void setPhoneInfo (String in) {
       this.phoneInfo = in;
   }
   
   public String getPhoneInfo () {
       return this.phoneInfo;
   }
   
   
   public void setFaxInfo (String in) {
       this.faxInfo = in;
   }
   
   
   public String getFaxInfo () {
       return this.faxInfo;
   }
   
   
   public void setEmailInfo (String in) {
       this.emailInfo = in;
   }
   
   public String getEmailInfo () { 
       return this.emailInfo;
   }
   
   public void setPhysicalInfo (String in) {
       this.physicalInfo = in; 
   }
   
   public String getPhysicalInfo () { 
       return this.physicalInfo;
   }
   
   public void setGeoInfo (String in) {
       this.geoInfo = in;
   }
   
   public String getGeoInfo () {
       return this.geoInfo;
   }
   
  public void addRestriction(String fact, String attribute, String value) {
    restrictions.add(new Restriction(getName(), fact, attribute, value));
  }

  public List getRestrictions() {
    return restrictions;
  }
}
