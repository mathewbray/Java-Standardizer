/*====================================================================*\

ValueOutOfBoundsException.java

Class: "value out of bounds" exception.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// CLASS: "VALUE OUT OF BOUNDS" EXCEPTION


public class ValueOutOfBoundsException
	extends RuntimeException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ValueOutOfBoundsException()
	{
	}

	//------------------------------------------------------------------

	public ValueOutOfBoundsException(String messageStr)
	{
		super(messageStr);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
