/*
* The contents of this file are subject to the BT "ZEUS" Open Source 
* Licence (L77741), Version 1.0 (the "Licence"); you may not use this file 
* except in compliance with the Licence. You may obtain a copy of the Licence
* from $ZEUS_INSTALL/licence.html or alternatively from
* http://www.labs.bt.com/projects/agents/zeus/licence.htm
* 
* Except as stated in Clause 7 of the Licence, software distributed under the
* Licence is distributed WITHOUT WARRANTY OF ANY KIND, either express or 
* implied. See the Licence for the specific language governing rights and 
* limitations under the Licence.
* 
* The Original Code is within the package zeus.*.
* The Initial Developer of the Original Code is British Telecommunications
* public limited company, whose registered office is at 81 Newgate Street, 
* London, EC1A 7AJ, England. Portions created by British Telecommunications 
* public limited company are Copyright 1996-2002. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



package zeus.concepts;

import java.io.*;
import java.util.*;
import zeus.util.*;
import zeus.concepts.fn.*;
import zeus.generator.*;
import zeus.generator.code.*;
import zeus.rete.*;

public class ZeusParser {
   public static final AbilityDbItem abilityDbItem(
      OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.abilityDbItem(db);
      }
      catch(Exception e) {
         Core.ERROR(null,1,"Error parsing " + str);
         return null;
      }
   }


   public static final Vector abilityDbItemList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.abilityDbItemList(db);
      }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,2,"Error parsing " + str);
         return null;
      }
   }


   public static final Vector abilitySpecList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.abilitySpecList(db);
      }
      catch(Exception e) {
          e.printStackTrace(); 
         Core.ERROR(null,3,"Error parsing " + str);
         return null;
      }
   }

   public static final AbilitySpec abilitySpec(OntologyDb db, String str) {
       try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.abilitySpec(db);
      }
      catch(Exception e) {
          e.printStackTrace(); 
         Core.ERROR(null,4,"Error parsing " + str);
         return null;
      }
   }
   public static final Acquaintance acquaintance(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.acquaintance(db);
      }
      catch(Exception e) {
         Core.ERROR(null,5,"Error parsing " + str);
         return null;
      }

   }
   public static final Address address(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.address();
      }
      catch(Exception e) {
         Core.ERROR(null,6,"Error parsing " + str);
         return null;
      }
   }
   public static final Vector addressList(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.addressList();
      }
      catch(Exception e) {
          e.printStackTrace(); 
         Core.ERROR(null,7,"Error parsing " + str);
         return null;
      }
   }
   public static final Vector addressList(InputStream stream) {
     try {
         Parser parser = new Parser(stream);
         return parser.addressList();
      }
      catch(Exception e) {
         Core.ERROR(null,61,"Error parsing " + stream);
         e.printStackTrace();
         return null;
      }
   }

   public static final AgentDescription agentDescription(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.agentDescription(db);
      }
      catch(Exception e) {
         Core.ERROR(null,8,"Error parsing " + str);
         return null;
      }

   }
   public static final ConsumerRecord consumerRecord(String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.consumerRecord();
      }
      catch(Exception e) {
         Core.ERROR(null,10,"Error parsing " + str);
         return null;
      }

   }
   public static final Fact fact(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.fact(db);
      }
      catch(Exception e) {
         Core.ERROR(null,11,"Error parsing " + str);
//         e.printStackTrace();
         return null;
      }
   }

   public static final Vector factList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.factList(db);
      }
      catch(Exception e) {
          e.printStackTrace(); 
         Core.ERROR(null,12,"Error parsing " + str);
         return null;
      }
   }
   public static final FactSummary factSummary(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.factSummary(db);
      }
      catch(Exception e) {
         Core.ERROR(null,13,"Error parsing " + str);
         return null;
      }
   }


    public static final Vector goalList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.goalList(db);
      }
      catch(Exception e) {
         Core.ERROR(null,14,"Error parsing " + str);
         return null;
      }
   }

   public static final Goal goal(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.goal(db);
      }
      catch(Exception e) {
         Core.ERROR(null,15,"Error parsing " + str);
         return null;
      }
   }
   public static final GoalSummary goalSummary(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.goalSummary();
      }
      catch(Exception e) {
         Core.ERROR(null,16,"Error parsing " + str);
         return null;
      }
   }
   
   
  
   
   public static final FIPAPerformative fipaPerformative (String str) { 
    try {
        str = str.toLowerCase(); 
        FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
        return parser.Message(); 
    }

 catch (Exception e) { 
	                  /*  try {
	                    java.io.File file = new java.io.File("debugperfparser.out");
	                    java.io.FileOutputStream fileout = new java.io.FileOutputStream(file);
	                    java.io.PrintWriter fw= new java.io.PrintWriter(fileout); 
	                    e.printStackTrace(fw);
	                    fw.flush();
	                    fw.close();} catch (Exception ne) { ne.printStackTrace(); }*/
         e.printStackTrace(); 
       //  Core.ERROR(null,17,"Error parsing " + str);
         return null;
      }
   }
   
   
   public static final Hashtable addresses (String str) {
        try {
        FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
        return parser.Addresses(); 
    }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,17,"Error parsing " + str);
         return null;
      }
   }
   
   
   public static final FIPA_AID_Address fipaAddress(String str) {
        try {
        FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
        return parser.SenderName(); 
    }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,17,"Error parsing " + str);
         return null;
      }
   }
   
   
   /** 
    is this quicker? 
    Should be!
    */
   public static final Performative performative (byte [] bytes) {
     try {
         PerformativeParser parser = new PerformativeParser(new ByteArrayInputStream(bytes));
         return parser.Message();
      }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,17,"Error parsing " + new String (bytes));
         return null;
      }
   }
            
   
   
   
   public synchronized static final Performative performative(String str) {
     try {
        str = str.toLowerCase();
         PerformativeParser parser = new PerformativeParser(new ByteArrayInputStream(str.getBytes()));
         return parser.Message();
      }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,17,"Error parsing " + str);
         return null;
      }
   }
   
   public static final ProducerRecord producerRecord(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.producerRecord();
      }
      catch(Exception e) {
         Core.ERROR(null,18,"Error parsing " + str);
         return null;
      }
   }
   public static final ProtocolInfo protocolInfo(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.protocolInfo(db);
      }
      catch(Exception e) {
         Core.ERROR(null,19,"Error parsing " + str);
         return null;
      }
   }
   public static final StrategyInfo strategyInfo(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.strategyInfo(db);
      }
      catch(Exception e) {
         Core.ERROR(null,20,"Error parsing " + str);
         return null;
      }
   }
   public static final Relationship relationship(String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.relationship();
      }
      catch(Exception e) {
         Core.ERROR(null,21,"Error parsing " + str);
         return null;
      }
   }
   public static final Vector relationshipList(String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.relationshipList();
      }
      catch(Exception e) {
         Core.ERROR(null,210,"Error parsing " + str);
         return null;
      }
   }
   public static final ReportRec reportRec(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reportRec(db);
      }
      catch(Exception e) {
         Core.ERROR(null,22,"Error parsing " + str);
         return null;
      }
   }
   public static final ReservationEntry reservationEntry(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reservationEntry();
      }
      catch(Exception e) {
         Core.ERROR(null,23,"Error parsing " + str);
         return null;
      }
   }
   public static final RoutingRecord routingRecord(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.routingRecord();
      }
      catch(Exception e) {
         Core.ERROR(null,24,"Error parsing " + str);
         return null;
      }
   }
   public static final SuppliedDb suppliedDb(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.suppliedDb(db);
      }
      catch(Exception e) {
         Core.ERROR(null,25,"Error parsing " + str);
         return null;
      }
   }
   public static final SuppliedItem suppliedItem(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.suppliedItem(db);
      }
      catch(Exception e) {
         Core.ERROR(null,26,"Error parsing " + str);
         return null;
      }
   }
   public static final ResolutionContext resolutionContext(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.resolutionContext(db);
      }
      catch(Exception e) {
         Core.ERROR(null,27,"Error parsing " + str);
         return null;
      }

   }
   public static final AbstractTask abstractTask(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.abstractTask(db);
      }
      catch(Exception e) {
         Core.ERROR(null,28,"Error parsing " + str);
         return null;
      }

   }
   public static final PrimitiveTask primitiveTask(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.primitiveTask(db);
      }
      catch(Exception e) {
         Core.ERROR(null,29,"Error parsing " + str);
         return null;
      }

   }
   public static final SummaryTask summaryTask(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.summaryTask(db);
      }
      catch(Exception e) {
         Core.ERROR(null,30,"Error parsing " + str);
         return null;
      }
   }
   public static final PlanScript planScript(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.planScript(db);
      }
      catch(Exception e) {
         Core.ERROR(null,301,"Error parsing " + str);
         return null;
      }
   }
   public static final TaskLink taskLink(String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.taskLink();
      }
      catch(Exception e) {
         Core.ERROR(null,31,"Error parsing " + str);
         return null;
      }
   }
   public static final TaskNode taskNode(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.taskNode(db);
      }
      catch(Exception e) {
         Core.ERROR(null,32,"Error parsing " + str);
         return null;
      }
   }
   public static final TaskSummary taskSummary(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.taskSummary(db);
      }
      catch(Exception e) {
         Core.ERROR(null,33,"Error parsing " + str);
         return null;
      }
   }
   public static final Ordering ordering(String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.ordering();
      }
      catch(Exception e) {
         Core.ERROR(null,34,"Error parsing " + str);
         return null;
      }
   }
   public static final AbstractTask reteKB(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reteKB(db);
      }
      catch(Exception e) {
         Core.ERROR(null,35,"Error parsing " + str);
         return null;
      }
   }
   public static final AbstractTask reteKB(OntologyDb db, InputStream stream) {
      try {
         Parser parser = new Parser(stream);
         return parser.reteKB(db);
      }
      catch(Exception e) {
         Core.ERROR(null,63,"Error parsing\n" + e);
e.printStackTrace();
         return null;
      }
   }
   public static final Rule reteRule(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reteRule(db);
      }
      catch(Exception e) {
         Core.ERROR(null,36,"Error parsing " + str);
         return null;
      }
   }
   public static final Pattern retePattern(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.retePattern(db);
      }
      catch(Exception e) {
         Core.ERROR(null,37,"Error parsing " + str);
         return null;
      }
   }
   public static final Action reteAction(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reteAction(db);
      }
      catch(Exception e) {
         Core.ERROR(null,38,"Error parsing " + str);
         return null;
      }
   }
   public static final Vector reteFactList(OntologyDb db, InputStream stream) {
     try {
         Parser parser = new Parser(stream);
         return parser.reteFactList(db);
      }
      catch(Exception e) {
         Core.ERROR(null,390,"Error parsing reteFactList from stream" + stream);
         return null;
      }
   }
   public static final Vector reteFactList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reteFactList(db);
      }
      catch(Exception e) {
         Core.ERROR(null,391,"Error parsing " + str);
         return null;
      }
   }

   public static final ReteFact reteFact(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reteFact(db);
      }
      catch(Exception e) {
         e.printStackTrace();
         Core.ERROR(null,39,"Error parsing " + str);
         return null;
      }
   }
   
   
   public static final Vector retePatternList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.retePatternList(db);
      }
      catch(Exception e) {
        e.printStackTrace();
         Core.ERROR(null,40,"Error parsing " + str);
         return null;
      }
   }
   public static final Vector reteActionList(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.reteActionList(db);
      }
      catch(Exception e) {
        e.printStackTrace(); 
         Core.ERROR(null,62,"Error parsing " + str);
         return null;
      }
   }
   public static final AgentInfo agentInfo(
     GeneratorModel genmodel, GenerationPlan genplan, String str){
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.agentInfo(genmodel,genplan);
      }
      catch(Exception e) {
         Core.ERROR(null,41,"Error parsing " + str);
         return null;
      }
   }
   public static final DbProxyInfo dbProxyInfo(String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.dbProxyInfo();
      }
      catch(Exception e) {
         Core.ERROR(null,42,"Error parsing " + str);
         return null;
      }
   }
   public static final FacilitatorInfo facilitatorInfo(GenerationPlan genplan, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.facilitatorInfo(genplan);
      }
      catch(Exception e) {
         Core.ERROR(null,43,"Error parsing " + str);
         return null;
      }
   }
   public static final VisualiserInfo visualiserInfo(GenerationPlan genplan, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.visualiserInfo(genplan);
      }
      catch(Exception e) {
         Core.ERROR(null,44,"Error parsing " + str);
         return null;
      }
   }
   public static final NameserverInfo nameserverInfo(String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.nameserverInfo();
      }
      catch(Exception e) {
         Core.ERROR(null,45,"Error parsing " + str);
         return null;
      }
   }
   public static final TaskInfo taskInfo(GeneratorModel genmodel, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.taskInfo(genmodel);
      }
      catch(Exception e) {
         Core.ERROR(null,46,"Error parsing " + str);
         return null;
      }
   }
   
   
   public static final Vector taskList(OntologyDb db, String str) {
     try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.taskList(db);
      }
      catch(Exception e) {
         Core.ERROR(null,47,"Error parsing " + str);
         return null;
      }
   }
   
   
   public static final ValueFunction Expression(String str) {
      return Expression(null,str);
   }
   
   
   public static final ValueFunction Expression(OntologyDb db, String str) {
      try {
         Parser parser = new Parser(new ByteArrayInputStream(str.getBytes()));
         return parser.Expression(db);
      }
      catch(Exception e) {
         return null;
      }
   }
   
   
    public static final FIPA_AMS_Management_Content FIPA_AMS_Management_Content (String str) { 
         try {
            FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
            return parser.FIPA_AMS_Management_Content(); 
          }
    catch (Exception e) { 
	                   /* try {
	                    java.io.File file = new java.io.File("debugperfparser.out");
	                    java.io.FileOutputStream fileout = new java.io.FileOutputStream(file);
	                    java.io.PrintWriter fw= new java.io.PrintWriter(fileout); 
	                    e.printStackTrace(fw);
	                    fw.flush();
	                    fw.close();} catch (Exception ne) { ne.printStackTrace(); }   */
	                    e.printStackTrace();
		return (null);
      }
   }
   
   
    public static final FIPA_DF_Management_Content FIPA_DF_Management_Content (String str) { 
         try {
            FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
            return parser.FIPA_DF_Management_Content(); 
          }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,49,"Error parsing " + str);
         return null;
      }
   }
   
    public static final DF_Description DF_Content (String str) { 
         try {
            FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
            return parser.DFDescription(); 
          }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,49,"Error parsing " + str);
         return null;
      }
   }
   
    public static final Vector descriptionSet (String str) { 
   try {
            FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
            return parser.descriptions(); 
          }
      catch(Exception e) {
         e.printStackTrace(); 
         return null;
      }
    }
    
   public static final StringHashtable Transports (String str) { 
         try {
            FIPAParser parser = new FIPAParser (new ByteArrayInputStream(str.getBytes()));
            return parser.Transports(); 
          }
      catch(Exception e) {
         e.printStackTrace(); 
         Core.ERROR(null,50,"Error parsing " + str);
         return null;
      }
   }

    public static void main (String argv[]) {
	descriptionSet (argv[0]); 
    }
}
