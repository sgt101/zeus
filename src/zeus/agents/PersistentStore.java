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
 * @(#)PersistentStore.java 1.00
 */

package zeus.agents;

import java.util.*;
import zeus.util.*;
import zeus.concepts.Performative;

/**
 * <P> This abstract class provides an interface to storage platforms so
 * that messaging data (i.e. communication sessions among agents) may be
 * stored for future playback and manipulation.
 * It acts a bridge between a ZEUS database proxy (i.e. DbProxy) and
 * and a persistent storage mechanism (e.g. a flat file or a database such
 * as Oracle). Typically a ZEUS Visualiser agent talks to a DbProxy which
 * in turn forwards the requests to subclasses of this abstract class. A
 * class that extends this abstract class  should provide implementations
 * for following methods:
 * <pre>
 *  public  abstract void createSession(String replyKey, String agent,
 *                  String sessionType, String sessionId, String accessKey);
 *  public abstract void deleteSession(String replyKey, String agent,
 *                                     String sessionType, String sessionId);
 *  public abstract void getAllSessions(String replyKey, String agent,
 *                                        String sessionType);
 *  public abstract void deleteSessionType(String replyKey, String agent,
 *                                         String sessionType);
 *  public abstract void openSession(String replyKey, String agent,
 *                    String sessionType, String sessionId, String accessKey);
 *
 *  public abstract void saveRecord(String replyKey, String agent,
 *                                  String accessKey, String record);
 *  public  void closeSession(String replyKey,String agent,String accessKey);
 *  public abstract void nextRecord(String replyKey,String agent,String accessKey);
 *  public abstract void priorRecord(String replyKey,String agent,String accessKey);
 *  public abstract void beginSession(String replyKey,String agent,String accessKey);
 *  public abstract void endSession(String replyKey,String agent,String accessKey);
 *  public abstract void getAgents(String replyKey,String agent,String accessKey);
 *  public abstract void countRecords(String replyKey,String agent,String accessKey);
 *
 *  </pre>
 * <br>
 * The methods have all or some of the following arguments:
 * <br> String <B> replyKey </B><I> Recipient's (i.e.the ZEUS Visualiser
 * agent) conversation key.</I>
 * <br> String <B> agent </B> <I> The name of the ZEUS Visualiser agent who
 * wants the communication sessions saved for future playback. </I>
 * <br> String <B> sessionType </B> <I> The type of information being saved
 * for
 * future playback (i.e. the tool within which the communication session is
 * taken place. Tools can be one of the following: Society
 * Viewer, Report Viewer, Statistics Viewer, Remote Viewer and Control Tool
 * Viewer.  </I>
 * <br> String <B>sessionId</B> <I> A unique identifier (typically the name
 * of the persistent storage i.e. flat file or database) representing a
 * communication session.</I>
 * <br> String <B> accessKey </B><I> A handle representing an agent
 * and session id used by subclasses of this abstract class to
 * access the persistent storage. </I>
 *<br>
 * Each implemented method should return a Zeus Performative object (<I>
 * using proxy.sendMsg(msg) where msg is a Performative object </I> ) by
 * specifying whether the operation failed or succeeds. If the operation
 * succeeds then instantiate a Performative class with <I> "inform" </I>
 * or <I> "failure" </I> if it failed. Instantiating a Performative class
 * should follow this format:
 * <br> Performative msg = new Performative("failure");
 * <br> msg.setReceiver(agent);
 * <br> msg.setInReplyTo(replyKey);
 * <br> msg.setContent("reason for failure or success");
 * <br> proxy.sendMsg(msg);
 */

/* A schematic diagram of subclasses of this abstract is as follows.
   A persistent storage has session types. Each session types has sessions
   associated with it. Conceptually a session is akin to a table in a
   database.


                        Persistent Storage (DB/Flat file ect.)
                                  |
                                  |
                                  |
                                  |
     --------------------------------------------------------------
     |                            |                               |
     |                            |                               |
     |                            |                               |
     |                            |                               |
  SessionType (VisualiserTool)   Society Tool              Remote Viewer
                                  |
                                  |
                                  |
                                  |
                         ---------------------
                         |        |          |
                         |        |          |
                         |        |          |
               Session1 (Tables)  Session2   Session3

*/

//--------------------------------------------------------------------------
public abstract class PersistentStore {
  protected DbProxy proxy = null;
  private boolean verifyAccess = true;
  private OrderedHashtable knownAgents = new OrderedHashtable();

//--------------------------------------------------------------------------

  /**
   * Sets the proxy.
   */
  public void setProxy(DbProxy proxy) {
     this.proxy = proxy;
  }

  /**
   * Sets access level.
   */
 public void setAccess(boolean access){
   verifyAccess = access;
 }

//--------------------------------------------------------------------------

  /**
    * Its purpose is to create a new  session (i.e. a flat file or database
    * table).
    * Returns <I> done </I> as the content of a Performative object if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    * <br> If the session exists open it for appending and return <I> done.
    * </I> <br> If the session doesn't exit create it for writing and return
    * <I> done.</I>
    * <br> If aforementioned operations fails return <I> failure. </I>
    */
  // Hint: A table can be named using a concatenation of the sessionType and
  // the sessionId.
//--------------------------------------------------------------------------
  public  abstract void createSession(String replyKey, String agent,
     String sessionType, String sessionId, String accessKey);

//--------------------------------------------------------------------------
   /**
    * Given a session type, delete the session type with the name
    * <I> sessionId.</I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void deleteSession(String replyKey, String agent,
     String sessionType, String sessionId);

//--------------------------------------------------------------------------
   /**
    * Given a session type, list all sessions (tables) associated with
    * that type.
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void getAllSessions(String replyKey, String agent,
     String sessionType);

//--------------------------------------------------------------------------
   /**
    * Delete a session type with its associated sessions (i.e. tables).
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void deleteSessionType(String replyKey, String agent,
     String sessionType);

//--------------------------------------------------------------------------
   /**
    * Given a session type, open the session with name <I> sessionId. </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void openSession(String replyKey, String agent,
     String sessionType, String sessionId, String accessKey);
//--------------------------------------------------------------------------
   /**
    * Save the record in a session identified by <I> accessKey. </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void saveRecord(String replyKey, String agent,
     String accessKey, String record);

//--------------------------------------------------------------------------
   /**
    * Close a session identified by <I> accessKey. </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void closeSession(String replyKey,String agent,String accessKey);

//--------------------------------------------------------------------------
   /**
    * Returns the next record in a  session identified by <I> accessKey.</I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */

  public abstract void nextRecord(String replyKey,String agent,String accessKey);

//--------------------------------------------------------------------------
   /**
    * Returns the previous record in a  session identified by <I> accessKey.
    * </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */

  public abstract void priorRecord(String replyKey,String agent,String accessKey);

//--------------------------------------------------------------------------
   /**
    * Go to the beginning of a  session identified by <I> accessKey.
    * </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void beginSession(String replyKey,String agent,String accessKey);

//--------------------------------------------------------------------------
   /**
    * Go to the end of  a  session identified by <I> accessKey.
    * </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void endSession(String replyKey,String agent,String accessKey);

//--------------------------------------------------------------------------
   /**
    * Returns all known agents associated with a given session identified by
    * <I> accessKey. </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void getAgents(String replyKey,String agent,String accessKey);

//--------------------------------------------------------------------------
   /**
    * Returns the number of records in a  session identified by <I>
    * accessKey. </I>
    * Returns <I> done </I> as the content of  a Performative object  if the
    * operation succeeds else return reasons for failure to the specified
    * agent with the replyKey as agent's conversation key.
    */
  public abstract void countRecords(String replyKey,String agent,String accessKey);
//--------------------------------------------------------------------------
   protected boolean isAccessible(){
      return verifyAccess;
   }

}
