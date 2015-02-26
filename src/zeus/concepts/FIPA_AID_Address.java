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
    import zeus.util.*;
    import java.util.*;

    /**
        FIPA_AID_Address is an implementation of the Address interface that is used to
        hold addresses of agents that have identified themselves with FIPA format aid's
        <p>
        ISSUES <p>
        Resolvers: we don't do them yet, and we might need to.
        Personally I hate the idea....

        */
    public class FIPA_AID_Address implements Address {


    private String name = null;
    private String host = null;
    private String port = new String ("900");
    private String type = new String ("FIPA_AID");
    private Vector addresses = new Vector();
    private String alias = null;
    private boolean forwardSet = false;

    public FIPA_AID_Address (FIPA_AID_Address copy) {
        this.name = copy.getName();
        this.host = copy.getHost();
        this.port = String.valueOf(copy.getPort());
        this.type = copy.getType();
        this.addresses = copy.getAddresses();
        this.alias = copy.getAlias();
        this.forwardSet = copy.getForwardingRuleSet();
    }




    public FIPA_AID_Address(String address) {
           this(ZeusParser.fipaAddress(address));


                /*StringTokenizer tokens = new StringTokenizer(address,"@ \n");
                try {
                /*    tokens.nextToken();
                    tokens.nextToken();
                    this.setName ((String) tokens.nextToken());
                    this.setHost ((String) tokens.nextToken());
                    tokens.nextToken();
                    tokens.nextToken();
                    System.out.println("host = " + this.host);
                    String restStr = new String();
                    while (tokens.hasMoreElements()) { restStr+= tokens.nextElement();}
                    StringTokenizer ntokens = new StringTokenizer(restStr);
                    while (ntokens.hasMoreTokens()) {
                        String addr = ntokens.nextToken();
                        System.out.println("adding address: " + addr);
                        addresses.addElement(addr);
                    }*/


    }


    public FIPA_AID_Address (String name, String host) {
        this.host = host;
        this.name = name;
    }

        /**
            return the name of the agent (ie. the acc bit of acc@fipa.bt.com)
            */
    public String getName() {
        return this.name;
    }

        /**
            return the host name (ie. the fipa.bt.com bit of acc@fipa.bt.com)
            */
    public String getHost(){
        return this.host;
    }


    /**
        this method always returns "FIPA_AID"
        */
    public String getType() {
        return ("FIPA_AID");
    }


    /**
        this method always returns 900
        */
    public int getPort(){
        return (900);
    }


    /**
        set the value of the alias for this address that is being used at this time,
        if the value is already set then return the current value. <p>
        <u> if a value is set already this method will not reset it </u>
        <P> note, if you want to set an alias with a current value then you should use
        <i> void resetAlias(String newVal) </i>
        @see resetAlias
        */
    public String setAlias (String possibleValue) {
    if (this.alias==null)
            this.alias = possibleValue;
    return this.alias;

    }


    /**
        reset the value of the alias for this address regardless of its
        current state
        */
    public void resetAlias (String newVal) {
        this.alias = newVal;
    }


    /**
        retrieve the value of the alias for this address
        */
    public String getAlias () {
        return this.alias;
    }


    /**
        if this address has the same name value and the same host value return true
        */
    public boolean equals(Address addr) {
        return name.equals(addr.getName()) &&
                host.equals(addr.getHost());


    }



    /**
        if the addresses share a host name return true;
        also return true if the addresses sequence of this address share a common
        host name and transport<p>
        WARNING: I am not sure if this is the right behaviour.
        */
    public boolean sameAddress(Address addr) {
        return host.equals(addr.getHost());
    }


    /**
        return a zeus address - ie. one that can be handled by Zeus internally.
        */
    public String toString () {
        return( "(" +
                ":name " + name + " " +
                ":host \"" + host + "\" " +
                ":port " + port + " " +
                ":type " + type +
                ")"
                );

    }


    /**
        return a String version in FIPA_AID format
        */
    public String toFIPAString () {
        return ("agent-identifier  " +
                " :name " + getName() +"@" + getHost() +
                " :addresses (sequence " + allAddresses() + ")");
    }


    /**
        addAddress could be confusing to the unwary - this method is used to store
        one of the list of addresses that fipa uses ie. <p>
        <i> :addresses (sequence iiop://foo.com/ACC http://foo.com/ACC ) </I> <p>
        Personally I think that this is all a big mistake, forced on FIPA by the
        use of agent containers and platforms, which are a big mistake. Still, this
        is the way the wind is blowing....
        */
    public void addAddress(String address) {
        addresses.addElement(address);
    }


    /** all addresses just spits out the content of the addresses vector in a
    nicely formatted way */
    private String allAddresses () {
        Enumeration allAddresses = addresses.elements();
        String retVal = new String();
        while (allAddresses.hasMoreElements()) {
            retVal += (String) allAddresses.nextElement() +" ";

        }
        return (retVal);
    }


    public Iterator iterator() {
        return addresses.iterator();
    }

     /**
        getAddresses is protected because I don't want to expose the vector but
        need access to copy it internally. Use allAddresses() or iterator() as an
        alternatives
        */
    protected Vector getAddresses () {
        return this.addresses;
    }


    public void setName (String name) {
        this.name = name;
    }


    public void setHost (String host) {
        this.host = host;
    }



    /**
        forwarding rules used for zeus housekeeping
        */
    public boolean getForwardingRuleSet () {
        return forwardSet;
    }


    /**
        forwarding rules used for zeus housekeeping
        */
    public void setForwardingRuleSet (boolean val ) {
        forwardSet = val;
    }


    /**
        return this zeus.concepts.FIPA_AID_Address rendered as a FIPA.AgentID.
        This is might be used by zeus.actors.service.Transport implementors in order to
        create valid FIPA_99 messages <p>
        ISSUES:<p>
        No resolvers or userDefinedProperties yet...
        (29/01/01 - set resolvers and udp's to empty arrays)
        02/02/01 removed addresses - is this why FIPA-os acc's can't forward things????
        */
    public FIPA.AgentID getAgentID () {
        FIPA.AgentID retVal = new FIPA.AgentID();
        retVal.name = this.name+"@"+this.host;
        retVal.addresses = new String[addresses.size()+1];
        retVal.resolvers = new FIPA.AgentID [0];
    // retVal.resolvers[0] = new FIPA.AgentID();
        retVal.userDefinedProperties  = new FIPA.Property[0];
    //  retVal.userDefinedProperties[0] = new FIPA.Property();
        int count = 0; // was 1
    Enumeration elems = addresses.elements();

    // indended recivers seems to be the problem - try fipaos-suniiop-ext ??
    //retVal.addresses[0] = "fipaos-suniiop-ext";
        // hack for FIPA-OS
        //retVal.addresses[0] = "rmi://"+ host + ":3000"+"/"+ name; // was in
        while (elems.hasMoreElements()) {
        retVal.addresses[count] = (String) elems.nextElement();
            count++;}
        // resolvers and userDefinedProperties are left as null ... for now
        return (retVal);





    }



    /**
        return this as XML as per XC00084C
        */
    public String toXML () {
            String retVal = new String("\t\t\t<agent-identifier>\n");
            retVal+= "\t\t\t\t<name>" + getName() + "@" + getHost() +"</name>\n";
            retVal+= "\t\t\t\t<addresses>\n";
            Iterator addresses = iterator();
            if (SystemProps.FIPA_IMPLEMENTED_TIGHTLY) { // I dispair
             while (addresses.hasNext()) {
                String current = (String) addresses.next();
                retVal+= "\t\t\t\t\t<url>";
                retVal += current +"</url>\n"; }
                }
                /*else {  // no, it really is depressing.
                // hours, and hours OF MY TIME (soon I will be dead).
                while (addresses.hasNext()) {
                    retVal += "\t\t\t\t\t <url>" + addresses.next() +"</url>";
                    // not to mention the millions of ecus and dollars spent on
                    // building other tool kits. I am sick of this....
                }
                          */
                else {
                        String current = new String();
                        while (addresses.hasNext()) {
                                current = (String) addresses.next();
                                }
                     	retVal += "\t\t\t\t\t <url>" + current +"</url>";
            retVal+="\n\t\t\t\t</addresses>\n";
            retVal += "\t\t\t</agent-identifier>\n";
                }// end else
         return retVal;
         
         // end everything
         // I wish... ;-) 
    }

    /**
     * allow the type to be manipulated
     */
    public void setType(String type) {
        this.type = type; 
    }    




    }

