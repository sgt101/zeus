package FIPA;


/**
* FIPA/ReceivedObject.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from FIPA.idl
* 09 March 2001 16:39:32 o'clock GMT
*/

public final class ReceivedObject implements org.omg.CORBA.portable.IDLEntity
{
  public String by = null;
  public String from = null;
  public FIPA.DateTime date = null;
  public String id = null;
  public String via = null;

  public ReceivedObject ()
  {
  } // ctor

  public ReceivedObject (String _by, String _from, FIPA.DateTime _date, String _id, String _via)
  {
    by = _by;
    from = _from;
    date = _date;
    id = _id;
    via = _via;
  } // ctor

} // class ReceivedObject