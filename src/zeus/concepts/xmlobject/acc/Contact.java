
package zeus.concepts.xmlobject.acc;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.Dispatcher;
import javax.xml.bind.DuplicateAttributeException;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableRootElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MissingAttributeException;
import javax.xml.bind.RootElement;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class Contact
    extends MarshallableRootElement
    implements java.io.Serializable, RootElement
{

    private String _ZEUSName;
    private String _FIPAAddress;

    public String getZEUSName() {
        return _ZEUSName;
    }

    public void setZEUSName(String _ZEUSName) {
        this._ZEUSName = _ZEUSName;
        if (_ZEUSName == null) {
            invalidate();
        }
    }

    public String getFIPAAddress() {
        return _FIPAAddress;
    }

    public void setFIPAAddress(String _FIPAAddress) {
        this._FIPAAddress = _FIPAAddress;
        if (_FIPAAddress == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_ZEUSName == null) {
            throw new MissingAttributeException("ZEUS-name");
        }
        if (_FIPAAddress == null) {
            throw new MissingAttributeException("FIPA-address");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("contact");
        w.attribute("ZEUS-name", _ZEUSName.toString());
        w.attribute("FIPA-address", _FIPAAddress.toString());
        w.end("contact");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("contact");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("ZEUS-name")) {
                if (_ZEUSName!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _ZEUSName = xs.takeAttributeValue();
                continue;
            }
            if (an.equals("FIPA-address")) {
                if (_FIPAAddress!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _FIPAAddress = xs.takeAttributeValue();
                continue;
            }
            throw new InvalidAttributeException(an);
        }
        xs.takeEnd("contact");
    }

    public static Contact unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Contact unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Contact unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Contact) d.unmarshal(xs, (Contact.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Contact)) {
            return false;
        }
        Contact tob = ((Contact) ob);
        if (_ZEUSName!= null) {
            if (tob._ZEUSName == null) {
                return false;
            }
            if (!_ZEUSName.equals(tob._ZEUSName)) {
                return false;
            }
        } else {
            if (tob._ZEUSName!= null) {
                return false;
            }
        }
        if (_FIPAAddress!= null) {
            if (tob._FIPAAddress == null) {
                return false;
            }
            if (!_FIPAAddress.equals(tob._FIPAAddress)) {
                return false;
            }
        } else {
            if (tob._FIPAAddress!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_ZEUSName!= null)?_ZEUSName.hashCode(): 0));
        h = ((127 *h)+((_FIPAAddress!= null)?_FIPAAddress.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<contact");
        if (_ZEUSName!= null) {
            sb.append(" ZEUS-name=");
            sb.append(_ZEUSName.toString());
        }
        if (_FIPAAddress!= null) {
            sb.append(" FIPA-address=");
            sb.append(_FIPAAddress.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        Dispatcher d = new Dispatcher();
        d.register("contact", (Contact.class));
        d.register("contacts", (Contacts.class));
        d.freezeElementNameMap();
        return d;
    }

}
