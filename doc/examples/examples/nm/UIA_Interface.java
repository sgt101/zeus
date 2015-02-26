import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import zeus.util.*;
import zeus.concepts.*;
import zeus.agents.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.gui.*;
import zeus.concepts.fn.*;

public class UIA_Interface extends JFrame implements ZeusExternal {

   protected AgentContext context = null;
   protected JTextField[] nodes = new JTextField[4];
   protected JTextField[] values = new JTextField[4];
   protected JButton clearBtn, executeBtn;
   protected JTextField vc_field;

   public UIA_Interface() {
      SymAction listener = new SymAction();
      clearBtn = new JButton("Clear");
      clearBtn.addActionListener(listener);
      executeBtn = new JButton("Send");
      executeBtn.addActionListener(listener);

      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(1,2,5,5));
      panel.add(clearBtn);
      panel.add(executeBtn);

      JPanel contentPane = (JPanel) getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(panel,BorderLayout.SOUTH);

      panel = new JPanel();
      panel.setLayout(new GridLayout(1,2,5,5));

      panel.add(new JLabel("VC Name:"));
      vc_field = new JTextField("",20);
      panel.add(vc_field);

			contentPane.add(panel,BorderLayout.NORTH);

      panel = new JPanel();
      panel.setLayout(new GridLayout(5,2,5,5));

      panel.add(new JLabel("Node Name"));
      panel.add(new JLabel("Node Status"));

      for(int i = 0; i < 4; i++ ) {
         nodes[i] = new JTextField("",20);
         values[i] = new JTextField("",20);
         panel.add(nodes[i]);
         panel.add(values[i]);
      }

 			contentPane.add(panel,BorderLayout.CENTER);
      setVisible(false);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
   }

   public void exec(AgentContext context) {
      this.context = context;
      context.ResourceDb().addFactMonitor(new SymFactMonitor(),
         FactEvent.ADD_MASK|FactEvent.DELETE_MASK);

      pack();
      setVisible(true);
      repaint();
      setResizable(false);
   }

   protected class SymFactMonitor extends FactAdapter {
      public void factAddedEvent(FactEvent e) {
         Fact f1 = e.getFact();
         if ( f1.getType().equals("NodeStatus") ) {

            String element = "element_";
            String status = "status_";
            String name, value;
            for(int i = 0; i < 4; i++ ) {
               name = f1.getValue(element + (i + 1));
               if ( name.equals("nil") ) {
                  nodes[i].setText("");
                  values[i].setText("");
               }
               else {
                  nodes[i].setText(name);
                  values[i].setText(f1.getValue(status + (i + 1)));
               }
            }
            repaint();
         }
      }
   }

   /* This is where the user will have the ability to press the
      send button and the UIA will execute an event. This can be
      changed by putting it in a loop and it will automatically fire */
   protected class SymAction implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();

         if ( src == clearBtn ) {
            vc_field.setText("");
            for(int i = 0; i < 4; i++ ) {
               nodes[i].setText("");
               values[i].setText("");
            }
         }

         else if ( src == executeBtn ) {
  	        Fact request = context.OntologyDb().getFact(Fact.FACT,"MonitorRequest");
            request.setValue("id",vc_field.getText());
            context.ResourceDb().add(request);
         }
      }
   }
}
