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



package zeus.actors;

import java.util.*;
import zeus.util.*;
import zeus.concepts.*;

public class Decomposition extends Hashtable {
   protected Vector constraints;
   // REM constraints don't seem to be enforced yet!?
   protected Vector links;
   protected int node_pointer = -1;
   protected String root = null;
   protected Vector nodeList = new Vector();
   protected Planner planner = null;


   // meaningless init  to allow rearch
   public Decomposition () {
   ;
   }

   public Decomposition(Planner planner, String key, PlanRecord parent,
                        Vector path, Goal goal, SummaryTask task) {

      this.planner = planner;
      links = task.links();
      constraints = task.constraints();

      boolean first_time = true;
      String rightNode, leftNode;
      Enumeration enum;
      TaskLink link;
      Hashtable pairs = new Hashtable();
      KeyValue st;
      Vector temp, curr = new Vector();
      curr.addElement(TaskNode.END);
      int count = 0;
      while( !curr.isEmpty() ) {
         temp = new Vector();
         enum = links.elements();
         while( enum.hasMoreElements() ) {
            link = (TaskLink)enum.nextElement();
            rightNode = link.getRightNode();
            if ( curr.contains(rightNode) ) {
               leftNode = link.getLeftNode();
               if ( !leftNode.equals(TaskNode.BEGIN) ) {
                  if ( !temp.contains(leftNode) )
                     temp.addElement(leftNode);

                  if ( !this.containsKey(leftNode) ) {
                     this.add(task.getNode(leftNode));
                     st = new KeyValue(leftNode,count++);
                     Assert.notFalse( pairs.put(st.key,st) == null );
                  }
                  else {
                     st = (KeyValue)pairs.get(leftNode);
                     st.value = (double)count++;
                  }

                  this.addParentNode(leftNode,rightNode);
               }
            }
         }
         if ( first_time ) {
            determineRoot(key,parent,path,goal,temp);
            first_time = false;
         }
         curr = temp;
      }

      // Finally remove all TaskNode.BEGIN & TaskNode.END
      enum = links.elements();
      while( enum.hasMoreElements() ) {
         link = (TaskLink)enum.nextElement();
         if ( link.referencesNode(TaskNode.BEGIN) ||
	      link.referencesNode(TaskNode.END) )
            links.removeElement(link);
      }

      // Then setup node iterator
      KeyValue tmp = new KeyValue();
      KeyValue[] data = new KeyValue[pairs.size()];
      enum = pairs.elements();
      for(int i = 0; enum.hasMoreElements(); i++ )
         data[i] = (KeyValue)enum.nextElement();

      // sort data
      boolean swapped = true;
      while( swapped ) {
         swapped = false;
         for( int i = 0; i < data.length-1; i++ ) {
            if ( data[i].value > data[i+1].value ) {
               tmp.set(data[i]);
               data[i].set(data[i+1]);
               data[i+1].set(tmp);
               swapped = true;
            }
        }
      }
      for(int i = 0; i < data.length; i++ )
         nodeList.addElement(data[i].key);
      node_pointer = 0;
   }

   protected void determineRoot(String key, PlanRecord parent, Vector path,
                                Goal goal, Vector candidates) {

      Assert.notFalse(candidates.size() == 1);
      Fact f1 = goal.getFact();
      String nodeId;
      Fact[] produced;
      DecompositionStruct struct;
      Bindings b = new Bindings(planner.getAgentContext().whoami());
      for(int i = 0; i < candidates.size(); i++ ) {
         nodeId = (String)candidates.elementAt(i);
         struct = (DecompositionStruct)this.get(nodeId);
         produced = struct.node.getPostconditions();
         for(int j = 0; j < produced.length; j++, b.clear()) {
            if ( !produced[j].isSideEffect() && produced[j].unifiesWith(f1,b) ) {
               struct.node.resolve(b);
               root = nodeId;
               struct.key = key;
               struct.goal = new Goal(goal);
               struct.parent_record = parent;
               struct.path = path;
               return;
            }
         }
      }
   }

   protected void add(TaskNode node) {
      DecompositionStruct struct;
      struct = new DecompositionStruct(planner.getAgentContext().whoami(),node);
      Assert.notFalse( put(node.getName(),struct) == null);
   }

   protected void addParentNode(String childId, String parentId) {
      if ( parentId.equals(TaskNode.END) ) return;
      DecompositionStruct struct = (DecompositionStruct)get(childId);
      if ( !struct.parents.contains(parentId) ) {
         struct.parents.addElement(parentId);
         addChildNode(parentId,childId);
      }
   }
   protected void addChildNode(String parentId, String childId) {
      if ( childId.equals(TaskNode.BEGIN) ) return;
      DecompositionStruct struct = (DecompositionStruct)get(parentId);
      if ( !struct.children.contains(childId) )
         struct.children.addElement(childId);
   }

   public synchronized String nextNode() {
      String nodeId;
      if ( node_pointer < nodeList.size() ) {
         nodeId = (String)nodeList.elementAt(node_pointer++);
         Core.DEBUG(2,"nextNode(): id = " + nodeId);
         return allParentsScheduled(nodeId) ? nodeId : nextNode();
      }
      return null;
   }
   public synchronized void reset() {
      node_pointer = 0;
   }

   protected TaskLink findLink(String left, String right) {
      TaskLink link;
      Enumeration enum = links.elements();
      while( enum.hasMoreElements() ) {
         link = (TaskLink)enum.nextElement();
         if ( link.getLeftNode().equals(left) &&
              link.getRightNode().equals(right) ) return link;
      }
      return null;
   }

   protected boolean allParentsScheduled(String nodeId) {
      String parentId = null;
      DecompositionStruct struct, st, pt;
      struct = (DecompositionStruct)get(nodeId);
      for(int i = 0; i < struct.parents.size(); i++ ) {
         parentId = (String)struct.parents.elementAt(i);
         st = (DecompositionStruct)get(parentId);
         Core.DEBUG(2,"allParentsScheduled(): nodeId = " + nodeId +
                           " parentId = " + parentId + " parent.scheduled = " +
                           st.scheduled);
         if ( !st.scheduled ) return false;
      }

      if ( struct.parents.isEmpty() || struct.goal != null )
         return true;

      // First, nominate one of your parents as the primary parent
      struct.current_parent = (String)struct.parents.elementAt(0);
      pt = (DecompositionStruct)get(struct.current_parent);
      struct.parent_record = pt.record;

      // Next, set your key to that of your primary parent
      struct.key = pt.key;

      // Next, determine link to primary parent
      TaskLink link = findLink(nodeId,struct.current_parent);
      struct.parent_link = link.getId();

      // Next, set your primary goal to be a subgoal of your primary parent
      Fact produced = null;
      produced = pt.node.getPrecondition(link.getRightGroup(),link.getRightArg());

      if ( pt.record == null ) {
         // external contract
         struct.goal = new Goal(pt.image.whichType(),
                                planner.getAgentContext().newId("subgoal"),
                                produced,
                                planner.getAgentContext().whoami());

         if ( pt.image.isContinuous() ) {
            int s = pt.image.getStartTime();
            int e = pt.image.getEndTime();
            int n = pt.image.getInvocations();
            int u = (e-s)/n;
            struct.goal.setStartTime(s);
            struct.goal.setEndTime(e - u);
            struct.goal.setInvocations(n);
         }
         else {
            struct.goal.setEndTime(getStartTime(link,pt));
         }
         struct.goal.setConfirmTime(pt.image.getConfirmTime());
         struct.goal.setPriority(pt.image.getPriority());
         struct.goal.setCost(0);
         struct.goal.setRootId(pt.image.getRootId());
      }
      else {
         Task task = pt.record.getTask();
         Fact[] consumed = task.getPreconditions();
         Bindings b = new Bindings(planner.getAgentContext().whoami());
         boolean found = false;
         for(int i = 0; !found && i < consumed.length; i++, b.clear()) {
            if ( produced.unifiesWith(consumed[i],b) ) {
               struct.goal = pt.record.createSubgoal(consumed[i],i);
               found = true;
            }
         }
         Assert.notFalse(found);
      }
      int start = struct.goal.getStartTime();
      int end = struct.goal.getEndTime();
      // now adjust times so that current struct precedes all its parents
      TaskLink link1;
      for(int i = 0; i < struct.parents.size(); i++ ) {
         parentId = (String)struct.parents.elementAt(i);
         st = (DecompositionStruct)get(parentId);
         link1 = findLink(nodeId,parentId);
         if ( st.record == null ) {
            if ( st.image.isContinuous() ) {
               int s = st.image.getStartTime();
               int e = st.image.getEndTime();
               int n = st.image.getInvocations();
               int u = (e-s)/n;
               start = Math.min(start,s);
               end = Math.min(end,e-u);
            }
            else {
               end = Math.min(end,getStartTime(link1,st));
            }
         }
         else {
            if ( st.goal.isContinuous() ) {
                start = Math.min(start,st.record.getStartTime());
                end = Math.min(end,st.record.getEndTime() -
                               st.record.getTask().getTime());
            }
            else {
               end = Math.min(end,st.record.getStartTime());
            }
         }
      }

      struct.goal.setEndTime(end);
      if ( struct.goal.isContinuous() )
         struct.goal.setStartTime(start);

      // Now, compute path
      struct.path = Misc.copyVector(pt.path);
      struct.path.addElement(pt.goal.getFact());

      // Finally remove primary link;
      links.removeElement(link);
      return true;
   }

   protected int getStartTime(TaskLink link, DecompositionStruct struct) {
      String itemId = (String)struct.lookupTable.get(link.getId());
      SuppliedItem item = struct.given.getSuppliedItem(itemId);
      return item.getEarliestReservationTime();
   }
   protected String[] getReservationId(TaskLink link,
                                       DecompositionStruct struct) {
      String itemId = (String)struct.lookupTable.get(link.getId());
      SuppliedItem item = struct.given.getSuppliedItem(itemId);
      return item.getReservationId();
   }

   public Fact[] getPreconditions(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.node.getPreconditions();
   }
   public Fact[] getPostconditions(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.node.getPostconditions();
   }

   public Vector getPath(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.path;
   }
   public Goal getGoal(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.goal;
   }
   public String getKey(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.key;
   }
   public PlanRecord getParentRecord(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.parent_record;
   }

   public void setRecords(String node, Vector records) {
      DecompositionStruct st = (DecompositionStruct)this.get(node);
      st.records = records;
      st.record = (PlanRecord)records.elementAt(0);
      st.scheduled = true;

      if ( node.equals(root) ) return;

      DecompositionStruct pt = (DecompositionStruct)this.get(st.current_parent);
      if ( pt.record == null ) {
         String itemId = (String)pt.lookupTable.get(st.parent_link);
         SuppliedItem item = pt.given.getSuppliedItem(itemId);
         String[] refs = item.getReservationId();

         String consumer, consumer_id;
         String comms_key;
         int amount, start;
         boolean consumed;

         String producer = st.agent;
         String producer_id = st.record.getGoal().getId();
         for(int i = 0; i < refs.length; i++ ) {
            consumer = item.getReservingAgent(refs[i]);
            consumer_id = item.getReservationGoalId(refs[i]);
            comms_key = item.getReservationCommsKey(refs[i]);

            amount = item.getReservedAmount(refs[i]);
            start = item.getReservationTime(refs[i]);
            consumed = item.isReservationConsumed(refs[i]);
            Assert.notFalse(amount > 0);

            pt.image.addProducer(itemId, refs[i], comms_key,
                                 producer, producer_id, consumer, consumer_id);

            st.record.getProducedDb().replaceOrAdd(
               st.goal.getId(),
               consumer_id + "/" + refs[i], start, amount, consumed );
         }
      }
   }
   public boolean allNodesScheduled() {
      DecompositionStruct struct;
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         struct = (DecompositionStruct)enum.nextElement();
         if ( !struct.scheduled ) return false;
      }
      return true;
   }
   public void setQueued(String node, boolean set) {
      DecompositionStruct struct;
      struct = (DecompositionStruct)get(node);
      struct.queued = set;
   }
   public boolean isQueued(String node) {
      DecompositionStruct struct;
      struct = (DecompositionStruct)get(node);
      return struct.queued;
   }
   public boolean isScheduled(String node) {
      DecompositionStruct struct;
      struct = (DecompositionStruct)get(node);
      return struct.scheduled;
   }
   public PlanRecord getRecord(String node) {
      DecompositionStruct struct = (DecompositionStruct)this.get(node);
      return struct.record;
   }
   public PlanRecord getRootRecord() {
      DecompositionStruct struct = (DecompositionStruct)this.get(root);
      return struct.record;
   }

   public String getNodeWithGoalId(String goalId) {
      Core.DEBUG(3,"getNodeWithId\n" + this);
      DecompositionStruct struct;
      Enumeration enum = this.elements();
      while( enum.hasMoreElements() ) {
         struct = (DecompositionStruct)enum.nextElement();
         if ( struct.goal != null &&
              struct.goal.getId().equals(goalId) )
            return struct.node.getName();
      }
      return null;
   }
   public void setImage(String node, Goal image, String agent,
                        String delegation_key) {
      Core.DEBUG(3,"SetImage: node = " + node);
      Core.DEBUG(3,"SetImage: image = " + image);
      DecompositionStruct st = (DecompositionStruct)this.get(node);
      Core.DEBUG(3,"SetImage: st = " + st);
      SuppliedDb db = image.getSuppliedDb();
      Core.DEBUG(3,"SetImage: db = " + db);
      Assert.notFalse(st.given.add(db));
      Core.DEBUG(3,"SetImage: After assert");
      st.image = image;
      st.agent = agent;
      st.scheduled = true;
      st.queued = false;

/*
   REM: what if node is the root node of decomposition graph?
*/
      String consumer, consumer_id;
      int amount, start;
      boolean consumed;
      String itemId;
      SuppliedItem item;
      String[] refs;
      String producer, producer_id;
      String comms_key;

      DecompositionStruct pt = (DecompositionStruct)this.get(st.current_parent);
      producer = st.agent;
      producer_id = st.image.getId();

      if ( pt.record == null ) {
         itemId = (String)pt.lookupTable.get(st.parent_link);
         item = pt.given.getSuppliedItem(itemId);
         refs = item.getReservationId();

         for(int i = 0; i < refs.length; i++ ) {
            consumer = item.getReservingAgent(refs[i]);
            consumer_id = item.getReservationGoalId(refs[i]);
            comms_key = item.getReservationCommsKey(refs[i]);

            amount = item.getReservedAmount(refs[i]);
            start = item.getReservationTime(refs[i]);
            consumed = item.isReservationConsumed(refs[i]);
            Assert.notFalse(amount > 0);
   
            pt.image.addProducer(itemId, refs[i], comms_key,
                                 producer, producer_id, consumer, consumer_id);

            st.image.addConsumer(producer, producer_id, consumer, consumer_id,
                                 refs[i], comms_key, start, amount, consumed);
         }
      }
      else {
         String use_ref = planner.getAgentContext().newId("used");

         consumer = planner.getAgentContext().whoami();
         consumer_id = pt.goal.getId();

         start = pt.record.getStartTime();
         int precond_position = pt.record.getConsumedPosition(producer_id);
         consumed = pt.record.isPreconditionConsumed(precond_position);
         amount = pt.record.getAmountUsed(precond_position);
         Assert.notFalse(amount > 0);
        
         st.image.addConsumer( producer, producer_id,
                               consumer, consumer_id, use_ref, delegation_key,
                               start, amount, consumed ); 

         pt.record.getConsumedDb().update( producer_id + "/" + use_ref,
                                           producer_id );
      }
      st.key = delegation_key;
   }

   public void enforceLinks() {
      TaskLink link;
      LinkInfo info;
      SuppliedItem item;
      String[] refs;

      String itemId, producer, producer_id, consumer, consumer_id, comms_key;
      int amount, start;
      boolean consumed;

      Enumeration enum = links.elements();
      while( enum.hasMoreElements() ) {
         link = (TaskLink)enum.nextElement();
         Core.DEBUG(2,"About to hardChain " + link.getId());
         info = getLinkInfo(link);
         if ( info.child.record != null ) {
            if ( info.parent.record != null ) {
               info.child.record.getProducedDb().hardChain(
                  info.effect_position,info.amount,info.parent.record,
                  info.precond_position
               );
            }
            else {
               itemId = (String)info.parent.lookupTable.get(link.getId());
               item = info.parent.given.getSuppliedItem(itemId);
               refs = item.getReservationId();

               producer = planner.getAgentContext().whoami();
               producer_id = info.child.goal.getId();
               for(int i = 0; i < refs.length; i++ ) {
                  consumer = item.getReservingAgent(refs[i]);
                  consumer_id = item.getReservationGoalId(refs[i]);
                  comms_key = item.getReservationCommsKey(refs[i]);

                  info.parent.image.addProducer(
                     itemId, refs[i], comms_key,
                     producer, producer_id, consumer, consumer_id
                  );

                  amount = item.getReservedAmount(refs[i]);
                  start = item.getReservationTime(refs[i]);
                  consumed = item.isReservationConsumed(refs[i]);
                  Assert.notFalse(amount > 0);

                  info.child.record.getProducedDb().hardChain(
                     info.effect_position, consumer_id + "/" + refs[i],
                     amount, start, consumed
                  );
               }
            }
         }
         else {
            if ( info.parent.record != null ) {
               String use_ref = planner.getAgentContext().newId("used");
               info.child.image.addConsumer(
                  info.child.agent, info.child.goal.getId(),
                  planner.getAgentContext().whoami(), info.parent.goal.getId(),
                  use_ref, info.child.key,
                  info.start, info.amount, info.consumed
               );
               info.parent.record.getConsumedDb().add(
                  info.precond_position,info.child.goal.getId() + "/" + use_ref,
                  info.amount);

            }
            else {
               itemId = (String)info.parent.lookupTable.get(link.getId());
               item = info.parent.given.getSuppliedItem(itemId);
               refs = item.getReservationId();

               producer = info.child.agent;
               producer_id = info.child.goal.getId();;
               for(int i = 0; i < refs.length; i++ ) {
                  consumer = item.getReservingAgent(refs[i]);
                  consumer_id = item.getReservationGoalId(refs[i]);
                  comms_key = item.getReservationCommsKey(refs[i]);

                  info.parent.image.addProducer(
                     itemId,refs[i],comms_key,
                     producer,producer_id,consumer,consumer_id
                  );

                  amount = item.getReservedAmount(refs[i]);
                  start = item.getReservationTime(refs[i]);
                  consumed = item.isReservationConsumed(refs[i]);
                  Assert.notFalse(amount > 0);

                  info.child.image.addConsumer(
                     info.child.agent, info.child.goal.getId(),
                     consumer, consumer_id,
                     refs[i], comms_key, start, amount, consumed
                  );
               }
            }
         }
      }
      links.removeAllElements();
   }

   protected LinkInfo getLinkInfo(TaskLink link) {
      LinkInfo info = new LinkInfo();
      Fact f1;
      Fact[] data;
      Bindings b = new Bindings(planner.getAgentContext().whoami());

      info.child = (DecompositionStruct)get(link.getLeftNode());
      info.parent = (DecompositionStruct)get(link.getRightNode());

      if ( info.child.record != null ) {
         f1 = info.child.node.getPostcondition(link.getLeftGroup(),link.getLeftArg());
         data = info.child.record.getTask().getPostconditions();
         for(int i = 0; i < data.length; i++, b.clear()) {
            if ( f1.unifiesWith(data[i],b) ) {
               info.effect_position = i;
               break;
            }
         }
      }

      if ( info.parent.record != null ) {
         f1 = info.parent.node.getPrecondition(link.getRightGroup(),link.getRightArg());
         data = info.parent.record.getTask().getPreconditions();
         for(int i = 0; i < data.length; i++, b.clear()) {
            if ( f1.unifiesWith(data[i],b) ) {
               info.precond_position = i;
               info.amount = data[i].getNumber();
               if ( info.amount == 0 ) {
                  System.err.println("Error: integer expected in " +
                  "task.precond.fact.no field." +
                  "\nEnsure \"no\" constraints are defined in all task " +
                  "specifications >> " + link.getId());

                  // ERROR FIX
                  info.amount = 1;
               }
               info.consumed = !data[i].isReadOnly();
               info.start = info.parent.record.getStartTime();
               break;
            }
         }
      }
      return info;
   }

   public SuppliedDb getSuppliedDb(String node) {
      DecompositionStruct st = (DecompositionStruct)get(node);
      if ( st.given != null ) return st.given;

      st.given = new SuppliedDb(planner.getAgentContext().OntologyDb());
      st.lookupTable = new Hashtable();
      Enumeration enum = links.elements();
      TaskLink link;
      Fact f1;
      String me = planner.getAgentContext().whoami();
      String id;
      SuppliedItem item;
      while( enum.hasMoreElements() ) {
         link = (TaskLink)enum.nextElement();
         if ( link.getRightNode().equals(node) ) {
            f1 = st.node.getPrecondition(link.getRightGroup(),link.getRightArg());
            id = planner.getAgentContext().newId("supplied");
            item = new SuppliedItem(id,link.getId(),me,f1);
            st.given.add(item);
            st.lookupTable.put(link.getId(),id);
         }
      }
      return st.given;
   }
}

class LinkInfo {
   public DecompositionStruct child = null;
   public DecompositionStruct parent = null;
   public int effect_position = -1;
   public int precond_position = -1;
   public int amount = 0;
   public int start = -1;
   public boolean consumed = false;
}

class DecompositionStruct {
   public TaskNode node = null;
   public PlanRecord record = null;
   public Vector records = null;
   public String key = null;
   public Goal goal = null;
   public Goal image = null;
   public String agent = null;
   public String parent_link = null;
   public Vector path = null;
   public PlanRecord parent_record = null;
   public String current_parent = null;
   public Vector parents = new Vector();
   public Vector children = new Vector();
   public boolean scheduled = false;
   public boolean queued = false;
   public SuppliedDb given = null;
   public Hashtable lookupTable = null;

   public DecompositionStruct(String self, TaskNode node) {
      Assert.notNull(node);
      this.node = node;
      agent = self;
   }
   public String toString() {
      return "(:node " + node.getName() + "\n" +
             " :records " + records + "\n" +
             " :record " + record + "\n" +
             " :key " + key + "\n" +
             " :goal " + goal + "\n" +
             " :image " + image + "\n" +
             " :agent " + agent + "\n" +
             " :path " + path + "\n" +
             " :parent_record " + parent_record + "\n" +
             " :current_parent " + current_parent + "\n" +
             " :parent_link " + parent_link + "\n" +
             " :parents " + parents + "\n" +
             " :children " + children + "\n" +
             " :scheduled " + scheduled + "\n" +
             " :queued " + queued + "\n" +
             " :given " + given + "\n" +
             " :lookupTable " + lookupTable + "\n" +
             ")";
   }
}

