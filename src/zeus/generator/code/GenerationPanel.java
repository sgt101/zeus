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



/*****************************************************************************
* GenerationPanel.java
*
* The 'summary' panel for the code generation process
*****************************************************************************/

package zeus.generator.code;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

import zeus.gui.help.*;
import zeus.util.*;
import zeus.generator.*;

public class GenerationPanel extends JPanel
                             implements ActionListener,
                                        ChangeListener,
                                        FocusListener {

  static final String[] ERROR_MESSAGE = {
     /* 0 */ "Please select a row before\ncalling this operation"
  };

  protected JTable               table;
  protected GenerationTableModel model;

  protected JTextField      dirField;
  protected JRadioButton    win;
  protected JRadioButton    unix;
  protected JRadioButton    zsh; 
  protected JButton         selectBtn;
  protected GenerationPlan  genplan;
  protected JTextArea       textArea;

  public GenerationPanel(GenerationPlan genplan) {
    this.genplan = genplan;
    genplan.addChangeListener(this);

    setBackground(Color.lightGray);

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc = new GridBagConstraints();

    JPanel dirPanel = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8,8,0,8);
    gridBagLayout.setConstraints(dirPanel,gbc);
    add(dirPanel);

    JPanel topPanel = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(8,8,0,8);
    gbc.weightx = gbc.weighty = 1;
    gridBagLayout.setConstraints(topPanel,gbc);
    add(topPanel);

    JPanel bottomPanel = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(8,8,0,8);
    gbc.weightx = gbc.weighty = 1;
    gridBagLayout.setConstraints(bottomPanel,gbc);
    add(bottomPanel);

    // create top panel
    topPanel.setBackground(Color.lightGray);
    TitledBorder b1 = (BorderFactory.createTitledBorder("Generation Plan"));
    b1.setTitlePosition(TitledBorder.TOP);
    b1.setTitleJustification(TitledBorder.RIGHT);
    b1.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    b1.setTitleColor(Color.blue);
    topPanel.setBorder(b1);

    model = new GenerationTableModel(genplan);
    JToolBar toolbar = new GenerationToolBar();

    TableColumnModel tm = new DefaultTableColumnModel();
    TableColumn column;

    column = new TableColumn(GenerationInfo.NAME,24);
    column.setHeaderValue(model.getColumnName(GenerationInfo.NAME));
    tm.addColumn(column);

    column = new TableColumn(GenerationInfo.TYPE,24);
    column.setHeaderValue(model.getColumnName(GenerationInfo.TYPE));
    tm.addColumn(column);

    column = new TableColumn(GenerationInfo.COMMAND,24);
    column.setHeaderValue(model.getColumnName(GenerationInfo.COMMAND));
    tm.addColumn(column);

    table = new JTable(model,tm);
    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(300,160));
    table.setBackground(Color.white);

    gridBagLayout = new GridBagLayout();
    gbc = new GridBagConstraints();
    topPanel.setLayout(gridBagLayout);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.insets = new Insets(0,8,0,0);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gridBagLayout.setConstraints(toolbar,gbc);
    topPanel.add(toolbar);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(scrollPane, gbc);
    topPanel.add(scrollPane);

    // create top panel
    bottomPanel.setBackground(Color.lightGray);
    b1 = (BorderFactory.createTitledBorder("Generation Messages"));
    b1.setTitlePosition(TitledBorder.TOP);
    b1.setTitleJustification(TitledBorder.RIGHT);
    b1.setTitleFont(new Font("Helvetica", Font.BOLD, 14));
    b1.setTitleColor(Color.blue);
    bottomPanel.setBorder(b1);

    textArea = new JTextArea(12,80);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setBackground(Color.white);

    scrollPane = new JScrollPane(textArea);
    scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollPane.setPreferredSize(new Dimension(300,160));

    gridBagLayout = new GridBagLayout();
    gbc = new GridBagConstraints();
    bottomPanel.setLayout(gridBagLayout);

    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(8,8,8,8);
    gridBagLayout.setConstraints(scrollPane,gbc);
    bottomPanel.add(scrollPane);

    // directory panel info
    dirPanel.setBackground(Color.lightGray);
    BevelBorder b2 = new BevelBorder(BevelBorder.LOWERED);
    dirPanel.setBorder(b2);

    gridBagLayout = new GridBagLayout();
    gbc = new GridBagConstraints();
    dirPanel.setLayout(gridBagLayout);

    dirField = new JTextField(20);
    dirField.addActionListener(this);
    dirField.addFocusListener(this);
    dirField.setText(genplan.getDirectory());

    String path = SystemProps.getProperty("gif.dir") + "generator" +
                  System.getProperty("file.separator");

    selectBtn = new JButton("Choose Target Directory", new ImageIcon(path + "open.gif"));
    selectBtn.setToolTipText("Choose where source code will be written");
    selectBtn.addActionListener(this);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(4,4,4,4);
    gridBagLayout.setConstraints(selectBtn, gbc);
    dirPanel.add(selectBtn);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(4,4,4,4);
    gridBagLayout.setConstraints(dirField, gbc);
    dirPanel.add(dirField);

    JLabel groupLbl = new JLabel("Create Scripts for ...", JLabel.CENTER);
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(4,4,4,0);
    gridBagLayout.setConstraints(groupLbl, gbc);
    dirPanel.add(groupLbl);

    win = new JRadioButton(GenerationPlan.WINDOWS,false);
    unix = new JRadioButton(GenerationPlan.UNIX,false);
    zsh = new JRadioButton (GenerationPlan.ZSH,false); 
    
    unix.setBackground(Color.lightGray);
    win.setBackground(Color.lightGray);
    zsh.setBackground(Color.lightGray); 
    
    unix.addActionListener(this);
    win.addActionListener(this);
    zsh.addActionListener(this); 

    if ( genplan.getPlatform().equals(GenerationPlan.WINDOWS) )
      win.setSelected(true);
    else
      unix.setSelected(true);
    ButtonGroup platform = new ButtonGroup();
    platform.add(win);
    platform.add(unix);
    
   

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(4,4,4,4);
    gridBagLayout.setConstraints(win,gbc);
    dirPanel.add(win);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(4,4,4,4);
    gridBagLayout.setConstraints(unix,gbc);
    dirPanel.add(unix);
    
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(4,4,4,4);
    gridBagLayout.setConstraints(zsh,gbc);
    dirPanel.add(zsh);
  }

   protected class GenerationToolBar extends JToolBar
                                     implements ActionListener {
   
     protected JToggleButton agentBtn;
     protected JToggleButton taskBtn;
     protected JToggleButton utilityBtn;
     protected JButton       deleteBtn;
     public    JToggleButton helpBtn;
     protected HelpWindow    helpWin;
   
     public GenerationToolBar()  {
       setBackground(Color.lightGray);
       setBorder(new BevelBorder(BevelBorder.LOWERED));
       setFloatable(false);
   
       String sep = System.getProperty("file.separator");
       String path = SystemProps.getProperty("gif.dir") + "generator" + sep;
   
       utilityBtn = new JToggleButton(new ImageIcon(path + "showutility.gif"), true);
       add(utilityBtn);
       utilityBtn.setToolTipText("Hide/Show Utility Agents");
       utilityBtn.setMargin(new Insets(0,0,0,0));
       utilityBtn.addActionListener(this);
   
       agentBtn = new JToggleButton(new ImageIcon(path + "showagents.gif"), true);
       add(agentBtn);
       agentBtn.setToolTipText("Hide/Show Task Agents");
       agentBtn.setMargin(new Insets(0,0,0,0));
       agentBtn.addActionListener(this);
   
       taskBtn = new JToggleButton(new ImageIcon(path + "showtasks.gif"), true);
       add(taskBtn);
       taskBtn.setToolTipText("Hide/Show Tasks");
       taskBtn.setMargin(new Insets(0,0,0,0));
       taskBtn.addActionListener(this);
   
       deleteBtn = new JButton(new ImageIcon(path + "delete1.gif"));
       add(deleteBtn);
       deleteBtn.setToolTipText("Delete selected entry");
       deleteBtn.setMargin(new Insets(0,0,0,0));
       deleteBtn.addActionListener(this);
   
       addSeparator();
   
       helpBtn = new JToggleButton(new ImageIcon(path + "help.gif"));
       add(helpBtn);
       helpBtn.setToolTipText("Help");
       helpBtn.setMargin(new Insets(0,0,0,0));
       helpBtn.addActionListener(this);
     }
     public void actionPerformed(ActionEvent e) {
       Object src = e.getSource();
   
       if ( src == agentBtn )
         model.setFilter(GenerationTableModel.AGENT_FILTER);
       else if ( src == taskBtn )
         model.setFilter(GenerationTableModel.TASK_FILTER);
       else if ( src == utilityBtn )
         model.setFilter(GenerationTableModel.UTILITY_FILTER);
       else if ( src == deleteBtn ) {
         if (table.getSelectedRow() == -1) {
            errorMsg(0);
            return;
         }
         else
           model.removeRows(table.getSelectedRows());
       }
       else if ( src == helpBtn ) {
         if ( helpBtn.isSelected() ) {
            helpWin = new HelpWindow(SwingUtilities.getRoot(this),
              getLocation(), "generator", "Generation Table");
            helpWin.setSource(helpBtn);
         }
         else
            helpWin.dispose();
       }
     }
   }
   
   protected void errorMsg(int tag) {
      JOptionPane.showMessageDialog(this,ERROR_MESSAGE[tag],
                                    "Error", JOptionPane.ERROR_MESSAGE);
   }
   
   public void    setWriteDir(String dir) { dirField.setText(dir); }
   public String  getWriteDir()           { return dirField.getText(); }
   public boolean isWindowsPlatform()     { return win.isSelected(); }
   public boolean isUnixPlatform()        { return unix.isSelected(); }
   
   public boolean isZsh() { return zsh.isSelected();}
   
   
   protected String getDirectory() {
     String path = getWriteDir();
     FileDialog f = new FileDialog((Frame)SwingUtilities.getRoot(this),
        "Select (a file within) Target Directory", FileDialog.LOAD);
     f.setDirectory(path);
     f.pack();
     f.setVisible(true);
     return f.getDirectory();
   }
   
   protected JTextArea getTextArea() {
     return textArea;
   }
   
   
   public void actionPerformed(ActionEvent e)  {
     Object src = e.getSource();
     if ( src == selectBtn ) {
        String dir = getWriteDir();
        genplan.setDirectory(dir);
        setWriteDir(dir);
     }
     else if ( src == dirField ) {
        String dir = getWriteDir();
        setWriteDir(dir);
        genplan.setDirectory(dir);

        if ( !dir.equals(genplan.getDirectory()) )
           genplan.setDirectory(dir);
     }
     else if ( src == unix ) {
        if ( !genplan.getPlatform().equals(GenerationPlan.UNIX) )
           genplan.setPlatform(GenerationPlan.UNIX);
     }
     else if ( src == win ) {
        if ( !genplan.getPlatform().equals(GenerationPlan.WINDOWS) )
           genplan.setPlatform(GenerationPlan.WINDOWS);
     }
     else if (src == zsh) { 
         if (!genplan.getShell().equals(GenerationPlan.ZSH))
            genplan.setShell(GenerationPlan.ZSH); 
         else 
            genplan.setShell (GenerationPlan.NONE); 
     }
     
   }
   
   
   public void focusGained(FocusEvent e) {
   }
   
   
   public void focusLost(FocusEvent e) {
      if ( e.getSource() == dirField ) {
        String dir = getWriteDir();
        if ( !dir.equals(genplan.getDirectory()) )
           genplan.setDirectory(dir);
      }
   }

   public void stateChanged(ChangeEvent e) {
      if ( e.getSource() == genplan ) {
         String dir = getWriteDir();
         String gen_dir = genplan.getDirectory();
         if ( !dir.equals(gen_dir) )
            setWriteDir(gen_dir);
   
         String gen_platform = genplan.getPlatform();
         if ( gen_platform.equals(GenerationPlan.UNIX) &&
              !unix.isSelected() )
            unix.setSelected(true);
         else if ( gen_platform.equals(GenerationPlan.WINDOWS) &&
              !win.isSelected() )
            win.setSelected(true);
      }
   }
}
