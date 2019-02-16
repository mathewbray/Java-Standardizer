/*====================================================================*\

ArgumentOutOfBoundsException.java

"Argument out of bounds" exception class.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// "ARGUMENT OUT OF BOUNDS" EXCEPTION CLASS


public class ArgumentOutOfBoundsException
	extends IllegalArgumentException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ArgumentOutOfBoundsException()
	{
	}

	//------------------------------------------------------------------

	public ArgumentOutOfBoundsException(String messageStr)
	{
		super(messageStr);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
