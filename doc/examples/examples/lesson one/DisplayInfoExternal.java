// I am not sure if all the zeus imports are required for this example,
// but they are likely to be needed for more complex examples
import zeus.agents.*;
import zeus.util.*;
import zeus.concepts.*;
import zeus.gui.fields.*;
import zeus.actors.*;
import zeus.actors.event.*;
import zeus.generator.util.*;

/** 
* DisplayerInfoExternal prints our a string onto System.out 
* Note this is research quaility software.
* @author Jamie Stark Jan 2001
*/
public class DisplayInfoExternal implements TaskExternal{     
    
    // To implement the TaskExternal we must implement the exec method
     public void exec(TaskContext tc){
        // The taskContext's inputArgs is an array Fact arrays.
        // There is only one input so we want the first, 
        // i.e. get the first precondition which is an outputFact
        Fact[] outputFact = tc.getInputArgs()[0];
        // We then get the outputFact
        // Which is an array. Since there is only one fact it is the first one
        // We print the value of the info attribute.
        System.out.println("*****************************");
        System.out.println(""+outputFact[0].getValue("info"));
        System.out.println("*****************************");
    }   
}
