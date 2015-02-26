  /*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
  package sl;
  
  /**
    This exception is thrown when some problem occurs in the concrete parsing
    subsystem accessed through this interface. If an exception is thrown by the
    underlying parser, it is wrapped with a <code>Codec.CodecException</code>,
    which is then rethrown.
  */
  public class CodecException extends Exception {
    /**
    @serial
    */
    private Throwable nested;

    /**
      Construct a new <code>CodecException</code>
      @param msg The message for this exception.
      @param t The exception wrapped by this object.
    */
    public CodecException(String msg, Throwable t) {
      super(msg);
      nested = t;
    }

    /**
      Reads the exception wrapped by this object.
      @return the <code>Throwable</code> object that is the exception thrown by
      the concrete parsing subsystem.
    */
    public Throwable getNested() {
      return nested;
    }

    public void printStackTrace() {
      if (nested != null)
	nested.printStackTrace();
      super.printStackTrace();
    }
  }