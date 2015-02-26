/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase FAA_path_manager
   (route_request
      ?request <- (MonitorRequest)
      =>
      (send_message (receiver VCW) (content ?request) (type inform))
      (retract ?request)
   )
   (route_response_to_FTM
      ?path <- (VCPath)
      =>
      (send_message (receiver FTM) (content ?path) (type inform))
      (retract ?path)
   )
   (route_response_to_FSM
      ?node_status <- (NodeStatus)
      =>
      (send_message (receiver FSM) (content ?node_status) (type inform))
      (retract ?node_status)
   )
)
