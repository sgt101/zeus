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



package zeus.gui.graph;

import java.util.EventObject;
import java.awt.Point;

public class GraphIconEvent extends EventObject {

  private boolean  right_mouse_pressed  = false;
  private boolean  right_mouse_dragged  = false;
  private boolean  right_mouse_released  = false;
  private Point graphPoint = null;

  public GraphIconEvent(GraphIcon source) {
     super(source);
  }
  public void setRightMousePressed(){
    right_mouse_pressed = true;
    right_mouse_released = false;
    right_mouse_dragged = false;
  }
   public void setRightMouseDragged(){
    right_mouse_dragged = true;
    right_mouse_released = false;
    right_mouse_pressed = false;
  }
   public void setRightMouseReleased(){
    right_mouse_released = true;
    right_mouse_pressed = false;
    right_mouse_dragged = false;
  }

  public boolean isRightMousePressed(){
    return right_mouse_pressed;
  }
  public boolean isRightMouseReleased(){
    return right_mouse_released;
  }
  public boolean isRightMouseDragged(){
    return right_mouse_dragged;
  }
  public void setPoint(Point graphPoint ){
    this.graphPoint = graphPoint;
  }
  public Point getPoint(){
    return graphPoint;
  }
}
