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
*
*
* A pop-up dialog that prompts selection of a fact
*****************************************************************************/
package zeus.generator.task.rulebase;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import zeus.ontology.facts.*;
import zeus.ontology.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.concepts.fn.*;


public class FactPanel extends JPanel implements ActionListener {
  static final String FACT_MARKER = "<-";

  protected  FactTreeUI treeView;
  protected  JButton    factIDBtn, factBtn;
  RuleUI     parent;
  OntologyDb db;

  public FactPanel(RuleUI parent, OntologyDb db) {
    this.parent = parent;
    this.db = db;
    setBorder(new EmptyBorder(10,10,10,10));
    setBackground(Color.lightGray);
    setLayout(new BorderLayout());

    treeView = new FactTreeUI(db );
    JScrollPane treePane = new JScrollPane(treeView);
    treePane.setPreferredSize(new Dimension(400,150));
    add(treePane,BorderLayout.CENTER);

    JPanel controlpane = new JPanel();
    controlpane.setLayout(new GridLayout(1,2,10,10));
    add(controlpane,BorderLayout.SOUTH);

    factBtn = new JButton("Insert Fact");
    factBtn.addActionListener(this);
    factIDBtn = new JButton("Insert with ID");
    factIDBtn.addActionListener(this);
    controlpane.add(factBtn);
    controlpane.add(factIDBtn);
  }
//---------------------------------------------------------------------------
  public void actionPerformed(ActionEvent evt) {
    String aValue = null;
    Fact f = null;

    String name = treeView.getSelectedNodeName();
    if ( name == null ) return;

    f = db.getFact(Fact.VARIABLE,name);

    if ( evt.getSource() == factBtn && f != null) {
       aValue = getAttributeValues(f);
    }
    else if ( evt.getSource() == factIDBtn && f != null ) {
       aValue = f.getId() + " " + FACT_MARKER + " " + getAttributeValues(f);
    }
    if ( aValue != null ) {
       parent.appendTextTo(aValue);
//       System.out.println(aValue);
   }

  }
//---------------------------------------------------------------------------
   public String getAttributeValues(Fact f){
      String[] a = f.listAttributes();
      ValueFunction[] v = f.listValues();
      String attValues = "(" + f.getType();

      for(int i = 0; i < a.length; i++ )
          attValues += " (" + a[i] + " " + v[i] + ")";

      return attValues + ")";
   }
//---------------------------------------------------------------------------
}
