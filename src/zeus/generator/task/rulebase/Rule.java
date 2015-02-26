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



package zeus.generator.task.rulebase;

import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.Color;

public class Rule implements DocumentListener  {

      String name;
      int priority;
      PlainDocument lhs;
      PlainDocument rhs;
      protected EventListenerList changeListeners = new EventListenerList();
//------------------------------------------------------------------------------
      public Rule(String name, int priority) {
        this.name = name;
        this.priority = priority;
        lhs = new PlainDocument();
        lhs.addDocumentListener(this);
        rhs = new PlainDocument();
        rhs.addDocumentListener(this);
      }
//--------------------------------------------------------------------------
       public String getName() {
            return name;
       }
//--------------------------------------------------------------------------
       public int getPriority() {
            return priority;
       }
//--------------------------------------------------------------------------
       public PlainDocument getLHS() {
         return lhs;
       }
//--------------------------------------------------------------------------
       public PlainDocument getRHS() {
         return rhs;
       }
//--------------------------------------------------------------------------
       public void setName(String name) {
          this.name = name;
       }
//--------------------------------------------------------------------------
       public void setPriority(int priority) {
          this.priority = priority;
       }
//--------------------------------------------------------------------------
       public String getCondition() {
          try {
            return lhs.getText(0,lhs.getLength());
          }
          catch (BadLocationException e) {
//             e.printStackTrace();
          }
          return null;
       }
//--------------------------------------------------------------------------
       public String getConclusion() {
          try {
            return rhs.getText(0,rhs.getLength());
          }
          catch (BadLocationException e) {
//             e.printStackTrace();
          }
          return null;
       }
//--------------------------------------------------------------------------
      public void setPatterns(Vector patterns) {
           zeus.rete.Pattern pattern;
           int pos = lhs.getStartPosition().getOffset();

	   for(int i = 0; i < patterns.size(); i++ )  {
             pattern = (zeus.rete.Pattern) patterns.elementAt(i);
             try {
	       lhs.insertString(pos,pattern.toString(),null);
             }
             catch (BadLocationException e) {
//               System.out.println("Error (VVV" + pos +")"+  pattern );
	       //e.printStackTrace();
             }
             pos = lhs.getLength();

	     try {
	       lhs.insertString(pos,"\n",null);
             }
	     catch (BadLocationException e) {
//               System.out.println("Error (VVV"+ pos + ")"+  pattern);
	       //e.printStackTrace();
             }
             pos =  lhs.getLength();
           }
      }
//--------------------------------------------------------------------------
      public void setActions(Vector actions) {
         zeus.rete.Action action;
         int pos = rhs.getStartPosition().getOffset();

         for(int i = 0; i < actions.size(); i++ )  {
            action = (zeus.rete.Action) actions.elementAt(i);
            try {
	       rhs.insertString(pos,action.toString(),null);
            }
	    catch (BadLocationException e) {
//               System.out.println("Error (XXX"+ pos + ")"+  action);
	       //e.printStackTrace();
            }

	    pos =  rhs.getLength();

	    try {
               rhs.insertString(pos,"\n",null);
            }
	    catch (BadLocationException e) {
//                System.out.println("Error (XXX"+ pos + ")"+  action);
	       //e.printStackTrace();
            }
            pos = rhs.getLength();
         }
      }
//--------------------------------------------------------------------------
     String getPatterns() {
       try {
          return lhs.getText(0, lhs.getLength());
        }
        catch(BadLocationException e) {
//           e.printStackTrace();
        }
        return "";
     }
//--------------------------------------------------------------------------
     String getActions() {
        try {
          return rhs.getText(0, rhs.getLength());
        }
        catch(BadLocationException e) {
//           e.printStackTrace();
        }
        return "";
     }
//------------------------------------------------------------------------------
     public void insertUpdate(DocumentEvent e) {
        fireChanged();
     }
//------------------------------------------------------------------------------
     public void removeUpdate(DocumentEvent e) {
        fireChanged();
     }
//------------------------------------------------------------------------------
     public void changedUpdate(DocumentEvent e) {
         fireChanged();
     }
//------------------------------------------------------------------------------
     public void addChangeListener(ChangeListener x) {
        changeListeners.add(ChangeListener.class, x);
     }
//------------------------------------------------------------------------------
     public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
     }
//------------------------------------------------------------------------------
     protected void fireChanged() {
        ChangeEvent c = new ChangeEvent(this);
        Object[] listeners = changeListeners.getListenerList();
        for(int i= listeners.length-2; i >= 0; i -=2) {
           if (listeners[i] == ChangeListener.class) {
              ChangeListener cl = (ChangeListener)listeners[i+1];
              cl.stateChanged(c);
           }
        }
     }
//------------------------------------------------------------------------------

}
