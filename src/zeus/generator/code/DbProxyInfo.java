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

public class DbProxyInfo extends GenerationInfo {
   protected static int count = 0;

   public boolean has_gui = false;
   public String zeus_external = null;
   public String path = null;
   public String dns_file = NameserverInfo.DEFAULT_DNS;

   public DbProxyInfo() {
      name = DBPROXY + (count++);
   }
   public DbProxyInfo(String name) {
      this.name = name;
   }

   public String[] summarize() {
      String[] out = new String[4];
      out[NAME] = name;
      out[TYPE] = DBPROXY;
      out[ID]   = id;

      out[COMMAND] = "java zeus.agents.DbProxy " + name +
                     " -p " + path + " -s " + dns_file;
      if ( has_gui )
         out[COMMAND] += " -gui zeus.agentviewer.BasicViewer";
      if ( zeus_external != null )
         out[COMMAND] += " -e " + zeus_external;

      return out;
   }
   public String isValid() {
      String error = "";
      if ( path == null )
         error += "DbProxy " + name + ": path not specified\n";
      if ( dns_file == null )
         error += "DbProxy " + name + ": domain nameserver file not specified\n";

      if ( error.equals("") ) return null;
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
      if ( path != null )
         s += " :path \"" + Misc.escape(path) + "\"";
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
      if ( path != null )
         s += " :path \"" + Misc.escape(path) + "\"" + eol;
      s += ")";
      return s;
   }
}
