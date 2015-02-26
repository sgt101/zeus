/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Register
   (registerWithEnvironment
      ?aN <- (agentsName (name ?var6))
      =>
      (send_message (receiver Environment) (content ?aN) (type inform))
   )
   (exitedMaze9
      ?me <- (mazeExited (id ?var6))
      ?obst <- (obstacle)
      ?aR <- (agentRegistered (name ?var19))
      ?tM <- (thisMove)
      (agentsName (name ?var33))
      =>
      (retract ?me)
      (retract ?obst)
      (retract ?aR)
      (retract ?tM)
      (assert (thisMove (north false) (west false) (east false) (south false) (id ?var33)))
   )
)
