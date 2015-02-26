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



package zeus.concepts.fn;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

public class FieldFn extends ValueFunction {
   protected String arg = null;

   public FieldFn(String arg) {
      super(FIELD,8);
      this.arg = arg;
   }
   public ValueFunction mirror() {
      return new FieldFn(arg);
   }
   public String toString() {
      return arg;
   }
   Object getArg(int position) {
      if (position != 0) throw new ArrayIndexOutOfBoundsException(position);
      return arg;
   }
   String getBase() {
      int index = arg.indexOf(Fact.A_CHR);
      // ignore preceding Fact.V_CHR or Fact.F_CHR
      return arg.substring(1,index); 
   }
   public ValueFunction duplicate(DuplicationTable table) {
      // Example ?man12.name.firstname => ?var101.name.firstname.

      int index = arg.indexOf(Fact.A_CHR);
      // ignore preceding Fact.V_CHR or Fact.F_CHR
      String base =  arg.substring(1,index);

      base = table.getRef(base);

      String result = "" + arg.charAt(0) + base +
                      arg.substring(index,arg.length());

      return new FieldFn(result);
   }
   public boolean references(ValueFunction var) {
      return this.equals(var);
   }
   public Vector variables() {
      Vector out = new Vector();
      if ( arg.charAt(0) == Fact.V_CHR ) {
         String s = "" + arg.charAt(0) + getBase();
         out.addElement(new VarFn(s));
      }
      return out;
   }
   public boolean isDeterminate() {
      return false;
   }
   public ValueFunction resolve(ResolutionContext c, Bindings b) {
      StringTokenizer st = new StringTokenizer(arg,Fact.A_STR);
      String attribute, fid;
      ValueFunction value;
      Fact f1;

      fid = st.nextToken();
      f1 = c.lookUp(fid);
      if ( f1 == null ) return this;

      while( st.hasMoreTokens() ) {
         attribute = st.nextToken();
         value = f1.getFn(attribute);
         value = value.resolve(c,b);
         if ( !st.hasMoreTokens() )
            return b.lookUp(value);
         else {
            if ( value.isDeterminate() ) {
               value = value.evaluationFn();
               switch( value.getID() ) {
                  case TYPE:
                       fid = value.toString();
                       f1 = c.lookUp(fid);
                       break;

                  default:
                       throw new IllegalArgumentException("Illegal " + 
                         " attribute \'" + value + "\' in \'" + this + "\'");
               }
            }
            else 
               return b.lookUp(this);
         }
      }
      throw new IllegalArgumentException("Cannot resolve \'" + this + "\'");
   }
   public boolean equals(Object any) {
      if ( !(any instanceof FieldFn) ) return false;
      FieldFn fn = (FieldFn)any;
      return arg.equals((String)fn.getArg());
   }
   ValueFunction unify(ValueFunction fn, Bindings b) {
      switch( fn.getID() ) {
         case FIELD:
         case VECT:
         case BOOL:
         case DATE:
         case ID:
         case INT:
         case REAL:
         case TYPE:
         case TIME:
              return new AndFn(this,fn);

         default:
              throw new IllegalArgumentException("Cannot unify \'" + fn +
                 "\' with \'" + this + "\'");
      }
   }

}
