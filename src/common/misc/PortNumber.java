/*====================================================================*\

PortNumber.java

Class: methods for accessing a port-number file.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

//----------------------------------------------------------------------


// CLASS: METHODS FOR ACCESSING A PORT-NUMBER FILE


public class PortNumber
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	PORT_FILENAME_PREFIX	= "port-";

	private static final	String	ENCODING_NAME_UTF8	= "UTF-8";

	private static final	String	INVALID_PORT_NUMBER_STR			= "The port number is invalid";
	private static final	String	FAILED_TO_READ_STR				= "Failed to read the file from ";
	private static final	String	FAILED_TO_WRITE_STR				= "Failed to write the file to ";
	private static final	String	FAILED_TO_LOCK_STR				= "Failed to lock the file.";
	private static final	String	FAILED_TO_CREATE_DIRECTORY_STR	= "Failed to create the directory ";
	private static final	String	FILE_IS_TOO_LONG_STR			= "The file is too long.";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PortNumber()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int getValue(String key)
	{
		String pathname = PropertiesPathname.getPathname();
		return ((pathname == null) ? -1 : getValue(pathname, key));
	}

	//------------------------------------------------------------------

	public static int getValue(String dirPath,
							   String key)
	{
		// Get content of file
		String str = readFile(getPathname(dirPath, key));

		// Parse value
		int value = -1;
		if (str != null)
		{
			try
			{
				value = Integer.parseInt(str);
			}
			catch (IllegalArgumentException e)
			{
				System.err.println(dirPath);
				System.err.println(key);
				System.err.println(INVALID_PORT_NUMBER_STR);
			}
		}

		return value;
	}

	//------------------------------------------------------------------

	public static void setValue(String key,
								int    value)
	{
		String pathname = PropertiesPathname.getPathname();
		if (pathname != null)
			setValue(pathname, key, value);
	}

	//------------------------------------------------------------------

	public static void setValue(String dirPath,
								String key,
								int    value)
	{
		// Convert value to string and write it to file
		writeFile(getPathname(dirPath, key), Integer.toString(value));
	}

	//------------------------------------------------------------------

	private static String readFile(String pathname)
	{
		String content = null;;
		RandomAccessFile file = null;
		try
		{
			// Open random-access file for reading
			file = new RandomAccessFile(new File(pathname), "r");

			// Lock file channel
			if (file.getChannel().tryLock(0, Long.MAX_VALUE, true) == null)
				throw new Exception(FAILED_TO_LOCK_STR);

			// Read file
			long length = file.length();
			if (length > Integer.MAX_VALUE)
				throw new Exception(FILE_IS_TOO_LONG_STR);
			byte[] buffer = new byte[(int)length];
			file.readFully(buffer);

			// Convert file content to string
			content = new String(buffer, ENCODING_NAME_UTF8);
		}
		catch (Exception e)
		{
			System.err.println(FAILED_TO_READ_STR + pathname);
			if (e.getMessage() != null)
				System.err.println(e.getMessage());
		}
		finally
		{
			// Close file
			try
			{
				if (file != null)
					file.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		return content;
	}

	//------------------------------------------------------------------

	private static void writeFile(String pathname,
								  String content)
	{
		RandomAccessFile file = null;
		try
		{
			// Create directory
			File outFile = new File(pathname);
			File outDirectory = outFile.getAbsoluteFile().getParentFile();
			if ((outDirectory != null) && !outDirectory.exists() && !outDirectory.mkdirs())
				throw new Exception(FAILED_TO_CREATE_DIRECTORY_STR + outDirectory.getPath());

			// Open random-access file for reading and writing
			file = new RandomAccessFile(outFile, "rw");

			// Lock file channel
			if (file.getChannel().tryLock() == null)
				throw new Exception(FAILED_TO_LOCK_STR);

			// Write file
			file.setLength(0);
			file.write(content.getBytes(ENCODING_NAME_UTF8));
			file.getChannel().force(false);
		}
		catch (Exception e)
		{
			System.err.println(FAILED_TO_WRITE_STR + pathname);
			if (e.getMessage() != null)
				System.err.println(e.getMessage());
		}
		finally
		{
			// Close file
			try
			{
				if (file != null)
					file.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
	}

	//------------------------------------------------------------------

	private static String getPathname(String dirPath,
									  String key)
	{
		String pathname = (dirPath.endsWith(File.separator) || dirPath.endsWith("/")) ? dirPath
																					  : dirPath + File.separator;
		return pathname + PORT_FILENAME_PREFIX + key;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
