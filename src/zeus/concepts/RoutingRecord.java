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

public class RoutingRecord {
   public String comms_key = null;
   public String use_ref = null;
   public String producer = null;
   public String producer_id = null;
   public String consumer = null;
   public String consumer_id = null;

   protected RoutingRecord() {
   }

   public RoutingRecord(String producer, String producer_id,
                        String consumer, String consumer_id,
                        String use_ref, String comms_key) {
      this.use_ref = use_ref;
      this.comms_key = comms_key;
      this.producer = producer;
      this.producer_id = producer_id;
      this.consumer = consumer;
      this.consumer_id = consumer_id;
   }

   public String toString() {
      String out = "(:producer " + producer + " " +
                    ":producer_id " + producer_id + " " +
                    ":consumer " + consumer + " " +
                    ":consumer_id " + consumer_id + " " +
                    ":use_ref " + use_ref + " " +
                    ":comms_key " + comms_key +
                   ")";
      return out;
   }

   public boolean equals(Object ob) {
      if ( !(ob instanceof RoutingRecord) ) return false;
      RoutingRecord rec = (RoutingRecord)ob;
      return rec.producer.equals(producer) &&
             rec.producer_id.equals(producer_id) &&
             rec.consumer.equals(consumer) &&
             rec.consumer_id.equals(consumer_id) &&
             rec.use_ref.equals(use_ref);
   }
}
