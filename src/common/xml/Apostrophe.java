/*====================================================================*\

Apostrophe.java

Apostrophe enumeration.

\*====================================================================*/


// PACKAGE


package common.xml;

//----------------------------------------------------------------------


// IMPORTS


import common.misc.IStringKeyed;

//----------------------------------------------------------------------


// APOSTROPHE ENUMERATION


public enum Apostrophe
	implements IStringKeyed
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	CHARACTER
	(
		"character",
		"'",
		null
	),

	XML_ENTITY
	(
		"xmlEntity",
		XmlConstants.ENTITY_APOS,
		XmlConstants.EntityName.APOS
	),

	NUMERIC_ENTITY
	(
		"numericEntity",
		"&#39;",
		"#39"
	);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private Apostrophe(String key,
					   String text,
					   String entityName)
	{
		this.key = key;
		this.text = text;
		this.entityName = entityName;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Apostrophe get(int index)
	{
		return (((index >= 0) && (index < values().length)) ? values()[index] : null);
	}

	//------------------------------------------------------------------

	public static Apostrophe forKey(String key)
	{
		for (Apostrophe value : values())
		{
			if (value.key.equals(key))
				return value;
		}
		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IStringKeyed interface
////////////////////////////////////////////////////////////////////////

	public String getKey()
	{
		return key;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return text;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getEntityName()
	{
		return entityName;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	String	key;
	private	String	text;
	private	String	entityName;

}

//----------------------------------------------------------------------
