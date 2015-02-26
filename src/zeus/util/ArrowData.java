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
import java.awt.*;

public class ArrowData {

public static final double ARROW_ANGLE    = Math.PI/12.0;
public static final double ARROW_RATIO    = 0.1;
public static final double ARROW_LMIN     = 7;
public static final double ARROW_LMAX     = 14;
public static final double ARROW_LENGTH   = 10;
public static final double MTOL      	  = 1.0E-12;

public static Point[] getPoints( double x1, double y1, double x2, double y2 ) {

     double L, A, B;
     Point[] xpoints = new Point[4];
     for( int i = 0; i < 4; i++ )
        xpoints[i] = new Point(0,0);

     // L = Math.sqrt( Math.pow(y2-y1,2.0)+Math.pow(x2-x1,2.0) );
     // L *= ARROW_RATIO;

     // if ( L < ARROW_LMAX || L > ARROW_LMIN ) L = ARROW_LENGTH;
     L = ARROW_LENGTH;

     B = GetAngle(x1,y1,x2,y2);
     A = B-ARROW_ANGLE;

     xpoints[0].x = (int) (x2-L*Math.cos(A));
     xpoints[0].y = (int) (y2-L*Math.sin(A));

     xpoints[1].x = (int) x2;
     xpoints[1].y = (int) y2;

     A = B+ARROW_ANGLE;

     xpoints[2].x = (int) (x2-L*Math.cos(A));
     xpoints[2].y = (int) (y2-L*Math.sin(A));

     xpoints[3].x = xpoints[0].x;
     xpoints[3].y = xpoints[0].y;

     return xpoints;
}

public static double GetAngle( double x1, double y1, double x2, double y2 ) {
       double B, x, y;

       x = x2 - x1;
       y = y2 - y1;

       if ( Math.abs(x) < MTOL && Math.abs(y) < MTOL ) return( 0.0 );

       B = Math.atan(y/x);

            if ( x >= 0.0  && y >= 0.0 ) B += 0.0;
       else if ( x <  0.0  && y >= 0.0 ) B += Math.PI;
       else if ( x <  0.0  && y <  0.0 ) B += Math.PI;
       else if ( x >= 0.0  && y <  0.0 ) B += 2.0*Math.PI;

       return( B );
}

}

