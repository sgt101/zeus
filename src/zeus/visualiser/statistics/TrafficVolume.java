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
    this is a test of the Sourceforge CVS.
    Please ignore this comment!
*/

package zeus.visualiser.statistics;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.rtn.Engine;

public class TrafficVolume {
   protected Hashtable  goalTraffic = new Hashtable();
   protected Hashtable  negotiationTraffic = new Hashtable();
   protected Hashtable  allTraffic = new Hashtable();
   protected Hashtable  referenceTable = new Hashtable();
   protected boolean    updatingGoalTraffic =  true;
   protected OntologyDb ontology;


   public TrafficVolume(OntologyDb ontology) {
      this.ontology = ontology;
   }

   public synchronized void clear() {
      allTraffic.clear();
      clearGoalTraffic();
   }

   public synchronized void clearGoalTraffic() {
      goalTraffic.clear();
      negotiationTraffic.clear();
      referenceTable.clear();
   }

   public boolean isUpdatingGoalTraffic() {
      return updatingGoalTraffic;
   }

   public synchronized void setUpdatingGoalTraffic(boolean set) {
      updatingGoalTraffic = set;
   }

   public synchronized void update(Performative msg) {
      updateTraffic(msg);
      updateGoalTraffic(msg);
   }

   protected void updateTraffic(Performative msg) {
/**
      allTraffic structure:

      sender --> Hashtable
                    |
                 receiver --> int[Performative.MESSAGE_TYPES]
*/
      String sender = msg.getSender();
      String receiver = msg.getReceiver();
      String type = msg.getType();

      Hashtable inner = (Hashtable)allTraffic.get(sender);
      if ( inner == null ) {
         inner = new Hashtable();
         allTraffic.put(sender,inner);
      }
      int[] data = (int[])inner.get(receiver);
      if ( data == null ) {
         data = new int[Performative.MESSAGE_TYPES.length];
         for(int i = 0; i < data.length; i++ )
            data[i] = 0;
         inner.put(receiver,data);
      }
      int j = Misc.whichPosition(type,Performative.MESSAGE_TYPES);
      Core.ERROR(j != -1,1,this);
      data[j] += 1;
   }

   protected void updateGoalTraffic(Performative msg) {
      if ( !updatingGoalTraffic ) return;

      MsgContentHandler hd;
      Goal g;
      String rootId;

      String sender = msg.getSender();
      String receiver = msg.getReceiver();
      String type = msg.getType();
      String reply_with = msg.getReplyWith();
      String in_reply_to = msg.getInReplyTo();
      String content = msg.getContent();
      String key = (reply_with != null) ? reply_with : in_reply_to;

      if ( type.equals("cfp") || type.equals("propose") ||
           type.equals("accept-proposal") || type.equals("reject-proposal") ) {
         g = ZeusParser.goal(ontology,content);
         rootId = g.getRootId();
         updateGoalTraffic(sender,receiver,type,rootId,key);
         updateNegotiationTraffic(sender,receiver,type,key,g);
      }
      else if ( referenceTable.containsKey(key) ) {
         if ( type.equals("cancel") || type.equals("failure") ) {
            g = ZeusParser.goal(ontology,content);
            rootId = g.getRootId();
            updateGoalTraffic(sender,receiver,type,rootId,key);
         }
         else if ( type.equals("inform") ) {
            hd = new MsgContentHandler(content);
            updateGoalTraffic(sender,receiver,hd.tag(),null,key);
         }
      }
   }

   protected void updateGoalTraffic(String sender, String receiver, String type,
                                    String rootId, String reply_tag) {
/**
      goalTraffic structure:

      rootId --> Hashtable
                    |
                  sender --> Hashtable
                                |
                             receiver --> int[Engine.COORDINATION_MESSAGE_TYPES]
*/

      if ( rootId == null ) {
         if ( (rootId = (String)referenceTable.get(reply_tag)) == null )
            rootId = "--UnknownId--";
      }

      referenceTable.put(reply_tag,rootId);

      Hashtable outer = (Hashtable)goalTraffic.get(rootId);
      if ( outer == null ) {
         outer = new Hashtable();
         goalTraffic.put(rootId,outer);
      }
      Hashtable  inner = (Hashtable)outer.get(sender);
      if ( inner == null ) {
         inner = new Hashtable();
         outer.put(sender,inner);
      }
      int[] data = (int[])inner.get(receiver);
      if ( data == null ) {
         data = new int[Engine.COORDINATION_MESSAGE_TYPES.length];
         for(int i = 0; i < data.length; i++ ) data[i] = 0;
         inner.put(receiver,data);
      }
      int j = Misc.whichPosition(type,Engine.COORDINATION_MESSAGE_TYPES);
      Core.ERROR(j != -1,2,this);
      data[j] += 1;
   }
   protected void updateNegotiationTraffic(String sender, String receiver,
      String msg_type, String reply_tag, Goal g) {
/**
      negotiationTraffic structure:

      rootId --> Hashtable
                    |
               goalId[Fact] --> Hashtable
                                 |
                           sender receiver --> Hashtable
                                                   |
                                                 sender --> Vector
*/
      String rootId = g.getRootId();
      String goalId = g.getId() + "[" + g.getFactType() + "]";
      double cost = g.getCost();

      // REM temp hack to avoid zero-value cost in graphs
      if ( Math.abs(cost) < 1.0E-12 ) return;

      if ( rootId == null ) {
         if ( (rootId = (String)referenceTable.get(reply_tag)) == null )
            rootId = "--UnknownId--";
      }

      referenceTable.put(reply_tag,rootId);

      Hashtable outer = (Hashtable)negotiationTraffic.get(rootId);
      if ( outer == null ) {
         outer = new Hashtable();
         negotiationTraffic.put(rootId,outer);
      }
      Hashtable inner = (Hashtable)outer.get(goalId);
      if ( inner == null ) {
         inner = new Hashtable();
         outer.put(goalId,inner);
      }
      String Id = sender.compareTo(receiver) > 0 ?
                  sender + " " + receiver : receiver + " " + sender;

      Hashtable innermost = (Hashtable)inner.get(Id);
      if ( innermost == null ) {
         innermost = new Hashtable();
         inner.put(Id,innermost);
      }

      Vector List = (Vector)innermost.get(sender);
      if ( List == null ) {
         List = new Vector();
         innermost.put(sender,List);
      }
      List.addElement(new Double(cost));
   }

   public String[] getDistributionByTypeLabels() {
      return Performative.MESSAGE_TYPES;
   }

   public synchronized double[] getDistributionByTypeData() {
      double result[] = new double[Performative.MESSAGE_TYPES.length];
      for(int i = 0; i < result.length; i++ ) result[i] = 0.0;
      Enumeration enum = allTraffic.elements();
      Hashtable inner;
      while( enum.hasMoreElements() ) {
         inner = (Hashtable)enum.nextElement();
         Enumeration elements = inner.elements();
         while( elements.hasMoreElements() ) {
            int[] data = (int[])elements.nextElement();
            for(int i = 0; i < result.length; i++ )
               result[i] += data[i];
         }
      }
      return result;
   }

   public synchronized String[] getDistributionByAgentLabels() {
      String[] labels = new String[allTraffic.size()];
      Enumeration enum = allTraffic.keys();
      for(int i = 0; i < labels.length; i++ )
         labels[i] = (String)enum.nextElement();
      return labels;
   }

   public String[] getDistributionByAgentKeys() {
      return Performative.MESSAGE_TYPES;
   }

   public synchronized double[][] getDistributionByAgentData() {
      if ( allTraffic.isEmpty() ) return null;

      double[][] result =
         new double[allTraffic.size()][Performative.MESSAGE_TYPES.length];
      for(int i = 0; i < result.length; i++ )
      for(int j = 0; j < result[i].length; j++ )
         result[i][j] = 0.0;

      Enumeration enum = allTraffic.elements();
      Hashtable inner;
      for(int i = 0; i < result.length; i++ ) {
         inner = (Hashtable) enum.nextElement();
         Enumeration elements = inner.elements();
         int[] data;
         while( elements.hasMoreElements() ) {
            data = (int[])elements.nextElement();
            for(int j = 0; j < result[i].length; j++ )
               result[i][j] += data[j];
         }
      }
      return result;
   }

   public synchronized String[] getCurrentGoals() {
      String[] goals = new String[goalTraffic.size()];
      Enumeration enum = goalTraffic.keys();
      for(int i = 0; i < goals.length; i++ )
         goals[i] = (String)enum.nextElement();
      return goals;
   }

   public String[] getDistributionByGoalKeys() {
      return Engine.COORDINATION_MESSAGE_TYPES;
   }

   public synchronized String[] getDistributionByGoalLabels(String[] goals) {
      if ( goals == null || goals.length == 0 ) return null;
      Vector List = new Vector();
      for(int i = 0; i < goals.length; i++ ) {
         Hashtable outer = (Hashtable)goalTraffic.get(goals[i]);
         if ( outer != null ) {
            Enumeration enum = outer.keys();
            while( enum.hasMoreElements() ) {
               String sender = (String)enum.nextElement();
               if ( !List.contains(sender) )
                  List.addElement(sender);
            }
         }
      }
      return Misc.stringArray(List);
   }

   public synchronized Hashtable getNegotiationGoals() {
      Hashtable output = new Hashtable();
      String rootId, goalId, Id;
      Hashtable outer, inner;
      HSet entry;
      Enumeration goals, agents;
      Enumeration enum = negotiationTraffic.keys();
      while( enum.hasMoreElements() ) {
         rootId = (String)enum.nextElement();
	 outer = (Hashtable)negotiationTraffic.get(rootId);
         goals = outer.keys();
         entry = new HSet();
         while( goals.hasMoreElements() ) {
            goalId = (String)goals.nextElement();
            inner = (Hashtable)outer.get(goalId);
            agents = inner.keys();
            while( agents.hasMoreElements() ) {
               Id = (String)agents.nextElement();
               entry.add(goalId + " " + Id);
            }
         }
         output.put(rootId,entry);
      }
      return output;
   }

   public synchronized double[][] getDistributionByNegotiationDialogueXData(
      String[] user_goals) {

      if ( user_goals == null || user_goals.length == 0 ) return null;

      /*
         user_goals[0] : rootId
         user_goals[1] : goalId[Fact] sender receiver
      */

      MsgContentHandler hd = new MsgContentHandler(user_goals[1]);
      String goalId = hd.tag();
      String Id = hd.data();
      String sender = hd.data(0);
      String receiver = hd.data(1);

      Hashtable outer = (Hashtable)negotiationTraffic.get(user_goals[0]);
      Hashtable inner = (Hashtable)outer.get(goalId);
      Hashtable innermost = (Hashtable)inner.get(Id);
      Vector senderList = (Vector)innermost.get(sender);
      Vector receiverList = (Vector)innermost.get(receiver);

      int senderSize = (senderList != null) ? senderList.size() : 0;
      int receiverSize = (receiverList != null) ? receiverList.size() : 0;
      int num = Math.max(senderSize,receiverSize);

      if ( num == 0 ) return null;

      double[][] output = new double[2][num];
      for(int i = 0; i < output.length; i++ )
      for(int j = 0; j < output[i].length; j++ )
         output[i][j] = (double)j;
      return output;
   }

   public synchronized String[] getDistributionByNegotiationDialogueKeys(
      String[] user_goals) {
      if ( user_goals == null || user_goals.length == 0 )
         return null;

      /*
         user_goals[0] : rootId
         user_goals[1] : goalId[Fact] sender receiver
      */

      MsgContentHandler hd = new MsgContentHandler(user_goals[1]);
      String sender = hd.data(0);
      String receiver = hd.data(1);

      String[] output = new String[2];
      output[0] = sender;
      output[1] = receiver;
      return output;
   }

   public synchronized double[][] getDistributionByNegotiationDialogueData(
      String[] goals) {

      if ( goals == null || goals.length == 0 ) return null;

      MsgContentHandler hd = new MsgContentHandler(goals[1]);
      String goalId = hd.tag();
      String Id = hd.data();
      String sender = hd.data(0);
      String receiver = hd.data(1);

      Hashtable outer = (Hashtable)negotiationTraffic.get(goals[0]);
      Hashtable inner = (Hashtable)outer.get(goalId);
      Hashtable innermost = (Hashtable)inner.get(Id);
      Vector senderList = (Vector)innermost.get(sender);
      Vector receiverList = (Vector)innermost.get(receiver);

      double[][] result = new double[2][];
      Double value;

      int senderSize = (senderList != null) ? senderList.size() : 0;
      result[0] = new double[senderSize];
      for(int i = 0; senderList != null && i < senderSize; i++ ) {
         value = (Double)senderList.elementAt(i);
         result[0][i] = value.doubleValue();
      }

      int receiverSize = (receiverList != null) ? receiverList.size() : 0;
      result[1] = new double[receiverSize];
      for(int i = 0; receiverList != null && i < receiverSize; i++ ) {
         value = (Double)receiverList.elementAt(i);
         result[1][i] = value.doubleValue();
      }
      return result;
   }

   public synchronized double[][] getDistributionByGoalData(String[] goals) {
      if ( goals == null || goals.length == 0 ) return null;
      String[] senders = getDistributionByGoalLabels(goals);
      if ( senders == null || senders.length == 0 ) return null;

      double[][] result = new double[senders.length][Engine.COORDINATION_MESSAGE_TYPES.length];
      for(int i = 0; i < result.length; i++ )
      for(int j = 0; j < result[i].length; j++ )
         result[i][j] = 0.0;

      Hashtable outer, inner;
      int[] data;
      for(int i = 0; i < goals.length; i++ ) {
         outer = (Hashtable)goalTraffic.get(goals[i]);
         if ( outer != null ) {
            for(int j = 0; j < senders.length; j++ ) {
               inner = (Hashtable)outer.get(senders[j]);
               if ( inner != null ) {
                  Enumeration enum = inner.elements();
                  while( enum.hasMoreElements() ) {
                     data = (int[])enum.nextElement();
                     for(int k = 0; k < data.length; k++ )
                        result[j][k] += data[k];
                  }
               }
            }
         }
      }
      return result;
   }
   public synchronized String[] getInterAgentTrafficLabels() {
      String[] labels = new String[allTraffic.size()];
      Enumeration enum = allTraffic.keys();
      for(int i = 0; i < labels.length; i++ )
         labels[i] = (String)enum.nextElement();
      return labels;
   }
   public synchronized double[][] getInterAgentTrafficData() {
      if ( allTraffic.isEmpty() ) return null;

      String[] labels = getInterAgentTrafficLabels();
      double[][] result = new double[labels.length][labels.length];
      for(int i = 0; i < result.length; i++ )
      for(int j = 0; j < result[i].length; j++ )
         result[i][j] = 0.0;

      Hashtable inner;
      int[] data;
      int sum;
      for(int i = 0; i < result.length; i++ ) {
         inner = (Hashtable) allTraffic.get(labels[i]);
         for(int j = 0; j < result[i].length; j++ ) {
            sum = 0;
            data = (int[])inner.get(labels[j]);
            if ( data != null ) {
               for(int k = 0; k < data.length; k++ )
                  sum += data[k];
            }
            result[i][j] = sum;
         }
      }
      return result;
   }

}
