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
  *  this class is used to generate the script for the agent nameserver
  *  Change Log
  *  ----------
  *  26/6/01 - removed the "java" bit as this is now done by the scriptwriter
    */
public class NameserverInfo extends GenerationInfo {
   protected static int count = 0;
   static final String DEFAULT_DNS =
      SystemProps.getProperty("nameserver.dns.default");
   static final String DEFAULT_PERIOD =
      SystemProps.getProperty("nameserver.period.default");

   public boolean is_root;
   public boolean has_gui;
   public String zeus_external = null;
   public String address_output_file;
   public String time_grain;
   public String dns_file;

   public NameserverInfo() {
      this(NAMESERVER + (count++));
   }

   public NameserverInfo(String name) {
      this.name = name;
      is_root = (count == 1);
      has_gui = false;
      zeus_external = null;
      if ( is_root ) {
         address_output_file = DEFAULT_DNS;
         time_grain = DEFAULT_PERIOD;
         dns_file = null;
      }
      else {
         address_output_file = null;
         time_grain = null;
         dns_file = DEFAULT_DNS;
      }
   }
   public NameserverInfo(String name, boolean is_root) {
      this.name = name;
      this.is_root = is_root;
      has_gui = false;
      zeus_external = null;
      if ( is_root ) {
         address_output_file = DEFAULT_DNS;
         time_grain = DEFAULT_PERIOD;
         dns_file = null;
      }
      else {
         address_output_file = null;
         time_grain = null;
         dns_file = DEFAULT_DNS;
      }
   }


   public String[] summarize() {
      String[] out = new String[4];
      out[NAME] = name;
      out[TYPE] = NAMESERVER;
      out[ID]   = id;
        // simon removed java 26/6/01
      out[COMMAND] = "zeus.agents.ANServer " + name;

      if ( dns_file != null )
         out[COMMAND] += " -s " + dns_file;
      if ( is_root )
         out[COMMAND] += " -t " + time_grain;
      if ( address_output_file != null )
         out[COMMAND] += " -f " + address_output_file;
      if ( has_gui )
         out[COMMAND] += " -gui zeus.agentviewer.BasicViewer";
      if ( zeus_external != null )
         out[COMMAND] += " -e " + zeus_external;

      return out;
   }
   public String isValid() {
      String error = null;
      if ( !is_root && dns_file == null )
         error = "Nameserver " + name + ": (not root) and domain nameserver file not specified\n";

      return error;
   }

   public String toString() {
      String s = "";
      s += "(:name " + name;
      s += " :is_root " + is_root;
      s += " :has_gui " + has_gui;
      if ( host != null && !host.equals(LOCALHOST) )
         s += " :host \"" + Misc.escape(host) + "\"";
      if ( zeus_external != null )
         s += " :zeus_external \"" + Misc.escape(zeus_external) + "\"";
      if ( dns_file != null )
         s += " :dns_file \"" + Misc.escape(dns_file) + "\"";
      if ( address_output_file != null )
         s += " :address_output_file \"" + Misc.escape(address_output_file) + "\"";
      if ( time_grain != null )
         s += " :time_grain " + time_grain;
      s += ")";
      return s;
   }

   public String pprint(int sp) {
      String tabs = Misc.spaces(sp);
      String eol  = "\n" + tabs;

      String s = tabs;
      s += "(:name " + name + eol;
      s += " :is_root " + is_root + eol;
      s += " :has_gui " + has_gui + eol;
      if ( host != null && !host.equals(LOCALHOST) )
         s += " :host \"" + Misc.escape(host) + "\"" + eol;
      if ( zeus_external != null )
         s += " :zeus_external \"" + Misc.escape(zeus_external) + "\"" + eol;
      if ( dns_file != null )
         s += " :dns_file \"" + Misc.escape(dns_file) + "\"" + eol;
      if ( address_output_file != null )
         s += " :address_output_file \"" + Misc.escape(address_output_file) + "\"" + eol;
      if ( time_grain != null )
         s += " :time_grain " + time_grain + eol;
      s += ")";
      return s;
   }
}
