/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase FSM_request_routing
   (route_request
      ?request <- (MonitorRequest)
      =>
      (send_message (receiver FAA) (content ?request) (type inform))
      (retract ?request)
   )
   (route_response
      ?node_status <- (NodeStatus)
      =>
      (send_message (receiver UIA) (content ?node_status) (type inform))
      (retract ?node_status)
   )
)
