/*====================================================================*\

CssMediaRule.java

CSS media rule class.

\*====================================================================*/


// PACKAGE


package common.html;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import common.exception.UnexpectedRuntimeException;

import common.misc.IStringKeyed;
import common.misc.StringUtils;

//----------------------------------------------------------------------


// CSS MEDIA RULE CLASS


public class CssMediaRule
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	MEDIA_KEYWORD	= "@media";

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// MEDIA TYPES


	public enum MediaType
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		PRINT   ("print"),
		SCREEN  ("screen");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private MediaType(String key)
		{
			this.key = key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	key;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public CssMediaRule(MediaType mediaType)
	{
		this(EnumSet.of(mediaType));
	}

	//------------------------------------------------------------------

	public CssMediaRule(EnumSet<MediaType> mediaTypes)
	{
		this.mediaTypes = mediaTypes.clone();
		ruleSets = new ArrayList<>();
	}

	//------------------------------------------------------------------

	public CssMediaRule(EnumSet<MediaType> mediaTypes,
						CssRuleSet...      ruleSets)
	{
		this(mediaTypes);
		Collections.addAll(this.ruleSets, ruleSets);
	}

	//------------------------------------------------------------------

	public CssMediaRule(EnumSet<MediaType> mediaTypes,
						List<CssRuleSet>   ruleSets)
	{
		this(mediaTypes);
		this.ruleSets.addAll(ruleSets);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public CssMediaRule clone()
	{
		try
		{
			CssMediaRule copy = (CssMediaRule)super.clone();
			copy.mediaTypes = mediaTypes.clone();
			copy.ruleSets = new ArrayList<>();
			for (CssRuleSet ruleSet : ruleSets)
				copy.ruleSets.add(ruleSet.clone());
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

	public EnumSet<MediaType> getMediaTypes()
	{
		return mediaTypes.clone();
	}

	//------------------------------------------------------------------

	public List<CssRuleSet> getRuleSets()
	{
		return Collections.unmodifiableList(ruleSets);
	}

	//------------------------------------------------------------------

	public void setRuleSets(List<CssRuleSet> ruleSets)
	{
		this.ruleSets.clear();
		this.ruleSets.addAll(ruleSets);
	}

	//------------------------------------------------------------------

	public void addRuleSet(CssRuleSet ruleSet)
	{
		ruleSets.add(ruleSet);
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
		List<String> mediaTypeStrs = new ArrayList<>();
		for (MediaType mediaType : mediaTypes)
			mediaTypeStrs.add(mediaType.getKey());
		List<String> strs = new ArrayList<>();
		strs.add(indentStr + MEDIA_KEYWORD + " " + StringUtils.join(", ", mediaTypeStrs));
		strs.add(indentStr + CssConstants.BLOCK_START_STR);
		for (CssRuleSet ruleSet : ruleSets)
			strs.addAll(ruleSet.toStrings(indent + CssConstants.INDENT_INCREMENT));
		strs.add(indentStr + CssConstants.BLOCK_END_STR);
		return strs;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	EnumSet<MediaType>	mediaTypes;
	private	List<CssRuleSet>	ruleSets;

}

//----------------------------------------------------------------------
