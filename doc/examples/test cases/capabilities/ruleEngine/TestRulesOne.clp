/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase TestRulesOne
   (SimpleRule
      ?var1 <- (Entity (unit_cost ?var2) (number ?var3))
      =>
      (retract ?var1)
   )
   (ifRule
      (Entity (unit_cost ?var14) (number ?var15))
      =>
      (if true then
 (send_message (receiver ?var3) (reply-with ?var5) (reply-by ?var7) (reply-to ?var12) (in-reply-to ?var6) (language ?var9) (sender ?var2) (content ?var4) (ontology ?var8) (type ?var1) (conversation-id ?var11) (protocol ?var10)))
   )
)
