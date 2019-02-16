/*====================================================================*\

IByteDataInputStream.java

Byte data input stream interface.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import common.exception.AppException;

//----------------------------------------------------------------------


// BYTE DATA INPUT STREAM INTERFACE


public interface IByteDataInputStream
	extends IDataInput
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	int read(byte[] buffer,
			 int    offset,
			 int    length)
		throws AppException;

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
