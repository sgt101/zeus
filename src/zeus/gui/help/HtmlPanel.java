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



/*****************************************************************************
* HtmlPanel.java
*
* A wrapper around the Swing HTML JEditorPane (from Swing Set 1.7)
****************************************************************************/

package zeus.gui.help;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;


public class HtmlPanel extends JPanel implements HyperlinkListener {
  protected JEditorPane html;
  protected URL currentDoc;
  protected HelpWindow parent;


  public HtmlPanel(HelpWindow parent) {
     this.parent = parent;
     setLayout(new BorderLayout());

     html = new JEditorPane();
     html.setEditable(false);
     html.addHyperlinkListener(this);
     JScrollPane scroller = new JScrollPane();
     scroller.setPreferredSize(new Dimension(360, 180));
     scroller.setMinimumSize(new Dimension(360,180));
     JViewport viewport = scroller.getViewport();
     viewport.add(html);
     viewport.setBackingStoreEnabled(true);
     add(scroller, BorderLayout.CENTER);
  }

  // The follow hyperlink method
  public void hyperlinkUpdate(HyperlinkEvent e) {
     if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        URL url = e.getURL();
        parent.addToHistory(url.toString());
        linkActivated(e.getURL());
     }
  }

  // Used to move between pages without altering history
  public void setPage(String doc) {
     try {
        linkActivated(new URL(doc));
     }
     catch (MalformedURLException e) {
        System.out.println("Malformed URL: " + e);
     }
     catch (IOException e) {
        System.out.println("IOException: " + e);
     }
  }


  /**
    * Follows the reference in an link.  The given url is the requested reference.
    * By default this calls <a href="#setPage">setPage</a>, and if an exception is
    * thrown the original previous document is restored and a beep sounded.  If an
    * attempt was made to follow a link, but it represented a malformed url, this
    * method will be called with a null argument.
    *
    * @param u the URL to follow
    */
  protected void linkActivated(URL url) {
     currentDoc = url;
     Cursor cursor = html.getCursor();
     Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
     html.setCursor(waitCursor);
     SwingUtilities.invokeLater(new PageLoader(url,cursor));
  }

  /**
    * temporary class that loads synchronously (although
    * later than the request so that a cursor change can be done).
    */
  class PageLoader implements Runnable {

     URL url;
     Cursor cursor;

     PageLoader(URL url, Cursor cursor) {
        this.url = url;
        this.cursor = cursor;
     }

     public void run() {
        if (url == null) {
	   html.setCursor(cursor); // restore the original cursor
	   // PENDING(prinz) remove this hack when automatic validation is activated.
           Container cont = html.getParent();
	   cont.repaint();
        }
	else {
	   Document doc = html.getDocument();
	   try {
	      html.setPage(url);
	   }
	   catch(IOException ioe) {
	      // html.setDocument(doc);
	      getToolkit().beep();
	   }
	   finally {
	      // schedule the cursor to revert after the paint has happended.
	      url = null;
	      SwingUtilities.invokeLater(this);
	   }
	}
     }
  }
}