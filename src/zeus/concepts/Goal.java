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
 * @(#)Goal.java 1.00
 */

package zeus.concepts;

import java.lang.reflect.*;
import java.util.*;
import zeus.util.*;

/**
 * The Goal class is an important agent-level conceptual data structure.
 * Goals describe objectives, namely the achievement of the {@link Fact} object
 * that it contains, subject to the constraints that are its other variables. <p>
 *
 * This class contains many methods that allow the attributes of goal objects
 * to be set and modified, although typically developers will only call one of 
 * the constructors.
 */

public class Goal
{
   public static final boolean DISCRETE = true;
   public static final boolean CONTINUOUS = false;
   public static final int     DEFAULT_PRIORITY = 1;
   public static final int     MIN_PRIORITY = 1;
   public static final int     MAX_PRIORITY = 10;

   /** The fact description whose achievement is the objective of the goal */
   protected Fact     fact = null;

   protected int      start_time = -1;
   protected int      end_time = -1;
   protected double   cost = 0;
   protected int      invocations = 0;
   protected boolean  type = DISCRETE;
   protected int      priority = DEFAULT_PRIORITY;
   protected String   id = null;
   protected String   image = null;
   protected String   desired_by;
   protected String   root_id = null;
   protected String[] media = null;
   protected Time     replyTime = null;
   protected Time     confirmTime = null;
   protected String   user_data_type = null;
   protected Object   user_data = null;

   protected ResolutionContext context = null;
   protected SuppliedDb given = null;
   protected Vector   producer_records = null;
   protected Vector   consumer_records = null;

   public Goal(boolean type, String id, Fact fact, String desired_by) {
      this.type = type;
      this.id = id;
      this.fact = new Fact(fact);
      this.desired_by = desired_by;
   }

   public Goal(String id, Fact fact, int end_time, double cost,
               String agent, double confirm ) {
      this.type = DISCRETE;
      this.id = id;
      this.fact = new Fact( fact );
      this.end_time = end_time;
      this.cost = cost;
      this.desired_by = agent;
      this.confirmTime = new Time(confirm);
   }

   public Goal(String id, Fact fact, int time, double cost, int priority,
               String agent, Time replyTime, Time confirmTime ) {
      this.type = DISCRETE;
      this.id = id;
      this.fact = new Fact( fact );
      this.end_time = time;
      this.cost = cost;
      this.priority = priority;
      this.desired_by = agent;
      this.replyTime = replyTime;
      this.confirmTime = confirmTime;
   }

   public Goal(String id, Fact fact, int startTime, int endTime,
               double cost, int priority, int invocations,
               String agent, Time replyTime, Time confirmTime) {
      this.type = CONTINUOUS;
      this.id = id;
      this.fact = new Fact( fact );
      this.end_time = endTime;
      this.start_time = startTime;
      this.cost = cost;
      this.priority = priority;
      this.invocations = invocations;
      this.desired_by = agent;
      this.replyTime = replyTime;
      this.confirmTime = confirmTime;
   }


   public Goal(Goal goal) {
      if ( goal.getType().equals("discrete") )
         type = DISCRETE;
      else
         type = CONTINUOUS;

      id = goal.getId();
      fact = goal.getFact();
      end_time = goal.getEndTime();

      if ( type == CONTINUOUS ) {
         start_time = goal.getStartTime();
         invocations = goal.getInvocations();
      }

      cost = goal.getCost();
      priority = goal.getPriority();
      desired_by = goal.getDesiredBy();
      root_id = goal.getRootId();
      media = goal.getTargetMedia();
      replyTime = goal.getReplyTime();
      confirmTime = goal.getConfirmTime();
      image = goal.getImage();

      user_data_type = goal.getUserDataType();
      user_data = goal.getUserData();
      context = goal.getResolutionContext();
      given = goal.getSuppliedDb();
      producer_records = goal.getProducerRecords();
      consumer_records = goal.getConsumerRecords();
   }

   public Goal duplicate(String name, GenSym genSym) {
      DuplicationTable table = new DuplicationTable(name,genSym);
      return duplicate(table);
   }
   public Goal duplicate(DuplicationTable table) {
      Goal g;
      if ( type == DISCRETE )
         g = new Goal(id, fact.duplicate(table), end_time, cost,
                      priority, desired_by, replyTime, confirmTime);
      else
         g = new Goal(id, fact.duplicate(table), start_time, end_time, cost,
                      priority, invocations, desired_by, replyTime,
                      confirmTime);

      g.setRootId(this.getRootId());
      g.setImage(this.getImage());
      g.setTargetMedia(this.getTargetMedia());

      g.setUserDataType(this.getUserDataType());
      g.setUserData(this.getUserData());
      g.setProducerRecords(this.getProducerRecords());
      g.setConsumerRecords(this.getConsumerRecords());

      if ( context != null )
         g.setResolutionContext(context.duplicate(table));

      if ( given != null )
         g.setSuppliedDb(given.duplicate(table));

      return g;
   }

   public String getType() {
      if ( type ) return "discrete";
      else return "continuous";
   }

   public boolean whichType()       { return type; }
   public void    setId(String id)  { Assert.notNull(id); this.id = id; }
   public String  getId()           { return id; }
   public String  getImage()        { return image; }
   public Fact    getFact()         { return new Fact(fact); }
   public String  getFactType()     { return fact.getType(); }
   public String  getFactId()       { return fact.getId(); }
 
   public int     getEndTime()      { return end_time; }
   public double  getCost()         { return cost; }
   public int     getPriority()     { return priority; }
   public String  getDesiredBy()    { return desired_by; }

   public String  getUserDataType() { return user_data_type; }
   public Object  getUserData()     { return user_data; }

   public boolean isDiscrete()      { return type == DISCRETE; }
   public boolean isContinuous()    { return type == CONTINUOUS; }

   public void setProducerRecords(Vector records) {
      this.producer_records = records;
   }

   public void setConsumerRecords(Vector records) {
      this.consumer_records = records;
   }

   public void addProducer(String supply_ref, String use_ref, String comms_key,
                           String producer, String producer_id,
                           String consumer, String consumer_id) {
      ProducerRecord entry = new ProducerRecord(supply_ref, use_ref, comms_key,
         producer, producer_id, consumer, consumer_id);
      addProducer(entry);
   }

   public void addProducer(ProducerRecord entry) {
      if ( producer_records == null )
         producer_records = new Vector();
      producer_records.addElement(entry);
   }

   public void addConsumer(String producer, String producer_id,
                           String consumer, String consumer_id,
                           String use_ref, String comms_key,
                           int start, int amount, boolean consumed) {
      ConsumerRecord entry = new ConsumerRecord(producer, producer_id,
         consumer, consumer_id, use_ref, comms_key, start, amount, consumed);
      addConsumer(entry);
   }

   public void addConsumer(ConsumerRecord entry) {
      if ( consumer_records == null )
         consumer_records = new Vector();
      consumer_records.addElement(entry);
   }

   public void setSuppliedDb(SuppliedDb given) {
      this.given = given;
   }

   public void setResolutionContext(ResolutionContext context) {
      this.context = context;
   }

   public void setUserDataType(String data_type) {
      this.user_data_type = data_type;
   }

   public void setUserData(Object data) {
      this.user_data = data;
   }

   public void setImage(String image) {
      this.image = image;
   }

   public void setFact(Fact fact)  {
      Assert.notNull(fact);
      this.fact = fact;
   }

   public void setFactType(String type)  {
      fact.setType(type);
   }

   public void setStartTime(int time) {
      start_time = time;
   }

   public void setInvocations(int invocations) {
      // discrete goal --> no # invocations
      Assert.notFalse( type == CONTINUOUS );
      Assert.notFalse( invocations >= 0 );
      this.invocations = invocations;
   }

   public void setEndTime(int time) {
      end_time = time;
   }

   public void setCost(double cost) {
      Assert.notFalse( cost >= 0 );
      this.cost = cost;
   }

   public void setPriority(int priority) {
      Assert.notFalse( priority >= MIN_PRIORITY && priority <= MAX_PRIORITY );
      this.priority = priority;
   }
   public void setDesiredBy(String person) {
      Assert.notNull(person);
      desired_by = person;
   }

   public Vector getProducerRecords() {
      return producer_records;
   }

   public Vector getConsumerRecords() {
      return consumer_records;
   }

   public SuppliedDb getSuppliedDb() {
      return given;
   }

   public ResolutionContext getResolutionContext() {
      return context;
   }

   public int getStartTime() {
      return start_time;
   }

   public int getInvocations()  {
      // discrete goal --> no # invocations
      Assert.notFalse( type == CONTINUOUS );
      return invocations;
   }

   public String getRootId() {
      if ( root_id == null ) return id;
      else return root_id;
   }

   public void setRootId( String rootId ) {
      Assert.notNull(rootId);
      this.root_id = rootId;
   }

   public void setTargetMedia(String[] media) {
      this.media = media;
   }

   public void setReplyTime(Time value) {
      replyTime = new Time(value);
   }

   public void setConfirmTime(Time value) {
      confirmTime = new Time(value);
   }

   public void setReplyTime(double value) {
      replyTime = new Time(value);
   }

   public void setConfirmTime(double value) {
      confirmTime = new Time(value);
   }

   public Time getReplyTime() {
      return replyTime;
   }

   public Time getConfirmTime() {
      return confirmTime;
   }

   public String[] getTargetMedia() {
      return media;
   }

   public AbilitySpec getAbility() {
      return new AbilitySpec("goal_generated",fact, 0, 0);
   }

   public boolean constrain(Bindings bindings) {
      return fact.resolve(bindings);
   }

   public boolean equals(Goal goal ) {
      return id.equals( goal.getId() ) &&
             fact.getId().equals( goal.getFact().getId() ) &&
             getType().equals( goal.getType() );
   }

 public String toSL() {
     // String s = new String("(action (inform " + desired_by +"((iota "+fact.toSL() ) ;
     // s+= "( achieve " + fact.toSL() + "(goal_id ("+id +"))))))";
       // (action (inform Agent ((iota ?goal_instance (achieve ?goal_instance)))))

        String slType = new String ();
        if (DISCRETE) slType = "I";
        if (CONTINUOUS) slType = "PG";
        if (DISCRETE && CONTINUOUS) slType = "U";
        if (!DISCRETE && !CONTINUOUS) slType = "U";
	
	String s = new String ("( " + slType + " " +desired_by + " ("+id+" "+ fact.toSL());
	s+= "(parameters :start_time "+start_time + " :end_time "+ end_time;
        s+= " :cost " + cost +" :priority " + priority;
	s+=")))";
	// (PG Agent (goal_idID (goal_istance)))

      return s.trim() ;
   }

   public String toString() {
      System.out.println("Goal ==\n"+this.toSL());
      String s = new String("(");

      s += ":id "   + id + " ";
      s += ":desired_by " + desired_by + " ";
      s += ":type " + type + " ";
      s += ":fact " + fact.toString() + " ";

      if ( image != null )
         s += ":image "   + image + " ";

      if ( type == CONTINUOUS )
         s += ":start_time " + start_time + " ";

      s += ":end_time " + end_time + " ";
      s += ":cost " + cost + " ";
      s += ":priority " + priority + " ";

      if ( type == CONTINUOUS )
         s += ":invocations " + invocations + " ";

      if ( root_id != null )
         s += ":root_id " + root_id + " ";

      if ( media != null ) {
         s += ":media (";
         for( int i = 0; i < media.length; i++ )
            s += "\"" + media[i] + "\"" + " ";
         s = s.trim() + ")";
      }
      if ( replyTime != null )
         s += ":reply_time " + replyTime + " ";

      if ( confirmTime != null )
         s += ":confirm_time " + confirmTime + " ";

      if ( user_data_type != null ) {
         s += ":user_data_type \"" + user_data_type + "\" ";

         if ( user_data != null )
            s += ":user_data " + Misc.OPAQUE_CHAR + user_data + Misc.OPAQUE_CHAR + " ";
      }

      if ( context != null )
         s += ":context (" + context + ") ";

      if ( given != null )
         s += ":given (" + given + ") ";

      if ( producer_records != null && !producer_records.isEmpty() ) {
         s += ":producer_records (";
         for(int i = 0; i < producer_records.size(); i++ )
            s += producer_records.elementAt(i);
         s += ") ";
      }

      if ( consumer_records != null && !consumer_records.isEmpty() ) {
         s += ":consumer_records (";
         for(int i = 0; i < consumer_records.size(); i++ )
            s += consumer_records.elementAt(i);
         s += ") ";
      }

      return s.trim() + ")";
   }
}
