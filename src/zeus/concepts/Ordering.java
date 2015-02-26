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
    Ordering is used in the task chaining process for PrimitiveTasks 
    ChangeLog
    ---------
    07 -06 -01 Simon adds () init for extensibility
    */
public class Ordering implements Reference {
   protected String lhs;
   protected String rhs;
   
   public Ordering () { 
   }
   
   public Ordering( String lhs, String rhs ) {
      Assert.notNull(lhs);
      Assert.notNull(rhs);

      this.lhs = lhs;
      this.rhs = rhs;
   }

   public Ordering( Ordering constr ) {
      lhs = constr.getLHS();
      rhs = constr.getRHS();
   }

   public String getLHS()       { return lhs; }
   public String getRHS()       { return rhs; }
   public String getId()        { return lhs+rhs;  }

   public void setLHS(String arg) {
      Assert.notNull(arg);
      lhs = arg;
   }

   public void setRHS(String arg) {
      Assert.notNull(arg);
      rhs = arg;
   }

   public boolean equals(Ordering constr ) {
      return lhs.equals(constr.getLHS()) &&
             rhs.equals(constr.getRHS());
   }

   public String toString() {
      return( "(" +
               ":lhs " + lhs + " " +
               ":rhs " + rhs +
              ")"
            );
   }

   public String pprint() {
      return pprint(0);
   }
   public String pprint(int sp) {
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = "(:lhs " + lhs + eol +
                  ":rhs " + rhs + eol;
      return s.trim() + "\n" + tabs + ")";
   }

   public boolean references( String id ) {
      StringTokenizer st;
      String token;

      st = new StringTokenizer(lhs,".");
      token = st.nextToken();
      if ( token.equals(id) ) return true;
      return false;
   }

   public Ordering duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public Ordering duplicate(DuplicationTable table) {
      ValueFunction lhs_fn = new VarFn(lhs);
      ValueFunction rhs_fn = new VarFn(rhs);
      String _lhs = (lhs_fn.duplicate(table)).toString();
      String _rhs = (rhs_fn.duplicate(table)).toString();
      return new Ordering(_lhs,_rhs);
   }
}