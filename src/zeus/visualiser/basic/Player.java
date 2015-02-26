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



/*******************************************************************
 *                    Video Tools for Zeus                         *
 *   Provides functionality to use record and playback feature     *
 *******************************************************************/

package zeus.visualiser.basic;

import java.util.*;
import javax.swing.*;

import zeus.util.*;
import zeus.gui.*;
import zeus.concepts.*;
import zeus.actors.*;


public class Player extends Thread {
  static final int COUNT = 0;
  static final int LIST = 1;
  static final int NEXT = 2;
  static final int PRIOR = 3;
  static final int FIRST = 4;
  static final int LAST = 5;
  static final int OPEN = 6;
  static final int CLOSE = 7;
  static final int READY = 8;
  static final int STOP = 9;

  private static long CDEFAULT_SPEED = 1000;
  private long DEFAULT_SPEED = CDEFAULT_SPEED;

  protected VideoTool video;

  private int max_count = -1;
  private int cmd = OPEN;
  private boolean playback = true;
  private boolean single_step = false;
  private long timeout;

  void setSpeed(long speed) { DEFAULT_SPEED = speed; }
  long getSpeed()           { return DEFAULT_SPEED; }

  static long getDefaultSpeed()       { return CDEFAULT_SPEED; }
  static void setDefaultSpeed(long s) { CDEFAULT_SPEED = s; }

  Player(VideoTool video) {
    Assert.notNull(video);
    this.video  = video;

    this.setPriority(Thread.NORM_PRIORITY-2);
    this.start();
  }

  private boolean ready() {
    switch( cmd ) {
    case READY:
    case NEXT:
    case PRIOR:
      return true;
    default:
      return false;
    }
  }

  synchronized void first() {
    if ( !ready() ) return;
    cmd = FIRST;
    single_step = false;
    timeout = DEFAULT_SPEED;
    notify();
  }

  synchronized void last() {
    if ( !ready() ) return;
    cmd = LAST;
    single_step = false;
    timeout = DEFAULT_SPEED;
    notify();
  }

  synchronized void forward_step() {
    if ( !ready() ) return;
    cmd = NEXT;
    single_step = true;
    timeout = DEFAULT_SPEED;
    notify();
  }

  synchronized void rewind_step() {
    if ( !ready() ) return;
    cmd = PRIOR;
    single_step = true;
    timeout = DEFAULT_SPEED;
    notify();
  }

  synchronized void forward() {
    if ( !ready() ) return;
    cmd = NEXT;
    single_step = false;
    timeout = DEFAULT_SPEED;
    notify();
   }

  synchronized void rewind() {
    if ( !ready() ) return;
    cmd = PRIOR;
    single_step = false;
    timeout = DEFAULT_SPEED;
    notify();
  }

  synchronized void fforward() {
    if ( !ready() ) return;
    cmd = NEXT;
    single_step = false;
    timeout = DEFAULT_SPEED/2;
    notify();
  }

  synchronized void frewind() {
    if ( !ready() ) return;
    cmd = PRIOR;
    single_step = false;
    timeout = DEFAULT_SPEED/2;
    notify();
  }

  synchronized void pause() {
    if ( !ready() ) return;
    cmd = READY;
    single_step = false;
    timeout = DEFAULT_SPEED;
  }

  synchronized void terminate() {
    playback = false;
    cmd = STOP;
    notify();
  }

  private boolean running() {
    switch( cmd ) {
       case NEXT:
       case PRIOR:
       case FIRST:
       case LAST:
       case STOP:
            return true;
       default:
            return false;
    }
  }

  void setCommand(int cmd) {
     this.cmd = cmd;
  }

  void setCount(int max) {
     this.max_count = max;
  }

  public void run() {
     int count = 0;
     while( playback ) {
        try {
           synchronized(this) {
              while( !running() )
                 wait();
           }
           if ( cmd == STOP ) return;

           if ( (cmd == NEXT  && count < max_count) ||
                (cmd == PRIOR && count > 0) ) {

              if ( cmd == NEXT )
  	         count++;
              else if ( cmd == PRIOR )
                 count--;

              if ( cmd == NEXT )
                 video.doPlayerCommand("db_next");
              else
	         video.doPlayerCommand("db_prior");

              if ( single_step )
	         cmd = READY;
              else
                 sleep(timeout);
           }
           else if ( cmd == FIRST || cmd == LAST ) {
              count = (cmd == FIRST) ? 0 : max_count;

              if ( cmd == FIRST )
	         video.doPlayerCommand("db_first");
              else
                 video.doPlayerCommand("db_last");

              single_step = false;
              cmd = READY;
           }
           else {
              synchronized(this) {
                 wait();
              }
           }
        }
        catch(InterruptedException e) {
        }
     }
  }
}
