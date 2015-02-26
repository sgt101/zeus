/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase OTCSteuerung
   (ErreichenDerDeadlineS1
      (GMT (time ?T))
      ?tradingOpen <- (TradingOpen (deadline ?T) (time ?forTime))
      (MyName (name ?MyName))
      =>
      (assert (TariffLoad (status requested) (time ?forTime) (forAgent ?MyName)))
      (retract ?tradingOpen)
   )
   (NeueZeit
      (GMT (time ?GmtTime))
      ?old <- (GMT (time ?OldTime))
      (test (?OldTime < ?GmtTime))
      =>
      (retract ?old)
   )
   (KostenaenderungswissenLoeschenS4
      (Production (quantity ?ProdQty) (time ?T))
      ?oldProd <- (ProductionCostOnChange (actualQuantity ?ActQty) (time ?T))
      (test (?ActQty != ?ProdQty))
      =>
      (retract ?oldProd)
   )
   (ProduktionRealisiertS5
      (TariffLoad (status asRequested) (quantity ?TariffQty) (time ?T))
      (OTCTrading (quantity ?TradingQty) (time ?T))
      (PXTrading (quantity ?PXQty) (time ?T))
      =>
      (assert (Production (status realised) (quantity ((?TariffQty + ?TradingQty) + ?PXQty)) (time ?T)))
   )
   (TariffLoadRequestS6
      ?tl <- (TariffLoad (status requested) (time ?T))
      ?lf <- (LoadForecast (time ?T))
      (MyName (name ?MyName))
      =>
      (modify ?lf (forAgent ?MyName))
      (send_message (type inform) (content ?lf) (receiver LoadForecastService))
      (retract ?tl)
   )
)
