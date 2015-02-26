/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Handel
   (sendGebot
      ?gebot <- (Gebot (verkauf_leistung ?var93) (name ?agentName) (kauf_preis ?var91) (kauf_leistung ?var95) (verkauf_preis ?var92))
      =>
      (send_message (type inform) (content ?gebot) (receiver Boerse))
      (retract ?gebot)
   )
)
