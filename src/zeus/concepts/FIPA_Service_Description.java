
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
import JADE_SL.abs.*;
import zeus.util.SystemProps; 


/** 
 * provide a datatype that is used to hold the service descriptions
 * and provide matching services for them
 *@author Simon Thompson
 *@version various, but actively used from 1.3 on.
 */
public class FIPA_Service_Description implements ContentElement { 
    
    private String name = null; 
    private String type = null;
    private Vector services = null; 
    private Vector protocol = null; 
    private Vector ontology = null; 
    private Vector language = null;
    private String ownership = null; 
    private Vector properties = null; 
    
    
    public FIPA_Service_Description () { 
        ;
    }
    
    /** 
     * this takes a service description in the form of a parse tree from 
     *the JADE_SL parser, and then pulls the leaves off and puts it into 
     *this nice datastructure
     */
    public FIPA_Service_Description (AbsConcept description) {
     AbsPrimitive typePrim = (AbsPrimitive) description.getAbsObject("type"); 
     setType (typePrim.getString()); 
     AbsPrimitive ownershipPrim = (AbsPrimitive) description.getAbsObject("ownership"); 
     setOwnership (ownershipPrim.getString()); 
     AbsPrimitive namePrim = (AbsPrimitive) description.getAbsObject ("name"); 
     // I think it is a simple string in service descriptions. 
     setName (namePrim.getString()); 
     AbsAggregate propertiesConcept = (AbsAggregate) description.getAbsObject ("properties"); 
     Vector props = SL_Util.makeVector(propertiesConcept); 
     setProperties (props); 
     AbsAggregate langAggr = (AbsAggregate) description.getAbsObject ("language"); 
     Vector langs = SL_Util.makeVector (langAggr); 
     setLanguage(langs); 
     AbsAggregate protoAggr = (AbsAggregate) description.getAbsObject ("protocol"); 
     Vector protos = SL_Util.makeVector (protoAggr); 
     setProtocol (protos); 
     AbsAggregate ontoAggr = (AbsAggregate) description.getAbsObject ("ontology"); 
     Vector ontos = SL_Util.makeVector (ontoAggr); 
     setOntology (ontos);       
    }
    
    
    public void setType (String type) { 
        this.type = type; 
    }
    
    
    public String getType () { 
        return type; 
    }
    
    
    
    public void setOwnership (String ownership) {
     this.ownership = ownership;   
    }
    
    
    public String getOwnership () { 
        return ownership; 
    }
    
    public void setProperties (Vector props) { 
        this.properties = props; 
    }
    
    
    public Vector getProperties () { 
        return properties;    
    }
    
    
    public void setName (String name) { 
      this.name = name;
    }


    public String getName () { 
        return this.name; 
    }

    
   public void setServices (Vector services ) { 
    this.services = services; 
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
    
    /**
     *setAgentcitiesProperties is called with three parameters to make the 
     *service registration in the DF carry the metadata required for service 
     *lookup in agentcities. 
     *@param classfication is a string that gives the services classifcation 
     *as in the sort of service that it is {information,...} 
     *@param domain is a string that indicates the type of domain that the 
     *service applies to 
     *@param scope is the geographic range of the service; is it useful only 
     *within 2 miles of a particular place for example?
     *@author Simon Thompson
     *@date 27/1/03
     *below is the set of properties that this should emit. 
     *you must set the servicePlatform property in .zeus.prp for this to 
     *work, the first property is automatically generated. 
     *(Property
     *  :name “DAML-SServiceProfile”
     *   :value “http://link2.daml-s.description.org/”
     *   )
     *(Property
     *  :name “ServiceClassification::ServiceType”
     *  :value “Information”
     * )
     * (Property
     *  :name “ServiceClassification::Domain”
     *  :value “Entertainment::FoodAndBeverages::Restaurant”
     * )
     * (Property
     *  :name “ServiceClassification::GeographicScope”
     *  :value “World::Europe::Portugal::district:Lisboa”
     * )
     **/
     public void setAgentcitiesProperties ( String classification, String domain, String scope) { 
      
         Vector properties = new Vector(); 
         String taxonomy = new String("( property :name \\\"DAML-SServiceProfile\\\" :value \\\""+ SystemProps.getProperty("servicePlatform") +
                                        "/services/classes/"+ getName() + "\\\") "); 
         properties.addElement(taxonomy); 
         String serviceClass = new String("(property :name \\\"ServiceClassification::ServiceType\\\" :value \\\""+classification +"\\\")  "); 
         properties.addElement(serviceClass); 
         String serviceDomain = new String ("(property :name \\\"ServiceClassification::Domain\\\"  :value \\\""+domain+"\\\")  "); 
         properties.addElement(serviceDomain); 
         String serviceScope = new String ("(property :name \\\"ServiceClassification::GeographicScope\\\" :value \\\""+ scope+"\\\")  "); 
         properties.addElement(serviceScope); 
         
         this.setProperties(properties); 
         
     }
    
       /** 
        * returns a String representation of the service description, formatted into SL 
        *
        **/
    public String toString () { 
        String retVal = new String ("(service-description ");  
        if (name!=null) { 
            retVal += ":name " + name +" ";
        }
        if (type != null) { 
            retVal += ":type " + type + " ";
        }   
        if (protocol != null ) { 
            retVal += SL_Util.makeSet (":protocol", protocol);
        }
        if (ontology != null ) { 
            retVal += SL_Util.makeSet (":ontology",ontology); 
        }
        if (ownership != null) { 
            retVal += ":ownership " + ownership + " "; 
        }
        if (language!= null) { 
           retVal += SL_Util.makeSet (":language",language);
        }
        if (properties != null ){ 
          retVal += SL_Util.makeSet(":properties", properties); 
        }
          retVal += ")"; 
       debug (retVal); 
       return retVal; 
       
    }
    
    /** 
        for debug
        */    
    public static void main (String argv[]) { 
     FIPA_Service_Description fds = new FIPA_Service_Description (); 
     System.out.println(fds.toString()); 
        
    }
    
    /**
     *debug method - prints to the system, or not depending on 
     *if this is activated (commented out) or not
     */
    void debug (String str) { 
        System.out.println("FIPA_Service_Description: " + str); 
    }
    
    
    
    /** 
        match checks to see if the thing sent to it matches (according to 
        generalx == specificx criteria) with it
        This method assumes that this object is the general case and the toMatchTo 
        object is the specific case, so toMatchTo can have fields that are 
        null here and still match to this. 
        */
    public boolean match (ContentElement matcher) { 
        if (!(matcher instanceof zeus.concepts.FIPA_Service_Description)) return false; 
        
        FIPA_Service_Description toMatchTo = (FIPA_Service_Description) matcher; 
        
        if (name != null) { 
            if (!name.equals (toMatchTo.getName())) 
            {
                debug ("names don't match"); 
                return false; 
            }}
        if (type != null) { 
            if (!type.equals (toMatchTo.getType())) {
                debug ("types don't match, me = " + type +" he = " + toMatchTo.getType());   
                return false; 
            }}
        if (services !=null && (toMatchTo.getServices() != null)) { 
            if (!vecMatch (services, toMatchTo.getServices())) {
                debug ("services don't match"); 
                return false; 
                }}
        if (protocol != null && (toMatchTo.getProtocol() != null)) { 
            if (!vecMatch (protocol, toMatchTo.getProtocol ())) { 
                debug ("protocols don't match"); 
                return false;
            }}
        if (ontology != null && (toMatchTo.getOntology()!=null) ) { 
            if (!vecMatch (ontology, toMatchTo.getOntology())) { 
                debug ("ontologies don't match"); 
                return false;
              }}
        if ( language != null && (toMatchTo.getLanguage()!=null) ) { 
            if (!vecMatch (language, toMatchTo.getLanguage ())) { 
                debug ("languages don't match "); 
                return false; 
            }}
        if (ownership != null ) {
            if (!ownership.equals (toMatchTo.getOwnership())) { 
                debug ("ownerships don't match"); 
                return false;
            }}
        if (properties != null && (toMatchTo.getProperties()!=null)) { 
            if (!vecMatch (properties, toMatchTo.getProperties ())) {
                debug ("properties don't match"); 
                return false; 
            }}
        return (true); 
    }
        
    
       /**
        *checks through the elements of both vectors ans tries to find if they 
        *contain the same thing
        */
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
                    ;
                }
                else {
                    System.out.println("elementToTest.toString = " + elementToTest.toString() + " element.toString = " + element.toString()); 
                    if (elementToTest.toString().equals (element.toString())) 
                        found = true; 
                }
            }
            if (found == false) return false; 
        }
        return true; 
    }
    
    
    }

    


