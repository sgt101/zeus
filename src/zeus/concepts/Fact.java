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
 * @(#)Fact.java 1.00
 */

package zeus.concepts;

import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.fn.*;

/**
 * The Fact class is key conceptual data structure in ZEUS, since Fact objects
 * store the information that collectively forms the application ontology.
 * Each fact consists of a number of attribute-value pairs, which are stored
 * in the {@link AttributeList} variable. <p>
 * Fact objects come in two forms:
 * <ul>
 * <li> Fact Descriptions - these represent actual entities (always shown prefixed
 * with an @ symbol).
 * <li> Fact Variables - these are descriptions of virtual entities that can
 * be instantiated as Facts (these always shown prefixed with a ? symbol).
 * </ul> <p>
 * Facts originate from template descriptions, so they can only be created
 * from an existing ontology by invoking the {@link OntologyDb#getFact}
 * method of the agent's {@link OntologyDb}.
 *
 *This class is a little case study in how a little knowledge can be a dangerous 
 *thing. It seems that the concept of checking boolean flags one at a time didn't
 *appeal to whoever implemented it (DN?) and instead a int based mask system 
 *has been used to check and set the characteristics of the class. Unfortunately
 *I suspect that the mask is used in side effects for a number of algorithms 
 *in the core reasoners, so while I would like to remove this nonsense, I so far
 *haven't summed up the courage!
 */

public class Fact {
   public static final String NONVAR = "fact";
   public static final String VAR    = "var";
   public static final String V_STR  = "?";
   public static final char   V_CHR  = '?';
   public static final String F_STR  = "@";
   public static final char   F_CHR  = '@';
   public static final String A_STR  = ".";
   public static final char   A_CHR  = '.';


   public static final boolean FACT = false;
   public static final boolean VARIABLE = true;
   public static final String  SELF_NAME = "self";
   public static final String  THIS_NAME = "this";
   public static final String  THIS = V_STR + THIS_NAME;
   public static final String  SELF = V_STR + SELF_NAME;

   public static final String newVar(GenSym genSym) {
      return genSym.plainId(V_STR+VAR);
   }

   protected static String name(boolean isVariable, GenSym genSym) {
      String s = (isVariable ? VAR : NONVAR);
      return  genSym.plainId(s);

   }

   
   // one can only wonder at the mind that would think that 
   // setting binary masks for tests in a Java program 
   // is a sane idea.
   // still, I daren't remove it....
   protected static final int IS_VARIABLE    = 1;
   protected static final int IS_NEGATION    = 2;
   protected static final int IS_READ_ONLY   = 4;
   protected static final int IS_REPLACED    = 8;
   protected static final int IS_LOCAL       = 16;
   protected static final int IS_SIDE_EFFECT = 32;

   transient ValueFunction functor = null;
   transient String        descp = null;

   protected String        id;
   protected String        type;
   protected int           modifiers;
   protected AttributeList attr;
   protected OntologyDb    ontology;

   public Fact () { 
    ;
   }

   Fact(boolean is_variable, String type, OntologyDb ontology, GenSym genSym) {
      this.id        = name(is_variable,genSym);
      this.type      = type;
      this.modifiers = is_variable ? IS_VARIABLE : 0;
      this.ontology  = ontology;
      setFunctor();
   }


   public Fact(Fact fact) {
      this.id        = fact.ID();
      this.type      = fact.getType();
      this.modifiers = fact.getModifiers();
      this.attr      = new AttributeList(fact.getAttributeList());
      this.ontology  = fact.getOntologyDb();
      setFunctor();
   }


   public Fact(boolean is_variable, Fact fact) {
      this(fact);
      setIsVariable(is_variable);
   }


   protected Fact(String type, String id, int modifiers,
                  AttributeList attr, OntologyDb ontology) {
      this.id        = id;
      this.type      = type;
      this.modifiers = modifiers;
      this.attr      = attr;
      this.ontology  = ontology;
      setFunctor();
   }

   
   /** 
    *made this accessor public (simon 23/9/02)
    *with the object of using it to get service description information
    **/    
    public OntologyDb getOntologyDb() {
      return ontology;
   }


   /**
    * Here mode defines whether or not this fact owns the attribute
    * being edited, where 'true' implies the fact owns the attribute
    */
   public TreeNode createAttributeTree(boolean mode) {

      String name = (mode) ? SELF : getId();
      return ontology.createAttributeTree(type,name);
   }

   // in case you are wondering, which, believe me, I did a lot when I saw this 
   // I think that what is going on here is an amazingly complex scheme to test
   // whether some boolean flags are set
   // it appears to work
   // I am loath to change it 
   // but it may be some of the worst code I ever saw.
   public static boolean isVariable(int x)   { return (x&IS_VARIABLE)    != 0; }
   public static boolean isNegative(int x)   { return (x&IS_NEGATION)    != 0; }
   public static boolean isReadOnly(int x)   { return (x&IS_READ_ONLY)   != 0; }
   public static boolean isLocal(int x)      { return (x&IS_LOCAL)       != 0; }
   public static boolean isReplaced(int x)   { return (x&IS_REPLACED)    != 0; }
   public static boolean isSideEffect(int x) { return (x&IS_SIDE_EFFECT) != 0; }

   public boolean isVariable()   { return isVariable(modifiers);   }
   public boolean isNegative()   { return isNegative(modifiers);   }
   public boolean isReadOnly()   { return isReadOnly(modifiers);   }
   public boolean isLocal()      { return isLocal(modifiers);      }
   public boolean isReplaced()   { return isReplaced(modifiers);   }
   public boolean isSideEffect() { return isSideEffect(modifiers); }

   public String ID()           { return id; }
   public String getId()        { return descp; }
   public String getType()      { return type; }
   public int    getModifiers() { return modifiers; }

   public void setId(String id) {
      Assert.notNull(id);
      this.id = id;
      setFunctor();
   }

   public void setType(String type) {
      Assert.notNull(type);
      this.type = type;
      setFunctor();
   }


   public void setModifiers(int modifiers) {
      this.modifiers = modifiers;
      setFunctor();
   }

   public static int setIsVariable(int x, boolean set) {
      if ( set )
         x |= IS_VARIABLE;
      else
         x &= ~IS_VARIABLE;
      return x;
   }

   public static int setIsNegative(int x, boolean set) {
      // if IS_NEGATION then clear IS_READ_ONLY, IS_LOCAL and IS_REPLACED
      // otherwise clear IS_NEGATION
      if ( set ) {
         x &= ~(IS_READ_ONLY|IS_LOCAL|IS_REPLACED);
         x |= IS_NEGATION;
      }
      else
         x &= ~IS_NEGATION;
      return x;
   }

   public static int setIsReadOnly(int x, boolean set) {
      // if IS_READ_ONLY then clear IS_NEGATION and IS_REPLACED
      // otherwise clear IS_READ_ONLY
      if ( set ) {
         x &= ~(IS_NEGATION|IS_REPLACED);
         x |= IS_READ_ONLY;
      }
      else
         x &= ~IS_READ_ONLY;
      return x;
   }

   public static int setIsLocal(int x, boolean set) {
      // if IS_LOCAL then clear IS_NEGATION
      // otherwise clear IS_LOCAL
      if ( set ) {
         x &= ~IS_NEGATION;
         x |= IS_LOCAL;
      }
      else
         x &= ~IS_LOCAL;
      return x;
   }

   public static int setIsReplaced(int x, boolean set) {
      // if IS_REPLACED then clear IS_READ_ONLY and IS_NEGATION
      // otherwise clear IS_REPLACED
      if ( set ) {
         x &= ~(IS_READ_ONLY|IS_NEGATION);
         x |= IS_REPLACED;
      }
      else
         x &= ~IS_REPLACED;
      return x;
   }

   public static int setIsSideEffect(int x, boolean set) {
      if ( set )
         x |= IS_SIDE_EFFECT;
      else
         x &= ~IS_SIDE_EFFECT;
      return x;
   }


   public void setIsVariable(boolean set) {
      modifiers = setIsVariable(modifiers,set);
      setFunctor();
   }
   public void setIsNegative(boolean set) {
      modifiers = setIsNegative(modifiers,set);
   }
   public void setIsReadOnly(boolean set) {
      modifiers = setIsReadOnly(modifiers,set);
   }
   public void setIsLocal(boolean set) {
      modifiers = setIsLocal(modifiers,set);
   }
   public void setIsReplaced(boolean set) {
      modifiers = setIsReplaced(modifiers,set);
   }
   public void setIsSideEffect(boolean set) {
      modifiers = setIsSideEffect(modifiers,set);
   }

   public String[] listAttributes()  {
      return attr.listAttributes();
   }
   public ValueFunction[] listValues()  {
      return attr.listValues();
   }
   public ValueFunction[] variables()  {
      Vector out = attr.variables();
      if ( isVariable() && !out.contains(functor()) )
         out.addElement(functor());

      ValueFunction[] result = new ValueFunction[out.size()];
      for(int i = 0; i < result.length; i++ )
         result[i] = (ValueFunction)out.elementAt(i);
      return result;
   }
   
   /** 
    getAttributeList returns a list of the attributes of the Fact. 
    This is public in 1.2.1 so that it can be manipulated directly
    */
   public AttributeList getAttributeList()  { 
      return attr;
   }

   public String getValue(String attribute) {
      String value = attr.getValue(attribute);
      if ( value == null )
         Core.USER_ERROR("Fact.getValue(): No attribute \'" + attribute +
           "\' found in fact \'" + this + "\'");
      return value;
   }
   
   
   public ValueFunction getFn(String attribute) {
      ValueFunction value = attr.getFn(attribute);
      if ( value == null )
         Core.USER_ERROR("Fact.getValue(): No attribute \'" + attribute +
           "\' found in fact \'" + this + "\'");
      return value;
   }

   public int getInt(String attribute) {
      try {
         PrimitiveNumericFn fn;
         fn = (PrimitiveNumericFn)attr.getFn(attribute);
         return fn.intValue();
      }
      catch(ClassCastException e) {
         Core.USER_ERROR("Fact.getInt(\'" + attribute +
            "\') called for non-ground value \'" + this + "\'");
         return 0;
      }
      catch(NullPointerException e1) {
         Core.USER_ERROR("Fact.getInt(): No attribute \'" + attribute +
           "\' found in fact \'" + this + "\'");
         return 0;
      }
   }

   public double getDouble(String attribute) {
      try {
         PrimitiveNumericFn fn;
         fn = (PrimitiveNumericFn)attr.getFn(attribute);
         return fn.doubleValue();
      }
      catch(ClassCastException e) {
         Core.USER_ERROR("Fact.getDouble(\'" + attribute +
            "\') called for non-ground value \'" + this + "\'");
         return 0;
      }
      catch(NullPointerException e1) {
         Core.USER_ERROR("Fact.getDouble(): No attribute \'" + attribute +
           "\' found in fact \'" + this + "\'");
         return 0;
      }
   }

   public long getLong(String attribute) {
      try {
         PrimitiveNumericFn fn;
         fn = (PrimitiveNumericFn)attr.getFn(attribute);
         return fn.longValue();
      }
      catch(ClassCastException e) {
         Core.USER_ERROR("Fact.getLong(\'" + attribute +
            "\') called for non-ground value \'" + this + "\'");
         return 0;
      }
      catch(NullPointerException e1) {
         Core.USER_ERROR("Fact.getLong(): No attribute \'" + attribute +
           "\' found in fact \'" + this + "\'");
         return 0;
      }
   }

   public int getNumber() {
      if ( !isa(OntologyDb.ENTITY) ) return 1;

      try {
         PrimitiveNumericFn fn;
         fn = (PrimitiveNumericFn)attr.getFn(OntologyDb.NUMBER);
         return fn.intValue();
      }
      catch(ClassCastException e) {
       
            e.printStackTrace(); 
          
         Core.USER_ERROR("getNumber() called for non-ground value \'" +
            this + "\'");
         return 0;
      }
   }

   public void setNumber(int x) {
      if ( isa(OntologyDb.ENTITY) )
         setValue(OntologyDb.NUMBER,x);
      else if ( x != 1 )
         Core.USER_ERROR("setNumber() called for non-" + OntologyDb.ENTITY +
            " object \'" + this + "\'");
   }
   public void setNumber(VarFn var1) {
      if ( isa(OntologyDb.ENTITY) )
         setValue(OntologyDb.NUMBER,var1);
      else
         Core.USER_ERROR("setNumber() called for non-" + OntologyDb.ENTITY +
            " object \'" + this + "\'");
   }

   public double getNetCost() {
      if ( isa(OntologyDb.ENTITY) && !isDeterminate(OntologyDb.COST) ) return 0;
      return getNumber() * getUnitCost();
   }
   public double getUnitCost() {
      if ( !isa(OntologyDb.ENTITY) ) return 0;

      try {
         PrimitiveNumericFn fn;
         fn = (PrimitiveNumericFn)attr.getFn(OntologyDb.COST);
         return fn.doubleValue();
      }
      catch(ClassCastException e) {
         Core.USER_ERROR("getUnitCost() called for non-ground value \'" +
            this + "\'");
         return 0;
      }
   }
   public void setUnitCost(double x) {
      if ( isa(OntologyDb.ENTITY) )
         setValue(OntologyDb.COST,x);
      else
         Core.USER_ERROR("setUnitCost() called for non-" + OntologyDb.ENTITY +
            " object \'" + this + "\'");
   }
   public void setUnitCost(VarFn var1) {
      if ( isa(OntologyDb.ENTITY) )
         setValue(OntologyDb.COST,var1);
      else
         Core.USER_ERROR("setUnitCost() called for non-" + OntologyDb.ENTITY +
         " object \'" + this + "\'");
   }

   public VarFn newVar() {
      return new VarFn(newVar(ontology.GenSym()));
   }

   public void setValue(String attribute, int value) {
      setValue(attribute, new IntFn(value));
   }
   public void setValue(String attribute, long value) {
      setValue(attribute, new IntFn(value));
   }
   public void setValue(String attribute, double value) {
      setValue(attribute, new RealFn(value));
   }
   public void setValue(String attribute, boolean value) {
      setValue(attribute, BoolFn.newBoolFn(value));
   }
   public void setValue(String attribute, String value) {
      ValueFunction fn = ZeusParser.Expression(ontology,value);
      if ( fn == null )
         Core.USER_ERROR("Cannot parse value \'" + value +
            "\' in Fact.setValue()");
      else
         setValue(attribute,fn);
   }
   public void setValue(String attribute, ValueFunction value) {
      if ( !ontology.validate(type,attribute,value) )
         Core.USER_ERROR("Setting value \'" + value + "\' for " +
            "attribute \'" + attribute + "\' in fact \'" + this + "\'");
      else
         attr.setValue(attribute, value);
   }
   void setAttributeList(AttributeList List) {
      Assert.notNull(List);
      attr = List;
   }

   public void setValues(String[] input) {
      if ( input.length%2 != 0 ) {
         Core.USER_ERROR("Fact.setValues():: improper input length \"{" +
            Misc.concat(input) + "}\".");
         return;
      }

      for(int i = 0; i < input.length; i += 2 )
         setValue(input[i],input[i+1]);
   }
 
 /*
    public boolean equals (Object obj) { 
        if ( !(obj instanceof Fact) ) {
            debug ("not a fact!!!"); 
            return false;
            }
      Fact f = (Fact)obj;
      if (!isa(f.getType())) { 
        debug (getId() + " ret false on type"); 
        return false; }
      if ( isVariable() || f.isVariable() ){
         boolean retval = attr.equals(f.getAttributeList());
         if (retval == false) { 
            debug (getId() + " ret false in attrtest"); 
            return false;
            }
            else {
                debug (getId() + " ret true in attrtest"); 
                return true; 
            }
                
         }
      else { 
         boolean retval = descp.equals(f.getId()); 
         
        if (retval == false) { 
            debug (getId() + " ret false in descp test"); 
            return false;
            }
            else {
                debug (getId() + " ret true in descp test"); 
                return true; 
            }
        }
        
    }*/
 
 /** 
        possibly this should return a true if it is equals in an oo sense? 
            */
   public boolean equals(Object obj) {
      debug ("normal equality " + this.toString()); 
      if ( !(obj instanceof Fact) ) return false;
      Fact f = (Fact)obj;
      if ( !type.equals(f.getType()) )
          return false;
      if ( isVariable() || f.isVariable() )
         return attr.equals(f.getAttributeList());
      else
         return descp.equals(f.getId());
   }


   protected void setFunctor() {
      descp = isVariable() ? V_STR + id : F_STR + id;
      functor = isVariable() ? (ValueFunction)(new VarFn(descp))
                             : (ValueFunction)(new TypeFn(descp));
   }

   public boolean isDeterminate() {
      return isVariable() ? false : attr.isDeterminate();
   }

   public boolean isDeterminate(String attribute) {
      return attr.isDeterminate(attribute);
   }

   public String[] objectAttributeNames() {
      String[] a = attr.listAttributes();
      String object = getId();
      for(int i = 0; i < a.length; i++ )
         a[i] = object + A_STR + a[i];
      return a;
   }

   public String toString() {

   // test only
        //System.out.println("SL ::\n" + this.toSL());
   // end test
      String s = "(:type " + type + " " +
                  ":id " + id + " " +
                   ":modifiers " + modifiers;
      if ( !attr.isEmpty() )
         s += " :attributes " + attr.toString();
      return s + ")";
   }



   /**
        added by Simon on 20/02/02
        Get the SL value of this fact
        An abs factory implementation would be better, but I want to have a go at this
        first before investing too heavily in a final implementation
        */
   public String toSL () {
        String s = "(" +  type+" ";
        if (!attr.isEmpty())
                s +=  attr.toSL();
                return s +")";
   }



   public String pprint() {
      return pprint(0);
   }


   public String pprint(int sp) {
      String suffix, prefix;
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = "(:type " + type + eol +
                  ":id " + id + eol +
                  ":modifiers " + modifiers + eol;

      if ( !attr.isEmpty() ) {
         prefix = ":attributes ";
         suffix = Misc.spaces(1 + sp + prefix.length());
         s += prefix + attr.pprint(suffix.length());
      }
      return s.trim() + "\n" + tabs + ")";
   }

   
   /** 
    *this is a bit of a mess, because we are passing a parameter that is 
    *used to ppring the fact into DAML format...
    *header is the front of the parameter, footer is the close tag
    */
   public String printDAML (String header,String footer) { 
       String names [] = attr.getNames();
       String out = new String ();
       for (int count = 0;count<names.length; count++) {
       out += header + "#" + type +":" + names[count] + "=" + attr.getValue(names[count]) + "\\\""+footer+"\\n\";\n"; 
       }
       return out;
   }

   public boolean unifiesWith(Fact f, Bindings bindings) {
      if ( type.equals(f.getType()) ) {

         if ( !isVariable() && !f.isVariable() && !descp.equals(f.getId()) )
            return false;

         Bindings b = new Bindings(bindings);
         ValueFunction me  = functor();
         ValueFunction you = f.functor();

         if ( me.unifiesWith(you,b) == null ||
              !attr.unifiesWith(f.getAttributeList(),b) )
            return false;

         bindings.set(b);
         return true;
      }
      return false;
   }

   public boolean unifiesWithChild(Fact f, Bindings bindings) {
      if ( type.equals(f.getType()) )
         return unifiesWith(f,bindings);

      else if ( ontology.isAncestorOf(f.getType(),type) ) {
         if ( !isVariable() && !f.isVariable() )
            return false;

         Bindings b = new Bindings(bindings);

         if ( !attr.unifiesWith(f.getAttributeList(),b) )
            return false;

         bindings.set(b);
         return true;
      }
      return false;
   }

   public boolean isa(String ancestor) {
      return type.equals(ancestor) || ontology.isAncestorOf(type,ancestor);
   }

   public ValueFunction functor() {
      return (ValueFunction) functor;
   }
   public boolean resolve(Bindings bindings) {
      return resolve(new ResolutionContext(),bindings);
   }
   public boolean resolve(ResolutionContext context, Bindings bindings) {
      Object obj = context.put(THIS,this);
      boolean status = attr.resolve(context,bindings);
      if ( obj != null ) context.put(THIS,obj);
      return status;
   }

   public boolean disjoin(Fact f) {
      if ( !type.equals(f.getType()) ) return false;
      attr.disjoin(f.getAttributeList());
      return true;
   }

   public Fact duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }

   
   public Fact duplicate(DuplicationTable table) {
      String id1 = table.getRef(id);
      AttributeList attr1 = attr.duplicate(table);
      Fact f1 = new Fact(type,id1,modifiers,attr1,ontology);
      return f1;
   }

    /** 
        test to see if any of the values in this fact are mapped to 
        the valeus in the @param mapper, make a map of attno to attno for all the 
        attributes so that they can be mapped in the method map
        */
    public synchronized String[] buildMap(Fact mapper) { 
        debug (this.type); 
        String [] retVal = new String[attr.size()];
        Enumeration keys = attr.keys(); 
        AttributeList mapperAttr = mapper.getAttributeList(); 
        int count = 0; 
        while (keys.hasMoreElements()){ 
         Enumeration mapperKeys = mapperAttr.keys(); 
         String key = (String) keys.nextElement(); 
         String val = attr.getValue(key); 
         int count2 = 0; 
         boolean done = false; 
         while (mapperKeys.hasMoreElements()&& !done) { 
            String mkey = (String) mapperKeys.nextElement();
            String mval = mapperAttr.getValue(mkey); 
            ValueFunction mVf = mapperAttr.getFn (mkey); 
            debug ("mVf = " + mVf.toString() + " " + mVf.getClass()); 
            ValueFunction vf = attr.getFn (key); 
            debug ("vf = " + vf.toString()); 
            if (mVf.equals (vf)) { 
                    debug ("mVf == vf"); 
            }
            else {
                debug ("mVf != vf"); 
            }
            debug ("in buildMap mkey = " + mkey + " mval = " + mval + " val = " + val);  
            if (mval.equals (val) && val.startsWith("?")) { 
                debug ("mapping:" + mval + " = " + val);
                retVal[count] = mkey;
                done = true; 
            }
            else if (val.startsWith(mapper.getId())) {
                debug("ID's match!!");
                String attrVal = val.substring(mapper.getId().length()+1, val.length()); 
                debug ("attrVal == " + attrVal); 
                if (!mkey.equals(attrVal)) {
                        debug (mkey + " != " + attrVal); 
                        retVal [count] = null;}
                        else {
                            debug (mkey + " == " + attrVal); 
                            retVal [count] = mkey; 
                            done = true; 
                        }
                        }
            else {
                retVal [count] = null; }
            count2++;
         }
         count++;
        }  
        return (retVal); 
    }
    
    /**
        map the values of the attributes in this fact to the values in the 
        fact mapper. map is an array of ints such that map[1] = 3 indicates 
        that the attribute value[3] of mapper should be mapped to the value 1 of 
        this fact
        */
    public synchronized void doMap (Fact mapper, String [] tmap) { 
        Core.DEBUG(1,"in doMap in Fact\n"); 
        AttributeList mapperAttr = mapper.getAttributeList(); 
        debug ("attr == " + attr.toString()); 
        debug ("mapperAttr = " + mapperAttr.toString()); 
        AttributeList newAttr = attr.duplicate("newAttr", new GenSym("temp")); // was attr
        
        debug ("newAttr == " + newAttr.toString()); 
        Enumeration keys = attr.keys(); 
        int count = 0; 
        while (keys.hasMoreElements()){ 
            String key = (String) keys.nextElement(); 
            debug("int doMap key = " + key); 
            ValueFunction val = attr.getFn(key); 
            if (tmap [count] != null) { 
                debug("altering value"); 
                newAttr.remove(key); 
                debug ("tmap = " + tmap [count]); 
                debug("count = "  + String.valueOf(count)); 
           //     debug ("mapper.getVal(count-1) = " + mapper.getVal(count-1).toString()); 
                debug ("mapper.getFn(tmap[count]) = " + mapper.getFn(tmap[count])); 
                newAttr.put(key,mapper.getFn(tmap[count])); // simon adds -1 // and removes it - what happens?
            }
            else 
                newAttr.put (key, val); 
            count++; 
        }
            attr = newAttr;
               
        }
            
            
            
   public synchronized ValueFunction getVal(int pos) { 
     Enumeration keys = attr.keys(); 
     int count = 0; 
     if (pos < attr.size()) { 
       String key = null; 
       for (count = 0; count <= pos; count++) { 
            key = (String)keys.nextElement(); 
       }
       debug ("key = " + key); 
       
       return  attr.getFn(key); }
       else 
       debug (String.valueOf(attr.size())); 
       return null; 
     }
            

    public void map (Fact toMap) { 
        String [] mapval = buildMap (toMap); 
        doMap (toMap, mapval); 
    }


    private void debug (String str) { 
       //System.out.println("fact>> " + str); 
    }


    public static void main (String argv[]) { 
        ;
     
        
    }

}
