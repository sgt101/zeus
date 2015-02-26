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


public class FIPA_AMS_Management_Content {
    
  private FIPA_AID_Address implementor = null; 
  private AMS_Description subject = null; 
  private boolean type = false; 
  private String action = null; 
  private String result = null;
  private String context = null; 
  private String outputType = null; 
  
  
  public String getSubjectName () { 
    if (implementor != null )
        return (implementor.getName()); 
    else 
        return null; 
        }



 public AMS_Description getDescription () { 
  return (subject);   
 }
 
 
    public FIPA_AID_Address getAddress() { 
        return (subject.getName()); 
    }

    
  
  
  public void setContext (String context) { 
    this.context = context; 
  }
    
  
  public void setImplementor (FIPA_AID_Address addr) { 
    implementor = addr; 
  }
  
  
  public void setSubject (AMS_Description desc) {
    subject = desc; 
  }
  
  
  public void setType (boolean in) {
    type = in;
  }
  
  
  public void setAction (String act) { 
    action = act;
  }
  
  
  public void setResult (String val) {
    result = val; 
  }
  
  
  public void setResult (boolean res) {
    if (res) result = new String("true");
    else result = new String ("false"); 
  }
  
  
  public void setOutputType (String out) { 
    this.outputType = out; 
  }

  
  
  public String toString () { 
    
   String retVal = new String (); 
if (result!=null) {
        retVal += "(" ;
        if (outputType != null) retVal += outputType; }
    if (context != null ) { 
        retVal += "(" + context;  }
   if (!type) 
    retVal += "(action \n"; 
    else 
    retVal += "(done \n";
   if (implementor != null ) { 
        retVal += "(" + implementor.toFIPAString() + ")"; 
   }
   if (action!=null) { 
    retVal += "(" + action +"\n"; 
   if (subject != null) { 
    retVal += subject.toString(); }
    retVal += ")"; 
   }
    if (result!=null) 
    retVal += result+")";
    if (context != null ) 
        retVal += ")"; 
    retVal +=")"; 
   return (retVal); 
  }
    
    
  }
  



  
  
  
  
  
    
    
    
    
