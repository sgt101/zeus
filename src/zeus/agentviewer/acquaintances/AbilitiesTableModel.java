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
* public limited company are Copyright 1996-9. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



package zeus.agentviewer.acquaintances;

import javax.swing.table.*;
import javax.swing.*;
import java.util.*;
import zeus.concepts.AbilitySpec;
import zeus.concepts.Fact;
import zeus.util.OrderedHashtable;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.concepts.*;
import zeus.ontology.*;



public class AbilitiesTableModel  extends AbstractTableModel 
                                  implements AbilityMonitor{

      private static final int FACT     = 0;
      private static final int COST     = 1;
      private static final int DURATION = 2;

      private String[] header = {"Fact","Cost","Duration"};
      private Vector   data ;
      private String agent;

      private OrderedHashtable allAbilities;
      OrganisationDb organisationDb;
      OntologyDb ontologyDb;
//------------------------------------------------------------------------------
      public AbilitiesTableModel(AgentContext context){
          allAbilities = new OrderedHashtable();
          organisationDb = context.OrganisationDb();
          ontologyDb = context.OntologyDb();
          data = null;
          agent = null;
          organisationDb.addAbilityMonitor(this,
             AbilityEvent.ADD_MASK | AbilityEvent.DELETE_MASK |
             AbilityEvent.MODIFY_MASK,true );
      }

//------------------------------------------------------------------------------
       public int getRowCount() {

             return (data == null )? 0:data.size();
       }
//------------------------------------------------------------------------------
       public int getColumnCount(){
           return header.length;
       }
//------------------------------------------------------------------------------
       public boolean isCellEditable(int row, int col) {
          return false;
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {

           AbilitySpec des = (AbilitySpec) data.elementAt(row);
            switch(col) {
                case FACT:
                     return  des.getType();
                case COST:
                     return new Double(des.getCost() ) ;
                case DURATION:
                     return new Integer(des.getTime() );
            }
            return null;
       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) { return  header[col]; }
//------------------------------------------------------------------------------
       boolean validateInput(String str){
           try {
              Integer.parseInt(str.trim());
           }
           catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(null,"Invalid Input","Enter values",
                                            JOptionPane.ERROR_MESSAGE);
              return false;
           }
           return true;
       }
//------------------------------------------------------------------------------

        public Fact  getAttributesof(int row){
          return ((AbilitySpec)data.elementAt(row)).getFact();
       }
//------------------------------------------------------------------------------
       void makeFact(String name){
           Fact f = ontologyDb.getFact(Fact.VARIABLE,name);
           AbilitySpec a = new AbilitySpec(f,0,0);
           organisationDb.add(agent,a);
           System.out.println("make fact:agent  "+ agent);
       }
//------------------------------------------------------------------------------
       public void addAbility(String agent, AbilitySpec ability) {
          if (allAbilities.containsKey(agent)) {
            Vector abilities = (Vector) allAbilities.get(agent);
            if (abilities.contains(ability)) return;
             abilities.addElement(ability);
          }
          else {
            Vector abilities = new Vector();
            abilities.addElement(ability);
            allAbilities.put(agent,abilities);
          }
          fireTableDataChanged();
       }

//------------------------------------------------------------------------------
       public void removeAbility(String agent, AbilitySpec ability) {
          //System.out.println("removing ability: " + ability);
          if (allAbilities.containsKey(agent)) {
            Vector abilities = (Vector) allAbilities.get(agent);

            /*
            System.out.println("all:  " + abilities);
            abilities.removeElement(ability);
            */
            for (int i = 0; i < abilities.size(); i++) {
               AbilitySpec anAbility = (AbilitySpec) abilities.elementAt(i);
               if ( anAbility.equals(ability) ) {
                // System.out.println("removed");
                 abilities.removeElement(anAbility);
                 // no abilities, so remove
                 if (abilities.isEmpty())
                  allAbilities.remove(agent);
                 break;
               }
            }

          }
          fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public void modifyAbility(String agent, AbilitySpec ability) {
          if (allAbilities.containsKey(agent)) {
            Vector abilities = (Vector) allAbilities.get(agent);
            for (int i = 0; i < abilities.size(); i++) {
               AbilitySpec anAbility = (AbilitySpec) abilities.elementAt(i);
               if ( anAbility.getType().equals(ability.getType()) ) {
                 anAbility = ability;
                 break;
               }
            }

          }
          fireTableDataChanged();
       }

//------------------------------------------------------------------------------
       public AbilitySpec getAbility(String agent, String afactType) {

          if (allAbilities.containsKey(agent)) {
            Vector abilities = (Vector) allAbilities.get(agent);
             for (int i = 0; i < abilities.size(); i++) {
               AbilitySpec anAbility = (AbilitySpec) abilities.elementAt(i);
               if ( anAbility.getType().equals(afactType) )
                 return anAbility;
             }
          }
          return null;
       }
//------------------------------------------------------------------------------
       public AbilitySpec getAbility(String afactType) {

            Vector abilities = (Vector) allAbilities.get(getAgent());
             for (int i = 0; i < abilities.size(); i++) {
               AbilitySpec anAbility = (AbilitySpec) abilities.elementAt(i);
               if ( anAbility.getType().equals(afactType) )
                 return anAbility;
             }
             return null;
       }


//------------------------------------------------------------------------------
       public void setToNull(){
          data = null;
          fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public void setAbilitiesof(String agent){
            data = (Vector) allAbilities.get(agent);
            this.agent = agent;
            fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public String getAgent(){
          return agent;
       }
//------------------------------------------------------------------------------
       void deleteFact(int row){
         String factType = (String) getValueAt(row,0);
         organisationDb.del(agent,getAbility(agent,factType));
       }
//------------------------------------------------------------------------------
       public boolean hasAbilities(String agent){
            if (allAbilities.containsKey(agent) == false) {
              data = null;
              this.agent = agent;
              fireTableDataChanged();
              return false;
            }
            else
             return true;
       }

//------------------------------------------------------------------------------
      public void abilityAddedEvent(AbilityEvent event) {
        addAbility(event.getAgent(), event.getAbility());
      }
//------------------------------------------------------------------------------
     public void abilityModifiedEvent(AbilityEvent event) {
       System.out.println("Called event modified");
        modifyAbility(event.getAgent(), event.getAbility());
     }
//------------------------------------------------------------------------------
     public void abilityDeletedEvent(AbilityEvent event) {
       removeAbility(event.getAgent(), event.getAbility());
     }
//------------------------------------------------------------------------------
     public void abilityAccessedEvent(AbilityEvent event) {}

//------------------------------------------------------------------------------
       public void removeZeusEventMonitors(){
         organisationDb.removeAbilityMonitor(this,
                                           AbilityEvent.ADD_MASK | AbilityEvent.DELETE_MASK
                                           | AbilityEvent.MODIFY_MASK);
        
       }


}
