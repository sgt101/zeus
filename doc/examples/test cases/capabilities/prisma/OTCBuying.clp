/*
	This software was produced as a part of research
	activities. It is not intended to be used as commercial
	or industrial software by any organisation. Except as
	explicitly stated, no guarantees are given as to its
	reliability or trustworthiness if used for purposes other
	than those for which it was originally intended.
 
	(c) British Telecommunications plc 1999.
*/

(:Rulebase OTCBuying
   (EinkaufangebotErstellen2a
      (TradingOpen (time ?T))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (unitCost ?ProdUnitCost) (quantity ?ProdQty) (time ?T))
      (ProductionCostOnChange (targetQuantity ?TargetQtyPlus) (targetQuantityUnitCost ?PlusUnitCost) (actualQuantity ?ProdQty) (time ?T))
      (ProductionCostOnChange (targetQuantity ?TargetQtyMinus) (targetQuantityUnitCost ?MinusUnitCost) (actualQuantity ?ProdQty) (time ?T))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (test (?ProdQty <= ?TargetQtyPlus))
      (test (?ProdQty >= ?TargetQtyMinus))
      (test (?CapQty >= ?ProdQty))
      (test (((?Mcp - ?PlusUnitCost) * (?TargetQtyPlus - ?ProdQty)) <= (((?ProdUnitCost - ?Mcp) * ?ProdQty) + ((?Mcp - ?MinusUnitCost) * ?TargetQtyMinus))))
      =>
      (if (5 < (?ProdQty - ?TargetQtyMinus)) then
 (bind ?demandQty 5)
 else
 (bind ?demandQty (?ProdQty - ?TargetQtyMinus)))
      (assert (OTCDemand (status init) (time ?T) (quantity ?demandQty) (expectedCost ?PlusUnitCost)))
   )
   (EinkaufangebotErstellen2b
      (TradingOpen (time ?T))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (unitCost ?ProdUnitCost) (quantity ?ProdQty) (time ?T))
      (ProductionCostOnChange (targetQuantity ?TargetQtyMinus) (targetQuantityUnitCost ?MinusUnitCost) (actualQuantity ?ProdQty) (time ?T))
      (SpinningReserve (price ?SprPrice) (time ?T))
      (test (?ProdQty >= ?TargetQtyMinus))
      (test (?CapQty < ?ProdQty))
      =>
      (if (5 < (?ProdQty - ?CapQty)) then
 (bind ?demandQty 5)
 else
 (bind ?demandQty (?ProdQty - ?CapQty)))
      (assert (OTCDemand (status init) (time ?T) (quantity ?demandQty) (expectedCost ?SprPrice)))
   )
   (EinkaufpreisFestlegen8a
      ?demandInit <- (OTCDemand (status init) (time ?T) (quantity ?OtcQty) (expectedCost ?OtcExpCost) (id ?DemandId))
      (MyName (name ?TheName))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (quantity ?ProdQty) (time ?T))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (RiskAttitude (attitude friendly))
      (test (?CapQty < ?ProdQty))
      =>
      (modify ?demandInit (status isOpen) (expectedCost (1.5 * ?Mcp)))
   )
   (EinkaufpreisFestlegen8b
      ?demandInit <- (OTCDemand (status init) (time ?T) (quantity ?OtcQty) (expectedCost ?OtcExpCost) (id ?DemandId))
      (MyName (name ?TheName))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (quantity ?ProdQty) (time ?T))
      (SpinningReserve (price ?SprPrice) (time ?T))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (not (RiskAttitude (attitude friendly)))
      (test (?CapQty < ?ProdQty))
      =>
      (modify ?demandInit (status isOpen) (expectedCost (0.9 * ?SprPrice)))
   )
   (EinkaufpreisFestlegen8cUnd8d
      ?demandInit <- (OTCDemand (status init) (time ?T) (quantity ?OtcQty) (expectedCost ?OtcExpCost) (id ?DemandId))
      (MyName (name ?TheName))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (quantity ?ProdQty) (time ?T))
      (UtilizationRate (rate ?IsRate) (time ?T))
      (UtilizationTarget (rate ?TargetRate) (time ?T) (delta ?TargetDelta))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (test (?CapQty >= ?ProdQty))
      (test (?IsRate > (?TargetRate * (1 + ?TargetDelta))))
      =>
      (if (?OtcExpCost > ?Mcp) then
 (bind ?myPrice ?Mcp)
 else
 (bind ?myPrice ?OtcExpCost))
      (modify ?demandInit (status isOpen) (expectedCost ?myPrice))
   )
   (EinkaufpreisFestlegen8eUnd8f
      ?demandInit <- (OTCDemand (status init) (time ?T) (quantity ?OtcQty) (expectedCost ?OtcExpCost) (id ?DemandId))
      (MyName (name ?TheName))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (quantity ?ProdQty) (time ?T))
      (UtilizationRate (rate ?IsRate) (time ?T))
      (UtilizationTarget (rate ?TargetRate) (time ?T) (delta ?TargetDelta))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (test (?CapQty >= ?ProdQty))
      (test (?IsRate < (?TargetRate * (1 - ?TargetDelta))))
      =>
      (if (?OtcExpCost > ?Mcp) then
 (bind ?myPrice ?Mcp)
 else
 (bind ?myPrice ?OtcExpCost))
      (modify ?demandInit (status isOpen) (expectedCost ?myPrice))
   )
   (EinkaufpreisFestlegen8g
      ?demandInit <- (OTCDemand (status init) (time ?T) (quantity ?OtcQty) (expectedCost ?OtcExpCost) (id ?DemandId))
      (MyName (name ?TheName))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (quantity ?ProdQty) (time ?T))
      (UtilizationRate (rate ?IsRate) (time ?T))
      (UtilizationTarget (rate ?TargetRate) (time ?T) (delta ?TargetDelta))
      (RiskAttitude (attitude friendly) (acceptableLoss ?AcceptableLoss))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (test (?CapQty >= ?ProdQty))
      (test (?IsRate <= (?TargetRate * (1 + ?TargetDelta))))
      (test (?IsRate >= (?TargetRate * (1 - ?TargetDelta))))
      (test (((?OtcExpCost / ?Mcp) - 1) > ?AcceptableLoss))
      =>
      (modify ?demandInit (status isOpen) (expectedCost ((1 - ?AcceptableLoss) * ?OtcExpCost)))
   )
   (EinkaufpreisFestlegen8hUnd8i
      ?demandInit <- (OTCDemand (status init) (time ?T) (quantity ?OtcQty) (expectedCost ?OtcExpCost) (id ?DemandId))
      (MyName (name ?TheName))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (Production (quantity ?ProdQty) (time ?T))
      (UtilizationRate (rate ?IsRate) (time ?T))
      (UtilizationTarget (rate ?TargetRate) (time ?T) (delta ?TargetDelta))
      (not (RiskAttitude (attitude friendly)))
      (PXMarketData (mcp ?Mcp) (time ?T))
      (test (?CapQty >= ?ProdQty))
      (test (?IsRate <= (?TargetRate * (1 + ?TargetDelta))))
      (test (?IsRate >= (?TargetRate * (1 - ?TargetDelta))))
      (test (?OtcExpCost > ?Mcp))
      =>
      (if (?OtcExpCost > ?Mcp) then
 (bind ?MyPrice ?Mcp)
 else
 (bind ?MyPrice ?OtcExpCost))
      (modify ?demandInit (status isOpen) (expectedCost ?MyPrice))
   )
   (ErfolgreichenEinkaufVerbuchen6a
      ?otcAvgPrice <- (OTCAveragePrice (avgprice ?AvgPrice) (time ?T))
      ?otcDemand <- (OTCDemand (status isOpen) (quantity ?OtcQty) (time ?T) (id ?DemandId))
      ?otcTrading <- (OTCTrading (quantity ?TradingQty) (time ?T))
      ?production <- (Production (quantity ?ProdQty) (time ?T))
      ?contract <- (OTCContract (status ?ContractStatus) (name ?OtherName) (price ?OtcPrice) (time ?T) (quantity ?OtcQty))
      (test (?ContractStatus != accountedFor))
      (not (MyName (name ?OtherName)))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (TradingOpen (time ?T))
      (test ((?ProdQty - ?OtcQty) >= 0))
      =>
      (modify ?otcAvgPrice (avgprice (((?AvgPrice * ?TradingQty) - (?OtcQty * ?OtcPrice)) / (?TradingQty - ?OtcQty))))
      (modify ?otcDemand (status bought))
      (modify ?otcTrading (quantity (?TradingQty - ?OtcQty)))
      (modify ?production (quantity (?ProdQty - ?OtcQty)))
      (modify ?contract (status accountedFor) (demandId ?DemandId))
   )
   (ErfolgreichenEinkaufVerbuchen6b
      ?otcAvgPrice <- (OTCAveragePrice (avgprice ?AvgPrice) (time ?T))
      ?otcDemand <- (OTCDemand (status isOpen) (quantity ?OtcQty) (time ?T) (id ?DemandId))
      ?otcTrading <- (OTCTrading (quantity ?TradingQty) (time ?T))
      ?production <- (Production (quantity ?ProdQty) (time ?T))
      ?contract <- (OTCContract (status ?ContractStatus) (name ?OtherName) (price ?OtcPrice) (time ?T) (quantity ?OtcQty))
      (test (?ContractStatus != accountedFor))
      (not (MyName (name ?OtherName)))
      (CapacityAt (quantity ?CapQty) (time ?T))
      (TradingOpen (time ?T))
      (test ((?ProdQty - ?OtcQty) < 0))
      =>
      (modify ?otcAvgPrice (avgprice (((?AvgPrice * ?TradingQty) - (?OtcQty * ?OtcPrice)) / (?TradingQty - ?OtcQty))))
      (modify ?otcDemand (status bought))
      (modify ?otcTrading (quantity (?TradingQty - ?OtcQty)))
      (modify ?production (quantity (?ProdQty - ?OtcQty)))
      (modify ?contract (status accountedFor) (demandId ?DemandId))
   )
)
