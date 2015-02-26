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



package zeus.agentviewer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.awt.event.*;
import zeus.util.*;
import zeus.actors.AgentContext;
import zeus.gui.fields.*;

class ControlOptionsDialog extends JDialog implements ActionListener {

    AgentContext context;
    JPanel contentPane;

    RealNumberField registration_timeout, facilitator_timeout,
                    address_timeout, accept_timeout, addressbook_refresh,
		    facilitator_refresh, replan_period;

    JPanel centerPanel, leftPanel, rightPanel;
    JCheckBox share_plan, execute_earliest;
    JLabel label;

    JButton okBtn,applyBtn,cancelBtn;


    /**
        class constructor for this dialog. 
        <p> 
        NOTES:<br> 
        refactored during 1.1 to allow compilation with interfaced AgentContexts. 
        <P> 
        TO DO: <br>
        work out what the hell this really is for....
        */
   public ControlOptionsDialog(AgentViewer agentviewer,AgentContext context) {
      super(agentviewer,"Set Agent's Context");
      this.context = context;

      //---leftpanel
      leftPanel = new JPanel(new GridLayout(9,2));
      leftPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      leftPanel.add(new JLabel("Share plan "));
      share_plan = new JCheckBox();
      share_plan.setSelected(SystemProps.getState("share.plan"));
      //share_plan.setFont(new Font("Helvetica", Font.PLAIN, 8));
      leftPanel.add(share_plan);

      leftPanel.add(new JLabel("Execute earliest"));
      execute_earliest = new JCheckBox();
      execute_earliest.setSelected(SystemProps.getState("execute.earliest"));
      //execute_earliest.setFont(new Font("Helvetica", Font.PLAIN, 8));
      leftPanel.add(execute_earliest);

      leftPanel.add(new JLabel("Registration timeout"));
      registration_timeout = new RealNumberField(0,10);
      registration_timeout.setValue(context.getRegistrationTimeout());
      leftPanel.add(registration_timeout);

      leftPanel.add(new JLabel("Broker timeout"));
      facilitator_timeout = new RealNumberField(0,10);
      facilitator_timeout.setValue(context.getFacilitatorTimeout());
      leftPanel.add(facilitator_timeout);

      leftPanel.add(new JLabel("Address timeout"));
      address_timeout = new RealNumberField(0,10);
      address_timeout.setValue(context.getAddressTimeout());
      leftPanel.add(address_timeout);

      leftPanel.add(new JLabel("Accept timeout"));
      accept_timeout = new RealNumberField(0,10);
      accept_timeout.setValue(context.getAcceptTimeout());
      leftPanel.add(accept_timeout);

      leftPanel.add(new JLabel("Addressbook refresh "));
      addressbook_refresh = new RealNumberField(0,10);
      addressbook_refresh.setValue(context.getAddressBookRefresh());
      leftPanel.add(addressbook_refresh);

      leftPanel.add(new JLabel("Broker refresh"));
      facilitator_refresh = new RealNumberField(0,10);
      facilitator_refresh.setValue(context.getFacilitatorRefresh());
      leftPanel.add(facilitator_refresh);

      leftPanel.add(new JLabel("Replan period"));
      replan_period = new RealNumberField(0,10);
      replan_period.setValue(context.getReplanPeriod());
      leftPanel.add(replan_period);

      //--    buttons
      JPanel outerBtn = new JPanel(new BorderLayout());

      JPanel btnPanel = new JPanel();
      btnPanel.setLayout(new BoxLayout(btnPanel,BoxLayout.X_AXIS));

      outerBtn.add(BorderLayout.NORTH,new JSeparator(SwingConstants.HORIZONTAL));
      outerBtn.add(BorderLayout.CENTER,btnPanel);

      okBtn = new JButton("OK");
      okBtn.addActionListener(this);
      okBtn.setForeground(Color.black);
      okBtn.setFont(new  Font("Helvetica", Font.BOLD, 14));
      btnPanel.add(okBtn);
      btnPanel.add(Box.createRigidArea(new Dimension(20,10)));

      applyBtn = new JButton("Apply");
      applyBtn.addActionListener(this);
      applyBtn.setForeground(Color.black);
      applyBtn.setFont(new  Font("Helvetica", Font.BOLD, 14));

      btnPanel.add(applyBtn);
      btnPanel.add(Box.createRigidArea(new Dimension(20,10)));

      cancelBtn = new JButton("Cancel");
      cancelBtn.setForeground(Color.black);
      cancelBtn.setFont(new  Font("Helvetica", Font.BOLD, 14));
      cancelBtn.addActionListener(this);
      btnPanel.add(cancelBtn);


      contentPane = (JPanel) getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(BorderLayout.CENTER,leftPanel);
      contentPane.add(BorderLayout.SOUTH,outerBtn);

      pack();
      setVisible(true);
      setModal(true);
   }


    /**
        applySettings is a package protected method that is used to set the agents working 
        parameters 
        <P> notes: heavily refactored during the 1.1 rearchitect
        */
      void applySettings() {
         context.setSharePlan (share_plan.isSelected());
         context.setExecuteEarliest(execute_earliest.isSelected());

         Double value;
	     value = registration_timeout.getValue(context.getRegistrationTimeout());
         context.setRegistrationTimeout (value.doubleValue());

	     value = facilitator_timeout.getValue(context.getFacilitatorTimeout());
         context.setFacilitatorTimeout(value.doubleValue());
         
	     value = address_timeout.getValue(context.getAddressTimeout());
         context.setAddressTimeout (value.doubleValue());
	 
	     value = accept_timeout.getValue(context.getAcceptTimeout());
         context.setAcceptTimeout(value.doubleValue());
	 
	     value = addressbook_refresh.getValue(context.getAddressBookRefresh());
         context.setAddressBookRefresh(value.doubleValue());

	     value = facilitator_refresh.getValue(context.getFacilitatorRefresh());
         context.setFacilitatorRefresh (value.doubleValue());

	     value = replan_period.getValue(context.getReplanPeriod());
         context.setReplanPeriod(value.doubleValue());
      }



     public void actionPerformed(ActionEvent evt) {
         Object source = evt.getSource();

         if ( source == applyBtn ) {
            applySettings();
           return;
         }
         else if ( source == okBtn) {
           applySettings();
           this.dispose();
         }
         else if (source == cancelBtn) {
           this.dispose();
         }
     }

}
