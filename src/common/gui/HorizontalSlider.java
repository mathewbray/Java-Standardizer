/*====================================================================*\

HorizontalSlider.java

Horizontal slider class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import common.misc.KeyAction;

//----------------------------------------------------------------------


// HORIZONTAL SLIDER CLASS


public abstract class HorizontalSlider
	extends Slider
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_WIDTH		= 32;
	public static final		int	MIN_HEIGHT		= 12;
	public static final		int	MIN_KNOB_WIDTH	= 12;

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
									 Command.DECREMENT_UNIT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
									 Command.INCREMENT_UNIT),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK),
									 Command.DECREMENT_BLOCK),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK),
									 Command.INCREMENT_BLOCK),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
									 Command.DECREMENT_MAX),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
									 Command.INCREMENT_MAX)
	};

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public HorizontalSlider(int width,
							int height,
							int knobWidth)
	{
		this(width, height, knobWidth, DEFAULT_VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public HorizontalSlider(int    width,
							int    height,
							int    knobWidth,
							double value)
	{
		// Validate arguments
		if ((value < MIN_VALUE) || (value > MAX_VALUE))
			throw new IllegalArgumentException();

		// Initialise instance fields
		this.width = Math.max(MIN_WIDTH, width);
		this.height = Math.max(MIN_HEIGHT, height);
		knobRect = new Rectangle(BORDER_WIDTH, BORDER_WIDTH, Math.max(MIN_KNOB_WIDTH, knobWidth),
								 this.height - 2 * BORDER_WIDTH);
		unitIncrement = 1.0 / (double)widthToExtent(this.width, knobRect.width);
		blockIncrement = 10.0 * unitIncrement;
		dragDeltaX = -1;
		changeListeners = new ArrayList<>();

		// Set component attributes
		setOpaque(true);
		setFocusable(true);

		// Add commands to action map
		KeyAction.create(this, JComponent.WHEN_FOCUSED, this, KEY_COMMANDS);

		// Add listeners
		addFocusListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		// Set value
		forceValue(value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int extentToWidth(int extent,
									int knobWidth)
	{
		return (extent + knobWidth + 2 * BORDER_WIDTH);
	}

	//------------------------------------------------------------------

	public static int widthToExtent(int width,
									int knobWidth)
	{
		return (width - knobWidth - 2 * BORDER_WIDTH);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isAdjusting()
	{
		return (dragDeltaX >= 0);
	}

	//------------------------------------------------------------------

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(width, height);
	}

	//------------------------------------------------------------------

	@Override
	protected double getValue(MouseEvent event)
	{
		return getValue(Math.min(Math.max(BORDER_WIDTH, event.getX() - dragDeltaX),
								 width - knobRect.width - BORDER_WIDTH));
	}

	//------------------------------------------------------------------

	@Override
	protected void forceValue(double value)
	{
		this.value = value;
		knobRect.x = BORDER_WIDTH +
								(int)Math.round(value * (double)widthToExtent(width, knobRect.width));
		repaint();
		fireStateChanged();
	}

	//------------------------------------------------------------------

	@Override
	protected double getKnobValue()
	{
		return getValue(knobRect.x);
	}

	//------------------------------------------------------------------

	@Override
	protected void setDragDeltaCoord(Point   point,
									 boolean centred)
	{
		dragDeltaX = (point == null) ? -1
									 : centred ? knobRect.width / 2 : point.x - knobRect.x;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private double getValue(int x)
	{
		return ((double)(x - BORDER_WIDTH) / (double)widthToExtent(width, knobRect.width));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	int	dragDeltaX;

}

//----------------------------------------------------------------------
