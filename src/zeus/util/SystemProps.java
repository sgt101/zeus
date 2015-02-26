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




package zeus.util;

import zeus.concepts.*;
import java.util.*;
import java.io.*;


public class SystemProps {
   protected static Properties props = new Properties();
   protected static final String[] defaults = {
      "agent.language","java",
      "install.dir", System.getProperty("user.home"),
      "version.demo", "false",
      "version.id", "1.2.2",
      "application.gif", "gif_files.txt",
      "file.separator", ":",
      "debug.on", "false",
      "debug.level", "10",

      "HAP.address", "adastralcity.agentcities.net",
      "container.name", "defaultContainerCode",
      
      "system.protocols.respondent", "zeus.actors.graphs.ContractNetRespondent",
      "system.protocols.initiator", "zeus.actors.graphs.ContractNetInitiator",
//added by simon for test 17/05/01
    
      
// end add

      "friendly.name.zeus.actors.graphs.ContractNetRespondent",
         "Fipa-Contract-Net-Contractor",
      "friendly.name.zeus.actors.graphs.ContractNetInitiator",
         "Fipa-Contract-Net-Manager",
         
 // added by simon for test 17/5/01
 
//    "friendly.name.zeus.actors.graphs.EnglishAuctionInitiator", "FIPA-English-Auction-Manager", 
//    "friendly.name.zeus.actors.graphs.EnglishAuctionRespondent", "FIPA-English-Auction-Respondent",

      "system.strategy.initiator",
         "zeus.actors.graphs.LinearInitiatorEvaluator",
         //:zeus.actors.graphs.ExponentialInitiatorEvaluator",
      "system.strategy.respondent",
         "zeus.actors.graphs.LinearRespondentEvaluator",
         //:zeus.actors.graphs.ExponentialRespondentEvaluator",

      "system.strategy.initiator.default",
         "zeus.actors.graphs.DefaultInitiatorEvaluator",
      "system.strategy.respondent.default",
         "zeus.actors.graphs.DefaultRespondentEvaluator",

      "friendly.name.zeus.actors.graphs.DefaultInitiatorEvaluator",
         "Default-No-Negotiation",
      "friendly.name.zeus.actors.graphs.DefaultRespondentEvaluator",
         "Default-Fixed-Margin",

      "friendly.name.zeus.actors.graphs.LinearInitiatorEvaluator",
         "Linear-Growth",
      "friendly.name.zeus.actors.graphs.LinearRespondentEvaluator",
         "Linear-Decay",

   /*   "friendly.name.zeus.actors.graphs.ExponentialInitiatorEvaluator",
         "Exponential-Growth",
      "friendly.name.zeus.actors.graphs.ExponentialRespondentEvaluator",
         "Exponential-Decay",*/

      "share.plan", "true",
      "execute.earliest", "true",

      "registration.timeout", "3.0",
      "facilitator.timeout", "0.5",
      "address.timeout", "0.5",
      "accept.timeout", "1.5",
      "addressbook.refresh", "5.0",
      "facilitator.refresh", "5.0",
      "replan.period", "2.0",

      "facilitator.period.default", "5.0",
      "nameserver.period.default", "0.5",
      "nameserver.dns.default", "dns.db",
      "agent.names.nameserver", "Nameserver",
      "agent.names.facilitator", "Facilitator",
      "agent.names.visualiser", "Visualiser",
      "agent.names.dbProxy", "DbProxy",
      "agent.names.agent", "Agent",


      "planner.processors.min", "1",
      "planner.processors.max", "10",
      "planner.length.min", "10",
      "planner.length.max", "1000",
      "planner.doublebooking.min", "0",
      "planner.doublebooking.max", "100",

      "agent.default.name", "DefaultAgentName",
      "agent.default.class", "ZeusAgent",
      "agent.default.planner.processors", "1",
      "agent.default.planner.length", "20",
      "agent.default.planner.doublebooking", "0",

      "task.default.name", "DefaultTaskName",
      "task.default.time", "1",
      "task.default.cost", "0",

      "system.organisation.relations.default", "peer",
      "# Note: the ordering of the relations is important",
      "# Comment terminator",
      "system.organisation.relations", "superior:subordinate:peer:coworker",
      "ior.dir", "IOR_",
      "num.connections", "20",
      "http_root","E:\\webapps\\ROOT\\",
      "TradeHouseName","GateAgent",
      "TradeHousePlatform", "agntcity.barcelona.agentcities.net:2099/JADE",
      "service_platform", "http://193.113.27.14",
      "out_log","FIPA_out_log.html",
      "in_log","FIPA_in_log.html"
   };

   static {
      String home_dir = System.getProperty("user.home");
      String fsep = File.separator;

      if ( !home_dir.endsWith(fsep) ) home_dir += fsep;
 
      String filename = home_dir + ".zeus.prp";

      File file = new File(filename);
      try {
         props.load(new FileInputStream(file));
      }
      catch(Exception e) {
         e.printStackTrace();
      }

      for( int i = 0; i < defaults.length; i += 2 ) {
         if ( props.get(defaults[i]) == null )
            props.put(defaults[i], defaults[i+1]);
      }

      String zeus_dir = props.getProperty("install.dir");
      if ( !zeus_dir.endsWith(fsep) ) zeus_dir += fsep;

      file = new File(zeus_dir);
      if ( !file.exists() ) {
         System.err.println("Improper install.dir specified in " +
                            "'zeus.prp' properties file.\nExiting...");
         System.exit(0);
      }

      props.put("zeus.dir",zeus_dir);

      String gif_dir = zeus_dir + "gifs" + fsep;
      props.put("gif.dir",gif_dir);
   }

   public static String getProperty(String key) {
      return props.getProperty(key);
   }
   public static String getProperty(String key, String defaultValue) {
      return props.getProperty(key,defaultValue);
   }
   public static boolean getState(String key) {
      String s = props.getProperty(key);
      return (s != null) ? (Boolean.valueOf(s)).booleanValue() : false;
   }
   public static boolean getState(String key, boolean defaultValue) {
      String s = props.getProperty(key);
      return (s != null) ? (Boolean.valueOf(s)).booleanValue() : defaultValue;
   }
   public static double getDouble(String key) {
      String s = props.getProperty(key);
      try {
         return (s != null) ? (Double.valueOf(s)).doubleValue() : 0.0;
      }
      catch(NumberFormatException e) {
         System.err.println("Illegal format for " + key + " entry in " +
                            "properties database -- number format expected");
      }
      return 0.0;
   }
   public static double getDouble(String key, double defaultValue) {
      String s = props.getProperty(key);
      try {
         return (s != null) ? (Double.valueOf(s)).doubleValue() : defaultValue;
      }
      catch(NumberFormatException e) {
         System.err.println("Illegal format for " + key + " entry in " +
                            "properties database -- number format expected");
      }
      return defaultValue;
   }
   public static int getInt(String key) {
      String s = props.getProperty(key);
      try {
         return (s != null) ? (Integer.valueOf(s)).intValue() : 0;
      }
      catch(NumberFormatException e) {
         System.err.println("Illegal format for " + key + " entry in " +
                            "properties database -- number format expected");
      }
      return 0;
   }
   public static int getInt(String key, int defaultValue) {
      String s = props.getProperty(key);
      try {
         return (s != null) ? (Integer.valueOf(s)).intValue() : defaultValue;
      }
      catch(NumberFormatException e) {
         System.err.println("Illegal format for " + key + " entry in " +
                            "properties database -- number format expected");
      }
      return defaultValue;
   }
   public static long getLong(String key) {
      String s = props.getProperty(key);
      try {
         return (s != null) ? (Long.valueOf(s)).intValue() : 0;
      }
      catch(NumberFormatException e) {
         System.err.println("Illegal format for " + key + " entry in " +
                            "properties database -- number format expected");
      }
      return 0;
   }
   public static long getLong(String key, long defaultValue) {
      String s = props.getProperty(key);
      try {
         return (s != null) ? (Long.valueOf(s)).longValue() : defaultValue;
      }
      catch(NumberFormatException e) {
         System.err.println("Illegal format for " + key + " entry in " +
                            "properties database -- number format expected");
      }
      return defaultValue;
   }
   public static void list(PrintStream out) {
      props.list(out);
   }
   public static void list(PrintWriter out) {
      props.list(out);
   }
   public static synchronized void load(InputStream in) throws IOException {
      props.load(in);
   }
   public static Enumeration propertyNames() {
      return props.propertyNames();
   }
   public static synchronized void save(OutputStream out, String header) {
      props.save(out,header);
   }

   public static void main(String[] args) {
      System.out.println(SystemProps.getProperty("application.gif"));
   }
    
    /** 
        this is a patent hack. I am very, very sorry, 
        @author Simon Thompson
        @since 1.1 (hopefully removed later, in the perfect world...
        */
    public static boolean FIPA_IMPLEMENTED_TIGHTLY = false ;


    /** 
        transports is used to allow configuration of transport addresses at 
        runtime
        */
    private static StringHashtable transports = new StringHashtable(); 
    
    public static TransportConfig getTransport (String name) {
        System.out.println("trying to get transport " + name); 
        
        TransportConfig retVal = (TransportConfig) transports.getForString(name); 
        System.out.println(retVal); 
        return retVal; 
    }
    
    public static void setTransport (String name, TransportConfig conf) { 
        transports.put (name,conf);    
    }
    
    public static void setTransports (StringHashtable trans) { 
        transports = trans;
    }
    
}

