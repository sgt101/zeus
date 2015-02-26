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
 * @(#)DbProxy.java 1.00
 */

package zeus.agents;

import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;


/**
 * The implementation of the Zeus Database Proxy agent, which provides
 * {@link Visualiser} agents with a means of persistently storing agent
 * session information. A Database Proxy can serve as an interface to a
 * 3rd party database using the {@link PersistentStore} interface, or it can
 * store the session information its own ASCII file format ({@link FlatFile}. <p>
 *
 * It is unlikely that users will need to change or call directly any of the
 * methods of this class.
 */

public class DbProxy extends BasicAgent
{
   protected PersistentStore proxy = null;

   public DbProxy(String name, Vector nameservers, PersistentStore proxy) {
      super(SystemProps.getProperty("agent.names.dbProxy"),name, nameservers);
      Assert.notNull(proxy);
      this.proxy = proxy;

      String[][] pattern = {
         {"type", "request", "reply-with", "(\\w)(.*)", "content", "\\Adb_create(\\s+)(.*)\\Z"},
         {"type", "request", "reply-with", "(\\w)(.*)", "content", "\\Adb_delete(\\s+)(.*)\\Z"},
         {"type", "request", "reply-with", "(\\w)(.*)", "content", "\\Adb_purge(\\s+)(.*)\\Z"},
         {"type", "request", "reply-with", "(\\w)(.*)", "content", "\\Adb_open(\\s+)(.*)\\Z"},
         {"type", "request", "reply-with", "(\\w)(.*)", "content", "\\Adb_save(\\s+)(.*)\\Z"},
         {"type", "request", "reply-with", "(\\w)(.*)", "content", "\\Adb_close(\\s+)(\\w)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_next(\\s+)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_prior(\\s+)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_first(\\s+)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_last(\\s+)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_sessions(\\s+)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_list(\\s+)(.*)\\Z"},
         {"type", "query-ref", "reply-with", "(\\w)(.*)", "content", "\\Adb_count(\\s+)(.*)\\Z"}
      };

      MsgHandler handler = context.MsgHandler();
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[0], this, "db_create"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[1], this, "db_delete"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[2], this, "db_purge"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[3], this, "db_open"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[4], this, "db_save"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[5], this, "db_close"));

      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[6],  this, "db_next"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[7],  this, "db_prior"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[8],  this, "db_first"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[9],  this, "db_last"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[10], this, "db_sessions"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[11], this, "db_list"));
      handler.addRule(new MessageRuleImpl(context.newId("Rule"), pattern[12], this, "db_count"));
   }

   public void sendMsg(Performative msg) {
      context.MailBox().sendMsg(msg);
   }

   public void db_create(Performative msg) {
      /**
         create a new database session:
         message content contains: "db_create sessionType sessionId accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.createSession(reply_with,msg.getSender(),hd.data(0),hd.data(1),hd.data(2));
   }
   public void db_delete(Performative msg) {
      /**
         delete database session:
         message content contains: "db_delete sessionType sessionId"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.deleteSession(reply_with,msg.getSender(),hd.data(0),hd.data(1));
   }
   public void db_purge(Performative msg) {
      /**
         purge database; i.e. clear all sessions
         message content contains: "db_purge sessionType"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.deleteSessionType(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_open(Performative msg) {
      /**
         open a database session:
         message content contains: "db_open sessionType sessionId accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.openSession(reply_with,msg.getSender(),hd.data(0),hd.data(1),hd.data(2));
   }
   public void db_save(Performative msg) {
      /**
         save an entry
         message content contains: "db_save accessKey object"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.saveRecord(reply_with,msg.getSender(),hd.data(0),hd.rest(0));
   }
   public void db_close(Performative msg) {
      /**
         close a database session:
         message content contains: "db_close accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.closeSession(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_next(Performative msg) {
      /**
         next database record:
         message content contains: "db_next accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.nextRecord(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_prior(Performative msg) {
      /**
         prior database record:
         message content contains: "db_prior accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.priorRecord(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_first(Performative msg) {
      /**
         first database record:
         message content contains: "db_first accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.beginSession(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_last(Performative msg) {
      /**
         last database record:
         message content contains: "db_last accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.endSession(reply_with,msg.getSender(),hd.data(0));
   }

   public void db_sessions(Performative msg) {
      /**
         list all sessions stored in database
         message content contains: "db_sessions sessionType"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.getAllSessions(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_list(Performative msg) {
      /**
         list all agents referred to in the stored session
         message content contains: "db_list accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.getAgents(reply_with,msg.getSender(),hd.data(0));
   }
   public void db_count(Performative msg) {
      /**
         return count of records in session
         message content contains: "db_count accessKey"
     */
      MsgContentHandler hd = new MsgContentHandler(msg.getContent());
      String reply_with = msg.getReplyWith();
      proxy.countRecords(reply_with,msg.getSender(),hd.data(0));
   }

   protected static void version() {
      System.err.println("DbProxy version: " +
                         SystemProps.getProperty("version.id"));
      System.exit(0);
   }
   protected static void usage() {
      System.err.println("Usage: java zeus.agents.DbProxy <name> " +
                         "-p <db_classpath> -s <dns_file> " +
			 "[-gui ViewerProg] [-e ExternalProg] " +
			 "[-debug] [-v] [-h]");
      System.exit(0);
   }

   public static void main(String[] arg) {
      Vector nameservers = null;
      String dns_file = null;
      String path = null;
      DbProxy proxy = null;
      PersistentStore store = null;
      String gui = null;
      String external = null;
      String accessLevel = null;

      if ( arg.length < 5 )  usage();
      else
         for( int i = 1; i < arg.length; i++ ) {
            if ( arg[i].equals("-s") && ++i < arg.length )
               dns_file = arg[i];
            else if ( arg[i].equals("-p") && ++i < arg.length )
               path = arg[i];
            else if ( arg[i].equals("-e") && ++i < arg.length )
               external = arg[i];
            else if ( arg[i].equals("-gui") && ++i < arg.length )
              gui = arg[i];
            else if ( arg[i].equals("-debug") ) {
	      Core.debug = true;
              Core.setDebuggerOutputFile(arg[0] + ".log");
            }
	    else if ( arg[i].equals("-h") )
	      usage();
	    else if ( arg[i].equals("-v") )
	      version();
            else if ( arg[i].equals("-access") && ++i < arg.length)
	      accessLevel = arg[i];
            else
              usage();
         }

      if ( path == null ) {
         System.err.println("Database path must be specified with -p option");
         usage();
      }
      if ( dns_file == null ) {
         System.err.println("Domain nameserver file must be specified with -s option");
         usage();
      }

      try {
         nameservers = ZeusParser.addressList(new FileInputStream(dns_file));
	 if ( nameservers == null || nameservers.isEmpty() )
	    throw new IOException();

         Class c = Class.forName(path);
         store = (PersistentStore) c.newInstance();
         proxy = new DbProxy(arg[0],nameservers,store);
         store.setProxy(proxy);
         if (accessLevel != null ) {
           if (accessLevel.equals("true"))
             store.setAccess(true);
           else if (accessLevel.equals("false"))
            store.setAccess(false);
           else
            throw new Exception("Error specifying access level");
         }

         AgentContext context = proxy.getAgentContext();

         if ( gui != null ) {
            c = Class.forName(gui);
            BasicAgentUI ui = (BasicAgentUI) c.newInstance();
            context.set(ui);
            ui.set(context);
         }

         if ( external != null ) {
            c = Class.forName(external);
            ZeusExternal user_prog = (ZeusExternal)c.newInstance();
            context.set(user_prog);
            user_prog.exec(proxy.getAgentContext());
         }
      }
      catch (Exception e) {
         System.err.println(e);
         System.exit(0);
      }
   }
}
