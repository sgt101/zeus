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
  * used to generate the script that runs the visualiser
  * Change log 
  * ----------
  * 26/06/01 removed java from command line
  */
public class VisualiserInfo extends GenerationInfo {
   protected static int count = 0;

   public boolean has_gui = false;
   public String zeus_external = null;
   public String dns_file = NameserverInfo.DEFAULT_DNS;
   public String ontology_file = null;

   public VisualiserInfo(String file) {
      name = VISUALISER + (count++);
      ontology_file = file;
   }

   public VisualiserInfo(String name, String file) {
      this.name = name;
      ontology_file = file;
   }

    /** 
        removed java from command line (done in scriptwriter now) 
        Simon 26.06.01
        */
   public String[] summarize() {
      String[] out = new String[4];
      out[NAME] = name;
      out[TYPE] = VISUALISER;
      out[ID]   = id;
      out[COMMAND] = "zeus.visualiser.Visualiser " + name +
                     " -s " + dns_file;

      if ( ontology_file != null )
         out[COMMAND] += " -o " + ontology_file;
      if ( has_gui )
         out[COMMAND] += " -gui zeus.agentviewer.BasicViewer";
      if ( zeus_external != null )
         out[COMMAND] += " -e " + zeus_external;

      out[COMMAND] += " -quick";
      return out;
   }
   public String isValid() {
      String error = null;
      if (dns_file == null )
         error = "Visualiser " + name + ": domain nameserver file not specified\n";

      return error;
   }

   public String toString() {
      String s = "";
      s += "(:name " + name;
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

      String s = tabs;
      s += "(:name " + name + eol;
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
