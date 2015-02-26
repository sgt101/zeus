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
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;


public class NameField extends JTextField implements DocumentListener {

   public NameField()                         { super(); }
   public NameField(int columns)              { super(columns); }
   public NameField(String text)              { super(text); }
   public NameField(String text, int columns) { super(text,columns); }

   protected Document createDefaultModel() {
      Document doc = new NameFieldDocument();
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

   static char[] alphaSet = {
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
   };

   /** MS 230101 v1.05
    added '.' to otherSet
    ST 210301  v1.2
    added '-' to otherSet
    
    */
   static char[] otherSet = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_', '$', '.','-'
   };

   static class NameFieldDocument extends PlainDocument {
      public void insertString(int offs, String str, AttributeSet a)
         throws BadLocationException {

         if ( str == null ) return;
         str = str.trim();
         str = str.replace(' ','_');

         String buf = getText(0,offs) + str;
         buf = buf.toLowerCase();
         char[] array = buf.toCharArray();

         if ( array.length > 0 && !member(array[0],alphaSet) ) {
            Toolkit.getDefaultToolkit().beep();
            return;
         }
         for(int i = 1; i < array.length; i++ ) {
            if ( !member(array[i],alphaSet) && !member(array[i],otherSet) ) {
               Toolkit.getDefaultToolkit().beep();
               return;
            }
         }
         super.insertString(offs,str,a);
      }
   }
   static boolean member(char item, char[] array) {
      for(int i = 0; i < array.length; i++ )
         if ( array[i] == item ) return true;
      return false;
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
          
         

