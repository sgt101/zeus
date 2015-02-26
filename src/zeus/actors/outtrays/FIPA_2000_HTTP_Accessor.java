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
    package zeus.actors.outtrays;

    import java.io.*;
    import java.net.*;
    import java.util.*;
    
    /** 
        FIPA_2000_HTTP_Accessor is used to carry the information for a http connection 
        from the TransportFactory to the actuall FIPA_2000_HTTP_Transport 
        @author Simon Thompson 
        @since 1.1
        */
    public class FIPA_2000_HTTP_Accessor { 
        
        
        private String host = null; 
        private String port = null; 
        private String separator = "/"; 
        private String name = null; 
        
        
        public FIPA_2000_HTTP_Accessor (String address) { 
            debug (address);
            String interesting = address.substring(7); 
            StringTokenizer tokens = new StringTokenizer(interesting,":"); 
            host = tokens.nextToken(); 
            StringTokenizer nextTokens = new StringTokenizer(tokens.nextToken(),"/"); 
            try {
                port = nextTokens.nextToken();}
            catch (Exception e) {
                port = new String ("80");
            }
            try {
                name = nextTokens.nextToken();
                name = new String ();
            }
            catch(Exception e) {
            }
        }
        
        
        /** 
            use this to set the name of the entity to be contacted
            (usually acc/df/ams) note that if there is a name space then 
            include it here (ie. if the address is http://www.bt.com:80/agents/agentcities/adastral/acc 
            then the parameter entityName should be agents/a
            */
        public void setName (String entityName) { 
            this.name = entityName; 
        }
        
        
        /** 
            guess what this does?
            */
        public String getName () { 
            return this.name; 
        }

        
        
        /** 
            set the host that we are contacting : www.bt.com
            */
        public void setHost (String hostName) { 
            this.host = hostName; 
        }
        
        
        /** 
        set the port numnber : 80 for instance
        */
        public void setPort (String portNumber) { 
            this.port = portNumber; 
        }
        
        
        /** 
            get the port number  
            */
        public String getPort() { 
            return port;
        }
        
        
        /** 
            get the host 
            */
        public String getHost () { 
            return host; 
        }
        
        
        /** 
        set the port numnber : 80 for instance
        */
        public void setPort (int port ) { 
            this.port = String.valueOf(port); 
        }
            
            
        public String getAddress () { 
            return "http://" + host + ":" + port + separator + name; 
        }
        
        
        
        /** 
            open a socket to the receiving agent and send it the message
            */
        public boolean send (String message) { 
            try {
            Socket socket = new Socket(host, Integer.parseInt(port));
                
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true );  
	        InputStreamReader ins = new InputStreamReader (socket.getInputStream()); 
	        BufferedReader in = new BufferedReader(ins); 
	        out.println(message); 
	        out.flush(); 
	        String current = new String(); 
	        while (in.ready()) {
	            current += in.readLine(); 
	            current += "\n"; 
	        } 
	        out.close(); 
	        in.close();  
	        if (current.startsWith("HTTP:/1.1 200 OK")) {
	            return true; 
	        }
            else
                {
	                return false; 
	            }
	        } catch (Exception e) { 
	            e.printStackTrace(); 
	            System.out.println("Failed to open HTTP connection as expected, message *not* sent"); 
	            System.out.println("Message was : " + message); 
	            System.out.println("Not fatal, attempting to continue..."); 
	            return false; 
	        }

        }

        private void debug(String s){
                System.out.println("http_accessor >>" + s);
                }
        
    }