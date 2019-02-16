/*====================================================================*\

ClassUtils.java

Class: class-related utility methods.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.net.URL;

//----------------------------------------------------------------------


// CLASS: CLASS-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to Java classes.
 */

public class ClassUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The filename extension of a Java class file. */
	private static final	String	CLASS_FILENAME_EXTENSION	= ".class";

	/** The prefix of a JAR-scheme URL. */
	private static final	String	JAR_URL_PREFIX	= "jar:";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ClassUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified class was loaded from a JAR.
	 * @param  cls  the class of interest.
	 * @return {@code true} if the specified class was loaded from a JAR, {@code false} otherwise.
	 */

	public static boolean isFromJar(Class<?> cls)
	{
		URL url = cls.getResource(cls.getSimpleName() + CLASS_FILENAME_EXTENSION);
		return (url != null) && url.toString().startsWith(JAR_URL_PREFIX);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
