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

import java.io.*;
import java.util.*;
import zeus.util.*;


public class ProtocolInfo {
   public static final String INITIATOR  = "Initiator";
   public static final String RESPONDENT = "Respondent";

   protected String name;
   protected String type;
   protected Vector constraints = new Vector();
   
   public ProtocolInfo() { 
   }

   public ProtocolInfo(String name, String type, Fact fact) {
      Assert.notNull(name);
      Assert.notFalse(type.equals(INITIATOR) || type.equals(RESPONDENT));
      this.name = name;
      this.type = type;

      String strategy = (type.equals(RESPONDENT))
                        ? StrategyInfo.DEFAULT_RESPONDENT_STRATEGY
                        : StrategyInfo.DEFAULT_INITIATOR_STRATEGY;

      StrategyInfo info = new StrategyInfo(fact,strategy);
      constraints.addElement(info);
   }

   public ProtocolInfo(String name, String type, StrategyInfo[] input) {
      Assert.notNull(name);
      Assert.notFalse(type.equals(INITIATOR) || type.equals(RESPONDENT));
      this.name = name;
      this.type = type;
      setConstraints(input);
   }
   public ProtocolInfo(String name, String type, Vector input) {
      Assert.notNull(name);
      Assert.notFalse(type.equals(INITIATOR) || type.equals(RESPONDENT));
      this.name = name;
      this.type = type;
      setConstraints(input);
   }

   public ProtocolInfo(ProtocolInfo info) {
      name = info.getName();
      type = info.getType();
      setConstraints(info.getConstraints());
   }

   public ProtocolInfo duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public ProtocolInfo duplicate(DuplicationTable table) {
      Vector data = new Vector();
      StrategyInfo info;
      for(int i = 0; i < constraints.size(); i++ ) {
         info = (StrategyInfo)constraints.elementAt(i);
         data.addElement(info.duplicate(table));
      }
      return new ProtocolInfo(name,type,data);
   }

   public String getName() {
      return name;
   }
   public String getType() {
      return type;
   }

   public StrategyInfo[] getConstraints() {
      StrategyInfo[] info = new StrategyInfo[constraints.size()];
      for(int i = 0; i < constraints.size(); i++ )
         info[i] = new StrategyInfo((StrategyInfo)constraints.elementAt(i));
      return info;
   }

   public void setConstraints(StrategyInfo[] input) {
      constraints.removeAllElements();
      if ( input == null ) return;
      for(int i = 0; i < input.length; i++ )
         constraints.addElement(new StrategyInfo(input[i]));
   }
   public void setConstraints(Vector input) {
      constraints.removeAllElements();
      if ( input == null ) return;
      for(int i = 0; i < input.size(); i++ )
         constraints.addElement(new StrategyInfo((StrategyInfo)input.elementAt(i)));
   }

   public boolean resolve(Bindings b) {
      boolean status = true;
      StrategyInfo info;
      for(int i = 0; status && i < constraints.size(); i++ ) {
         info = (StrategyInfo)constraints.elementAt(i);
         status &= info.resolve(b);
      }
      return status;
   }

   public String toString() {
      String s = "(:name \"" + name + "\" :type " + type;
      s += " " + ":constraints (" + Misc.concat(constraints) + ")";
      s +=  ")";
      return s;
   }

   public String pprint() {
      return pprint(0);
   }
   public String pprint(int sp) {
      String prefix, suffix;
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs + " ";

      String s = tabs + "(:name \"" + name + "\"" + eol + ":type " + type + eol;
      prefix = ":constraints ";
      suffix = Misc.spaces(1 + sp + prefix.length());
      s += prefix + "(";
      for(int i = 0; i < constraints.size(); i++ )
         s += ((StrategyInfo)constraints.elementAt(i)).pprint(1+suffix.length()) +
              "\n" + suffix + " ";
      s = s.trim() + "\n" + suffix + ")" + eol;
      return s.trim() + "\n" + tabs + ")";
   }
}
