/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Task2
   (Rule0
      ?ifcond <- (super (name5 ?var25))
      =>
      (if (?ifcond == ?ifcond) then
 (retract ?ifcond)
 else
 (send_message (type inform) (content bubble) (receiver Agent1)))
   )
)