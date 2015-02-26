/*
 * SL_Util.java
 *
 * Created on 12 March 2002, 13:44

 */

package zeus.concepts;
import java.util.*; 
import JADE_SL.abs.*;

/**
 * provide some commonly used functors to do formatting. 
 * @author  Simon Thomposon
 * @version 
 */
public class SL_Util {

      
        /** 
         *  turn AbsAggregate into a Vector
         **/
        public static Vector makeVector(AbsAggregate aggr) { 
            Iterator iter = aggr.iterator(); 
            Vector vec=new Vector(); 
            while (iter.hasNext()) { 
                vec.addElement (iter.next()); }   
            return vec;
        }
    
    
    /**
     *used to turn a name and a set of addresses into a fipa agentid string 
     **/
    public static String makeAddressString (AbsPrimitive name, AbsAggregate addresses) { 
        String retVal = new String ("(agent-identifier :name "); 
        retVal += name.toString() +" :addresses (sequence "; 
        Iterator iter = addresses.iterator(); 
        while (iter.hasNext()) { 
            AbsPrimitive address = (AbsPrimitive) iter.next(); 
            retVal += address.toString() +" "; 
        }
        retVal +="))"; 
        return retVal; 
    }
    
     /** 
     *  makeSet is used to convert the vectors full of data into SL formatted strings
     **/ 
    public static String makeSet (String name, Vector members) { 
        String retVal = new String(); 
        String temp = new String(); 
        retVal += name +" (set "; 
        Enumeration holderEnum = members.elements(); 
        while (holderEnum.hasMoreElements()) {
            Object current = holderEnum.nextElement(); 
            if (current instanceof String) {
              temp = (String) current; 
              retVal += temp + " ";
            }
            else if (current instanceof AbsPrimitive) {
               temp = ((AbsPrimitive)current).toString();
               retVal += temp + " " ;}
            else if (current instanceof FIPA_Service_Description ) {
                temp = ((FIPA_Service_Description)current).toString(); 
                retVal += temp + " "; 
            }
            }
            retVal += ") "; 
    return retVal; 
    }
    
}
