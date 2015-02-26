// I am not sure if all the zeus imports are required for this example,
// but they are likely to be needed for more complex examples
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import zeus.agents.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.fields.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.generator.util.*;

/** 
* DisplayerGui enables a user to press a button that asserts a
* displayed goal into the agents context database.
* Note this is research quaility software.
* @author Jamie Stark Jan 2001
*/
public class DisplayerGui extends Frame implements ZeusExternal{
    
    /** The agent context enables the external to get at the attributes
        of an agent */
    private AgentContext agent;
    /** The Button to push */
    private Button button;
    
    /** Constructor sets up the frames and add an ActionListener to 
        the button*/
	public DisplayerGui(){
        button = new Button("Assert Goal");
        add(button);
		setSize(290,155);
		setVisible(true);
    	button.addActionListener(new ActionListener(){
    	    public void actionPerformed(ActionEvent event){
    			Object object = event.getSource();
	    		if (object ==button)
                		assertGoal();
            }
		});
	}
    	
   /**
   * This is called as the agent is created. Here all that happens is 
   * that AgentContext is stored
   */
    public void exec(AgentContext agentContext){
        agent = agentContext;    
    }
    
    /** This method does all the work. It creates a new displayed fact,
    * sets the value of the flag attribute to true, creates a new goal
    * using this fact and asserts it into the agents co-ordination engine.
    */
    public void assertGoal(){ 
      //Create a new fact usingthe ontology.
      Fact fact = agent.OntologyDb().getFact(Fact.FACT,"displayed");
      // Set he attribute flag to true.
      fact.setValue("flag","true");
      // Create new Goal.
      Goal g = new Goal(agent.newId("goal"), fact,
                        (int)(agent.now())+6, 0.1,agent.whoami(),(double)(agent.now()+3));          		 
      // Assert the goal in the agent's co-ordination engine.
	  agent.Engine().achieve(g);   
    } 
    
}    
