/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase UIA_request_manager
   (forward_user_request
      ?monitor_request <- (MonitorRequest)
      =>
      (send_message (receiver FSM) (content ?monitor_request) (type inform))
      (retract ?monitor_request)
   )
   (notify_user
      ?node_status <- (NodeStatus (status_4 ?s4) (status_3 ?s3) (status_2 ?s2) (status_1 ?s1) (element_4 ?e4) (element_3 ?e3) (element_2 ?e2) (element_1 ?e1) (id ?id))
      =>
      (println t Path ?id "has status" ?s1 "at node" ?e1 "; status" ?s2 "at node" ?e2 "; status" ?s3 "at node" ?e3 "; status" ?s4 "at node" ?e4)
      (retract ?node_status)
   )
)
