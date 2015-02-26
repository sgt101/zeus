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



/************************************************************************************************
* ControlPanel.java
*
* Panel for controlling the other panels
*
************************************************************************************************/

package zeus.generator.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import zeus.util.*;

public class ControlPanel extends JPanel
                          implements ActionListener,
                                     ChangeListener {

  protected JLabel        msgField       = new JLabel();
  protected JButton       nextButton     = new JButton();
  protected JButton       previousButton = new JButton();
  protected JButton       saveButton     = new JButton();
  protected JToggleButton infoButton;
  
  protected Editor editor;
  
  public ControlPanel(Editor editor, String title,
                      boolean isFirst, boolean isLast) {
    this.editor = editor;
    editor.addChangeListener(this);
    setBackground(java.awt.Color.yellow);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font ("Helvetica", Font.BOLD, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridwidth = 1;
    gbc.insets = new Insets(0,8,0,0);
    gridBagLayout.setConstraints(titleLabel,gbc);
    add(titleLabel);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0,16,0,16);
    gridBagLayout.setConstraints(msgField,gbc);
    add(msgField);

    JPanel iconPane = new JPanel();
    iconPane.setBackground(Color.yellow);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(0,0,0,8);
    gridBagLayout.setConstraints(iconPane, gbc);
    add(iconPane);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "generator" + sep;
    
    //create the icon buttons to be added to this panel.
    previousButton.setIcon(new ImageIcon(path + "previous.gif"));
    previousButton.setDisabledIcon(new ImageIcon(path + "next.gif"));
    previousButton.addActionListener(this);
    previousButton.setOpaque(true);    
    previousButton.setMargin(new Insets(0,0,0,0));
    if ( isFirst ) {
       previousButton.setEnabled(false);
       previousButton.setToolTipText("No Earlier Stage");
    }
    else {    
       previousButton.setToolTipText("Go Back to Previous Design Stage");
    }
    iconPane.add(previousButton);    
    iconPane.add(Box.createHorizontalStrut(16));
    
    saveButton.setOpaque(true);
    saveButton.setMargin(new Insets(0,0,0,0));
    saveButton.setIcon(new ImageIcon(path + "save.gif"));
    saveButton.setEnabled(false);
    saveButton.addActionListener(this);
    iconPane.add(saveButton);
    
    infoButton = new JToggleButton(new ImageIcon(path + "info.gif"));
    infoButton.setMargin(new Insets(0,0,0,0));
    infoButton.setOpaque(true);
    infoButton.addActionListener(this);
    infoButton.setToolTipText("Methodology Documentation");
    iconPane.add(infoButton);
    
    iconPane.add(Box.createHorizontalStrut(16));

    nextButton.setIcon(new ImageIcon(path + "next.gif"));
    nextButton.setMargin(new Insets(0,0,0,0));
    nextButton.setDisabledIcon(new ImageIcon(path + "previous.gif"));
    nextButton.addActionListener(this);
    nextButton.setOpaque(true);
    if ( isLast ) {
       nextButton.setEnabled(false);
       nextButton.setToolTipText("This is the Final Stage");
    }
    else {
       nextButton.setToolTipText("Go to Next Design Stage");
    }
    iconPane.add(nextButton);

    msgField.setForeground(Color.green);    
    msgField.setText(editor.getObjectName());
    msgField.setMinimumSize(new Dimension(200,20));
    msgField.setPreferredSize(new Dimension(200,20));
  }
  
  public void stateChanged(ChangeEvent e) {
    boolean changed = editor.hasChanged();
    saveButton.setEnabled(changed);
    String str = editor.getObjectName();
    if (changed) {
       str += " [Modified]";
       saveButton.setToolTipText("Save Changes to Disk");
    }
    else {
       saveButton.setToolTipText("No Save Necessary");
    }
    msgField.setText(str);
    saveButton.repaint();
  }
  
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if ( src == previousButton )
      editor.previous();
    else if ( src == infoButton )
      editor.help(infoButton);
    else if ( src == saveButton )
      editor.save();
    else if ( src == nextButton )
      editor.next();
  }
}
