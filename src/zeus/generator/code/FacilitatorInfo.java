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



package zeus.generator.code;

import zeus.util.*;
/** 
  * used to generate the script that runs the facilitator
  * Change log 
  * ----------
  * 26/06/01 removed java from command line
  */
public class FacilitatorInfo extends GenerationInfo {
   protected static int count = 0;
   static final String DEFAULT_PERIOD =
      SystemProps.getProperty("facilitator.period.default");

   public boolean has_gui = false;
   public String zeus_external = null;
   public String period = DEFAULT_PERIOD;
   public String dns_file = NameserverInfo.DEFAULT_DNS;
   public String ontology_file = null;

   public FacilitatorInfo(String file) {
      name = FACILITATOR + (count++);
      ontology_file = file;
   }
   public FacilitatorInfo(String name, String file) {
      this.name = name;
      ontology_file = file;
   }

   public String[] summarize() {
      String[] out = new String[4];
      out[NAME] = name;
      out[TYPE] = FACILITATOR;
      out[ID]   = id;

      out[COMMAND] = "zeus.agents.Facilitator " + name +
                     " -o " + ontology_file + " -s " + dns_file +
		     " -t " + period;
      if ( has_gui )
         out[COMMAND] += " -gui zeus.agentviewer.BasicViewer";
      if ( zeus_external != null )
         out[COMMAND] += " -e " + zeus_external;

      return out;
   }
   public String isValid() {
      String error = null;
      if ( dns_file == null )
         error = "Facilitator " + name + ": domain nameserver file not specified\n";

      return error;
   }

   public String toString() {
      double p = (Double.valueOf(period)).doubleValue();
      String p_str = Misc.decimalPlaces(p,2);

      String s = "";
      s += "(:name " + name;
      s += " :period " + p_str;
      s += " :has_gui " + has_gui;
      if ( host != null && !host.equals(LOCALHOST) )
         s += " :host \"" + Misc.escape(host) + "\"";
      if ( zeus_external != null )
         s += " :zeus_external \"" + Misc.escape(zeus_external) + "\"";
      if ( dns_file != null )
         s += " :dns_file \"" + Misc.escape(dns_file) + "\"";
      s += ")";
      return s;
   }

   public String pprint(int sp) {
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs;
      double p = (Double.valueOf(period)).doubleValue();
      String p_str = Misc.decimalPlaces(p,2);

      String s = tabs;
      s += "(:name " + name + eol;
      s += " :period " + p_str + eol;
      s += " :has_gui " + has_gui + eol;
      if ( host != null && !host.equals(LOCALHOST) )
         s += " :host \"" + Misc.escape(host) + "\"" + eol;
      if ( zeus_external != null )
         s += " :zeus_external \"" + Misc.escape(zeus_external) + "\"" + eol;
      if ( dns_file != null )
         s += " :dns_file \"" + Misc.escape(dns_file) + "\"" + eol;
      s += ")";
      return s;
   }
}
