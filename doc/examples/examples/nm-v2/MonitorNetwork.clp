/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase MonitorNetwork
   (notify_ftm
      ?element_status <- (NetworkElementStatus)
      =>
      (send_message (receiver FTM) (content ?element_status) (type inform))
      (retract ?element_status)
   )
)
