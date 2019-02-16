/*====================================================================*\

UnexpectedRuntimeException.java

Class: unexpected runtime exception.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// CLASS: UNEXPECTED RUNTIME EXCEPTION


public class UnexpectedRuntimeException
	extends RuntimeException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UnexpectedRuntimeException()
	{
	}

	//------------------------------------------------------------------

	public UnexpectedRuntimeException(String messageStr)
	{
		// Call superclass constructor
		super(messageStr);
	}

	//------------------------------------------------------------------

	public UnexpectedRuntimeException(Throwable cause)
	{
		// Call superclass constructor
		super(cause);
	}

	//------------------------------------------------------------------

	public UnexpectedRuntimeException(String    messageStr,
									  Throwable cause)
	{
		// Call superclass constructor
		super(messageStr, cause);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
