/*====================================================================*\

ResourceProperties.java

Resource properties class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import common.indexedsub.IndexedSub;

//----------------------------------------------------------------------


// RESOURCE PROPERTIES CLASS


public class ResourceProperties
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	ERROR_READING_PROPERTIES_STR	= "Location: %1\nAn error occurred when reading "
																		+ "the resource properties.";

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// RESOURCE PROPERTIES EXCEPTION CLASS


	public static class ResourcePropertiesException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ResourcePropertiesException(String    str,
											Throwable cause)
		{
			// Call superclass constructor
			super(str, cause);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws ResourcePropertiesException
	 *           if an error occurs when reading the properties from the resource.
	 */

	public ResourceProperties(String pathname)
	{
		// Call alternative constructor
		this(pathname, null);
	}

	//------------------------------------------------------------------

	/**
	 * @throws ResourcePropertiesException
	 *           if an error occurs when reading the properties from the resource.
	 */

	public ResourceProperties(String   pathname,
							  Class<?> cls)
	{
		map = new HashMap<>();
		try
		{
			// Open input stream on resource
			InputStream inStream = (cls == null) ? ClassLoader.getSystemResourceAsStream(pathname)
												 : cls.getResourceAsStream(pathname);
			if (inStream != null)
			{
				// Read properties from stream
				Properties properties = new Properties();
				properties.load(inStream);

				// Add properties to map
				for (String key : properties.stringPropertyNames())
					map.put(key, properties.getProperty(key));
			}
		}
		catch (Exception e)
		{
			throw new ResourcePropertiesException(IndexedSub.sub(ERROR_READING_PROPERTIES_STR, pathname), e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String get(String key)
	{
		return get(key, null);
	}

	//------------------------------------------------------------------

	public String get(String key,
					  String defaultValue)
	{
		String value = map.get(key);
		if (value == null)
			value = defaultValue;
		return value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	Map<String, String>	map;

}

//----------------------------------------------------------------------
