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
      ?obs <- (obstacle (north ?var179) (west ?var180) (east ?var181) (south ?var182))
      ?lastMove <- (thisMove (north false) (west false) (east false) (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north true) (west false) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obs)
   )
   (followWall_east9
      ?obst <- (obstacle (north true) (east false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east true) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (followWall_north9
      ?obst <- (obstacle (north false) (west true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north true) (west false) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (followWall_west9
      ?obst <- (obstacle (west false) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west true) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (followWall_south9
      ?obst <- (obstacle (east true) (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east false) (south true))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_east_blocked_east
      ?obst <- (obstacle (east true) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north true) (west false) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_east_blocked_east_openSouth
      ?obst <- (obstacle (east true) (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east false) (south true))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_south_blocked_south
      ?obst <- (obstacle (west true) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east true) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_south_blocked_south_openWest
      ?obst <- (obstacle (west false) (south true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west true) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_north_blocked_north
      ?obst <- (obstacle (north true) (east true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west true) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_north_blocked_north_openEast
      ?obst <- (obstacle (north true) (east false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east true) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_west_blocked_west
      ?obst <- (obstacle (north true) (west true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east false) (south true))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (going_west_blocked_west_openNorth
      ?obst <- (obstacle (north false) (west true))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north true) (west false) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (junctionSouth_goingWest
      ?obst <- (obstacle (south false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (west true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east false) (south true))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (junctionNorth_goingEast
      ?obst <- (obstacle (north false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (east true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north true) (west false) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (junctionWest_goingNorth
      ?obst <- (obstacle (west false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (north true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west true) (east false) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
   (junctionEast_goingSouth
      ?obst <- (obstacle (east false))
      ?agentReg <- (agentRegistered (name ?var15))
      ?lastMove <- (thisMove (south true))
      ?inMaze <- (inMaze (isInMaze true))
      =>
      (modify ?lastMove (north false) (west false) (east true) (south false))
      (send_message (receiver ?var15) (content ?lastMove) (type inform))
      (retract ?obst)
   )
)
