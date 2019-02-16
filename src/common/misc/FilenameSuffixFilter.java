/*====================================================================*\

FilenameSuffixFilter.java

Filename suffix filter class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//----------------------------------------------------------------------


// FILENAME SUFFIX FILTER CLASS


public class FilenameSuffixFilter
	extends javax.swing.filechooser.FileFilter
	implements java.io.FileFilter
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FilenameSuffixFilter(String    description,
								String... suffixes)
	{
		this(description, Arrays.asList(suffixes));
	}

	//------------------------------------------------------------------

	public FilenameSuffixFilter(String       description,
								List<String> suffixes)
	{
		this.suffixes = new ArrayList<>();
		StringBuilder buffer = new StringBuilder(128);
		buffer.append(description);
		buffer.append(" (");
		for (String suffix : suffixes)
		{
			if (!this.suffixes.isEmpty())
				buffer.append(", ");
			buffer.append('*');
			buffer.append(suffix);
			this.suffixes.add(suffix.toLowerCase());
		}
		buffer.append(')');
		this.description = buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FileFilter interface
////////////////////////////////////////////////////////////////////////

	public boolean accept(File file)
	{
		if (file.isDirectory())
			return true;

		String filename = file.getName().toLowerCase();
		for (String suffix : suffixes)
		{
			if (filename.endsWith(suffix))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FilenameSuffixFilter)
		{
			FilenameSuffixFilter filter = (FilenameSuffixFilter)obj;
			return (description.equals(filter.description) && suffixes.equals(filter.suffixes));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		return (description.hashCode() * 31 + suffixes.hashCode());
	}

	//------------------------------------------------------------------

	@Override
	public String getDescription()
	{
		return description;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getSuffix(int index)
	{
		return suffixes.get(index);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	List<String>	suffixes;
	private	String			description;

}

//----------------------------------------------------------------------
