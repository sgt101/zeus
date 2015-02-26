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
* HelpWindow.java
*
* A Generic Floating Window for displaying Help Messages
*****************************************************************************/

package zeus.gui.help;

import java.io.*;
import java.awt.*;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.util.SystemProps;


public class HelpWindow extends JFrame {
  protected HtmlPanel      textPane;
  protected JScrollPane    displayArea;
  protected HelpToolBar    helpBar;
  protected AbstractButton sourceBtn;
  protected Vector         history = new Vector();
  protected int            histpos = 0;


  public HelpWindow(Component parent, Point p, String subpath, String aspect) {
    addWindowListener(new HWHandler());

    setTitle("Help on " + aspect);
    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "help" + sep;
    ImageIcon icon = new ImageIcon(path + "questionicon.gif");
    setIconImage(icon.getImage());
    Point parentpos = parent.getLocation();
    int width = (parent.getSize()).width;
    Point dispos = new Point(parentpos.x + width, parentpos.y + p.y);
    setLocation(dispos.x, dispos.y);
    setSize(new Dimension(400,300));

    getContentPane().setLayout(new BorderLayout());
    JPanel innerPane = new JPanel();
    innerPane.setBackground(Color.yellow.brighter());
    BevelBorder border = new BevelBorder(BevelBorder.RAISED);
    innerPane.setBorder(border);

    String filename = aspect + sep + "what.html";
    String helppath = SystemProps.getProperty("zeus.dir") + "help" + sep +
                          subpath + sep;
    String fullpath = "file:" + helppath + filename;

    textPane = new HtmlPanel(this);
    history.addElement(fullpath);
    textPane.setPage(fullpath);

    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    innerPane.setLayout(gridBagLayout);

    helpBar = new HelpToolBar();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.gridheight = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = gbc.weighty = 0;
    gbc.insets = new Insets(2,2,2,2);
    gridBagLayout.setConstraints(helpBar,gbc);
    innerPane.add(helpBar);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = gbc.weighty = 1;
    gbc.insets = new Insets(16,16,16,16);
    gridBagLayout.setConstraints(textPane,gbc);
    innerPane.add(textPane);

    getContentPane().add("Center", innerPane);
    pack();
    show();
  }

  public void setSource(AbstractButton button) { sourceBtn = button; }

  public void addToHistory(String doc)  {
    history.addElement(doc);
    histpos++;
  }


  class HWHandler extends WindowAdapter  {
    public void windowClosing(WindowEvent event)  {
      if ( sourceBtn != null ) sourceBtn.setSelected(false);
      dispose();
    }
  }

class HelpToolBar extends JPanel implements ActionListener {
  protected JButton       prevBtn;
  protected JButton       nextBtn;

  public HelpToolBar() {
    setBorder(new BevelBorder(BevelBorder.LOWERED));
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setBackground(Color.yellow);

    String sep = System.getProperty("file.separator");
    String path = SystemProps.getProperty("gif.dir") + "help" + sep;

    // Previous Button
    prevBtn = new JButton(new ImageIcon(path + "previous.gif"));
    add(prevBtn);
    prevBtn.setToolTipText("Previous Entry");
    prevBtn.setMargin(new Insets(0,0,0,0));
    prevBtn.addActionListener(this);

    // New Button
    nextBtn = new JButton(new ImageIcon(path + "next.gif"));
    add(nextBtn);
    nextBtn.setToolTipText("Next Entry");
    nextBtn.setMargin(new Insets(0,0,0,0));
    nextBtn.addActionListener(this);

    add(Box.createHorizontalGlue() );

    JButton hicon = new JButton(new ImageIcon(path + "questionmark.gif"));
    hicon.setMargin(new Insets(0,0,0,0));
    add(hicon);
  }
  public void actionPerformed(ActionEvent e)  {
    Object src = e.getSource();
    if ( src == nextBtn && histpos+1 < history.size() ){
       histpos++;
       textPane.setPage((String)history.elementAt(histpos));
    }
    else if ( src == prevBtn && histpos > 0 ) {
      histpos--;
      textPane.setPage((String)history.elementAt(histpos));
    }
    textPane.repaint();
  }
}
}
