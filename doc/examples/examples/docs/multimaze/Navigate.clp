/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Navigate
   (start
      ?obs <- (obstacle (west ?var180) (east ?var181) (south ?var182) (north ?var179))
      ?lastMove <- (thisMove (west false) (east false) (south false) (north false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south false) (north true))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obs)
   )
   (followWall_east
      ?obst <- (obstacle (east false) (north true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east true) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (followWall_north
      ?obst <- (obstacle (west true) (north false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south false) (north true))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (followWall_west
      ?obst <- (obstacle (west false) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west true) (east false) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (followWall_south
      ?obst <- (obstacle (east true) (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south true) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_east_blocked_east
      ?obst <- (obstacle (east true) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south false) (north true))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_east_blocked_east_openSouth
      ?obst <- (obstacle (east true) (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south true) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_south_blocked_south
      ?obst <- (obstacle (west true) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east true) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_south_blocked_south_openWest
      ?obst <- (obstacle (west false) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west true) (east false) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_north_blocked_north
      ?obst <- (obstacle (east true) (north true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west true) (east false) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_north_blocked_north_openEast
      ?obst <- (obstacle (east false) (north true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east true) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_west_blocked_west
      ?obst <- (obstacle (west true) (north true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south true) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (going_west_blocked_west_openNorth
      ?obst <- (obstacle (west true) (north false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south false) (north true))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (junctionSouth_goingWest
      ?obst <- (obstacle (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south true) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (junctionNorth_goingEast
      ?obst <- (obstacle (north false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east false) (south false) (north true))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (junctionWest_goingNorth
      ?obst <- (obstacle (west false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west true) (east false) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
   (junctionEast_goingSouth
      ?obst <- (obstacle (east false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (west false) (east true) (south false) (north false))
      (send_message (type inform) (content ?lastMove) (receiver ?var15))
      (retract ?obst)
   )
)
