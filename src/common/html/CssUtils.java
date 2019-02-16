/*====================================================================*\

CssUtils.java

CSS utility methods class.

\*====================================================================*/


// PACKAGE


package common.html;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import common.misc.StringUtils;

//----------------------------------------------------------------------


// CSS UTILITY METHODS CLASS


public class CssUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	COMMENT_PREFIX	= "/*";
	public static final		String	COMMENT_SUFFIX	= "*/";

	private static final	int	SEPARATOR_LENGTH	= 72;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private CssUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String createSeparator()
	{
		StringBuilder buffer = new StringBuilder(SEPARATOR_LENGTH + 1);
		buffer.append(COMMENT_PREFIX);
		buffer.append(StringUtils.createCharArray('-',
												  SEPARATOR_LENGTH - COMMENT_PREFIX.length() -
																			COMMENT_SUFFIX.length()));
		buffer.append(COMMENT_SUFFIX);
		buffer.append('\n');
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static String createHeaderComment(List<? extends CharSequence> seqs)
	{
		char[] lineChars = StringUtils.createCharArray('*',
													   SEPARATOR_LENGTH - COMMENT_PREFIX.length() -
																				COMMENT_SUFFIX.length());
		StringBuilder buffer = new StringBuilder(1024);
		buffer.append(COMMENT_PREFIX);
		buffer.append(lineChars);
		buffer.append("*\\\n");
		for (CharSequence seq : seqs)
		{
			buffer.append(seq);
			buffer.append('\n');
		}
		buffer.append("\\*");
		buffer.append(lineChars);
		buffer.append(COMMENT_SUFFIX);
		buffer.append('\n');
		return buffer.toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
