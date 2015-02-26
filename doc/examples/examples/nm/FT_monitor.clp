/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase FT_monitor
   (activate_fault_table_look_up
      (VCPath (id ?id))
      =>
      (achieve (fact (NodeStatus (id ?id))) (end_time 3) (confirm_time 1))
   )
   (route_results
      ?path <- (VCPath (id ?id))
      ?node_status <- (NodeStatus (id ?id))
      =>
      (send_message (receiver FAA) (content ?node_status) (type inform))
      (retract ?path ?node_status)
   )
)
