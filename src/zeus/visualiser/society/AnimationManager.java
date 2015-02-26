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



package zeus.visualiser.society;

import java.awt.*;
import java.util.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.gui.*;
import zeus.gui.graph.*;
import zeus.generator.agent.AcquaintanceModel;


public class AnimationManager {
   public static final Hashtable MessageColor = new Hashtable();

   static {
      int size = AcquaintanceModel.RELATIONS_LIST.size();

      MessageColor.put("agree", ColorManager.getColor(size++));
  
      
      MessageColor.put("cancel", ColorManager.getColor(size++));

      
      MessageColor.put("confirm", ColorManager.getColor(size++));

      
      MessageColor.put("disconfirm", ColorManager.getColor(size++));

      
      MessageColor.put("failure", ColorManager.getColor(size++));

      
      MessageColor.put("inform", ColorManager.getColor(size++));

      
      MessageColor.put("inform-if", ColorManager.getColor(size++));
      
      MessageColor.put("inform-ref", ColorManager.getColor(size++));
      
      MessageColor.put("not-understood", ColorManager.getColor(size++));
      
      MessageColor.put("query-if", ColorManager.getColor(size++));
      
      
      MessageColor.put("query-ref", ColorManager.getColor(size++));
      MessageColor.put("refuse", ColorManager.getColor(size++));
      MessageColor.put("request", ColorManager.getColor(size++));
      MessageColor.put("request-when", ColorManager.getColor(size++));
      MessageColor.put("request-whenever", ColorManager.getColor(size++));
      MessageColor.put("subscribe", ColorManager.getColor(size++));
      MessageColor.put("accept-proposal", ColorManager.getColor(size++));
      MessageColor.put("cfp", ColorManager.getColor(size++));
      MessageColor.put("reject-proposal", ColorManager.getColor(size++));
      MessageColor.put("propose", ColorManager.getColor(size++));

   };

   protected AnimationQueue queue;
   protected Hashtable      dynamicColor = new Hashtable();
   protected AgentContext   context;
   protected Graph          graph;
 
   public AnimationManager(AgentContext context, Graph graph) {
      this.context = context;
      this.graph = graph; 
      queue = new AnimationQueue(graph);
   }
 
   public void terminate()          { queue.terminate(); }
   public void flushAnimator()      { queue.flush(); }
   public void setMode(int mode)    { queue.setMode(mode); }
   public int  getMode()            { return queue.getMode(); }
   public void setSpeed(long speed) { queue.setSpeed(speed); }
   public long getSpeed()           { return queue.getSpeed(); }
 
 
   private void debug (String str) { 
   //     System.out.println("AnimationManager>> " + str); 
   }
 
   public void animate(Performative msg) {
           debug("animate 1"); 
      String sender = msg.getSender();
         debug("animate 2"); 
      String receiver = msg.getReceiver();
    debug("animate 3"); 
      // check for null send/receive times
      if ( msg.getSendTime() == null )
         msg.setSendTime(new Time(0));
            debug("animate 4"); 
      if ( msg.getReceiveTime() == null )
         msg.setReceiveTime(new Time(0.05));
    debug("animate 5"); 
      if ( !sender.equals(receiver) ) {
           debug("animate 6"); 
         SocietyModel model = (SocietyModel)graph.getModel();
            debug ("model = " + model.toString()); 
            debug ("sender = " + sender); 
            debug ("receiver = " + receiver);
         GraphNode node1 = model.getNode(sender);
            debug ("node1 = "  + node1.toString()); 
         GraphNode node2 = model.getNode(receiver);
            debug ("node2 = " + node2.toString()); 
         if ( node1 != null && node2 != null &&
              graph.isVisible(node1) && graph.isVisible(node2) ) {
                   debug("animate 7"); 
 	    Color color = selectColor(msg);
 	    debug ("adding to queue"); 
 	    queue.add(msg,node1,node2,color);
         }
      }
   }
 
   protected Color selectColor(Performative msg) {
     Color color = null;
     String type = msg.getType().toLowerCase();
     String in_reply_to = msg.getInReplyTo();
     String reply_with = msg.getReplyWith();
 
     if ( in_reply_to != null )
        color = (Color) dynamicColor.get(in_reply_to);
     if ( color != null ) return color;
 
     if ( reply_with != null )
        color = (Color) dynamicColor.get(reply_with);
     if ( color != null ) return color;
 /*
     if ( type.equals("cfp") || type.equals("propose") ||
          type.equals("accept-proposal") || type.equals("reject-proposal") )
        color = selectDynamicColor(msg);*/
     else {
        if (MessageColor.containsKey(type)) { 
            color = (Color) MessageColor.get(type);}
            else {
                color = selectDynamicColor (msg); }
            

        if ( reply_with != null )
           dynamicColor.put(reply_with,color);
        else if ( in_reply_to != null )
           dynamicColor.put(in_reply_to,color);
     }
     return color;
   }
 
 
   protected Color selectDynamicColor(Performative msg) {
      String reply_with = msg.getReplyWith();
      String in_reply_to = msg.getInReplyTo();
      String content = msg.getContent();
 
      Vector List = ZeusParser.goalList(context.OntologyDb(),content);
      Goal g = (Goal)List.elementAt(0);
      String rootId = g.getRootId();
      Color color = (Color) dynamicColor.get(rootId);
      if ( color == null ) {
 	 color = nextColor();
         dynamicColor.put(rootId,color);
         dynamicColor.put(reply_with,color);
      }
      return color;
   }
 
   protected Color nextColor() {
     int r = (int) ((Math.random())*256);
     int b = (int) ((Math.random())*256);
     int g = (int) ((Math.random())*256);
     return new Color(r,b,g);
   }
}
