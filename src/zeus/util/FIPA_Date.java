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
    
    
    package zeus.util; 
    
    import java.util.*;
    import java.text.*;
    
    public class FIPA_Date {
        
        static Calendar cal = new GregorianCalendar (); 
        static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  


    public static  Date getTime() {
	    return cal.getTime();
    }
     
     
        public static String getDate () { 
               StringBuffer formatted = dateFormater.format(getTime(),new StringBuffer(),new FieldPosition(0)); 
               String val = formatted.toString(); 
               String temp = new String(val.substring (0,8) +"Z"+val.substring(8,val.length())); 
               return (temp);}
                  
   
    }