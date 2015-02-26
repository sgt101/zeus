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
/**
 * Change Log
 * ----------
 * Simon 28/0/00 - added a task external field, so that the agent writer can
 * check to see if an external is specified for this task, and then insert
 * a line of code to call it if required
 * Also added three methods to check, get and set the value of external
 */


package zeus.concepts;

import java.util.*;
import zeus.util.*;
import zeus.concepts.fn.*;

/**
 * PrimativeTask is the representation that the Agent Generator uses
 * to store task specifications and write them into Java using the {@see TaskWriter}
 */
public class PrimitiveTask extends Task {
    
    protected Vector consumed = new Vector();
    protected Vector produced = new Vector();
    protected Vector ordering = new Vector();
    
    public transient Vector _produced = null;
    
    protected int active_effect = -1;
    
    public PrimitiveTask() {
        type = PRIMITIVE;
    }
    
    public PrimitiveTask(String name, ValueFunction time, ValueFunction cost,
    Fact[] produced, Fact[] consumed,
    LogicalFn[] constraints, Ordering[] ordering) {
        this();
        //  System.out.println("init 1 : "  +name);
        setName(name);
        setTimeFn(time);
        setCostFn(cost);
        setPostconditions(produced); // consumed?
        setPreconditions(consumed);
        setConstraints(constraints);
        setOrdering(ordering);
    }
    
    
    public PrimitiveTask(String name, String time, String cost,
    Fact[] produced, Fact[] consumed,
    LogicalFn[] constraints, Ordering[] ordering) {
        this();
        //      System.out.println("init 2 : "  +name);
        
        setName(name);
        setTimeFn(time);
        setCostFn(cost);
        setPostconditions(produced);
        setPreconditions(consumed);
        setConstraints(constraints);
        setOrdering(ordering);
    }
    
    
    
    public PrimitiveTask(String name, ValueFunction time, ValueFunction cost,
    Vector produced, Vector consumed,
    Vector constraints, Vector ordering) {
        this();
        //      System.out.println("init 3 : "  +name);
        
        setName(name);
        setTimeFn(time);
        setCostFn(cost);
        setPostconditions(produced);
        setPreconditions(consumed);
        setConstraints(constraints);
        setOrdering(ordering);
    }
    
    
    
    public PrimitiveTask(PrimitiveTask task) {
        this();
        //      System.out.println("init 4 : "  +name);
        
        name = task.getName();
        cost = task.getCostFn();
        time = task.getTimeFn();
        setPostconditions( task.getPostconditions() );
        setPreconditions( task.getPreconditions() );
        setConstraints( task.getConstraints() );
        setOrdering( task.getOrdering() );
        setActiveEffect( task.getActiveEffectPos() );
    }
    
    //added for 1.04 by Simon
    protected String external = null;
    
    public boolean hasExternal() {
        return (external != null);
    }
    
    
    public String getExternal()  {
        return (external);
    }
    
    
    public void setExternal(String external) {
        this.external = external;
    }
    
    
    // end add 1.04
    
    
    
    public void setPostconditions( Vector List ) {
        produced.removeAllElements();
        for(int i = 0; i < List.size(); i++ )
            produced.addElement(new Fact((Fact)List.elementAt(i)));
    }
    
    
    
    public void setPostconditions( Fact[] List ) {
        produced.removeAllElements();
        for(int i = 0; i < List.length; i++ )
            produced.addElement(new Fact(List[i]));
    }
    
    public void setPreconditions( Vector List ) {
        consumed.removeAllElements();
        for(int i = 0; i < List.size(); i++ )
            consumed.addElement(new Fact((Fact)List.elementAt(i)));
    }
    
    public void setPreconditions( Fact[] List ) {
        consumed.removeAllElements();
        for(int i = 0; i < List.length; i++ )
            consumed.addElement(new Fact(List[i]));
    }
    
    public Fact[] getPostconditions() {
        Fact[] out = new Fact[produced.size()];
        for(int i = 0; i < produced.size(); i++)
            out[i] = new Fact((Fact)produced.elementAt(i));
        return out;
    }
    
    public Fact[] getOriginalPostconditions() {
        if ( _produced == null ) return getPostconditions();
        
        Fact[] out = new Fact[_produced.size()];
        for(int i = 0; i < _produced.size(); i++)
            out[i] = new Fact((Fact)_produced.elementAt(i));
        return out;
    }
    
    public void preprocess() {
        Fact f;
        boolean status = false;
        for(int i = 0; !status && i < consumed.size(); i++ ) {
            f = (Fact)consumed.elementAt(i);
            status = f.isReplaced();
        }
        if ( !status ) return;
        
        _produced = Misc.copyVector(produced);
        
        for(int i = 0; i < consumed.size(); i++ ) {
            f = (Fact)consumed.elementAt(i);
            if ( f.isReplaced() )
                produced.addElement(f);
        }
    }
    
    public int countPreconditions() {
        return consumed.size();
    }
    
    
    public int countPostconditions() {
        return produced.size();
    }
    
    public int[] numPreconditions() {
        int[] array = new int[consumed.size()];
        
        for(int i = 0; i < consumed.size(); i++)
            array[i] = ((Fact)consumed.elementAt(i)).getNumber();
        return array;
    }
    
    
    public int[] numPostconditions() {
        int[] array = new int[produced.size()];
        
        for(int i = 0; i < produced.size(); i++)
            array[i] = ((Fact)produced.elementAt(i)).getNumber();
        return array;
    }
    
    
    public Fact[] getPreconditions() {
        Fact[] out = new Fact[consumed.size()];
        
        for(int i = 0; i < consumed.size(); i++)
            out[i] = new Fact((Fact)consumed.elementAt(i));
        return out;
    }
    
    
    public Fact getPrecondition(String fid) {
        Fact f;
        for(int i = 0; i < consumed.size(); i++) {
            f = (Fact)consumed.elementAt(i);
            if ( fid.equals(f.getId()) )
                return f;
        }
        return null;
    }
    
    
    public Fact getPostcondition(String fid) {
        Fact f;
        for(int i = 0; i < produced.size(); i++) {
            f = (Fact)produced.elementAt(i);
            if ( fid.equals(f.getId()) )
                return f;
        }
        return null;
    }
    
    
    public Fact getPrecondition(int pos) {
        return (Fact)consumed.elementAt(pos);
    }
    public Fact getPostcondition(int pos) {
        return (Fact)produced.elementAt(pos);
    }
    
    public int getConsumedPos(Fact fact) {
        String fid = fact.getId();
        Fact f;
        for(int i = 0; i < consumed.size(); i++) {
            f = (Fact)consumed.elementAt(i);
            if ( fid.equals(f.getId()) )
                return i;
        }
        Assert.notNull(null);
        return -1;
    }
    public int getProducedPos(Fact fact) {
        String fid = fact.getId();
        Fact f;
        for(int i = 0; i < produced.size(); i++) {
            f = (Fact)produced.elementAt(i);
            if ( fid.equals(f.getId()) )
                return i;
        }
        Assert.notNull(null);
        return -1;
    }
    
    public void relaxNumberFields() {
        Fact f1;
        ValueFunction fn;
        for(int i = 0; i < consumed.size(); i++ ) {
            f1 = (Fact)consumed.elementAt(i);
            if ( f1.isa(OntologyDb.ENTITY) ) {
                fn = f1.getFn(OntologyDb.NUMBER);
                if ( fn.getID() == ValueFunction.INT )
                    f1.setValue(OntologyDb.NUMBER,f1.newVar());
            }
        }
        for(int i = 0; i < produced.size(); i++ ) {
            f1 = (Fact)produced.elementAt(i);
            if ( f1.isa(OntologyDb.ENTITY) ) {
                fn = f1.getFn(OntologyDb.NUMBER);
                if ( fn.getID() == ValueFunction.INT )
                    f1.setValue(OntologyDb.NUMBER,f1.newVar());
            }
        }
    }
    
    public void setOrdering( Vector List ) {
        ordering.removeAllElements();
        for( int i = 0; i < List.size(); i++ )
            ordering.addElement(new Ordering((Ordering)List.elementAt(i)));
    }
    
    public void setOrdering( Ordering[] List ) {
        ordering.removeAllElements();
        for( int i = 0; i < List.length; i++ )
            ordering.addElement(new Ordering(List[i]));
    }
    
    public Ordering[] getOrdering() {
        Ordering[] out = new Ordering[ordering.size()];
        
        for(int i = 0; i < ordering.size(); i++)
            out[i] = new Ordering((Ordering)ordering.elementAt(i));
        return out;
    }
    
    public boolean resolve(Bindings bindings) {
        boolean status = true;
        ResolutionContext context = getContext();
        
        time = time.resolve(context,bindings);
        if ( time == null ) return false;
        
        cost = cost.resolve(context,bindings);
        if ( cost == null ) return false;
        
        for(int i = 0; status && i < consumed.size(); i++ )
            status &= ((Fact)consumed.elementAt(i)).resolve(context,bindings);
        
        if ( !status ) return status;
        
        for(int i = 0; status && i < produced.size(); i++ )
            status &= ((Fact)produced.elementAt(i)).resolve(context,bindings);
        
        return status;
    }
    
    public ResolutionContext getContext() {
        if ( resolution_context != null ) return resolution_context;
        
        resolution_context = new ResolutionContext();
        resolution_context.add(produced);
        resolution_context.add(consumed);
        return resolution_context;
    }
    
    public boolean applyConstraints(Bindings bindings) {
        Bindings local = new Bindings(bindings);
        if ( !super.applyConstraints(local) ) return false;
        return resolve(local) && bindings.add(local);
    }
    
    public Fact[][] orderPreconditions() {
        if ( ordering.isEmpty() ) {
            Fact[][] result = new Fact[1][consumed.size()];
            for(int i = 0; i < consumed.size(); i++ )
                result[0][i] = (Fact)consumed.elementAt(i);
            return result;
        }
        Vector list = new Vector();
        Vector curr = new Vector();
        Vector next = new Vector();
        Vector cs_set = new Vector();
        Ordering cs;
        Fact f1;
        String fid;
        
        
        for(int i = 0; i < consumed.size(); i++ )
            curr.addElement(consumed.elementAt(i));
        
        for(int i = 0; i < ordering.size(); i++ )
            cs_set.addElement(ordering.elementAt(i));
        
        while( !cs_set.isEmpty() ) {
            for(int i = 0; i < curr.size(); i++ ) {
                f1 = (Fact)curr.elementAt(i);
                fid = f1.getId();
                for( int j = 0; j < cs_set.size(); j++ ) {
                    cs = (Ordering)cs_set.elementAt(j);
                    if ( fid.equals(cs.getRHS()) ) {
                        next.addElement(f1);
                        curr.removeElementAt(i--);
                        break;
                    }
                }
            }
            for(int i = 0; i < curr.size(); i++ ) {
                f1 = (Fact)curr.elementAt(i);
                fid = f1.getId();
                for( int j = 0; j < cs_set.size(); j++ ) {
                    cs = (Ordering)cs_set.elementAt(j);
                    if ( fid.equals(cs.getLHS()) ) {
                        cs_set.removeElementAt(j--);
                    }
                }
            }
            list.addElement(curr);
            curr = next;
            next = new Vector();
        }
        if ( !curr.isEmpty() ) list.addElement(curr);
        
        Fact[][] result = new Fact[list.size()][];
        for(int i = 0; i < list.size(); i++ ) {
            curr = (Vector) list.elementAt(i);
            result[i] = new Fact[curr.size()];
            for( int j = 0; j < curr.size(); j++ )
                result[i][j] = (Fact)curr.elementAt(j);
        }
        return result;
    }
    
    
    public String toString() {
        String s = "(:" + TaskTypes[type] + " " + name + " ";
        
        s += ":time (" + time + ") ";
        s += ":cost (" + cost + ") ";
        
        if ( !consumed.isEmpty() ) {
            s += ":consumed_facts (";
            for(int i = 0; i < consumed.size(); i++ )
                s += consumed.elementAt(i);
            s += ") ";
        }
        if ( !produced.isEmpty() ) {
            s += ":produced_facts (";
            for(int i = 0; i < produced.size(); i++ )
                s += produced.elementAt(i);
            s += ") ";
        }
        if ( !constraints.isEmpty() ) {
            s += ":constraints (";
            for(int i = 0; i < constraints.size(); i++ )
                s += "(" + constraints.elementAt(i) + ")";
            s += ") ";
        }
        if ( !ordering.isEmpty() ) {
            s += ":ordering (";
            for(int i = 0; i < ordering.size(); i++ )
                s += ordering.elementAt(i);
            s += ") ";
        }
        return s.trim() + ")";
    }
    
    public String pprint(int sp) {
        String suffix, prefix;
        String tabs = Misc.spaces(sp);
        String eol  = "\n" + tabs + " ";
        
        String s = "(:" + TaskTypes[type] + " " + name + eol;;
        
        s += ":time (" + time + ")" + eol;
        s += ":cost (" + cost + ")" + eol;
        
        if ( !consumed.isEmpty() ) {
            prefix = ":consumed_facts ";
            suffix = Misc.spaces(1 + sp + prefix.length());
            s += prefix + "(";
            for(int i = 0; i < consumed.size(); i++ )
                s += ((Fact)consumed.elementAt(i)).pprint(1+suffix.length()) +
                "\n" + suffix + " ";
            s = s.trim() + "\n" + suffix + ")" + eol;
        }
        if ( !produced.isEmpty() ) {
            prefix = ":produced_facts ";
            suffix = Misc.spaces(1 + sp + prefix.length());
            s += prefix + "(";
            for(int i = 0; i < produced.size(); i++ )
                s += ((Fact)produced.elementAt(i)).pprint(1+suffix.length()) +
                "\n" + suffix + " ";
            s = s.trim() + "\n" + suffix + ")" + eol;
        }
        if ( !constraints.isEmpty() ) {
            prefix = ":constraints ";
            suffix = Misc.spaces(1 + sp + prefix.length());
            s += prefix + "(" + "\n" + suffix + " ";
            for(int i = 0; i < constraints.size(); i++ )
                s += "(" + constraints.elementAt(i) + ")" +
                "\n" + suffix + " ";
            s = s.trim() + "\n" + suffix + ")" + eol;
        }
        if ( !ordering.isEmpty() ) {
            prefix = ":ordering ";
            suffix = Misc.spaces(1 + sp + prefix.length());
            s += prefix + "(";
            for(int i = 0; i < ordering.size(); i++ )
                s += ((Ordering)ordering.elementAt(i)).pprint(1+suffix.length()) +
                "\n" + suffix + " ";
            s = s.trim() + "\n" + suffix + ")" + eol;
        }
        return tabs + s.trim() + "\n" + tabs + ")";
    }
    
    public boolean isValid() {
        return true;
    }
    
    public void setActiveEffect(int j) {
        Assert.notFalse((j >= -1) && (j < produced.size()));
        active_effect = j;
    }
    public Fact getActiveEffect() {
        return getPostcondition(active_effect);
    }
    public int getActiveEffectPos() {
        return active_effect;
    }
    
    public AbstractTask duplicate(DuplicationTable table) {
        // System.out.println("duplicating");
        Fact[]       Xconsumed = new Fact[consumed.size()];
        Fact[]       Xproduced = new Fact[produced.size()];
        LogicalFn[]  Xconstraints = new LogicalFn[constraints.size()];
        Ordering[]   Xordering = new Ordering[ordering.size()];
        
        ValueFunction Xtime = time.duplicate(table);
        ValueFunction Xcost = cost.duplicate(table);
        
        for(int i = 0; i < consumed.size(); i++ )
            Xconsumed[i] = ((Fact)consumed.elementAt(i)).duplicate(table);
        
        for(int i = 0; i < produced.size(); i++ )
            Xproduced[i] = ((Fact)produced.elementAt(i)).duplicate(table);
        
        for(int i = 0; i < constraints.size(); i++ )
            Xconstraints[i] = (LogicalFn)((LogicalFn)constraints.elementAt(i)).duplicate(table);
        
        for(int i = 0; i < ordering.size(); i++ )
            Xordering[i] = ((Ordering)ordering.elementAt(i)).duplicate(table);
        
        PrimitiveTask task = new PrimitiveTask(name,Xtime,Xcost,Xproduced,
        Xconsumed,Xconstraints,Xordering);
        
        task.setActiveEffect(active_effect);
        
        return task;
    }
    
   
    
}
