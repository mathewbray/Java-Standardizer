/*====================================================================*\

RangeBar.java

Range bar base class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import common.misc.DoubleRange;
import common.misc.KeyAction;

//----------------------------------------------------------------------


// RANGE BAR BASE CLASS


public abstract class RangeBar
	extends JComponent
	implements ActionListener, FocusListener, MouseListener, MouseMotionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		double	MIN_VALUE	= 0.0;
	public static final		double	MAX_VALUE	= 1.0;

	public enum Bound
	{
		NONE,
		LOWER,
		UPPER
	}

	protected static final	int	BORDER_WIDTH	= 2;

	protected static final	Color	BACKGROUND_COLOUR		= new Color(232, 240, 232);
	protected static final	Color	RANGE_BACKGROUND_COLOUR	= new Color(240, 224, 176);
	protected static final	Color	INNER_BORDER_COLOUR		= new Color(144, 176, 144);
	protected static final	Color	END_REGION_COLOUR		= new Color(128, 144, 128);
	protected static final	Color	RANGE_COLOUR			= new Color(240, 160, 64);
	protected static final	Color	MARKER_COLOUR			= END_REGION_COLOUR;

	// Default values
	protected static final	DoubleRange	DEFAULT_RANGE	= new DoubleRange();

	// Commands
	private interface Command
	{
		String	DECREMENT_UNIT_LOWER	= "decrementUnitLower";
		String	INCREMENT_UNIT_LOWER	= "incrementUnitLower";
		String	DECREMENT_BLOCK_LOWER	= "decrementBlockLower";
		String	INCREMENT_BLOCK_LOWER	= "incrementBlockLower";
		String	DECREMENT_UNIT_UPPER	= "decrementUnitUpper";
		String	INCREMENT_UNIT_UPPER	= "incrementUnitUpper";
		String	DECREMENT_BLOCK_UPPER	= "decrementBlockUpper";
		String	INCREMENT_BLOCK_UPPER	= "incrementBlockUpper";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
									 Command.DECREMENT_UNIT_LOWER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
									 Command.INCREMENT_UNIT_LOWER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK),
									 Command.DECREMENT_BLOCK_LOWER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK),
									 Command.INCREMENT_BLOCK_LOWER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
									 Command.DECREMENT_UNIT_UPPER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
									 Command.INCREMENT_UNIT_UPPER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK),
									 Command.DECREMENT_BLOCK_UPPER),
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK),
									 Command.INCREMENT_BLOCK_UPPER)
	};

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// RANGE BAR EVENT CLASS


	public static class RangeBarEvent
		extends ChangeEvent
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private RangeBarEvent(Object source)
		{
			super(source);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Bound getBound()
		{
			return bound;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	Bound	bound;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected RangeBar()
	{
		// Initialise instance fields
		borderColour = INNER_BORDER_COLOUR;

		// Set component attributes
		setOpaque(true);
		setFocusable(true);

		// Add commands to action map
		KeyAction.create(this, JComponent.WHEN_FOCUSED, this, KEY_COMMANDS);

		// Add listeners
		addFocusListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	protected static DoubleRange getBoundedRange(DoubleRange range)
	{
		return new DoubleRange(Math.min(Math.max(MIN_VALUE, range.lowerBound), MAX_VALUE),
							   Math.min(Math.max(MIN_VALUE, range.upperBound), MAX_VALUE));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract void setActiveBound(MouseEvent event);

	//------------------------------------------------------------------

	protected abstract DoubleRange getRange(MouseEvent event);

	//------------------------------------------------------------------

	protected abstract void setRange(DoubleRange range,
									 Bound       bound);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.DECREMENT_UNIT_LOWER))
			onDecrementUnitLower();

		else if (command.equals(Command.INCREMENT_UNIT_LOWER))
			onIncrementUnitLower();

		else if (command.equals(Command.DECREMENT_BLOCK_LOWER))
			onDecrementBlockLower();

		else if (command.equals(Command.INCREMENT_BLOCK_LOWER))
			onIncrementBlockLower();

		else if (command.equals(Command.DECREMENT_UNIT_UPPER))
			onDecrementUnitUpper();

		else if (command.equals(Command.INCREMENT_UNIT_UPPER))
			onIncrementUnitUpper();

		else if (command.equals(Command.DECREMENT_BLOCK_UPPER))
			onDecrementBlockUpper();

		else if (command.equals(Command.INCREMENT_BLOCK_UPPER))
			onIncrementBlockUpper();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FocusListener interface
////////////////////////////////////////////////////////////////////////

	public void focusGained(FocusEvent event)
	{
		repaint();
	}

	//------------------------------------------------------------------

	public void focusLost(FocusEvent event)
	{
		repaint();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseListener interface
////////////////////////////////////////////////////////////////////////

	public void mouseClicked(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void mouseEntered(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void mouseExited(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void mousePressed(MouseEvent event)
	{
		requestFocusInWindow();

		if (SwingUtilities.isLeftMouseButton(event))
		{
			setActiveBound(event);
			forceRange(getRange(event), activeBound);
		}
	}

	//------------------------------------------------------------------

	public void mouseReleased(MouseEvent event)
	{
		if (SwingUtilities.isLeftMouseButton(event) && isAdjusting())
		{
			DoubleRange range = getRange(event);
			Bound bound = activeBound;
			activeBound = Bound.NONE;
			if (unitIncrement != 0.0)
			{
				range.lowerBound = Math.rint(range.lowerBound / unitIncrement) * unitIncrement;
				range.upperBound = Math.rint(range.upperBound / unitIncrement) * unitIncrement;
			}
			forceRange(range, bound);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseMotionListener interface
////////////////////////////////////////////////////////////////////////

	public void mouseDragged(MouseEvent event)
	{
		if (SwingUtilities.isLeftMouseButton(event) && isAdjusting())
			setRange(getRange(event), activeBound);
	}

	//------------------------------------------------------------------

	public void mouseMoved(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public DoubleRange getRange()
	{
		return range;
	}

	//------------------------------------------------------------------

	public double getUnitIncrement()
	{
		return unitIncrement;
	}

	//------------------------------------------------------------------

	public double getBlockIncrement()
	{
		return blockIncrement;
	}

	//------------------------------------------------------------------

	public boolean isAdjusting()
	{
		return (activeBound != Bound.NONE);
	}

	//------------------------------------------------------------------

	public void setRange(DoubleRange range)
	{
		setRange(range, Bound.NONE);
	}

	//------------------------------------------------------------------

	public void setUnitIncrement(double increment)
	{
		unitIncrement = increment;
	}

	//------------------------------------------------------------------

	public void setBlockIncrement(double increment)
	{
		blockIncrement = increment;
	}

	//------------------------------------------------------------------

	public void setBorderColour(Color colour)
	{
		borderColour = colour;
	}

	//------------------------------------------------------------------

	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	//------------------------------------------------------------------

	public void removeChangeListener(ChangeListener listener)
	{
		changeListeners.remove(listener);
	}

	//------------------------------------------------------------------

	public ChangeListener[] getChangeListeners()
	{
		return changeListeners.toArray(new ChangeListener[changeListeners.size()]);
	}

	//------------------------------------------------------------------

	protected void fireStateChanged(Bound bound)
	{
		if (changeEvent == null)
			changeEvent = new RangeBarEvent(this);
		changeEvent.bound = bound;
		for (int i = changeListeners.size() - 1; i >= 0; i--)
			changeListeners.get(i).stateChanged(changeEvent);
	}

	//------------------------------------------------------------------

	private void forceRange(DoubleRange range,
							Bound       bound)
	{
		if (range != null)
		{
			this.range = null;
			setRange(range, bound);
		}
	}

	//------------------------------------------------------------------

	private void incrementLowerBound(double increment)
	{
		setRange(new DoubleRange(Math.min(range.lowerBound + increment, range.upperBound),
								 range.upperBound),
				 Bound.LOWER);
	}

	//------------------------------------------------------------------

	private void incrementUpperBound(double increment)
	{
		setRange(new DoubleRange(range.lowerBound,
								 Math.max(range.lowerBound, range.upperBound + increment)),
				 Bound.UPPER);
	}

	//------------------------------------------------------------------

	private void onDecrementUnitLower()
	{
		incrementLowerBound(-unitIncrement);
	}

	//------------------------------------------------------------------

	private void onIncrementUnitLower()
	{
		incrementLowerBound(unitIncrement);
	}

	//------------------------------------------------------------------

	private void onDecrementBlockLower()
	{
		incrementLowerBound(-blockIncrement);
	}

	//------------------------------------------------------------------

	private void onIncrementBlockLower()
	{
		incrementLowerBound(blockIncrement);
	}

	//------------------------------------------------------------------

	private void onDecrementUnitUpper()
	{
		incrementUpperBound(-unitIncrement);
	}

	//------------------------------------------------------------------

	private void onIncrementUnitUpper()
	{
		incrementUpperBound(unitIncrement);
	}

	//------------------------------------------------------------------

	private void onDecrementBlockUpper()
	{
		incrementUpperBound(-blockIncrement);
	}

	//------------------------------------------------------------------

	private void onIncrementBlockUpper()
	{
		incrementUpperBound(blockIncrement);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	Bound					activeBound;
	protected	double					unitIncrement;
	protected	double					blockIncrement;
	protected	Color					borderColour;
	protected	List<ChangeListener>	changeListeners;
	protected	RangeBarEvent			changeEvent;
	protected	DoubleRange				range;

}

//----------------------------------------------------------------------
