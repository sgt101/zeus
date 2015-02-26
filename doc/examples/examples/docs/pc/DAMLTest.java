import com.hp.hpl.jena.daml.*;
import com.hp.hpl.jena.daml.common.*;
import com.hp.hpl.jena.rdf.arp.*;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.arp.lang.*;
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.common.*;
import com.ibm.icu.text.*;
import java.util.*;

public class DAMLTest {
    
    
    public DAMLTest(String fileName) {
        try {
            DAMLModel m = new DAMLModelImpl();
            //String uri = "http://" + context.whereAmI() + "/services/" + taskName;
            String uri = "file:///c:/work/zeus/examples/pc/services/" + fileName;
            System.out.println("\n\n\n\n\n>>>>>>>>>>>>>>>>");
            System.out.println(uri);
            m.read( uri );
            if (! m.getLoadSuccessful()) {
                System.out.println( "Failed to load DAML document!" );
            }
            else {
                System.out.println("Parsed happily!!!!");
                handleModel(m);
            }
        } catch (Throwable eeee) {
            eeee.printStackTrace();
        }}
    
    /**
     *collection method that invokes various subroutines to pretty print the model
     */
    public void handleModel( DAMLModel m) {
        dumpModel(m);
        // dumpObjects(m);
        dumpStatements(m);
        //   dumpClasses(m);
        // dumpProperties(m);
        // dumpInstances(m);
    }
    
    
    private void dumpModel(DAMLModel m) {
        System.out.println("Model Class = " + m.getClass().getName());
    }
    
    
    private void dumpStatements(DAMLModel m) {
        try {
            StmtIterator allStat = m.listStatements();
            System.out.println("ALL OBJECTS");
            while (allStat.hasNext()) {
                try {
                    System.out.println("\n------------------------------------------");
                    Statement stat = (Statement) allStat.next();
                    //  System.out.println("\tStatement");
                    // find it
                    Property prop = stat.getPredicate();
                    System.out.println("\t\tProperty = " + prop.getLocalName());
                    RDFNode obj = stat.getObject();
                    // now access the restrictions
                    try {
                        if (obj instanceof ResourceImpl) {
                            ResourceImpl res = (ResourceImpl) obj;
                            StmtIterator allProps = res.listProperties();
                            while (allProps.hasNext()) {
                                
                                Statement propStat = (Statement) allProps.next();
                                prop = propStat.getPredicate();
                                System.out.println("\t\t>Property = " + prop.getLocalName());
                                
                                RDFNode objj = propStat.getObject();
                                System.out.println("\t\t>>Object = " + objj.toString());
                                Resource ress = propStat.getSubject(); 
                                System.out.println ("\t\t>>>Subject = " + ress.toString()); 
                                //  Resource resn = propStat.getResource ();
                                //  System.out.println("\t\t>>Resource = " + resn.toString());
                            }
                        }
                        else {
                            LiteralImpl lit = (LiteralImpl) obj;
                            System.out.println(">>> Literal = " + lit.getString()); }
                            
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        //    Resource res = stat.getResource();
                        //  System.out.println("\t\tResource = " + res.toString());
                    }
                    catch (Exception e) {
                        ;}
                }
            }
            catch (Exception e) {
                ;
            }
        }
        
        private void dumpObjects(DAMLModel m) {
            try {
                NodeIterator allObj = m.listObjects();
                System.out.println("ALL OBJECTS");
                while (allObj.hasNext()) {
                    Object obj = allObj.next();
                    System.out.println("\t Object class = " + obj.getClass().getName());
                    System.out.println("\t Value = " + obj.toString());
                }}
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
        private void dumpProperties(DAMLModel m) {
            Iterator allProperties = m.listDAMLProperties();
            System.out.println("ALL PROPERTIES");
            while (allProperties.hasNext()) {
                Object prop = allProperties.next();
                System.out.println("\t Property class = " + prop.getClass().getName() );
                System.out.println("\t Value = " + prop.toString());
            }
        }
        
        
        private void dumpClasses(DAMLModel m) {
            Iterator allClasses = m.listDAMLClasses();
            System.out.println("ALL CLASSES");
            while (allClasses.hasNext()) {
                Object clas = allClasses.next();
                System.out.println("\t Class class = " + clas.getClass().getName());
                System.out.println("\t Value = " + clas.toString());
            }
        }
        
        
        private void dumpInstances(DAMLModel m) {
            Iterator allInstances = m.listDAMLInstances();
            System.out.println("ALL INSTANCES");
            while (allInstances.hasNext()) {
                Object inst = allInstances.next();
                System.out.println("\t Instance class = " + inst.getClass().getName());
                System.out.println("\t Value = " + inst.toString());
            }
        }
        
        
        public static void main(String args[] ) {
            new DAMLTest(args[0]);
        }
    }