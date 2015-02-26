/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Reactor
   (reaction
      (Fault (type ?t) (id ?id))
      =>
      (achieve (fact (Repair (type ?t) (fault ?id))) (end_time 9) (confirm_time 3))
   )
   (clear_fault
      ?fault <- (Fault (type ?type) (id ?id))
      ?fix <- (Repair (type ?type) (fault ?id))
      =>
      (send_message (type inform) (content ?fix) (receiver WorldSimulator))
      (retract ?fault ?fix)
   )
)
