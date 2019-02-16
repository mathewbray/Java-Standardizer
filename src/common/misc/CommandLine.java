/*====================================================================*\

CommandLine.java

Command line class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import common.exception.AppException;
import common.exception.FileException;

//----------------------------------------------------------------------


// COMMAND LINE CLASS


public class CommandLine<E extends Enum<E> & CommandLine.IOption<E>>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	OPTION_PREFIX			= "--";
	public static final		String	ARGUMENT_FILE_PREFIX	= "@";

	private static final	char	OPTION_ARGUMENT_SEPARATOR_CHAR	= '=';

	private enum State
	{
		OPTION,
		OPTION_ARGUMENT,
		NON_OPTION_ARGUMENT,
		DONE
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

		FILE_DOES_NOT_EXIST
		("The file does not exist."),

		UNCLOSED_QUOTATION
		("Line %1 contains an unclosed quotation."),

		INVALID_OPTION
		("'" + OPTION_PREFIX + "%1' is not a valid option."),

		MISSING_OPTION_ARGUMENT
		("The " + OPTION_PREFIX + "%1 option requires an argument."),

		UNEXPECTED_OPTION_ARGUMENT
		("The " + OPTION_PREFIX + "%1 option does not take an argument."),

		OPTION_AFTER_NON_OPTION_ARGUMENT
		("All options must precede non-option arguments.");

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
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// COMMAND-LINE OPTION INTERFACE


	public interface IOption<E extends Enum<E>>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		E getKey();

		//--------------------------------------------------------------

		String getName();

		//--------------------------------------------------------------

		public boolean hasArgument();

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// COMMAND-LINE ELEMENT CLASS


	public static class Element<E extends Enum<E>>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Element(IOption<E> option,
					   String     value)
		{
			this.option = option;
			this.value = value;
		}

		//--------------------------------------------------------------

		public Element(IOption<E> option)
		{
			this.option = option;
		}

		//--------------------------------------------------------------

		public Element(String value)
		{
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			String optionStr = getOptionString();
			return ((optionStr == null) ? (value == null) ? ""
														  : value
										: (value == null) ? optionStr
														  : optionStr + " " + value);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public IOption<E> getOption()
		{
			return option;
		}

		//--------------------------------------------------------------

		public String getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

		public String getOptionString()
		{
			return ((option == null) ? null : OPTION_PREFIX + option.getName());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	IOption<E>	option;
		private	String		value;

	}

	//==================================================================


	// PARSE EXCEPTION CLASS


	private static class ParseException
		extends AppException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ParseException(ErrorId id,
							   String  usageStr)
		{
			super(id);
			this.usageStr = usageStr;
		}

		//--------------------------------------------------------------

		private ParseException(ErrorId id,
							   String  str,
							   String  usageStr)
		{
			super(id, str);
			this.usageStr = usageStr;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getSuffix()
		{
			return ((usageStr == null) ? null : "\n" + usageStr);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	usageStr;

	}

	//==================================================================


	// ARGUMENT FILE EXCEPTION CLASS


	private static class ArgumentFileException
		extends FileException
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	ARGUMENT_FILE_STR	= "Command-line argument file: ";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ArgumentFileException(ErrorId id,
									  File    file)
		{
			super(id, file);
		}

		//--------------------------------------------------------------

		private ArgumentFileException(ErrorId id,
									  File    file,
									  String  substitutionStr)
		{
			super(id, file, substitutionStr);
		}

		//--------------------------------------------------------------

		private ArgumentFileException(FileException exception)
		{
			super(exception.getId(), exception.getFile(), exception.getCause(),
				  exception.getSubstitutionStrings());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getPrefix()
		{
			return (ARGUMENT_FILE_STR + super.getPrefix());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public CommandLine(Class<E> optionClass,
					   boolean  noOptionsAfterNonOptionArguments,
					   String   usageStr)
	{
		options = EnumSet.allOf(optionClass);
		this.noOptionsAfterNonOptionArguments = noOptionsAfterNonOptionArguments;
		this.usageStr = usageStr;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String argumentsToString(Iterable<String> args)
	{
		StringBuilder buffer = new StringBuilder();
		for (String arg : args)
		{
			if (buffer.length() > 0)
				buffer.append(' ');
			if (arg.isEmpty() || arg.contains(" "))
			{
				buffer.append('"');
				buffer.append(arg.replace("\"", "\"\""));
				buffer.append('"');
			}
			else
				buffer.append(arg);
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	private static List<String> readArgumentFile(String pathname,
												 String commentPrefix)
		throws AppException
	{
		// Test whether file exists
		File file = new File(PropertyString.parsePathname(pathname));
		if (!file.isFile())
			throw new ArgumentFileException(ErrorId.FILE_DOES_NOT_EXIST, file);

		// Read file
		try
		{
			List<String> arguments = new ArrayList<>();
			List<String> lines = TextFile.readLines(file, TextFile.ENCODING_NAME_UTF8);
			for (int i = 0; i < lines.size(); i++)
			{
				String str = lines.get(i);
				if (commentPrefix != null)
				{
					int index = str.indexOf(commentPrefix);
					if (index >= 0)
						str = str.substring(0, index);
				}
				str = str.trim();
				if (!str.isEmpty())
				{
					try
					{
						arguments.addAll(new Tokeniser(str, "\t ").getRemainingTokenStrings(true));
					}
					catch (Tokeniser.UnclosedQuotationException e)
					{
						throw new ArgumentFileException(ErrorId.UNCLOSED_QUOTATION, file, Integer.toString(i + 1));
					}
				}
			}
			return arguments;
		}
		catch (FileException e)
		{
			throw new ArgumentFileException(e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public List<Element<E>> parse(String[] arguments)
		throws AppException
	{
		return parse(Arrays.asList(arguments), false, null);
	}

	//------------------------------------------------------------------

	public List<Element<E>> parse(String[] arguments,
								  boolean  expand)
		throws AppException
	{
		return parse(Arrays.asList(arguments), expand, null);
	}

	//------------------------------------------------------------------

	public List<Element<E>> parse(String[] arguments,
								  boolean  expand,
								  String   commentPrefix)
		throws AppException
	{
		return parse(Arrays.asList(arguments), expand, commentPrefix);
	}

	//------------------------------------------------------------------

	public List<Element<E>> parse(List<String> arguments)
		throws AppException
	{
		return parse(arguments, false, null);
	}

	//------------------------------------------------------------------

	public List<Element<E>> parse(List<String> arguments,
								  boolean      expand)
		throws AppException
	{
		return parse(arguments, expand, null);
	}

	//------------------------------------------------------------------

	public List<Element<E>> parse(List<String> arguments,
								  boolean      expand,
								  String       commentPrefix)
		throws AppException
	{
		// Expand arguments
		if (expand)
		{
			List<String> argList = new ArrayList<>();
			for (String argument : arguments)
			{
				if (argument.startsWith(ARGUMENT_FILE_PREFIX))
				{
					String pathname = argument.substring(ARGUMENT_FILE_PREFIX.length());
					argList.addAll(readArgumentFile(pathname, commentPrefix));
				}
				else
					argList.add(argument);
			}
			arguments = argList;
		}

		// Initialise instance fields
		List<Element<E>> elements = new ArrayList<>();
		String argument = null;
		int argumentIndex = 0;
		boolean optionsEnded = false;
		IOption<E> option = null;
		State state = State.OPTION;

		// Parse arguments
		while (state != State.DONE)
		{
			switch (state)
			{
				case OPTION:
					if (argumentIndex < arguments.size())
					{
						argument = arguments.get(argumentIndex);
						if (argument.startsWith(OPTION_PREFIX))
						{
							++argumentIndex;
							argument = argument.substring(OPTION_PREFIX.length());
							if (argument.isEmpty())
							{
								optionsEnded = true;
								state = State.NON_OPTION_ARGUMENT;
							}
							else
							{
								option = null;
								int index = argument.indexOf(OPTION_ARGUMENT_SEPARATOR_CHAR);
								if (index < 0)
								{
									option = getOption(argument);
									if (option == null)
										throw new ParseException(ErrorId.INVALID_OPTION, argument, usageStr);
									if (option.hasArgument())
									{
										argument = null;
										state = State.OPTION_ARGUMENT;
									}
									else
										elements.add(new Element<>(option, null));
								}
								else
								{
									String name = argument.substring(0, index);
									option = getOption(name);
									if (option == null)
										throw new ParseException(ErrorId.INVALID_OPTION, name, usageStr);
									if (!option.hasArgument())
										throw new ParseException(ErrorId.UNEXPECTED_OPTION_ARGUMENT, name, usageStr);
									argument = argument.substring(index + 1);
									state = State.OPTION_ARGUMENT;
								}
							}
						}
						else
							state = State.NON_OPTION_ARGUMENT;
					}
					else
						state = State.DONE;
					break;

				case OPTION_ARGUMENT:
					if (argument == null)
					{
						if (argumentIndex >= arguments.size())
							throw new ParseException(ErrorId.MISSING_OPTION_ARGUMENT, option.getName(), usageStr);
						argument = arguments.get(argumentIndex++);
					}
					elements.add(new Element<>(option, argument));
					state = State.OPTION;
					break;

				case NON_OPTION_ARGUMENT:
					if (argumentIndex < arguments.size())
					{
						argument = arguments.get(argumentIndex);
						if (argument.startsWith(OPTION_PREFIX) && !optionsEnded)
						{
							if (noOptionsAfterNonOptionArguments)
								throw new ParseException(ErrorId.OPTION_AFTER_NON_OPTION_ARGUMENT, usageStr);
							state = State.OPTION;
						}
						else
						{
							elements.add(new Element<E>(argument));
							++argumentIndex;
						}
					}
					else
						state = State.DONE;
					break;

				case DONE:
					// do nothing
					break;
			}
		}

		return elements;
	}

	//------------------------------------------------------------------

	private IOption<E> getOption(String name)
	{
		for (IOption<E> option : options)
		{
			if (option.getName().equals(name))
				return option;
		}
		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	Set<E>	options;
	private	boolean	noOptionsAfterNonOptionArguments;
	private	String	usageStr;

}

//----------------------------------------------------------------------
