/*====================================================================*\

CollectionUtils.java

Class: utility methods relating to collections.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Collection;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS RELATING TO COLLECTIONS


public class CollectionUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private CollectionUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isNullOrEmpty(Collection<?> collection)
	{
		return ((collection == null) || collection.isEmpty());
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
