/*====================================================================*\

CssRuleSet.java

CSS rule set class.

\*====================================================================*/


// PACKAGE


package common.html;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.exception.UnexpectedRuntimeException;

import common.misc.StringUtils;

//----------------------------------------------------------------------


// CSS RULE SET CLASS


public class CssRuleSet
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	INDENT_STR	=
									StringUtils.createCharString(' ', CssConstants.INDENT_INCREMENT);

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// DECLARATION CLASS


	public static class Decl
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	char	SEPARATOR_CHAR					= ';';
		private static final	char	PROPERTY_VALUE_SEPARATOR_CHAR	= ':';

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Decl(String property,
					String value)
		{
			this.property = property;
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Decl clone()
		{
			try
			{
				return (Decl)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new UnexpectedRuntimeException(e);
			}
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return (property + PROPERTY_VALUE_SEPARATOR_CHAR + " " + value + SEPARATOR_CHAR);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	String	property;
		public	String	value;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public CssRuleSet(String selector)
	{
		this.selector = selector;
		declarations = new ArrayList<>();
	}

	//------------------------------------------------------------------

	public CssRuleSet(String  selector,
					  Decl... declarations)
	{
		this(selector);
		Collections.addAll(this.declarations, declarations);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public CssRuleSet clone()
	{
		try
		{
			CssRuleSet copy = (CssRuleSet)super.clone();
			copy.declarations = new ArrayList<>();
			for (Decl decl : declarations)
				copy.declarations.add(decl.clone());
			return copy;
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return StringUtils.join('\n', true, toStrings(0));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getSelector()
	{
		return selector;
	}

	//------------------------------------------------------------------

	public List<Decl> getDeclarations()
	{
		return Collections.unmodifiableList(declarations);
	}

	//------------------------------------------------------------------

	public void setSelector(String selector)
	{
		this.selector = selector;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public boolean addDeclaration(Decl declaration)
	{
		if (declaration.property == null)
			throw new IllegalArgumentException();

		for (int i = 0; i < declarations.size(); i++)
		{
			Decl decl = declarations.get(i);
			if (decl.property.equals(declaration.property))
			{
				declarations.set(i, declaration);
				return false;
			}
		}
		declarations.add(declaration);
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public boolean addDeclaration(String property,
								  String value)
	{
		if (property == null)
			throw new IllegalArgumentException();

		Decl decl = findDeclaration(property);
		if (decl == null)
		{
			declarations.add(new Decl(property, value));
			return true;
		}
		decl.value = value;
		return false;
	}

	//------------------------------------------------------------------

	public boolean hasProperty(String property)
	{
		return (findDeclaration(property) != null);
	}

	//------------------------------------------------------------------

	public Decl findDeclaration(String property)
	{
		for (Decl decl : declarations)
		{
			if (decl.property.equals(property))
				return decl;
		}
		return null;
	}

	//------------------------------------------------------------------

	public List<String> toStrings()
	{
		return toStrings(0);
	}

	//------------------------------------------------------------------

	public List<String> toStrings(int indent)
	{
		String indentStr = StringUtils.createCharString(' ', indent);
		List<String> strs = new ArrayList<>();
		strs.add(indentStr + selector);
		strs.add(indentStr + CssConstants.BLOCK_START_STR);
		for (Decl decl : declarations)
			strs.add(indentStr + INDENT_STR + decl);
		strs.add(indentStr + CssConstants.BLOCK_END_STR);
		return strs;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	String		selector;
	private	List<Decl>	declarations;

}

//----------------------------------------------------------------------
