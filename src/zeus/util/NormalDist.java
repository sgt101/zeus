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

public class NormalDist {

   public static final double MTOL      	  = 1.0E-12;

   public static double normdist(double xx, double mu, double sigma) {
      if ( Math.abs(sigma-0.0) < MTOL ) sigma = 0.001;

      double x = (xx < mu) ? 2.0*mu - xx : xx;
      double Zx = Math.exp(-0.5*Math.pow((x-mu)/sigma,2.0))/
                  (sigma*Math.sqrt(2.0*Math.PI));

      double p = 0.33267;
      double a1 = 0.4361836;
      double a2 = -0.1201676;
      double a3 = 0.9372980;

      double t = 1.0/(1.0+p*x);
      double Px = 1.0 - Zx*(a1*t + a2*Math.pow(t,2.0) + a3*Math.pow(t,3.0));
      
      return (xx < mu) ? 1 - Px : Px;
   }
   public static double normdist(double x) {
      return normdist(x,0.0,1.0);
   }

   public static double norminv(double q, double mu, double sigma) {
      return mu + sigma*norminv(q);
   }
   public static double norminv(double q) {
      if ( q == 0.50 ) return 0.0;

      q = 1.0-q;

      double p = (q > 0.0 && q < 0.5) ? q : (1.0 - q);
      double t = Math.sqrt(Math.log(1.0/Math.pow(p,2.0)));

      double c0 = 2.515517;
      double c1 = 0.802853;
      double c2 = 0.010328;

      double d1 = 1.432788;
      double d2 = 0.189269;
      double d3 = 0.001308;

      double X = t - (c0 + c1*t + c2*Math.pow(t,2.0))/
                 (1.0 + d1*t + d2*Math.pow(t,2.0) + d3*Math.pow(t,3.0));
      
      if ( q > 0.5 ) X *= -1.0;
      return X;
   }
   public static void main(String[] args) {
      for ( double i = -3.5; i < 4; i += 0.5 ) {
         double p = normdist(i);
         double x = norminv(p);
         System.out.println(i+"\t"+p+"\t"+x);
      }
   }
}

