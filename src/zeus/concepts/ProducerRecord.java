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

public class ProducerRecord extends RoutingRecord {
   public String supply_ref = null;

   public ProducerRecord(String supply_ref, String use_ref, String comms_key,
                         String producer, String producer_id,
                         String consumer, String consumer_id) {
      this.supply_ref = supply_ref;
      this.use_ref = use_ref;
      this.comms_key = comms_key;
      this.producer = producer;
      this.producer_id = producer_id;
      this.consumer = consumer;
      this.consumer_id = consumer_id;
   }

   public String toString() {
      String out = "(:supply_ref " + supply_ref + " " +
                    ":use_ref " + use_ref + " " +
                    ":comms_key " + comms_key + " " +
                    ":producer " + producer + " " +
                    ":producer_id " + producer_id + " " +
                    ":consumer " + consumer + " " +
                    ":consumer_id " + consumer_id +
                   ")";
      return out;
   }
}
