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



package zeus.generator.code;

import java.io.*;
import javax.swing.JTextArea;
import zeus.util.SystemProps;
import zeus.generator.GeneratorModel;

public class Writer 
{
  protected static final String standard_disclaimer =
      "/*\n" +
      "\tThis software was produced as a part of research\n" +
      "\tactivities. It is not intended to be used as commercial\n" +
      "\tor industrial software by any organisation. Except as\n" +
      "\texplicitly stated, no guarantees are given as to its\n" +
      "\treliability or trustworthiness if used for purposes other\n" +
      "\tthan those for which it was originally intended.\n \n" +
      "\t(c) British Telecommunications plc 1999.\n" +
      "*/\n";

  public static final int UNIX   = 0;
  public static final int WINDOWS = 1;
  public static final int ZEUS   = 2;

  private static final char[] SEPARATORS = {
      '/', '\\', (SystemProps.getProperty("file.separator")).charAt(0)
  };

  protected GenerationPlan genplan;
  protected GeneratorModel genmodel;
  protected String        directory;
  protected JTextArea    textArea;

  public Writer(GenerationPlan genplan, GeneratorModel genmodel,
                String directory, JTextArea textArea)
  {
      this.genplan = genplan;
      this.genmodel = genmodel;
      this.directory = directory;
      this.textArea = textArea;
  }

  
  public void setDirectory (String directory ){
      this.directory = directory;
  }
  
  
  public static String updateFilename(String input, int to) {
      if ( input == null ) return null;

      String os_name  = System.getProperty("os.name");
      if ( os_name.equals("SunOS") || os_name.equals("Solaris") )
        return input.replace(SEPARATORS[UNIX],SEPARATORS[to]);
      else
        return input.replace(SEPARATORS[WINDOWS],SEPARATORS[to]);
  }

  protected PrintWriter createFile(String name) throws IOException
  {
      File f1, f2;
      PrintWriter out;

      f1 = new File(directory + name);
      if ( f1.exists() )
      {
        f2 = new File(directory + name + "%");
        f1.renameTo(f2);
      }
      f1 = new File(directory + name);

      return new PrintWriter(new FileWriter(f1));
  }
}