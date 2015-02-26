/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase FaultSimulator
   (notify_agent
      ?fault <- (Fault (owner ?owner))
      =>
      (send_message (type inform) (content ?fault) (receiver ?owner))
   )
   (clear_fault
      ?fault <- (Fault (id ?id))
      ?fix <- (Repair (fault ?id))
      =>
      (retract ?fault ?fix)
   )
)
