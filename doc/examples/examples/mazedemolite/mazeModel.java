/**
 * mazeModel.java
 * Implements the responsibilities of the Environment agent's Model role
 * Notice that no Zeus code is imported, as this is not an agent-level component
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;



public class mazeModel
{
  public int mazeHeight = 10;
  public int mazeWidth = 10;

  public boolean [][] thisMaze =
  {
    {true,  true,  true,  true,  false, false, true,  true,  true,  true },
    {false, false, false, true,  false, true,  false, false, true,  false},
    {false, true,  false, false, false, false, false, false, true,  false},
    {false, true,  false, true,  true,  true,  true,  false, false, false},
    {false, true,  false, false, false, false, true,  true,  true,  false},
    {false, true,  false, true,  false, false, false, true,  false, false},
    {false, true,  true,  true,  false, true,  false, false, false, false},
    {false, true,  false, false, false, true,  false, true,  false, true},
    {false, true,  true,  true,  true,  true,  true,  true,  false, true},
    {false, false, false, false, false, false, false, false, false, true}
  };


  private boolean agentUpdate = true;
  private boolean mazeUpdate = true;
  private Dimension target = null;
  private Dimension agentToRemove = new Dimension();

  public void initialiseMaze(int mazeWidth, int mazeHeight)
  {
    //this.mazeWidth = mazeWidth;
    //this.mazeHeight = mazeHeight;
    //thisMaze = new boolean [mazeWidth] [mazeHeight];
    //for (int countOuter = 0; countOuter < mazeWidth; countOuter ++) {
    //     for (int countInner = 0; countInner < mazeHeight; countInner++) {
    //        thisMaze [countOuter] [countInner] = false; }}
    target  = new Dimension (mazeWidth/2, mazeHeight/2);
  }

  public mazeModel (int mazeWidth, int mazeHeight) {
    initialiseMaze(mazeWidth, mazeHeight);
  }

 public Hashtable agents = new Hashtable();

 public void moveNorth (String id) {
    Dimension agentPos = (Dimension) agents.get(id);
    if (agentPos == null) return; // if the agent doesn't exit No Effect!
    agentToRemove = agentPos; // so we can delete the old graphic neatly
    agentUpdate = true;
    if (agentPos.height > mazeHeight -1) return; // No Effect if Goal reached.
    agentPos.height--;
 }

 public void moveSouth (String id) {
    Dimension agentPos = (Dimension) agents.get(id);
    if (agentPos == null) return; // if the agent doesn't exit No Effect!
    agentToRemove = agentPos; // so we can delete the old graphic neatly
    agentUpdate = true;
    if (agentPos.height < 0) return; // No Effect at bottom of maze
    agentPos.height++;
 }

 public void moveEast (String id) {
    Dimension agentPos = (Dimension) agents.get(id);
    if (agentPos == null) return; // if the agent doesn't exit No Effect!
    agentToRemove = agentPos; // so we can delete the old graphic neatly
    agentUpdate = true;
    if (agentPos.width > mazeWidth -1) return; // No Effect if at east already.
    agentPos.width++;
 }

 public void moveWest (String id) {
    Dimension agentPos = (Dimension) agents.get(id);
    if (agentPos == null) return; // if the agent doesn't exit No Effect!
    agentToRemove = agentPos; // so we can delete the old graphic neatly
    agentUpdate = true;
    if (agentPos.width < 1) return; // No Effect if at max west already
    agentPos.width--;
 }

 public void registerAgent (String id, int col, int row) {
     Dimension agentPos = new Dimension();
     agentPos.width  = col;
     agentPos.height = row;
     agents.put(id, agentPos);
 }

 public String southVal(String id){
    try {
    id = lookupHash(id);
    Dimension position =(Dimension) agents.get(id);
    if (position == null) return ("null");
    int row = position.height;
    int col = position.width;
    if (row > mazeHeight - 1) return ("false");
        else if (thisMaze [row+1][col])
            return ("true");
            else return ("false");
    }catch (Exception e) { return ("true"); }
 }

  public String northVal (String id){
    try {
    id = lookupHash(id);
    Dimension position =(Dimension) agents.get(id);
    if (position == null) return ("null");
    int row = position.height;
    int col = position.width;
    if (row <  1) return ("true");
        else if (thisMaze [row-1][col])
            return ("true");
            else return ("false");
             }catch (Exception e) { return ("true"); }
    }

  public String eastVal (String id){
    try {
    id = lookupHash(id);
    Dimension position =(Dimension) agents.get(id);
    if (position == null) return ("null");
    int row = position.height;
    int col = position.width;
    if (col  > mazeWidth - 1 ) return ("true");
        else if (thisMaze [row][col+1])
            return ("true");
            else return ("false");
             }catch (Exception e) { return ("true"); }
    }

  public String westVal (String id){
    try {
    id = lookupHash(id);
    Dimension position =(Dimension) agents.get(id);
    if (position == null) return ("null");
    int row = position.height;
    int col = position.width;
    if (col < 1) return ("true");
        else if (thisMaze [row][col- 1])
            return ("true");
            else return ("false");
             }catch (Exception e) { return ("true"); }
    }

    public boolean boolVal(String value) {
        if (value.equals ("true")) return (true);
        if (value.equals("false")) return (false);
        return (false);
    }

  public String lookupHash (String id) {

    Enumeration allids = agents.keys();
    String currentStr = null;
    while (allids.hasMoreElements()) {
        currentStr = (String) allids.nextElement();
        if (currentStr.equals(id)) return (currentStr);}
    System.out.println("Error in MazeMap - agentId unknown: " + id);
    return ("null"); }

    /**
        if the agent (id) is allowed to move in the way indicated
        by the four booleans (north, east, south, west), return true
        else return false
        */

  public boolean isLegalMove (String id, boolean north, boolean east, boolean south, boolean west)
  {
    boolean northFlag = true;
    boolean eastFlag = true;
    boolean westFlag = true;
    boolean southFlag = true;
    id = lookupHash(id);
    if (north) northFlag = !boolVal(northVal(id));
    if (!northFlag) return (false);
    if (east) eastFlag =  !boolVal(eastVal(id));
    if (!eastFlag) return (false);
    if (west) westFlag = !boolVal(westVal (id));
    if (!westFlag) return (false);
    if (south) southFlag = !boolVal(southVal(id));
    if (!southFlag) return (false);

    return (true);
  }


  public boolean exited (String id) {
    id = lookupHash(id);
    Dimension pos = (Dimension) agents.get(id);
    if (pos.height == target.height && pos.width == target.width)
        return true;
    else
        return false; }

  public boolean isTarget (int col, int row) {
    if (target.width == row && target.height == col ) return true;
    else
      return false;
  }

  public void setTarget (int col, int row) {
    target.width = col;
    target.height = row;
  }


  public void changeStatus (int col, int row) {
    if (thisMaze [row][col]) thisMaze[row][col]= false;
    else
        thisMaze [row][col] = true;
  }


  public boolean moveAgent (String id, boolean north, boolean east, boolean south, boolean west)
  {
    System.out.println("In moveAgent");
    id = lookupHash(id);
    Dimension position = (Dimension) agents.get(id);
    agentToRemove = position;
    System.out.println("Agent " + id +" Vertical = " + String.valueOf(position.height) +
                       " Horizontal = " + String.valueOf (position.width) );
    if (north) {
      moveNorth(id);
      return (true);
    }
    if (south) {
      moveSouth (id);
      return (true);
    }
    if (east) {
      moveEast (id);
      return (true);
    }
    if (west) {
      moveWest(id);
      return (true);
    }
    return (false);
  }

}