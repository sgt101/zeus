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



package zeus.concepts;

import java.util.*;
import zeus.util.*;


public class GoalSummary extends Summary {
   public static String DISCRETE = "Discrete";
   public static String CONTINUOUS = "Continuous";
   public static String ENACTMENT = "Enactment";

   public static String RUNNING = "Running";
   public static String SUSPENDED = "Suspended";
   public static String CANCELLED = "Cancelled";
   public static String FAILED = "Failed";
   public static String COMPLETED = "Completed";

   protected String id;
   protected String type;
   protected String owner;
   protected String item;

   public GoalSummary(String id, String type, String status,
                      String owner, String item) {
      setId(id);
      setType(type);
      setStatus(status);
      setOwner(owner);
      setItem(item);
   }

   public GoalSummary(GoalSummary g) {
      id = g.getId();
      type = g.getType();
      status = g.getStatus();
      owner = g.getOwner();
      item = g.getItem();
   }

   public String getId()    { return id; }
   public String getType()  { return type; }
   public String getOwner() { return owner; }
   public String getItem()  { return item; }

   public String[] summarize() {
      String[] data = { item, owner, id, type, status };
      return data;
   }
   public void setId(String id) {
      Assert.notNull(id);
      this.id = id;
   }
   public void setType(String type) {
      Assert.notNull(type);
      Assert.notFalse( type.equals(DISCRETE) || type.equals(CONTINUOUS) ||
                       type.equals(ENACTMENT) );
      this.type = type;
   }
   public void setStatus(String status) {
      Assert.notNull(status);
      Assert.notFalse( status.equals(RUNNING) || status.equals(SUSPENDED) ||
                       status.equals(CANCELLED) || status.equals(FAILED) ||
                       status.equals(COMPLETED) || status.equals(UNKNOWN) );
      this.status = status;
   }
   public void setOwner(String owner) {
      Assert.notNull(owner);
      this.owner = owner;
   }
   public void setItem(String item) {
      Assert.notNull(item);
      this.item = item;
   }

   public boolean equals(GoalSummary g ) {
      return id.equals(g.getId()) && type.equals(g.getType()) &&
             status.equals(g.getStatus()) && owner.equals(g.getOwner()) &&
             item.equals(g.getItem());
   }

   public String toString() {
      return( "(" +
               ":id " + id + " " +
               ":type " + type + " " +
               ":status " + status +
               ":owner " + owner +
               ":item " + item +
              ")"
            );
   }
}
