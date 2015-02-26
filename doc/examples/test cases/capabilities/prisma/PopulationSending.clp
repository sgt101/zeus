/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase PopulationSending
   (logging
      ?var43 <- (JOPopulation (status unknown) (name ?forAgent) (className ?var46) (individuals ?var47))
      ?var48 <- (JOPopulation (status improved) (name ?forAgent) (className ?var51) (individuals ?var52))
      =>
      (send_message (type inform) (content ?var43) (receiver Logger))
      (send_message (type inform) (content ?var48) (receiver Logger))
      (send_message (type inform) (content ?var48) (receiver ?forAgent))
      (retract ?var43)
      (retract ?var48)
   )
)
