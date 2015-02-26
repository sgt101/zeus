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



package zeus.gui.fields;

import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class LargeTextField extends JTextArea implements DocumentListener {

   public LargeTextField()                             { super(); }
   public LargeTextField(int rows, int cols)           { super(rows,cols); }
   public LargeTextField(String t)                     { super(t); }
   public LargeTextField(String t, int rows, int cols) { super(t,rows,cols); }

   protected Document createDefaultModel() {
      Document doc = new LargeTextFieldDocument();
      doc.addDocumentListener(this);
      return doc;
   }

   public void insertUpdate(DocumentEvent e) {
      fireChanged();
   }
   public void removeUpdate(DocumentEvent e) {
      fireChanged();
   }
   public void changedUpdate(DocumentEvent e) {
      fireChanged();
   }

   static char[] specialChars = { '\t', '\n', '\r' };

   class LargeTextFieldDocument extends PlainDocument {
      public void insertString(int offs, String str, AttributeSet a)
         throws BadLocationException {

         if ( str != null && str.length() == 1 &&
              member(str.charAt(0),specialChars) ) {
            fireActionPerformed();
            return;
         }
         super.insertString(offs,str,a);
      }
   }
   static boolean member(char item, char[] array) {
      for(int i = 0; i < array.length; i++ )
         if ( array[i] == item ) return true;
      return false;
   }

   protected EventListenerList actionListeners = new EventListenerList();
   protected String actionCommand = "";

   public void addActionListener(ActionListener x) {
      actionListeners.add(ActionListener.class, x);
   }  
   public void removeActionListener(ActionListener x) {
      actionListeners.remove(ActionListener.class, x);
   }

   public String getActionCommand()           { return actionCommand; }
   public void   setActionCommand(String cmd) { actionCommand = cmd; }

   protected void fireActionPerformed() {
      ActionEvent evt =
         new ActionEvent(this,ActionEvent.ACTION_PERFORMED,actionCommand);
      Object[] listeners = actionListeners.getListenerList();
      for(int i= listeners.length-2; i >= 0; i -=2) {
         if (listeners[i] == ActionListener.class) {
            ActionListener l = (ActionListener)listeners[i+1];
            l.actionPerformed(evt);
         }
      }
   }

   protected EventListenerList changeListeners = new EventListenerList();
   public void addChangeListener(ChangeListener x) {
      changeListeners.add(ChangeListener.class, x);
   }  
   public void removeChangeListener(ChangeListener x) {
      changeListeners.remove(ChangeListener.class, x);
   }  
  
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
}
