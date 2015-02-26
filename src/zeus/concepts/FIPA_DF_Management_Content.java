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


/**
    this class is used to construct and store content if the FIPA Agent Management
    ontology that is either to be passed to, or from the DF
    */
public class FIPA_DF_Management_Content {

  private FIPA_AID_Address implementor = null;
  private DF_Description subject = null;
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

  /**
    provides debug access to the values
    */
   public String getImplementor () {
    if (implementor != null) {
        return this.implementor.toString();}
        else return ("NULL");
   }


  /**
    provides debug access to the values
    */
   public String getSubject() {
    if (subject != null) {
        return this.subject.toString(); }
        else return ("NULL");

   }


  /**
    provides debug access to the values
    */
  public String getAction () {
    if (action != null) {
        return this.action; }
        else return ("NULL");
  }


  /**
    provides debug access to the values
    */
  public String getType () {
      if (type) return ("TRUE");
        else
            return ("FALSE");
  }


  /**
    provides debug access to the values
    */
  public String getResult() {
    if (result != null) {
       return this.result;}
       else
        return ("NULL");
  }


  /**
    provides debug access to the values
    */
  public String getContext () {
    if (context != null) {
     return this.context;}
     else
        return ("NULL");
  }


  /**
    provides debug access to the values
    */
  public String getOutputType() {
    if (outputType != null) {
        return this.outputType; }
        else
            return ("NULL");

  }


 public DF_Description getDescription () {
  return (subject);
 }


    public FIPA_AID_Address getAddress() {
        return (subject.getName());
    }




  public void setContext (String context) {
    this.context = context;
  }


  /**
    use this to set the name of the agent that is expected to
    carry out the action
    */
  public void setImplementor (FIPA_AID_Address addr) {
    implementor = addr;
  }


  /**
    this is used to set the df-agent-description element of the management content
    */
  public void setSubject (DF_Description desc) {
    subject = desc;
  }


  /**
    I can't remember what this is for
    */
  public void setType (boolean in) {
    type = in;
  }

  /**
   ie. register...
   */
  public void setAction (String act) {
    action = act;
  }


  /**
    if this passed back as a result of a request then it might be "true" or "false" or
    some more meaningful string
    */
  public void setResult (String val) {
    result = val;
  }


/**
    if this passed back as a result of a request then it might be "true" or "false" or
    some more meaningful string
    */
  public void setResult (boolean res) {
    if (res) result = new String("true");
    else result = new String ("false");
  }


  public void setOutputType (String outType) {
    this.outputType = outType;
  }

  public String toString () {

   String retVal = new String ();
    if (result!=null) {
        retVal += "(" ;
        if (outputType != null) retVal += outputType; }
    if (context != null ) {
        retVal += "(" + context;  }
    if (type)
        retVal += "(done ";
    if (implementor != null )
        retVal += "(" + implementor.toFIPAString() + ")";
    if (action!=null) {
        retVal += action +" ";
    if (subject != null)
        retVal += subject.toString();
    retVal += ")";
    }
    if (result!=null)
        retVal += result+")";
    if (context != null )
        retVal += ")";
    retVal +=")";
    debug (retVal);
    return (retVal);
  }


  public void debug (String str) {
   // System.out.println("FIPA_DF_Management_Content : "+ str);
  }


  }













