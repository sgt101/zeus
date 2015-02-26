/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Task1
   (Rule1
      ?var34 <- (sub (name5 ?var35))
      ?var36 <- (super (name5 ?var37))
      =>
      (retract ?var34)
      (send_message (type inform) (content ?var36) (receiver Agent0))
   )
)
