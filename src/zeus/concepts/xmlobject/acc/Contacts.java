
package zeus.concepts.xmlobject.acc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.Dispatcher;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.InvalidContentObjectException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableObject;
import javax.xml.bind.MarshallableRootElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PredicatedLists;
import javax.xml.bind.PredicatedLists.Predicate;
import javax.xml.bind.RootElement;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidatableObject;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;
import zeus.concepts.xmlobject.acc.Contact;


public class Contacts
    extends MarshallableRootElement
    implements java.io.Serializable, RootElement
{

    private List _List = PredicatedLists.createInvalidating(this, new ListPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_List = new ListPredicate();

    public List getList() {
        return _List;
    }

    public void deleteList() {
        _List = null;
        invalidate();
    }

    public void emptyList() {
        _List = PredicatedLists.createInvalidating(this, pred_List, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        for (Iterator i = _List.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("contacts");
        if (_List.size()> 0) {
            for (Iterator i = _List.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("contacts");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("contacts");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        {
            List l = PredicatedLists.create(this, pred_List, new ArrayList());
            while (xs.atStart("contact")) {
                l.add(((Contact) u.unmarshal()));
            }
            _List = PredicatedLists.createInvalidating(this, pred_List, l);
        }
        xs.takeEnd("contacts");
    }

    public static Contacts unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Contacts unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Contacts unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Contacts) d.unmarshal(xs, (Contacts.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Contacts)) {
            return false;
        }
        Contacts tob = ((Contacts) ob);
        if (_List!= null) {
            if (tob._List == null) {
                return false;
            }
            if (!_List.equals(tob._List)) {
                return false;
            }
        } else {
            if (tob._List!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_List!= null)?_List.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<contacts");
        if (_List!= null) {
            sb.append(" contact=");
            sb.append(_List.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return Contact.newDispatcher();
    }


    private static class ListPredicate
        implements java.io.Serializable, PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof Contact)) {
                throw new InvalidContentObjectException(ob, (Contact.class));
            }
        }

    }

}
