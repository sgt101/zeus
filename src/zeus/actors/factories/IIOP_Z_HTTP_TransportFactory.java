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

package zeus.actors.factories;

//
import javax.agent.service.*;
import zeus.actors.outtrays.*;
import zeus.actors.intrays.*;
import java.util.*;
import javax.rmi.*;
import zeus.concepts.*;
import zeus.actors.*;
import java.io.*;
import fipa97.FIPA_Agent_97;
import fipa97.FIPA_Agent_97Helper;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import zeus.actors.outtrays.*;
import javax.naming.*;
import zeus.util.*;
import java.io.FileWriter;
import java.io.File;




/**
 * IIOP_Z_HTTP_TransportFactory is a simple implementation of the TransportFactory
 * that takes an address string and returns an appropriate transport for that agent.
 * <li> if the agent is a zeus agent then a wrapped version
 * of the standard zeus postman and server is returned. </li>
 * <li> if the agent is using a FIPA_97 IIOP transport then a wrapped FIPA_Agent_97 is
 * returned </li>
 * <li> if the agent is using a FIPA_99 IIOP transport then a wrapped FIPA.MTS is returned
 * </li>
 * <li> if any other transport is detected then an error is raised.</li>
 * <p> Note: we will seek  to support other transports, but it is likely that
 * this transport factory will not: so basically ensure that you DO NOT directly instantiate this
 * class. <p>
 * <B> <u>TO USE THIS</u> </B> <br>
 * Call zeus.actors.service.TransportFactoryMethod.getTransportFactory(), this will return a class of type
 * TransportFactory. In the initial implementation it will always be this class, *but*
 * in the future we may implement lightweightTransportFactory, IIOP_HTTP_TransportFactory
 * or JMS_TransportFactory, and use a system property to define which one is returned by
 * the TransportFactoryMethod in a given agent...
 * <p>
 * ISSUES </P>
 * is this at all sensible? Or am I making a mountain out of a mole hill? <br>
 * need context to make zeus transport
 * @author Simon Thompson
 * @since 1.1
 */

public class IIOP_Z_HTTP_TransportFactory implements TransportFactory {
    
    private ZeusAgentContext context = null;
    private int _maxSize = 100;
    private static ConnectionPool pool = new ConnectionPool();
    private File file; 
    
    public void setContext(ZeusAgentContext context) {
        this.context = context;
    }
    
    public void setLog (File file) {
     this.file = file;    
    }
    
    /**
     * this method returns a transport for this address. having called this
     * all you need to do to send a message is call Transport.send(Envelope);
     * <P> HOW IT WORKS <p>
     * 1) check and see if the address starts with the key word(s) iiop/iiopname
     * 2) split out the address (name.domain.type) and port (:9000) and use
     * them to initialise an orb
     * 3) then churn through the naming contexts that it uses - if an address is
     * of the form iiop://name.domain.type:9000/xxx/yyy/zzz/acc then this means
     * that there are three naming contexts (xxx,yyy,zzz) which must be found before
     * we can get hold of acc.
     * 4) when we have acc we will know because an ClassCastException will have been
     * thrown.. so handle it by trying to cast to either FIPA_Agent_97 or FIPA.MTS. If
     * you can do that then no probs, use the result to initialise a transport object of
     * the appropriate type and return it.
     * 5) if no cast works then throw an UnsupportedTransportException
     * 6) Handle the http: case (throw an Exception)
     * 7) Otherwise try a Zeus address, and if this works then return
     * a zeus transport object
     * <p> ISSUES <p>
     * This is a bad, bad method: appologies (I am in a hurry and rather up against it
     * here!). There is far too much code in one block and it is very complex - so
     * refactor, refactor!
     *
     */
    public OutTray getTransport(String address) throws TransportUnsupportedException {
        try {
            //  System.out.println("address at start = " + address);
            OutTray trans = pool.getConnection(address);
            return trans;
        }
        catch (NotFoundException e) {
            ;} // rest of method could be in this block, but just irrelevant really.
        
        if ( address.startsWith("iiop") || address.startsWith("iiopname")) {
            // open it, try FIPA_97, if that throws an exception then
            // try FIPA_99, then go home....
            StringTokenizer tokens = new StringTokenizer(address,"/");
            tokens.nextToken();
            String ofInterest = tokens.nextToken();
            //  System.out.println("of interest = " + ofInterest);
            StringTokenizer addressFinder = new StringTokenizer(ofInterest,":");
            String addr = addressFinder.nextToken(); // should be machine.domain.type
            //  System.out.println("addr = " + addr);
            String port = addressFinder.nextToken(); // should be a number
            //    System.out.println("port = " + port);
            Properties prop = new Properties();
            prop.setProperty("org.omg.CORBA.ORBInitialHost",addr);
            prop.setProperty("org.omg.CORBA.ORBInitialPort",port);
            String args [] = new String[0];
            ORB orb = ORB.init(args, prop);
            org.omg.CORBA.Object objb = null;
            debug(orb.toString());
            String list [] = orb.list_initial_services();
            for (int i = 0; i < list.length; i++) {
                debug(list[i]);
            }
            try {
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContext nc = NamingContextHelper.narrow(objRef);
                //testNamingContext (nc,orb);
                boolean done = false ;
                NameComponent allPath[] = new NameComponent [_maxSize];
                int count =0;
                String name = null;
                while (tokens.hasMoreTokens()) {
                    name = tokens.nextToken();
                    debug("name = " + name);
                }
                objb = resolveNaming(nc,orb,name);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                debug("objb == " + objb.toString());
                FIPA_Agent_97 target =  FIPA_Agent_97Helper.narrow(objb);
                
                OutTray trans = new FIPA_97_IIOP_Transport(target);
                pool.addConnection(address,trans);
                return trans;
            } catch (Exception e) {
                // exception is intensional and is handled by then next try
                //e.printStackTrace();
            }
            
            try {
                debug("objb == " + objb.toString());
                FIPA.MTS target = FIPA.MTSHelper.narrow( objb );
                
                debug("target == " + target.toString());
                OutTray trans = new FIPA_2000_IIOP_Transport(target);
                pool.addConnection(address,trans);
                return trans;
            } catch (Exception e) {
                // exception is intensional and is thrown next...
                //e.printStackTrace();
            }
            throw new TransportUnsupportedException("iiop target appears not to be returning a object with"+
            " an interface that is understood by Zeus" +
            "\n currently we have only FIPA_2000/99 (FIPA.MTS) & FIPA_97 (FIPA_Agent_97)" +
            "sorry");
        }
        else if (address.startsWith("corbaloc")) {
            address = address.substring(10,address.length());
            // System.out.println("address = " + address);
            StringTokenizer tokens = new StringTokenizer(address,"/");
            String ofInterest = tokens.nextToken();
            // System.out.println("of interest = " + ofInterest);
            StringTokenizer addressFinder = new StringTokenizer(ofInterest,":");
            String addr = addressFinder.nextToken(); // should be machine.domain.type
            // System.out.println("addr = " + addr);
            String port = addressFinder.nextToken(); // should be a number
            //System.out.println("port = " + port);
            Properties prop = new Properties();
            prop.setProperty("org.omg.CORBA.ORBInitialHost",addr);
            prop.setProperty("org.omg.CORBA.ORBInitialPort",port);
            String args [] = new String[0];
            ORB orb = ORB.init(args, prop);
            org.omg.CORBA.Object objb = null;
            debug(orb.toString());
            String list [] = orb.list_initial_services();
            for (int i = 0; i < list.length; i++) {
                debug(list[i]);
            }
            try {
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContext nc = NamingContextHelper.narrow(objRef);
                //testNamingContext (nc,orb);
                boolean done = false ;
                NameComponent allPath[] = new NameComponent [_maxSize];
                int count =0;
                String name = null;
                while (tokens.hasMoreTokens()) {
                    name = tokens.nextToken();
                    debug("name = " + name);
                }
                objb = resolveNaming(nc,orb,name);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                debug("objb == " + objb.toString());
                FIPA_Agent_97 target =  FIPA_Agent_97Helper.narrow(objb);
                OutTray trans = new FIPA_97_IIOP_Transport(target);
                pool.addConnection("corbaname::"+address,trans);
                return trans;
            } catch (Exception e) {
                // exception is intensional and is handled by then next try
                //e.printStackTrace();
            }
            
            try {
                debug("objb == " + objb.toString());
                FIPA.MTS target = FIPA.MTSHelper.narrow( objb );
                
                debug("target == " + target.toString());
                OutTray trans =  new FIPA_2000_IIOP_Transport(target);
                //  System.out.println("Setting address");
                pool.addConnection("corbaname::"+address,trans);
                return trans;
            } catch (Exception e) {
                // exception is intensional and is thrown next...
                //e.printStackTrace();
            }
            throw new TransportUnsupportedException("iiop target appears not to be returning a object with"+
            " an interface that is understood by Zeus" +
            "\n currently we have only FIPA_2000/99 (FIPA.MTS) & FIPA_97 (FIPA_Agent_97)" +
            "sorry");
            
        }
        else if (address.startsWith("corbaname")) {
            address = address.substring(11,address.length());
            // System.out.println("address = " + address);
            StringTokenizer tokens = new StringTokenizer(address,"/");
            String ofInterest = tokens.nextToken();
            // System.out.println("of interest = " + ofInterest);
            StringTokenizer addressFinder = new StringTokenizer(ofInterest,":");
            String addr = addressFinder.nextToken(); // should be machine.domain.type
            // System.out.println("addr = " + addr);
            String port = addressFinder.nextToken(); // should be a number
            //System.out.println("port = " + port);
            Properties prop = new Properties();
            prop.setProperty("org.omg.CORBA.ORBInitialHost",addr);
            prop.setProperty("org.omg.CORBA.ORBInitialPort",port);
            String args [] = new String[0];
            ORB orb = ORB.init(args, prop);
            org.omg.CORBA.Object objb = null;
            debug(orb.toString());
            String list [] = orb.list_initial_services();
            for (int i = 0; i < list.length; i++) {
                debug(list[i]);
            }
            try {
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContext nc = NamingContextHelper.narrow(objRef);
                //testNamingContext (nc,orb);
                boolean done = false ;
                NameComponent allPath[] = new NameComponent [_maxSize];
                int count =0;
                String name = null;
                while (tokens.hasMoreTokens()) {
                    name = tokens.nextToken();
                    debug("name = " + name);
                }
                objb = resolveNaming(nc,orb,name);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                debug("objb == " + objb.toString());
                FIPA_Agent_97 target =  FIPA_Agent_97Helper.narrow(objb);
                OutTray trans = new FIPA_97_IIOP_Transport(target);
                pool.addConnection("corbaname::"+address,trans);
                return trans;
            } catch (Exception e) {
                // exception is intensional and is handled by then next try
                //e.printStackTrace();
            }
            
            try {
                debug("objb == " + objb.toString());
                FIPA.MTS target = FIPA.MTSHelper.narrow( objb );
                
                debug("target == " + target.toString());
                OutTray trans =  new FIPA_2000_IIOP_Transport(target);
                //  System.out.println("Setting address");
                pool.addConnection("corbaname::"+address,trans);
                return trans;
            } catch (Exception e) {
                // exception is intensional and is thrown next...
                //e.printStackTrace();
            }
            throw new TransportUnsupportedException("iiop target appears not to be returning a object with"+
            " an interface that is understood by Zeus" +
            "\n currently we have only FIPA_2000/99 (FIPA.MTS) & FIPA_97 (FIPA_Agent_97)" +
            "sorry");
            
        }
        
        else if (address.startsWith("IOR")) {
            Properties prop = new Properties();
            // prop.setProperty("org.omg.CORBA.ORBInitialHost",addr);
            //prop.setProperty("org.omg.CORBA.ORBInitialPort",port);
            String args [] = new String[0];
            ORB orb = ORB.init(args,null);
            
            org.omg.CORBA.Object objb = orb.string_to_object(address);
            debug("orb = " + objb.toString());
            FIPA.MTS target = FIPA.MTSHelper.narrow(objb);
            debug("MTS = " + target.toString());
            OutTray trans = new FIPA_2000_IIOP_Transport(target);
            debug("trans = " + trans.toString());
            pool.addConnection(address,trans);
            return trans;
            
        }
        else if (address.startsWith("http")) {
            FIPA_2000_HTTP_Accessor conn = new FIPA_2000_HTTP_Accessor(address);
            OutTray trans = new FIPA_2000_HTTP_Transport(conn,file);
            pool.addConnection(address,trans);
            return (trans);
            
        }
        else {
            try {
                ZeusParser.address(address); // was commented...
                OutTray trans = new Zeus_Native_Transport(context);
                pool.addConnection(address,trans);
                return trans; }
            catch ( Exception e) {
                e.printStackTrace();
                throw new TransportUnsupportedException("No transport supported for this address " +
                "tried FIPA_97_IIOP, FIPA_2000_IIOP & zeus native");
                
                
            }
        }
    }
    
    
    
    
    private void testNamingContext(NamingContext nc,ORB orb ) {
        try {
            BindingListHolder bl = new BindingListHolder();
            BindingIteratorHolder blIt= new BindingIteratorHolder();
            nc.list(1000, bl, blIt);
            org.omg.CosNaming.Binding bindings[] = bl.value;
            if (bindings.length == 0) return;
            for (int i=0; i < bindings.length; i++) {
                // get the object reference for each binding
                org.omg.CORBA.Object obj = nc.resolve(bindings[i].binding_name);
                
                int lastIx = bindings[i].binding_name.length-1;
                
                // check to see if this is a naming context
                if (bindings[i].binding_type == BindingType.ncontext) {
                    testNamingContext((NamingContext) obj, orb);
                } else {
                    debug("Object: " + bindings[i].binding_name[lastIx].id);
                }
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * primative at the mo  - walks the whole tree
     */
    private org.omg.CORBA.Object resolveNaming(NamingContext nc,ORB orb, String name  ) {
        try {
            BindingListHolder bl = new BindingListHolder();
            BindingIteratorHolder blIt= new BindingIteratorHolder();
            nc.list(1000, bl, blIt);
            org.omg.CosNaming.Binding bindings[] = bl.value;
            debug(bindings.toString());
            // if (bindings.length == 0) return;
            for (int i=0; i < bindings.length; i++) {
                // get the object reference for each binding
                org.omg.CORBA.Object obj = nc.resolve(bindings[i].binding_name);
                if (obj == null) { debug("BAD NAMING CONTEXT"); }
                int lastIx = bindings[i].binding_name.length-1;
                
                // check to see if this is a naming context
                if (bindings[i].binding_type == BindingType.ncontext) {
                    debug("name == "+ name);
                    debug("orb == " + orb.toString());
                    debug("obj == " + obj.toString());
                    try {
                        NamingContext ncRef = NamingContextHelper.narrow(obj);
                        //   NamingContext namer = (NamingContext) obj;
                        obj = resolveNaming(ncRef, orb, name);
                        return (obj); }
                    catch (Exception e) {
                        debug("E");
                        e.printStackTrace();
                        debug(bindings[i].binding_type.toString());
                        for (int count = 0; count<bindings[i].binding_name.length; count++) {
                            debug(bindings[i].binding_name[count].id);
                            debug(bindings[i].binding_name[count].kind);}
                        
                        return (org.omg.CORBA.Object) obj;
                    }
                    
                } else {
                    debug("in else");
                    if ( bindings[i].binding_name[lastIx].id.equalsIgnoreCase(name)) {
                        debug("got a match");
                        return (obj);
                    }
                    else {
                        debug("bindings[i].binding_name[lastIx].id == " +bindings[i].binding_name[lastIx].id);
                        debug("but name was " + name);
                    }
                    
                }
                debug("end of loop");
            }
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // no match, returning null.
        return null;
    }// end resolveName
    
    
    
    
    void debug(String str) {
        System.out.println(str);
    }
     
    
}
