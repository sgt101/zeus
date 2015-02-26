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



package zeus.gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import zeus.util.*;
import zeus.gui.fields.*;


public class NumberDialog extends JDialog
                          implements ActionListener {
   protected JLabel label;
   protected WholeNumberField textfield;

   protected JButton okButton;
   protected JButton cancelButton;
   protected String OK = "Ok";
   protected String CANCEL = "Cancel";
   protected Long answer = null;

   public NumberDialog(Frame parent, String title, String label_string) {
      this(parent,title);
      this.setLabel(label_string);
   }
   public NumberDialog(Frame parent, String title, String label_string,
                       int min, int max, int number ) {
      this(parent,title,(long)min,(long)max);
      this.setLabel(label_string);
      this.setValue(number);
   }
   public NumberDialog(Frame parent, String title,
                       String label_string, int number ) {
      this(parent,title);
      this.setLabel(label_string);
      this.setValue(number);
   }
   public NumberDialog(Frame parent, String title, String label_string,
                       long min, long max, long number ) {
      this(parent,title,min,max);
      this.setLabel(label_string);
      this.setValue(number);
   }
   public NumberDialog(Frame parent, String title,
                       String label_string, long number ) {
      this(parent,title);
      this.setLabel(label_string);
      this.setValue(number);
   }
   public NumberDialog(Frame parent, String title) {
      this(parent,title,Long.MIN_VALUE,Long.MAX_VALUE);
   }
   public NumberDialog(Frame parent, String title, long min, long max) {
      super(parent,title,true);
      getContentPane().setLayout(new BorderLayout());

      JPanel p1 = new JPanel();
      p1.setLayout(new GridLayout(1,2,10,10));
      okButton = new JButton(OK);
      cancelButton = new JButton(CANCEL);
      p1.add(okButton);
      p1.add(cancelButton);

      label = new JLabel("--Undefined--");
      textfield = new WholeNumberField(min,max);
      textfield.setPreferredSize(new Dimension(100,20));
      textfield.setMinimumSize(new Dimension(100,20));

      JPanel p2 = new JPanel();
      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gbc = new GridBagConstraints();
      p2.setLayout(gb);

      gbc.fill = GridBagConstraints.NONE;
      gbc.weightx = gbc.weighty = 0;
      gbc.insets = new Insets(10,10,0,0);
      gbc.anchor = GridBagConstraints.WEST;
      gbc.gridwidth = 1;
      gb.setConstraints(label,gbc);
      p2.add(label);

      gbc.insets = new Insets(10,10,0,10);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = gbc.weighty = 1;
      gb.setConstraints(textfield,gbc);
      p2.add(textfield);

      JSeparator s1 = new JSeparator();
      gbc.insets = new Insets(10,0,10,0);
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = gbc.weighty = 0;
      gb.setConstraints(s1,gbc);
      p2.add(s1);

      getContentPane().add("Center",p2);
      getContentPane().add("South",p1);

      // Event handling
      textfield.addActionListener(this);
      okButton.addActionListener(this);
      cancelButton.addActionListener(this);

      this.addWindowListener(
          new WindowAdapter() {
             public void windowClosing(WindowEvent evt) {
		setVisible(false);
	     }
          }
      );
      this.pack();
   }

   public Long getValue() {
      answer = null;
      this.setVisible(true);
      return answer;
   }

   public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();

      if ( source == textfield || source == okButton ) {
         answer = textfield.getValue();
         if ( answer == null ) {
            int result = JOptionPane.showConfirmDialog(this,
               "Improperly specified integer value\nContinue?",
	       "Warning", JOptionPane.YES_NO_OPTION);
            switch(result) {
	       case JOptionPane.YES_OPTION:
                    break;

               case JOptionPane.NO_OPTION:
                    return;
            }
         }
         this.setVisible(false);
      }
      else if ( source == cancelButton ) {
         this.setVisible(false);
      }
   }

   public String getLabel() { return label.getText(); }

   public void setLabel(String text) {
      Assert.notNull(text);
      label.setText(text);
   }

   public void setValue(int number) {
      textfield.setValue(number);
   }
   public void setValue(long number) {
      textfield.setValue(number);
   }

   public static void main(String arg[]) {
      JFrame f = new JFrame("Test");
      f.setSize(200,200);
      f.show();
      NumberDialog m = new NumberDialog(f,"Enter number");
      m.setValue(123);
      System.out.println(m.getValue());
      System.exit(0);
   }


}
