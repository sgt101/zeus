/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Task0
   (Rule0
      ?var12 <- (super (name5 ?var13))
      ?var32 <- (sub (name5 ?var33))
      =>
      (retract ?var12)
      (send_message (type inform) (content ?var32) (receiver Agent1))
   )
)
