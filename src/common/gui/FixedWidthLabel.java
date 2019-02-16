/*====================================================================*\

FixedWidthLabel.java

Fixed-width label class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dimension;

import javax.swing.SwingConstants;

import common.misc.MaxValueMap;

//----------------------------------------------------------------------


// FIXED-WIDTH LABEL CLASS


public abstract class FixedWidthLabel
	extends FLabel
	implements MaxValueMap.IEntry
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected FixedWidthLabel(String text)
	{
		super(text);
		setHorizontalAlignment(SwingConstants.TRAILING);
		MaxValueMap.add(getKey(), this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract String getKey();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MaxValueMap.IEntry interface
////////////////////////////////////////////////////////////////////////

	public int getValue()
	{
		return getPreferredSize().width;
	}

	//------------------------------------------------------------------

	public void setValue(int value)
	{
		setPreferredSize(new Dimension(value, getPreferredSize().height));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
