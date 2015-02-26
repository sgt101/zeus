/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase LoadForecasting
   (sendtoLoggerAndLoadGenerator
      ?JOLF <- (JOLoadForecast (status ?var430) (name ?forAgent) (className ?var432) (expectedLoad ?var433) (toTime ?var434) (fromTime ?var435))
      =>
      (if (true == false) then
 (send_message (type inform) (content ?JOLF) (receiver ?Logger)) (send_message (type inform) (content ?JOLF) (receiver ?LoadGenerator)))
   )
   (sendRealisedLoad
      ?tl <- (TariffLoad (status asRequested) (forAgent ?Receiver))
      =>
      (send_message (type inform) (content ?tl) (receiver ?Receiver))
   )
)
