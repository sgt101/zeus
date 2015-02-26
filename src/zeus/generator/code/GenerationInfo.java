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

import java.net.*;
import zeus.util.SystemProps;

public abstract class GenerationInfo {
   static final int NAME    = 0;
   static final int TYPE    = 1;
   static final int COMMAND = 2;
   static final int ID      = 3;

   static final String NAMESERVER  = SystemProps.getProperty("agent.names.nameserver");
   static final String FACILITATOR = SystemProps.getProperty("agent.names.facilitator");
   static final String VISUALISER  = SystemProps.getProperty("agent.names.visualiser");
   static final String DBPROXY     = SystemProps.getProperty("agent.names.dbProxy");
   static final String AGENT       = SystemProps.getProperty("agent.names.agent");
   static final String TASK        = "Task";

   static String LOCALHOST;

   static {
      try {
         LOCALHOST = InetAddress.getLocalHost().getHostAddress();
      }
      catch(Exception e) {
         LOCALHOST = null;
      }
   }

   protected static int IdCounter = 0;
   public String id;
   public String name;
   public String host = LOCALHOST;

   protected GenerationInfo() {
      id = "GenerationInfo" + (IdCounter++);
   }
   protected GenerationInfo(String id) {
      this.id = id;
   }

   public String pprint() {
      return pprint(0);
   }

   public abstract String[] summarize();
   public abstract String   isValid();
   public abstract String   pprint(int sp);
}
