
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
* public limited company are Copyright 1996-2001. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/


package zeus.concepts; 
import java.util.*; 
 


/** 
    df_Description holds a description of an df as per fipa
    */

public class DF_Description { 
 
    private FIPA_AID_Address name = null; 
    private Vector services = new Vector(); 
    private Vector protocol = null; 
    private Vector ontology = null; 
    private Vector language = null;
    
    
    
    public void setName (FIPA_AID_Address aid) { 
      this.name = aid;
    }


    public FIPA_AID_Address getName () { 
        return this.name; 
    }

    
   public void addService (FIPA_Service_Description service ) { 
    this.services.addElement(service); 
   }
   
    
    public Vector getServices () { 
        return this.services; 
    }
    
    
    public void setProtocol (Vector protocol) { 
        this.protocol = protocol; 
    }
    
    
    public Vector getProtocol () { 
        return this.protocol; 
    }
    
    
    public void setOntology (Vector ontology) { 
        this.ontology = ontology; 
    }
    
    public Vector getOntology () { 
        return this.ontology;    
    }
    
    public void setLanguage (Vector language) { 
        this.language = language; 
    }
    
    public Vector getLanguage() { 
        return this.language; 
    }
    
       
    
    
    public String toString () { 
        String retVal = new String ("(df-agent-description ");  
        if (name!=null) { 
            retVal += ":name (" + name.toFIPAString()+") ";
        }
        if (services!= null) { 
          retVal += SL_Util.makeSet (":services", services); 
        }
        if (protocol != null ) { 
            retVal += SL_Util.makeSet (":protocol", protocol); 
        }
        if (ontology != null ) { 
           retVal += SL_Util.makeSet (":ontology", ontology); // was ontology
        }
        if (language!= null) { 
          retVal += SL_Util.makeSet (":language",language); 
        }
            
       retVal += ")"; 
              
       return retVal; 
       
    }
    
    /** 
        for debug
        */    
    public static void main (String argv[]) { 
     DF_Description df = new DF_Description (); 
     System.out.println(df.toString()); 
        
    }
 
    
    public boolean match (DF_Description desc) { 
        debug ("in match");
        if (name != null ) {
                if (!desc.getName().equals (name)) {
                    debug ("name doesnt match " + desc.getName() +" " + name ); 
                    return false;
                }
            } 
        if (services != null){
          if (!vecMatch(services,desc.getServices())) {
                debug ("services don't match " ); 
                return false; 
                }
        }
        if (protocol != null){
            if (!vecMatch(protocol,desc.getProtocol())) 
            {
                debug ("protocols don't match"); 
                return false; 
                }
        }
        if (ontology != null){
            if (!vecMatch(ontology,desc.getOntology())) {
                debug ("ontologies don't match "); 
                return false; 
            }
        }
        if (language != null) {
            if (!vecMatch(language,desc.getLanguage())) {
                debug ("languages don't match "); 
                return false;
            }
        }
        return (true); 
    }
    
    
    public boolean vecMatch (Vector vec1, Vector vec2) { 
        debug ("in vecMatch");
        Enumeration elementsNeeded = vec1.elements(); 
        while (elementsNeeded.hasMoreElements()) { 
            Enumeration elementsWeHaveGot = vec2.elements(); 
            boolean found = false; 
            Object element = elementsNeeded.nextElement(); 
            while (elementsWeHaveGot.hasMoreElements() &&!found) {
                Object elementToTest=elementsWeHaveGot.nextElement(); 
                if (element instanceof java.util.Vector 
                    && elementToTest instanceof java.util.Vector) {
                    found = vecMatch ((Vector)element,(Vector)elementToTest); }
                else if (element instanceof zeus.concepts.ContentElement) { 
                    debug ("is ContentElement") ; 
                    found = ((ContentElement)element).match ((ContentElement)elementToTest); 
                }
                else
                {
                    System.out.println("elementToTest = " + elementToTest); 
                    System.out.println("elementToTest.toString = " + elementToTest.toString() + " element.toString = " + element.toString()); 
                    if (elementToTest.toString().equals (element.toString())) 
                        found = true; 
                }
            }
            if (found == false) return false; 
        }
        return true; 
        
    }
 
    
    void debug (String str) { 
        System.out.println(str); 
        
    }
    
}