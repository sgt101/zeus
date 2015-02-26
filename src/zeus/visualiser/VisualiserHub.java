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



/*******************************************************************
 *          ZEUS Visualiser Hub Swing Implementation               *
 *******************************************************************/

package zeus.visualiser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.*;
import zeus.gui.help.*;
import zeus.actors.*;

import zeus.visualiser.basic.BasicTool;
import zeus.visualiser.report.ReportTool;
import zeus.visualiser.control.ControlTool;
import zeus.visualiser.statistics.StatisticsTool;
import zeus.visualiser.society.SocietyTool;


public class VisualiserHub extends JFrame {
    protected AgentContext    context = null;
    protected VisualiserModel model   = null;
    public    boolean         quick   = false;
    protected HelpWindow      helpWin = null;

    private static final String disclaimer =
        "This software was produced as a part of research\n" +
        "activities. It is not intended to be used as commercial\n" +
        "or industrial software by any organisation. Except as\n" +
        "explicitly stated, no guarantees are given as to its\n" +
        "reliability or trustworthiness if used for purposes other\n" +
        "than those for which it was originally intended.\n \n" +
        "(c) British Telecommunications plc 1996-9.";

    protected String path = SystemProps.getProperty("gif.dir") +
       File.separator + "visualiser" + File.separator;

    protected Icon headerIcon = new ImageIcon(path + "header.gif");
    protected Icon societyIcon = new ImageIcon(path + "society.gif");
    protected Icon reportIcon = new ImageIcon(path + "report.gif");
    protected Icon infoIcon = new ImageIcon(path + "info.gif");
    protected Icon controlIcon = new ImageIcon(path + "control.gif");
    protected Icon statsIcon = new ImageIcon(path + "stats.gif");
    protected Icon helpIcon = new ImageIcon(path + "help.gif");


    public VisualiserHub(AgentContext context, boolean quick) {
      super(context.whoami());

      this.context = context;
      this.model = new VisualiserModel(context);
      this.quick = quick;

      Address a;
      for(int i = 0; i < context.nameservers().size(); i++ ) {
         a = (Address)context.nameservers().elementAt(i);
         model.addNameserver(a.getName());
      }

      ImageIcon icon = new ImageIcon(path + "visicon.gif");
      setIconImage(icon.getImage());

      getContentPane().add(makeToolbar());
      pack();
      setResizable(false);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            int result = JOptionPane.showConfirmDialog(VisualiserHub.this,
               "Exit Visualiser?", "Exit", JOptionPane.YES_NO_OPTION);
            if ( result == JOptionPane.YES_OPTION )
	       System.exit(0);
         }
      });
      setVisible(true);
    }

    private void quickConnection(BasicTool tool) {
       if ( quick ) {
          quick = false;
          tool.quickConnect();
       }
    }

    private JPanel makeToolbar()  {
        JPanel toolbar = new JPanel();
        toolbar.setBorder(BorderFactory.createEtchedBorder());
        toolbar.setLayout(new GridLayout(7,1));

        JButton aboutBtn = new JButton(headerIcon);
        aboutBtn.setToolTipText("Information");
        aboutBtn.setMargin(new Insets(0,0,0,0));
        aboutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VisualiserHub.this, disclaimer);
            }
        });

        JButton societyBtn = new JButton(societyIcon);
        societyBtn.setToolTipText("View Agent Society");
        societyBtn.setMargin(new Insets(0,0,0,0));
        societyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              SocietyTool tool = new SocietyTool(context,model);
              quickConnection(tool);
              Point pt = getLocation();
              tool.setLocation(pt.x+getWidth(), pt.y);
            }
        });

        JButton reportBtn = new JButton(reportIcon);
        reportBtn.setToolTipText("View Agent Reports");
        reportBtn.setMargin(new Insets(0,0,0,0));
        reportBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ReportTool tool = new ReportTool(context,model);
              quickConnection(tool);
              Point pt = getLocation();
              tool.setLocation(pt.x+getWidth(), pt.y);
            }
        });

        JButton statsBtn = new JButton(statsIcon);
        statsBtn.setToolTipText("View Agent Statistics");
        statsBtn.setMargin(new Insets(0,0,0,0));
        statsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              StatisticsTool tool = new StatisticsTool(context,model);
              quickConnection(tool);
              Point pt = getLocation();
              tool.setLocation(pt.x+getWidth(), pt.y);
            }
        });

        JButton remoteBtn = new JButton(infoIcon);
        remoteBtn.setToolTipText("Remote Agent Viewer");
        remoteBtn.setMargin(new Insets(0,0,0,0));
        remoteBtn.setEnabled(false);
        remoteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        JButton controlBtn = new JButton(controlIcon);
        controlBtn.setToolTipText("Launch Control Tool");
        controlBtn.setMargin(new Insets(0,0,0,0));
        //controlBtn.setEnabled(false);
        controlBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              ControlTool tool = new ControlTool(context,model);
              quickConnection(tool);
              Point pt = getLocation();
              tool.setLocation(pt.x+getWidth(), pt.y);
            }
        });

        final JToggleButton helpBtn = new JToggleButton(helpIcon);
        helpBtn.setToolTipText("Help");
        helpBtn.setMargin(new Insets(0,0,0,0));
        helpBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Point pt = getLocation();
              JToggleButton button = (JToggleButton)e.getSource();
              if ( button.isSelected() ) {
                 helpWin = new HelpWindow(VisualiserHub.this, pt,
                    "visualiser", "Visualiser Hub");
                 helpWin.setSource(button);
              }
              else
                helpWin.dispose();
            }
        });

        toolbar.add(aboutBtn);
        toolbar.add(societyBtn);
        toolbar.add(reportBtn);
        toolbar.add(statsBtn);
        toolbar.add(remoteBtn);
        toolbar.add(controlBtn);
        toolbar.add(helpBtn);

        return toolbar;
    }
}
