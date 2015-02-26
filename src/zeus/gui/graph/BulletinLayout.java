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

import java.awt.*;
import zeus.util.Assert;

/**
 * Lays out components as though they were pinned to
 * a bulletin board.<p>
 *
 * Components are simply reshaped to their location and their
 * preferred size.  BulletinLayout is preferrable to setting
 * a container's layout manager to null and explicitly positioning
 * and sizing components.<p>
 *
 * @version 1.0, Apr 1 1996
 * @author  David Geary
 */
public class BulletinLayout implements LayoutManager {
    public BulletinLayout() {
    }
    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }
    public Dimension preferredLayoutSize(Container target) {
        Insets    insets      = target.insets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.countComponents();
        Component comp;
        Dimension d;
        Rectangle preferredBounds = new Rectangle(0,0);
        Rectangle compPreferredBounds;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.preferredSize();
                compPreferredBounds =
                    new Rectangle(comp.location());
                compPreferredBounds.width  = d.width;
                compPreferredBounds.height = d.height;

                preferredBounds =
                    preferredBounds.union(compPreferredBounds);
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }
    public Dimension minimumLayoutSize(Container target) {
        Insets    insets      = target.insets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.countComponents();
        Component comp;
        Dimension d;
        Rectangle minimumBounds = new Rectangle(0,0);
        Rectangle compMinimumBounds;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.minimumSize();
                compMinimumBounds =
                    new Rectangle(comp.location());
                compMinimumBounds.width  = d.width;
                compMinimumBounds.height = d.height;

                minimumBounds =
                    minimumBounds.union(compMinimumBounds);
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }
    public void layoutContainer(Container target) {
        Insets    insets      = target.insets();
        int       ncomponents = target.countComponents();
        Component comp;
        Dimension ps;
        Point loc;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                ps  = comp.preferredSize();
                loc = comp.location();

                comp.reshape(insets.left + loc.x,
                             insets.top + loc.y,
                             ps.width, ps.height);
            }
        }
    }
}
