/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase Handelsplatz
   (Registrierung
      ?aN <- (Registriert (name ?Name))
      =>
      (send_message (type inform) (content ?aN) (receiver ?Name))
      (retract ?aN)
   )
   (CFP
      ?var <- (CFP (name ?agName))
      =>
      (send_message (type inform) (content ?var) (receiver ?agName))
      (retract ?var)
   )
   (Abmeldung
      ?aN <- (Abgemeldet (name ?Name))
      =>
      (send_message (type inform) (content ?aN) (receiver ?Name))
      (retract ?aN)
   )
   (Ergebnis
      ?aN <- (Ergebnis (name ?Name))
      =>
      (send_message (type inform) (content ?aN) (receiver ?Name))
      (retract ?aN)
   )
)
