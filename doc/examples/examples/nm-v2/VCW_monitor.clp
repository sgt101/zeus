/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase VCW_monitor
   (activate_db_lookup
      ?request <- (MonitorRequest (id ?id))
      =>
      (achieve (fact (VCPath (id ?id))) (end_time 3) (confirm_time 1))
      (retract ?request)
   )
   (route_results
      ?path <- (VCPath)
      =>
      (send_message (receiver FAA) (content ?path) (type inform))
      (retract ?path)
   )
)
