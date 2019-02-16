/*====================================================================*\

TextRendering.java

Text rendering class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.util.Map;

import common.misc.IStringKeyed;

//----------------------------------------------------------------------


// TEXT RENDERING CLASS


public class TextRendering
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	DESKTOP_HINTS_PROPERTY_KEY	= "awt.font.desktophints";

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ANTIALIASING


	public enum Antialiasing
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		DEFAULT
		(
			"default",
			"Default",
			RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT
		),

		NONE
		(
			"none",
			"None",
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
		),

		STANDARD
		(
			"standard",
			"Standard",
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON
		),

		SUBPIXEL_H_RGB
		(
			"subpixelHRgb",
			"Subpixel, horizontal RGB",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
		),

		SUBPIXEL_H_BGR
		(
			"subpixelHBgr",
			"Subpixel, horizontal BGR",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR
		),

		SUBPIXEL_V_RGB
		(
			"subpixelVRgb",
			"Subpixel, vertical RGB",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB
		),

		SUBPIXEL_V_BGR
		(
			"subpixelVBgr",
			"Subpixel, vertical BGR",
			RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR
		);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Antialiasing(String key,
							 String text,
							 Object hintValue)
		{
			this.key = key;
			this.text = text;
			this.hintValue = hintValue;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static RenderingHints.Key getHintKey()
		{
			return RenderingHints.KEY_TEXT_ANTIALIASING;
		}

		//--------------------------------------------------------------

		public static Antialiasing forKey(String key)
		{
			for (Antialiasing value : values())
			{
				if (value.key.equals(key))
					return value;
			}
			return null;
		}

		//--------------------------------------------------------------

		public static Antialiasing forHintValue(Object hintValue)
		{
			for (Antialiasing value : values())
			{
				if (value.hintValue.equals(hintValue))
					return value;
			}
			return null;
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
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Object getHintValue()
		{
			if (this == DEFAULT)
			{
				Object value = getDesktopHint(RenderingHints.KEY_TEXT_ANTIALIASING);
				if (value != null)
					return value;
			}
			return hintValue;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;
		private	Object	hintValue;

	}

	//==================================================================


	// FONT FRACTIONAL METRICS


	public enum FractionalMetrics
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		DEFAULT
		(
			"default",
			"Default",
			RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT
		),

		OFF
		(
			"off",
			"Off",
			RenderingHints.VALUE_FRACTIONALMETRICS_OFF
		),

		ON
		(
			"on",
			"On",
			RenderingHints.VALUE_FRACTIONALMETRICS_ON
		);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private FractionalMetrics(String key,
								  String text,
								  Object hintValue)
		{
			this.key = key;
			this.text = text;
			this.hintValue = hintValue;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static RenderingHints.Key getHintKey()
		{
			return RenderingHints.KEY_FRACTIONALMETRICS;
		}

		//--------------------------------------------------------------

		public static FractionalMetrics forKey(String key)
		{
			for (FractionalMetrics value : values())
			{
				if (value.key.equals(key))
					return value;
			}
			return null;
		}

		//--------------------------------------------------------------

		public static FractionalMetrics forHintValue(Object hintValue)
		{
			for (FractionalMetrics value : values())
			{
				if (value.hintValue.equals(hintValue))
					return value;
			}
			return null;
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
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Object getHintValue()
		{
			if (this == DEFAULT)
			{
				Object value = getDesktopHint(RenderingHints.KEY_FRACTIONALMETRICS);
				if (value != null)
					return value;
			}
			return hintValue;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;
		private	Object	hintValue;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private TextRendering()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Antialiasing getAntialiasing()
	{
		return antialiasing;
	}

	//------------------------------------------------------------------

	public static FractionalMetrics getFractionalMetrics()
	{
		return fractionalMetrics;
	}

	//------------------------------------------------------------------

	public static void setAntialiasing(Antialiasing value)
	{
		if (value != null)
			antialiasing = value;
	}

	//------------------------------------------------------------------

	public static void setFractionalMetrics(FractionalMetrics value)
	{
		if (value != null)
			fractionalMetrics = value;
	}

	//------------------------------------------------------------------

	public static void setHints(Graphics2D gr)
	{
		gr.setRenderingHint(Antialiasing.getHintKey(), antialiasing.getHintValue());
		gr.setRenderingHint(FractionalMetrics.getHintKey(), fractionalMetrics.getHintValue());
	}

	//------------------------------------------------------------------

	public static Object getDesktopHint(Object key)
	{
		Map<?, ?> map =
				(Map<?, ?>)Toolkit.getDefaultToolkit().getDesktopProperty(DESKTOP_HINTS_PROPERTY_KEY);
		return ((map == null) ? null : map.get(key));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Antialiasing		antialiasing		= Antialiasing.DEFAULT;
	private static	FractionalMetrics	fractionalMetrics	= FractionalMetrics.DEFAULT;

}

//----------------------------------------------------------------------
