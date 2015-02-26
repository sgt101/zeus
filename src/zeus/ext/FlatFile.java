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



package zeus.ext;

import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.Performative;
import zeus.concepts.ZeusParser;
import zeus.agents.PersistentStore;
import zeus.concepts.ZeusParser;


public class FlatFile extends PersistentStore {

     protected Hashtable ht = new Hashtable();
     String fsep = System.getProperty("file.separator");
     String ROOT =  System.getProperty("user.dir") +  fsep + "dbs" + fsep;
     String NEWLINE = System.getProperty("line.separator");
     String NEWRECORD = NEWLINE + "_:RECORD";
     String RECORD_TAG = "_:RECORD";
     String PREFIX = "ps_";


//--------------------------------------------------------------------------
   public void createSession(String replyKey, String agent,
                   String sessionType, String sessionId, String accessKey){

       Performative msg;
       String type;
       try{
	 if (isAccessible())
	   type =  ROOT + sessionType;
         else
	   type = ROOT + PREFIX+agent + fsep + sessionType;

         File folder = new File(type);
         if(!folder.exists())  {
           if (!folder.mkdirs()) throw new SecurityException("Couldn't create sessionType");
         }

         msg = new Performative("inform");
         msg.setReceiver(agent);
         msg.setInReplyTo(replyKey);

	 File file = new File(type + fsep + sessionId);
	 if (file.exists())
           msg.setContent(sessionType + " created by "+ agent + " already exists... appending.");
         else
          msg.setContent("done");

	 RandomAccessFile fp = new RandomAccessFile(file,"rw");
	 ht.put(accessKey, fp);

       }
       catch(IOException ioe) {
         ioe.printStackTrace();
	 msg = new Performative("failure");
         msg.setReceiver(agent);
         msg.setInReplyTo(replyKey);
         msg.setContent(agent + " couldn't create " + sessionId);
       }
       catch(SecurityException  se) {
         se.printStackTrace();
         msg = new Performative("failure");
         msg.setReceiver(agent);
         msg.setInReplyTo(replyKey);
         msg.setContent("Access to create denied by security manager");
       }

       proxy.sendMsg(msg);
   }
//--------------------------------------------------------------------------
     public void deleteSession(String replyKey, String agent,
                                     String sessionType, String sessionId) {

        Performative msg=null;
        String fname;

        if (isAccessible())
           fname = ROOT +  sessionType + fsep +
	               sessionId;
         else
           fname = ROOT + PREFIX+agent + fsep + sessionType + fsep +
	               sessionId;

	File file = new File(fname);
        try {
	  if (file.exists()) {

	   if (!file.delete())
             throw new IOException("Delete failed. Close session" + sessionId);
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent("done");
          }
          else {
           msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(sessionId + " doesn't exist, delete failed");
          }
        }
        catch (IOException ioe){
          ioe.printStackTrace();
        }

	proxy.sendMsg(msg);
     }
//--------------------------------------------------------------------------
     public void countRecords(String replyKey,String agent,String accessKey){
	 int recordCount=0;
         long currentPos = 0;
         Performative msg = null;


	 try {
            RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
	    currentPos = fp.getFilePointer();

	    if (fp == null)
              throw new IOException("Error counting records with access " + accessKey);

            recordCount = getCountUpTo(fp, fp.length());
            msg = new Performative("inform");
            msg.setReceiver(agent);
            msg.setContent(Integer.toString(recordCount));
            msg.setInReplyTo(replyKey);
            fp.seek(currentPos);
         }
         catch (IOException e){
            e.printStackTrace();
            msg = new Performative("failure");
            msg.setReceiver(agent);
            msg.setContent("-1");
            msg.setInReplyTo(replyKey);
         }
         proxy.sendMsg(msg);
     }
 //-------------------------------------------------------------------------
     private int getCountUpTo(RandomAccessFile fp, long length){

	 int count = 0;
         String buf ;

         try {
           fp.seek(0);
           while (fp.getFilePointer() < length) {
             buf = fp.readLine();

             if (buf.equals(RECORD_TAG))
	       count += 1;
	   }
           fp.seek(length);  // reset pointer
         }
         catch (IOException e){
            e.printStackTrace();
         }
         return count;
     }
//--------------------------------------------------------------------------
     public void getAllSessions(String replyKey, String agent,
                                         String sessionType){

         Performative msg;
	 String list = "";
         String fname;

         if (isAccessible())
           fname = ROOT +  sessionType;
         else
           fname = ROOT + PREFIX+agent + fsep + sessionType;

         File folder = new File(fname);

         if(!folder.exists())  {
           System.out.println("Session type doesn't exist");
           msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setContent("Session type doesn't exist");
           msg.setInReplyTo(replyKey);
           proxy.sendMsg(msg);
           return;
         }

         String[]  dir = folder.list();
	 msg = new Performative("inform");
         msg.setReceiver(agent);
         msg.setContent(Misc.concat(dir));
         msg.setInReplyTo(replyKey);

	 proxy.sendMsg(msg);
     }
//--------------------------------------------------------------------------
    public void deleteSessionType(String replyKey, String agent,
                                           String sessionType){

	 Performative msg;
         File f;
	 String fname;

         if (isAccessible())
           fname = ROOT +  sessionType;
         else
           fname = ROOT + PREFIX+agent + fsep + sessionType;

	 File folder= new File(fname);

         if(!folder.exists())  {
           System.out.println("Session type doesn't exist");
           msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setContent("Session type doesn't exist: delete failed.");
           msg.setInReplyTo(replyKey);
           proxy.sendMsg(msg);
           return;
         }

         try {
            String[] dir = folder.list();
            for(int i=0; i<dir.length;i++) {
              f = new File(fname +fsep+ dir[i]);
              if (f.exists()) {
	         if (!f.delete())
                  throw new IOException("Delete Failed close all sessions");
              }
            }

            if (!folder.delete() )
              throw new IOException("Delete Failed close all sessions");
            msg = new Performative("inform");
            msg.setReceiver(agent);
            msg.setContent("done");
            msg.setInReplyTo(replyKey);
         }
         catch(IOException ioe ){
           ioe.printStackTrace();
           msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setContent("Error purging " + sessionType);
           msg.setInReplyTo(replyKey);
         }
        proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void openSession(String replyKey, String agent,
                      String sessionType, String sessionId, String accessKey){

        Performative msg;
        String fname;
	if (ht.contains(accessKey)){
          msg = new Performative("failure");
          msg.setReceiver(agent);
          msg.setInReplyTo(replyKey);
          msg.setContent(sessionId + " already opened");
          proxy.sendMsg(msg);
          return;
        }


	try {
         msg = new Performative("inform");
         msg.setReceiver(agent);
         msg.setInReplyTo(replyKey);

	 if (isAccessible())
           fname =ROOT+sessionType+fsep+sessionId;
         else
          fname = ROOT+PREFIX+agent+fsep+sessionType+fsep+sessionId;

	 File file = new File(fname);

	 if (!file.exists())
	    throw new IOException(sessionId + " doesn't exist");
         else  {
          RandomAccessFile fp = new RandomAccessFile(file,"rw");
	  ht.put(accessKey, fp);
	  msg.setContent("done");
         }
       }
       catch(IOException ioe) {
         ioe.printStackTrace();
	 msg = new Performative("failure");
         msg.setReceiver(agent);
         msg.setInReplyTo(replyKey);
         msg.setContent(agent + " couldn't open " + sessionId);
       }
       proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void saveRecord(String replyKey, String agent,
                                    String accessKey, String record){

        Performative msg;

	try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null) throw new IOException("Error file pointer doesn't exist.");
	   fp.seek(fp.length());
           if (fp.getFilePointer() != 0)
	     fp.writeBytes(NEWLINE);
	   fp.writeBytes(RECORD_TAG);
           fp.writeBytes(NEWLINE);
           fp.writeBytes(record);
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent("done");
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + " couldn't save " + record);
        }
        proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void closeSession(String replyKey,String agent,String accessKey){

       Performative msg;

	try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null)
	      throw new IOException("Error file pointer doesn't exist.");
	   fp.close();
           ht.remove(accessKey);
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent("done");
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + " couldn't close  with accesskey:" + accessKey);
        }
        proxy.sendMsg(msg);

    }
//--------------------------------------------------------------------------
    public void nextRecord(String replyKey,String agent,String accessKey){

	Performative msg;
	try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null)
	     throw new IOException("Error file pointer doesn't exist.");

	   String record = readRecord(fp);
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(record);
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + " couldn't read next record  with accesskey:" + accessKey);
        }
        proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void priorRecord(String replyKey,String agent,String accessKey){
        int cnt;
        String record = "";
        Performative msg;
        try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null)
	     throw new IOException("Error file pointer doesn't exist.");
	   cnt = getCountUpTo(fp,fp.getFilePointer());
           fp.seek(0);
           for (int i=0; i < cnt; i++)
               record = readRecord(fp);
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(record);
           
	   //move pointer to begin of record
	   fp.seek(0);
           for (int i=0; i < cnt-1; i++)
               readRecord(fp);
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + " couldn't locate prior record  with accesskey:" + accessKey);
        }
        proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void beginSession(String replyKey,String agent,String accessKey){

	Performative msg;
	try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null)
	     throw new IOException("Error file pointer doesn't exist.");

	   fp.seek(0);
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent("done");
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + " can't move to first record  with accesskey:" + accessKey);
        }
        proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void endSession(String replyKey,String agent,String accessKey){
        Performative msg;
	try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null)
	      throw new IOException("Error file pointer doesn't exist.");
           fp.seek(fp.length());
           msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent("done");
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + " can't move to last record  with accesskey:" + accessKey);
        }
        proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
    public void getAgents(String replyKey,String agent,String accessKey){
        int cnt;
        String record;
        HSet resultSet = new HSet();
        Performative perf;
        Performative msg;
        long currentPos;

	try {
	   RandomAccessFile fp = (RandomAccessFile) ht.get(accessKey);
           if (fp == null)
	     throw new IOException("Error file pointer doesn't exist.");
           currentPos = fp.getFilePointer();
	   cnt = getCountUpTo(fp,fp.length());
           fp.seek(0);
           for(int i=0; i< cnt; i++) {
	     record = readRecord(fp);
             perf = ZeusParser.performative(record);
             resultSet.add(perf.getSender());
             resultSet.add(perf.getReceiver());
           }

	   msg = new Performative("inform");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(Misc.concat(resultSet));
           fp.seek(currentPos);
        }
        catch(IOException e){
           e.printStackTrace();
	   msg = new Performative("failure");
           msg.setReceiver(agent);
           msg.setInReplyTo(replyKey);
           msg.setContent(agent + ": error lisitng agents with accesskey:" + accessKey);
        }

	proxy.sendMsg(msg);
    }
//--------------------------------------------------------------------------
/* Locate a record by looking for  NEWRECORD after reading rewind */
    private String readRecord(RandomAccessFile fp){
         String input = "";
         boolean begin=false;
         try {
	   while (fp.getFilePointer() < fp.length()) {
             String buf = fp.readLine();
             if (buf.equals(RECORD_TAG)) {
                begin = !begin;
                if (!begin)
		   break;
                else
                 continue;
             }
             if (begin)
              input += buf;
            }
            fp.seek(fp.getFilePointer()-NEWRECORD.length());
         }
         catch(IOException e){
           e.printStackTrace();
         }
	return input;
    }

//--------------------------------------------------------------------------

}