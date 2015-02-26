import java.io.*;
import java.util.*;

public class NMdb {
  RandomAccessFile fp;
  String fileName;


  public NMdb(String fname) {
    fileName = fname;
  }

  public void open(){
    try {
      fp = new RandomAccessFile(fileName,"r");
    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  public String[] getElements(String key){
    String[] elements;
    String input = null;
    try {
     fp.seek(0);
     while( (input=fp.readLine()) != null) {
      elements = getTokens(input);
      if(elements[0].equals(key)) {
        String[] es = new String[4];
        for(int i=1, j=0; i < elements.length; i++,j++)
         es[j] = elements[i];
        return es;
      }
     }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }

  private String[] getTokens(String str){
    StringTokenizer st = new StringTokenizer(str);
    int len = st.countTokens();
    String[] path = new String[len];

    for(int i=0; i<len; i++)
     path[i] = (String)st.nextToken();

     return path;
  }


  public void close() {
    try{
     fp.close();
    }
    catch(Exception e){
     e.printStackTrace();
    }

  }




}