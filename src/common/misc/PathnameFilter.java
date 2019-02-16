/*====================================================================*\

PathnameFilter.java

Pathname filter class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import common.exception.AppException;

//----------------------------------------------------------------------


// PATHNAME FILTER CLASS


public class PathnameFilter
	implements FileFilter
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	char	SINGLE_WILDCARD_CHAR		= '?';
	public static final	char	MULTIPLE_WILDCARD_CHAR		= '*';
	public static final	String	PATH_MULTIPLE_WILDCARD_STR	= "**";

	public static final	char	SEPARATOR_CHAR	= '/';
	public static final	String	SEPARATOR_STR	= Character.toString(SEPARATOR_CHAR);

	public enum ErrorMode
	{
		LIST,
		WRITE,
		LIST_AND_WRITE
	}

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ERROR IDENTIFIERS


	private enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		ACCESS_NOT_PERMITTED
		("Access to a file or directory in the pathname was not permitted."),

		FAILED_TO_CONVERT_TO_CANONICAL_FORM
		("Failed to convert the pathname to canonical form."),

		DOUBLE_DOT_AFTER_WILDCARD
		("\"..\" is not allowed after a wildcard in a pathname-filter pattern.");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// MULTIPLE FILTER CLASS


	public static class MultipleFilter
		extends PathnameFilter
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	char	SEPARATOR	= ';';

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(String[] patterns)
			throws AppException
		{
			this(patterns, null, false, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(String[] patterns,
							  String   basePathname)
			throws AppException
		{
			this(patterns, basePathname, false, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(String[] patterns,
							  boolean  ignoreCase)
			throws AppException
		{
			this(patterns, null, ignoreCase, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(String[] patterns,
							  boolean  ignoreCase,
							  boolean  normaliseDirectory)
			throws AppException
		{
			this(patterns, null, ignoreCase, normaliseDirectory);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(String[] patterns,
							  String   basePathname,
							  boolean  ignoreCase)
			throws AppException
		{
			this(patterns, basePathname, ignoreCase, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(String[] patterns,
							  String   basePathname,
							  boolean  ignoreCase,
							  boolean  normaliseDirectory)
			throws AppException
		{
			filters = new PathnameFilter[patterns.length];
			for (int i = 0; i < filters.length; i++)
				filters[i] = new PathnameFilter(patterns[i], basePathname, ignoreCase, normaliseDirectory);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(List<String> patterns)
			throws AppException
		{
			this(patterns, null, false, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(List<String> patterns,
							  String       basePathname)
			throws AppException
		{
			this(patterns, basePathname, false, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(List<String> patterns,
							  boolean      ignoreCase)
			throws AppException
		{
			this(patterns, null, ignoreCase, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(List<String> patterns,
							  boolean      ignoreCase,
							  boolean      normaliseDirectory)
			throws AppException
		{
			this(patterns, null, ignoreCase, normaliseDirectory);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(List<String> patterns,
							  String       basePathname,
							  boolean      ignoreCase)
			throws AppException
		{
			this(patterns, basePathname, ignoreCase, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 * @throws AppException
		 */

		public MultipleFilter(List<String> patterns,
							  String       basePathname,
							  boolean      ignoreCase,
							  boolean      normaliseDirectory)
			throws AppException
		{
			filters = new PathnameFilter[patterns.size()];
			for (int i = 0; i < filters.length; i++)
				filters[i] = new PathnameFilter(patterns.get(i), basePathname, ignoreCase, normaliseDirectory);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			StringBuilder buffer = new StringBuilder(256);
			for (int i = 0; i < filters.length; i++)
			{
				if (i > 0)
					buffer.append(SEPARATOR);
				buffer.append(filters[i]);
			}
			return buffer.toString();
		}

		//--------------------------------------------------------------

		@Override
		public boolean accept(File file)
		{
			for (PathnameFilter filter : filters)
			{
				if (filter.accept(file))
					return true;
			}
			return false;
		}

		//--------------------------------------------------------------

		@Override
		public boolean acceptDirectory(File directory)
		{
			for (PathnameFilter filter : filters)
			{
				if (filter.acceptDirectory(directory))
					return true;
			}
			return false;
		}

		//--------------------------------------------------------------

		@Override
		public void updateAbsolute()
			throws AppException
		{
			for (PathnameFilter filter : filters)
				filter.updateAbsolute();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getNumFilters()
		{
			return filters.length;
		}

		//--------------------------------------------------------------

		public PathnameFilter getFilter(int index)
		{
			return filters[index];
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	PathnameFilter[]	filters;

	}

	//==================================================================


	// PATTERN TOKEN CLASS


	private static class PatternToken
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private enum Kind
		{
			LITERAL,
			PATTERN,
			SINGLE_WILDCARD,
			MULTIPLE_WILDCARD,
			PATH_MULTIPLE_WILDCARD
		}

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PatternToken(Kind kind)
		{
			this(kind, null, false);
		}

		//--------------------------------------------------------------

		private PatternToken(Kind    kind,
							 String  value,
							 boolean ignoreCase)
		{
			this.kind = kind;
			this.value = value;
			comparisonValue = ignoreCase ? value.toLowerCase() : value;

			if (kind == Kind.PATTERN)
			{
				subtokens = new ArrayList<>();
				int index = 0;
				int startIndex = index;
				while (index < value.length())
				{
					switch (value.charAt(index))
					{
						case SINGLE_WILDCARD_CHAR:
							if (index > startIndex)
								subtokens.add(new PatternToken(value, startIndex, index, ignoreCase));
							subtokens.add(new PatternToken(Kind.SINGLE_WILDCARD));
							startIndex = ++index;
							break;

						case MULTIPLE_WILDCARD_CHAR:
							if (index > startIndex)
								subtokens.add(new PatternToken(value, startIndex, index, ignoreCase));
							subtokens.add(new PatternToken(Kind.MULTIPLE_WILDCARD));
							startIndex = ++index;
							break;

						default:
							++index;
							break;
					}
				}
				if (index > startIndex)
					subtokens.add(new PatternToken(value, startIndex, index, ignoreCase));
			}
		}

		//--------------------------------------------------------------

		private PatternToken(String  str,
							 boolean ignoreCase)
		{
			this(Kind.LITERAL, str, ignoreCase);
		}

		//--------------------------------------------------------------

		private PatternToken(String  str,
							 int     startIndex,
							 int     endIndex,
							 boolean ignoreCase)
		{
			this(str.substring(startIndex, endIndex), ignoreCase);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			String str = null;
			switch (kind)
			{
				case LITERAL:
					str = value;
					break;

				case PATTERN:
				{
					StringBuilder buffer = new StringBuilder();
					for (PatternToken token : subtokens)
						buffer.append(token);
					str = buffer.toString();
					break;
				}

				case SINGLE_WILDCARD:
					str = Character.toString(SINGLE_WILDCARD_CHAR);
					break;

				case MULTIPLE_WILDCARD:
					str = Character.toString(MULTIPLE_WILDCARD_CHAR);
					break;

				case PATH_MULTIPLE_WILDCARD:
					str = PATH_MULTIPLE_WILDCARD_STR;
					break;
			}
			return str;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private boolean match(String filename,
							  int    filenameIndex,
							  int    tokenIndex)
		{
			while (tokenIndex < subtokens.size())
			{
				PatternToken token = subtokens.get(tokenIndex++);
				switch (token.kind)
				{
					case LITERAL:
						if (!filename.startsWith(token.comparisonValue, filenameIndex))
							return false;
						filenameIndex += token.comparisonValue.length();
						break;

					case SINGLE_WILDCARD:
						if (filenameIndex > filename.length())
							return false;
						++filenameIndex;
						break;

					case MULTIPLE_WILDCARD:
						while (token.kind == PatternToken.Kind.MULTIPLE_WILDCARD)
						{
							if (tokenIndex >= subtokens.size())
								return true;
							token = subtokens.get(tokenIndex++);
						}
						--tokenIndex;
						while (filenameIndex < filename.length())
						{
							if (match(filename, filenameIndex, tokenIndex))
								return true;
							++filenameIndex;
						}
						return false;

					case PATH_MULTIPLE_WILDCARD:
					case PATTERN:
						// do nothing
						break;
				}
			}
			return (filenameIndex >= filename.length());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	Kind				kind;
		private	String				value;
		private	String				comparisonValue;
		private	List<PatternToken>	subtokens;

	}

	//==================================================================


	// PATTERN COMPARATOR CLASS


	private static class PatternComparator
		implements Comparator<PathnameFilter>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		public static final	PatternComparator	INSTANCE	= new PatternComparator();

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PatternComparator()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Comparator interface
	////////////////////////////////////////////////////////////////////

		public int compare(PathnameFilter filter1,
						   PathnameFilter filter2)
		{
			return (filter1.toString().compareTo(filter2.toString()));
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// FILE EXCEPTION CLASS


	private static class FileException
		extends common.exception.FileException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public FileException(AppException.IId id,
							 File             file)
		{
			super(id, file);
		}

		//--------------------------------------------------------------

		public FileException(AppException.IId id,
							 File             file,
							 Throwable        cause)
		{
			super(id, file, cause);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String getPathname()
		{
			return getFile().getPath().replace(File.separatorChar, SEPARATOR_CHAR);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public PathnameFilter()
	{
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public PathnameFilter(String pattern)
		throws AppException
	{
		this(pattern, null, false, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public PathnameFilter(String pattern,
						  String basePathname)
		throws AppException
	{
		this(pattern, basePathname, false, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public PathnameFilter(String  pattern,
						  boolean ignoreCase)
		throws AppException
	{
		this(pattern, null, ignoreCase, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public PathnameFilter(String  pattern,
						  boolean ignoreCase,
						  boolean normaliseDirectory)
		throws AppException
	{
		this(pattern, null, ignoreCase, normaliseDirectory);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public PathnameFilter(String  pattern,
						  String  basePathname,
						  boolean ignoreCase)
		throws AppException
	{
		this(pattern, null, ignoreCase, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 * @throws AppException
	 */

	public PathnameFilter(String  pattern,
						  String  basePathname,
						  boolean ignoreCase,
						  boolean normaliseDirectory)
		throws AppException
	{
		// Validate arguments
		if (pattern == null)
			throw new IllegalArgumentException();

		// Fix up pattern for directory
		pattern = normalisePathname(pattern);
		if (pattern.endsWith(SEPARATOR_STR))
			pattern += PATH_MULTIPLE_WILDCARD_STR;
		else
		{
			if (normaliseDirectory)
			{
				try
				{
					if (new File(pattern).isDirectory())
						pattern += SEPARATOR_STR + PATH_MULTIPLE_WILDCARD_STR;
				}
				catch (SecurityException e)
				{
					throw new FileException(ErrorId.ACCESS_NOT_PERMITTED, new File(pattern));
				}
			}
		}

		// Fix up base pathname
		if (basePathname != null)
		{
			basePathname = normalisePathname(basePathname);
			if (!basePathname.isEmpty() && !basePathname.endsWith(SEPARATOR_STR))
				basePathname += SEPARATOR_STR;
		}

		// Initialise instance fields
		this.pattern = pattern;
		this.basePathname = basePathname;
		this.ignoreCase = ignoreCase;

		// Initialise pattern tokens
		patternTokens = stringsToTokens(getPathnameComponents(new File(pattern)));
		for (int i = 0; i < patternTokens.size(); i++)
		{
			PatternToken token = patternTokens.get(i);
			if (token.kind == PatternToken.Kind.LITERAL)
			{
				if ((token.value.isEmpty() && (i > 0)) || token.value.equals("."))
					patternTokens.remove(i--);
				else if (hasWildcards && token.value.equals(".."))
					throw new AppException(ErrorId.DOUBLE_DOT_AFTER_WILDCARD);
			}
			else
			{
				hasWildcards = true;
				if (token.kind == PatternToken.Kind.PATH_MULTIPLE_WILDCARD)
					hasPathWildcards = true;
			}
		}

		// Initialise absolute pattern tokens
		updateAbsolute();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void setErrorMode(ErrorMode mode)
	{
		errorMode = mode;
	}

	//------------------------------------------------------------------

	public static List<File> getErrors()
	{
		return Collections.unmodifiableList(errors);
	}

	//------------------------------------------------------------------

	public static void clearErrors()
	{
		errors.clear();
	}

	//------------------------------------------------------------------

	public static void sort(List<PathnameFilter> list)
	{
		Collections.sort(list, PatternComparator.INSTANCE);
	}

	//------------------------------------------------------------------

	public static String normalisePathname(String pathname)
	{
		return pathname.replace(File.separatorChar, SEPARATOR_CHAR);
	}

	//------------------------------------------------------------------

	public static String toNormalisedPathname(File file)
	{
		return normalisePathname(file.getPath());
	}

	//------------------------------------------------------------------

	public static String toNormalisedPathname(File   file,
											  String pattern)
	{
		String pathname = normalisePathname(new File(file, pattern).getPath());
		return (normalisePathname(pattern).endsWith(SEPARATOR_STR) ? pathname + SEPARATOR_STR : pathname);
	}

	//------------------------------------------------------------------

	public static String normaliseDirectoryPathname(String pathname)
		throws AppException
	{
		try
		{
			pathname = normalisePathname(pathname);
			if (!pathname.endsWith(SEPARATOR_STR) && new File(pathname).isDirectory())
				pathname += SEPARATOR_STR;
			return pathname;
		}
		catch (SecurityException e)
		{
			throw new FileException(ErrorId.ACCESS_NOT_PERMITTED, new File(pathname));
		}
	}

	//------------------------------------------------------------------

	private static String[] splitPathname(String str)
	{
		return str.split(SEPARATOR_STR);
	}

	//------------------------------------------------------------------

	private static String toCanonicalPathname(File file)
		throws FileException
	{
		try
		{
			return normalisePathname(file.getCanonicalPath());
		}
		catch (SecurityException e)
		{
			throw new FileException(ErrorId.ACCESS_NOT_PERMITTED, file);
		}
		catch (IOException e)
		{
			throw new FileException(ErrorId.FAILED_TO_CONVERT_TO_CANONICAL_FORM, file, e);
		}
	}

	//------------------------------------------------------------------

	private static String[] getPathnameComponents(File file)
	{
		return splitPathname(toNormalisedPathname(file));
	}

	//------------------------------------------------------------------

	private static String[] getAbsolutePathnameComponents(File file)
		throws AppException
	{
		return splitPathname(toCanonicalPathname(file));
	}

	//------------------------------------------------------------------

	private static String[] tokensToPaths(List<PatternToken> tokens)
	{
		String[] paths = new String[2];

		StringBuilder buffer = new StringBuilder(256);
		int index = 0;
		while (index < tokens.size())
		{
			if (tokens.get(index).kind != PatternToken.Kind.LITERAL)
				break;
			if (index > 0)
				buffer.append(SEPARATOR_CHAR);
			buffer.append(tokens.get(index++));
		}
		paths[0] = buffer.toString();

		buffer.setLength(0);
		int startIndex = index;
		while (index < tokens.size())
		{
			if (index > startIndex)
				buffer.append(SEPARATOR_CHAR);
			buffer.append(tokens.get(index++));
		}
		paths[1] = buffer.toString();

		return paths;
	}

	//------------------------------------------------------------------

	private static boolean match(String[]           pathnameComponents,
								 int                pathnameComponentIndex,
								 List<PatternToken> patternTokens,
								 int                patternTokenIndex)
	{
		while (patternTokenIndex < patternTokens.size())
		{
			PatternToken token = patternTokens.get(patternTokenIndex++);
			switch (token.kind)
			{
				case LITERAL:
					if ((pathnameComponentIndex >= pathnameComponents.length)
						|| (!token.comparisonValue.equals(pathnameComponents[pathnameComponentIndex++])))
						return false;
					break;

				case PATTERN:
					if ((pathnameComponentIndex >= pathnameComponents.length)
						|| !token.match(pathnameComponents[pathnameComponentIndex++], 0, 0))
						return false;
					break;

				case PATH_MULTIPLE_WILDCARD:
					while (token.kind == PatternToken.Kind.PATH_MULTIPLE_WILDCARD)
					{
						if (patternTokenIndex >= patternTokens.size())
							return true;
						token = patternTokens.get(patternTokenIndex++);
					}
					--patternTokenIndex;
					while (pathnameComponentIndex < pathnameComponents.length)
					{
						if (match(pathnameComponents, pathnameComponentIndex, patternTokens, patternTokenIndex))
							return true;
						++pathnameComponentIndex;
					}
					return false;

				case SINGLE_WILDCARD:
				case MULTIPLE_WILDCARD:
					// do nothing
					break;
			}
		}
		return (pathnameComponentIndex >= pathnameComponents.length);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FileFilter interface
////////////////////////////////////////////////////////////////////////

	public boolean accept(File file)
	{
		// Don't accept an existing entity that is not a normal file
		if (file.exists() && !file.isFile())
			return false;

		// Accept all files if no pattern has been set; otherwise, match pathname against pattern
		return ((pattern == null) ? true : match(file));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return ((pattern == null) ? "" : pattern);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getPattern()
	{
		return pattern;
	}

	//------------------------------------------------------------------

	public String getBasePathname()
	{
		return basePathname;
	}

	//------------------------------------------------------------------

	/**
	 * @throws AppException
	 * @throws IllegalStateException
	 */

	public void setBasePathname(String pathname)
		throws AppException
	{
		basePathname = pathname;
		updateAbsolute();
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public String[] getPaths()
	{
		if (pattern == null)
			throw new IllegalStateException();

		return tokensToPaths(patternTokens);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public String[] getAbsolutePaths()
	{
		if (pattern == null)
			throw new IllegalStateException();

		return tokensToPaths(absolutePatternTokens);
	}

	//------------------------------------------------------------------

	public boolean containsWildcards()
	{
		return hasWildcards;
	}

	//------------------------------------------------------------------

	public boolean containsPathWildcards()
	{
		return hasPathWildcards;
	}

	//------------------------------------------------------------------

	public boolean acceptDirectory(File directory)
	{
		// Don't accept an existing entity that is not a directory
		if (directory.exists() && !directory.isDirectory())
			return false;

		// Accept all directories if no pattern has been set; otherwise, match pathname against pattern
		return ((pattern == null) ? true : match(directory));
	}

	//------------------------------------------------------------------

	/**
	 * This method may return an incorrect value if the match is relative and the base pathname, pattern or
	 * target pathname contains any dot or double-dot ("." or "..") components.
	 *
	 * @throws AppException
	 * @throws IllegalStateException
	 */

	public int getRelativeLength(File file)
		throws AppException
	{
		if (pattern == null)
			throw new IllegalStateException();

		return (isMatchAbsolute()
						? absolutePatternTokens.size() - getAbsolutePathnameComponents(file).length
						: getPathnameComponents(new File(basePathname)).length + patternTokens.size()
																				- getPathnameComponents(file).length);
	}

	//------------------------------------------------------------------

	public boolean match(File file)
	{
		try
		{
			String[] pathnameComponents = null;
			List<PatternToken> tokens = null;
			if (isMatchAbsolute())
			{
				pathnameComponents = getAbsolutePathnameComponents(file);
				tokens = absolutePatternTokens;
			}
			else
			{
				String pathname = toNormalisedPathname(file);
				if (!pathname.startsWith(basePathname))
					return false;
				pathnameComponents = getPathnameComponents(new File(pathname.substring(basePathname.length())));
				tokens = patternTokens;
			}
			if (ignoreCase)
			{
				for (int i = 0; i < pathnameComponents.length; i++)
					pathnameComponents[i] = pathnameComponents[i].toLowerCase();
			}
			return match(pathnameComponents, 0, tokens, 0);
		}
		catch (AppException e)
		{
			if ((errorMode == ErrorMode.LIST) || (errorMode == ErrorMode.LIST_AND_WRITE))
				errors.add(file);
			if ((errorMode == ErrorMode.WRITE) || (errorMode == ErrorMode.LIST_AND_WRITE))
				System.err.println(e);
			return false;
		}
	}

	//------------------------------------------------------------------

	/**
	 * @throws AppException
	 * @throws IllegalStateException
	 */

	public void updateAbsolute()
		throws AppException
	{
		if (isMatchAbsolute())
		{
			String[] paths = getPaths();
			String pathname = null;
			if (paths[0].isEmpty() && new File(paths[1]).isAbsolute())
				pathname = paths[1];
			else
			{
				pathname = toCanonicalPathname(new File(paths[0]));
				if (!paths[1].isEmpty())
					pathname += (paths[1].startsWith(SEPARATOR_STR) || pathname.endsWith(SEPARATOR_STR))
																				? paths[1]
																				: SEPARATOR_STR + paths[1];
			}
			absolutePatternTokens = stringsToTokens(splitPathname(pathname));
		}
	}

	//------------------------------------------------------------------

	private boolean isMatchAbsolute()
	{
		return ((basePathname == null)
				|| (!patternTokens.isEmpty() && new File(patternTokens.get(0).toString()).isAbsolute()));
	}

	//------------------------------------------------------------------

	private List<PatternToken> stringsToTokens(String[] strs)
	{
		List<PatternToken> tokens = new ArrayList<>();
		for (String str : strs)
			tokens.add(((str.indexOf(SINGLE_WILDCARD_CHAR) < 0) &&
						 (str.indexOf(MULTIPLE_WILDCARD_CHAR) < 0))
								? new PatternToken(str, ignoreCase)
								: str.equals(PATH_MULTIPLE_WILDCARD_STR)
										? new PatternToken(PatternToken.Kind.PATH_MULTIPLE_WILDCARD)
										: new PatternToken(PatternToken.Kind.PATTERN, str, ignoreCase));
		return tokens;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	ErrorMode	errorMode;
	private static	List<File>	errors		= new ArrayList<>();

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	String				pattern;
	private	String				basePathname;
	private	List<PatternToken>	patternTokens;
	private	List<PatternToken>	absolutePatternTokens;
	private	boolean				hasWildcards;
	private	boolean				hasPathWildcards;
	private	boolean				ignoreCase;

}

//----------------------------------------------------------------------
