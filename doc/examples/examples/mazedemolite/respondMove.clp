/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase respondMove
   (firstLegalMove
      ?move <- (moveMade (moved true))
      ?obst <- (obstacle (north ?var176) (west ?var177) (east ?var178) (south ?var179))
      =>
      (send_message (receiver Navigator) (content ?obst) (type inform))
      (retract ?move)
      (retract ?obst)
   )
   (illegalMove
      ?move <- (moveMade (moved false))
      ?obst <- (obstacle (north ?var176) (west ?var177) (east ?var178) (south ?var179))
      =>
      (send_message (receiver Navigator) (content ?obst) (type inform))
      (retract ?move)
      (retract ?obst)
   )
   (respondReg
      (agentsName (name ?var194))
      ?ar <- (agentRegistered (name ?var199))
      =>
      (send_message (receiver ?var194) (content ?ar) (type inform))
      (assert (inMaze (isInMaze true) (name ?var194)))
   )
   (sendInMaze
      ?im <- (inMaze (isInMaze true) (name ?var269))
      =>
      (send_message (receiver ?var269) (content ?im) (type inform))
   )
   (sendExited
      ?ex <- (mazeExited (id ?varH))
      =>
      (send_message (receiver ?varH) (content ?ex) (type inform))
   )
)
