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
* public limited company are Copyright 1996-9. All Rights Reserved.
* 
* THIS NOTICE MUST BE INCLUDED ON ANY COPY OF THIS FILE
*/



package zeus.util;

/* Author:           Lyndon Lee
   Program:          MMX.java 
   Created Date:     130597
   Last Update Date: 130597
   Function:         Agent's Multimedia Extension.
   Modifications:
*/

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class MMX {

    private String   SessionID;
    private String   MMXTarget;
    private String   AgentID;
    private int      mediaType;  
    private String   string;

    public final static int EMAIL = 1;
    public final static int PHONE = 2;
    public final static int VOICE = 3;
    public final static int POPUP = 4;

    public MMX() {}


    public void setSessionID(String s)  { SessionID = s; }
    public void setAgentID(String s)    { AgentID = s; }
    public void setString(String s)     { string = s; }
    public void setMMXTarget(String s)  { MMXTarget = s; }
    public void setMediaType(int i)     { mediaType = i; }

    public void informClient(String session, int i, String target) {
        if ( string == null ) return;
        setSessionID(session); 
        setMediaType(i); 
        setMMXTarget(target);
        informClient();
    }
    public void informClient() {
        if ( string == null ) return;
        switch (mediaType) {
            case EMAIL:
                sendEmail();
                break;
            case PHONE:
                phone();
                break;
            case VOICE:
                speak(true);
                break;
            case POPUP:
                speak(false);
        }
    }

    public void speak(boolean Voice) {

        Process child = null;
        Socket speakSocket = null;
        DataInputStream in = null;
        PrintStream out = null;

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(MMXTarget);
        } catch (UnknownHostException e) {
            System.out.println("IP address of " + MMXTarget + " cannot be found");
        }
        // System.out.println("IP address of " + MMXTarget + " is " + ip);

        while (speakSocket == null) {
            try {
                speakSocket = new Socket (ip,1234);
                in = new DataInputStream(speakSocket.getInputStream());
                out = new PrintStream(speakSocket.getOutputStream());
	    } catch (IOException e1) {

                if (child == null) {
                    System.out.println("creating remote process...\n");
                    try {
                        child = Runtime.getRuntime().exec("rsh " + MMXTarget + " jjj");
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    // System.out.println(" process OK...\n");
                } // end if (child = null)
                else {

                    System.out.println("Waiting to connect to MMX2");
                    try {
                        Thread.sleep(2000);
                    } catch(InterruptedException e ) {}
                    //System.out.println("woke up");
                } // end else
            } // end catch (IOException e1) 
        } // end while (speakSocket == null)

// System.out.println("1234 opened");
        try {
            String ss;

            if ((ss = in.readLine()) != null) {
                //System.out.println("MMX2 says:: " + ss);
                //System.out.println("sending " + s);

                out.println(Voice? "Voice": "PopUp");
                out.flush();

                out.println("AgentID");
                out.flush();
                out.println(AgentID);
                out.flush();

                out.println("Messages");
                out.flush();
                //System.out.println("sending " + string + " " + i);
                out.println(string);
                out.flush();
                out.println("End");
                out.flush();
                out.println("Bye");
                out.flush();
            } // end if ((ss = in.readLine()) != null)

            if ((ss = in.readLine()) != null) {
                //System.out.println("MMX2 says:: " + ss);
            }

            out.close();
            in.close();
            speakSocket.close();
        } catch (IOException e) {
                System.out.println("Unable to speak");
        }
    }

    public void phone() {
        Socket phoneSocket;
        PrintStream out;

        String message = 
                       "\r\nrequest_identity= "                 
                     + null                             
                     + "\r\nservice_instance_identity= "
                     + "0005"
                     + "\r\nstart_date_time= "
                     + null
                     + "\r\nstop_date_time= "
                     + null
                     + "\r\nsender_telephone_number= "
                     + "01473649733"
                     + "\r\nsender_name= "
                     + AgentID                 //200597
                     + "\r\nsender_e-mail_address= "
                     + "abc@xyz"
                     + "\r\nsender_fax_number= "
                     + null
                     + "\r\nsender_pager_number= "
                     + null
                     + "\r\nsender_SMS_number= "
                     + null
                     + "\r\nnumber_of_receivers= "
                     + "1"
                     + "\r\nreceivers_telephone_number(s)= "
                     + MMXTarget               //200597
                     + "\r\nreceivers_name= "
                     + null
                     + "\r\nreceivers_e-mail_address= "
                     + null
                     + "\r\nreceivers_fax_number= "
                     + null
                     + "\r\nreceivers_pager_number= "
                     + null
                     + "\r\nreceivers_SMS_number= "
                     + null
                     + "\r\nreceivers_status= "
                     + null
                     + "\r\nconfirmation_e-mail_address= "
                     + null
                     + "\r\nstart_now= "
                     + "YES"
                     + "\r\ndelete_service_instance= "
                     + null
                     + "\r\ncredit_card_pin= "   
                     + null
                     + "\r\ncredit_card_number= "
                     + null
                     + "\r\nmessage_start=\r\n"
                     + string                    //200597
                     + "\r\nmessage_stop=\r\n";


        try {
            phoneSocket = new Socket ("132.146.209.102", 4444);
            out = new PrintStream(phoneSocket.getOutputStream());

            try {

                out.println(message);
                out.flush();

                out.close();
                phoneSocket.close();
                //System.out.println("Client closing ...");
            } catch (IOException e) {
                System.out.println("Unable to make phone call");
            }

	} catch (IOException e1) {
            System.out.println("Unable to connect to MAP");
        }
    }


    public void sendEmail() {
        Socket mailSession;
// establish connection
        try {
            mailSession = new Socket("mailhost", 25);

            DataInputStream in = 
                new DataInputStream(mailSession.getInputStream());
            DataOutputStream out = 
                new DataOutputStream(mailSession.getOutputStream());

// check dialogue from SMTP

        String resp = getResponse(in);
        if (resp.charAt(0) != '2')
            throw new IOException("Bad SMTP dialogue\n");

        resp = mailCommand("HELO", in, out);
        if (resp.charAt(0) != '2')
            throw new IOException("Bad SMTP dialogue\n");

        resp = mailCommand("MAIL FROM:" + AgentID, in, out);
        if (resp.charAt(0) != '2')
            throw new IOException("Bad SMTP dialogue\n");

        resp = mailCommand("RCPT TO:" + MMXTarget, in, out);
        if (resp.charAt(0) != '2')
            throw new IOException("Bad SMTP dialogue\n");

        resp = mailCommand("DATA", in, out);
        if (resp.charAt(0) != '3')
            throw new IOException("Bad SMTP dialogue\n");

        out.writeBytes(string+"\n");

        resp = mailCommand(".", in, out);
        if (resp.charAt(0) != '2')
            throw new IOException("Bad SMTP dialogue\n");

// close connection
        mailSession.close();
        } 
        catch (IOException e )
        {  System.err.println("Unable to open SMTP connection.\n"); }

    }

    public String getResponse(DataInputStream in) {
        String resp = "";
        for (;;) {
            try {
                String line = in.readLine();

                if (line == null) 
                    throw new IOException("Bad SMTP response\n");
                if (line.length() < 3)
                    throw new IOException("Bad SMTP response\n");
                resp += line + "\n";
                if (line.length() == 3 || line.charAt(3) != '-')
                    return resp;
            }
            catch (IOException e ) {}
        }
    }

    public String mailCommand(String s, DataInputStream in, DataOutputStream out) {
        try {
            out.writeBytes(s + "\n");
            return getResponse(in);
        } 
        catch (IOException e )
        {  System.err.println("Unable to send command to SMTP server.\n"); }
        return null;
    }

    public static void main (String argv[]) {
        MMX mmx = new MMX();
        mmx.setAgentID("WonderAgent");
        String a = "hello\nthis is second\nend";
        mmx.setString(a);
        mmx.informClient("Session Wonder", PHONE, "605666");
        mmx.informClient("Session Wonder", EMAIL, "ndumudt@zion");
    } 

}
