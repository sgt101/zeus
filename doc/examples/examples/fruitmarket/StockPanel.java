/*****************************************************************************
* StockPanel.java
* Jaron Collis (jaron@info.bt.co.uk), March 1999
* Provides an example of how to interact with agent resources
*****************************************************************************/

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.concepts.fn.*;

public class StockPanel extends JPanel implements FactMonitor, ActionListener
{
  protected JLabel  appleLabel  = new JLabel();
  protected JLabel  orangeLabel = new JLabel();
  protected JLabel  pearLabel   = new JLabel();
  protected JLabel  bananaLabel = new JLabel();
  protected JLabel  melonLabel  = new JLabel();

  protected JButton moreApplesBtn;
  protected JButton moreOrangesBtn;
  protected JButton morePearsBtn;
  protected JButton moreBananasBtn;
  protected JButton moreMelonsBtn;
  protected JLabel  cashLabel;
  protected TraderFrontEnd UI;


  public StockPanel(TraderFrontEnd frontend)  {
    UI = frontend;

    GridBagLayout gb = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gb);
    setBackground(Color.lightGray);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    moreApplesBtn = new JButton("Supply");
    moreApplesBtn.addActionListener(this);

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(appleLabel,gbc);
    add(appleLabel);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(4,16,4,8);
    gb.setConstraints(moreApplesBtn, gbc);
    add(moreApplesBtn);

    moreOrangesBtn = new JButton("Supply");
    moreOrangesBtn.addActionListener(this);

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(orangeLabel,gbc);
    add(orangeLabel);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(4,16,4,8);
    gb.setConstraints(moreOrangesBtn, gbc);
    add(moreOrangesBtn);

    morePearsBtn = new JButton("Supply");
    morePearsBtn.addActionListener(this);

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(pearLabel,gbc);
    add(pearLabel);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(4,16,4,8);
    gb.setConstraints(morePearsBtn, gbc);
    add(morePearsBtn);

    moreBananasBtn = new JButton("Supply");
    moreBananasBtn.addActionListener(this);

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(bananaLabel,gbc);
    add(bananaLabel);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(4,16,4,8);
    gb.setConstraints(moreBananasBtn, gbc);
    add(moreBananasBtn);

    moreMelonsBtn = new JButton("Supply");
    moreMelonsBtn.addActionListener(this);

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(melonLabel,gbc);
    add(melonLabel);

    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(4,16,4,8);
    gb.setConstraints(moreMelonsBtn, gbc);
    add(moreMelonsBtn);

    Fact[] tmp = UI.agent.ResourceDb().all(OntologyDb.MONEY);
    double cash = 0;
    for(int i = 0; i < tmp.length; i++ ) {
       tmp[i].resolve(new Bindings());
       PrimitiveNumericFn numFn = (PrimitiveNumericFn)tmp[i].getFn(OntologyDb.AMOUNT);
       cash += numFn.doubleValue();
    }

    cashLabel = new JLabel("Bank Balance: " + Misc.decimalPlaces(cash,2));
    cashLabel.setFont(new Font("Helvetica",Font.PLAIN, 12));

    gbc.insets = new Insets(4,8,4,0);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gb.setConstraints(cashLabel, gbc);
    add(cashLabel);

    UI.agent.ResourceDb().addFactMonitor(this,
       FactEvent.ADD_MASK|FactEvent.DELETE_MASK, true);
  }

  protected void update(Fact f)  {
    Fact[] tmp = UI.agent.ResourceDb().all(f.getType());
    int num = 0;
    for(int i = 0; i < tmp.length; i++ )
       num += tmp[i].getNumber();

    if (f.getType().equals("apple"))
      appleLabel.setText("Boxes of apples in stock: " + num);
    else if (f.getType().equals("orange"))
      orangeLabel.setText("Boxes of oranges in stock: " + num);
    else if (f.getType().equals("pear"))
      pearLabel.setText("Boxes of pears in stock: " + num);
    else if (f.getType().equals("banana"))
      bananaLabel.setText("Boxes of bananas in stock: " + num);
    else if (f.getType().equals("melon"))
      melonLabel.setText("Boxes of melons in stock: " + num);
  }


  public void actionPerformed(ActionEvent evt)  {
    String fruit = null;
    if (evt.getSource() == moreApplesBtn)
      fruit = "apple";
    if (evt.getSource() == moreOrangesBtn)
      fruit = "orange";
    if (evt.getSource() == morePearsBtn)
      fruit = "pear";
    if (evt.getSource() == moreBananasBtn)
      fruit = "banana";
    if (evt.getSource() == moreMelonsBtn)
      fruit = "melon";

    UI.display("A box of " + fruit + "s arrives.");
    Fact[] tmp = UI.agent.ResourceDb().all(fruit);
    if ( tmp.length > 0 ) {
       Fact f2 = new Fact(tmp[0]);
       int no = f2.getNumber();
       f2.setNumber(++no);
       UI.agent.ResourceDb().modify(tmp[0], f2);
    }
  }

  public void factAddedEvent(FactEvent event)	{
    Fact fact = event.getFact();
    if ( fact.getType().equals(OntologyDb.MONEY) ) {
       Fact[] tmp = UI.agent.ResourceDb().all(OntologyDb.MONEY);
       double cash = 0;
       for(int i = 0; i < tmp.length; i++ ) {
          tmp[i].resolve(new Bindings());
          PrimitiveNumericFn numFn = (PrimitiveNumericFn)tmp[i].getFn(OntologyDb.AMOUNT);
          cash += numFn.doubleValue();
       }
       cashLabel.setText("Bank Balance: " + Misc.decimalPlaces(cash,2));
    }
    else
       update(fact);
  }

  public void factModifiedEvent(FactEvent event) {}   // kill this?
  public void factAccessedEvent(FactEvent event) {}

  public void factDeletedEvent(FactEvent event) {
    Fact fact = event.getFact();
    if ( fact.getType().equals(OntologyDb.MONEY) ) {
       Fact[] tmp = UI.agent.ResourceDb().all(OntologyDb.MONEY);
       double cash = 0;
       for(int i = 0; i < tmp.length; i++ ) {
          tmp[i].resolve(new Bindings());
          PrimitiveNumericFn numFn = (PrimitiveNumericFn)tmp[i].getFn(OntologyDb.AMOUNT);
          cash += numFn.doubleValue();
       }
       cashLabel.setText("Bank Balance: " + Misc.decimalPlaces(cash,2));
    }
    else
       update(fact);
  }
}
