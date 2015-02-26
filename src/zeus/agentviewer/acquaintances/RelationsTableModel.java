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
import zeus.actors.OrganisationDb;
import zeus.util.OrderedHashtable;

import zeus.actors.event.*;
import zeus.actors.*;
import zeus.concepts.*;




public class RelationsTableModel  extends AbstractTableModel implements RelationMonitor{

      private String   AGENT        = "Agent";
      private String   RELATION     = "Relation";

      private String[] header = {AGENT,RELATION};
      private OrderedHashtable   data;
      private OrganisationDb organisationDb;
//------------------------------------------------------------------------------
      public RelationsTableModel(AgentContext context){
          data = new OrderedHashtable();
          organisationDb = context.OrganisationDb();
          organisationDb.addRelationMonitor(this,
             RelationEvent.ADD_MASK | RelationEvent.DELETE_MASK |
             RelationEvent.MODIFY_MASK, true );
      }
//------------------------------------------------------------------------------
       public int getRowCount() {
           return data.size();
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
       public void setValueAt(Object value, int row, int col) {
       }
//------------------------------------------------------------------------------
       public Object getValueAt(int row, int col) {
            String name = (String) data.getKeyAt(row);

            if (getColumnName(col).equals(AGENT))  {
                return  (name);
             }
             else if (getColumnName(col).equals(RELATION))  {
                return   (String) data.get(name);
             }
             else return new String("Error in AgentRelationTableModel at getValueAt");

       }
//------------------------------------------------------------------------------
       public String getColumnName(int col) {
            return  header[col];
       }
//------------------------------------------------------------------------------
       public void AddRelation(String name, String rel){
          data.put(name,rel);
          fireTableDataChanged();
       }
//------------------------------------------------------------------------------
       public void ModifyRelation(String name, String newRelation){
             data.put(name,newRelation);
             fireTableDataChanged();
       }

//------------------------------------------------------------------------------
       public void removeRelation(String rel){
          data.remove(rel);
          fireTableDataChanged();
       }

//------------------------------------------------------------------------------
       public String getName(int row){
          return (String) data.getKeyAt(row);
       }
//------------------------------------------------------------------------------
       public void relationAddedEvent(RelationEvent event) {
         AddRelation(event.getAgent(),event.getRelation());
       }
//------------------------------------------------------------------------------
       public void relationModifiedEvent(RelationEvent event) {
         ModifyRelation(event.getAgent(),event.getRelation());
       }
//------------------------------------------------------------------------------
       public void relationDeletedEvent(RelationEvent event) {
         removeRelation(event.getAgent());
       }
//------------------------------------------------------------------------------
       public void relationAccessedEvent(RelationEvent event) {}
//------------------------------------------------------------------------------
       public void removeZeusEventMonitors(){
         organisationDb.removeRelationMonitor(this,
            RelationEvent.ADD_MASK | RelationEvent.DELETE_MASK |
            RelationEvent.MODIFY_MASK);
       }

}