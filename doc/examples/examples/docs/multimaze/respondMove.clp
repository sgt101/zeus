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
      ?moveFlag <- (moveMade (moved true) (id ?agName))
      ?obst <- (obstacle (west ?var177) (east ?var178) (south ?var179) (north ?var176) (id ?agName))
      =>
      (send_message (type inform) (content ?obst) (receiver ?agName))
      (retract ?moveFlag)
      (retract ?obst)
   )
   (illegalMove
      ?moveFlag <- (moveMade (moved true) (id ?agName))
      ?lastMove <- (thisMove (west ?var20) (east ?var21) (south ?var22) (north ?var19) (id ?agName))
      ?obst <- (obstacle (west ?var177) (east ?var178) (south ?var179) (north ?var176) (id ?agName))
      =>
      (send_message (type inform) (content ?obst) (receiver ?agName))
      (retract ?moveFlag)
      (retract ?obst)
      (retract ?lastMove)
   )
   (respondReg
      (agentsName (name ?agName))
      ?ar <- (agentRegistered (name ?envName))
      =>
      (send_message (type inform) (content ?ar) (receiver ?agName))
      (assert (inMaze (isInMaze true) (name ?agName)))
   )
   (sendInMaze
      ?im <- (inMaze (isInMaze true) (name ?var269))
      =>
      (send_message (type inform) (content ?im) (receiver ?var269))
   )
   (sendExited
      ?ex <- (mazeExited (id ?varH))
      =>
      (send_message (type inform) (content ?ex) (receiver ?varH))
   )
)
