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

import java.util.*;


public class Selector implements Enumeration {
  Object[][] data;
  int count = 0;
  
  public Selector(Object[] input) {
    Assert.notNull(input);
    int m = 1;
    Vector v;
    for( int i = 0; i < input.length; i++ ) {
      if ( input[i] instanceof Vector )
	m *= ((Vector)input[i]).size();
      else if ( input[i] instanceof Object[] )
	m *= ((Object[])input[i]).length;
      else
	Assert.notNull(null);
    }
    count = 0;
    data = new Object[m][input.length];
    Object[] choice = new Object[input.length];
    select(0,input,choice);
    count = 0;
  }

  public boolean hasMoreElements() {
    return count < data.length;
  }

  public Object nextElement() {
    return data[count++];
  }

  public void reset() {
    count = 0;
  }
  
  protected void select(int position, Object[] input, Object[] choice) {
    if ( position >= input.length ) {
      for( int i = 0; i < input.length; i++ )
	data[count][i] = choice[i];
      count++;
      return;
    }
    
    if ( input[position] instanceof Vector ) {
      Vector v = (Vector)input[position];
      for( int i = 0; i < v.size(); i++ ) 
      {
	choice[position] = v.elementAt(i);
	select(position+1,input,choice);
      }
    }
    else if ( input[position] instanceof Object[] ) {
      Object[] v = (Object[])input[position];
      for( int i = 0; i < v.length; i++ ) {
	choice[position] = v[i];
	select(position+1,input,choice);
      }
    }
  }
  
  public static void main(String[] arg) {
    String[][] data = { {"a", "b", "c"}, 
			{"1", "2", "3", "4"},
			{"u", "v", "x", "y", "z"}
    };
    
    Selector selector = new Selector(data);
    int i = 1;
    while( selector.hasMoreElements() ) {
      Object[] results = (Object[])selector.nextElement();
      System.out.print((i++) + " ");
      for( int j = 0; j < results.length; j++ ) 
	System.out.print((String)results[j] + " ");
      System.out.println();
    }
  }
}
