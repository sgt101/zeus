package zeus.actors.outtrays;

import java.net.*;
import java.io.*;
import java.util.Date;

public class ReplyListener implements Runnable {
    
    BufferedInputStream is;
    PrintWriter os;
    File file;
    
    public ReplyListener(BufferedInputStream is, PrintWriter os, File file) {
        this.os = os;
        this.is = is;
        this.file = file;
    }
    
    
    public void run() {
        BufferedReader r=new BufferedReader(new InputStreamReader(is));
        
        // now reading response
        
        String ln;
        Date today = new Date();
        int month = today.getMonth() + 1;
        int year = today.getYear();
        
        try {
            String all = new String("Response received " + + today.getDate() +"/" + String.valueOf(month) +"/" + String.valueOf(year) + " at " + today.getHours() +":" +today.getMinutes() +":" + today.getSeconds()+"\n");
            while( (ln=r.readLine()) != null ) {
                debug(ln);
                all+=ln;}
            FileWriter log = new FileWriter(file,true);
            log.write(all);
            log.write("\n\n");
            log.close();
            os.close();
            is.close();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
    
    
    private void debug(String str) {
        //  System.out.println("reply:" + str);
        
    }
}