/*====================================================================*\

Slider.java

Slider base class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//----------------------------------------------------------------------


// SLIDER BASE CLASS


public abstract class Slider
	extends JComponent
	implements ActionListener, FocusListener, MouseListener, MouseMotionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		double	MIN_VALUE		= 0.0;
	public static final		double	MAX_VALUE		= 1.0;
	public static final		double	DEFAULT_VALUE	= 0.0;

	protected static final	int	BORDER_WIDTH	= 2;

	// Commands
	protected interface Command
	{
		String	DECREMENT_UNIT	= "decrementUnit";
		String	INCREMENT_UNIT	= "incrementUnit";
		String	DECREMENT_BLOCK	= "decrementBlock";
		String	INCREMENT_BLOCK	= "incrementBlock";
		String	DECREMENT_MAX	= "decrementMax";
		String	INCREMENT_MAX	= "incrementMax";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected Slider()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	protected static double getBoundedValue(double value)
	{
		return Math.min(Math.max(MIN_VALUE, value), MAX_VALUE);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract boolean isAdjusting();

	//------------------------------------------------------------------

	@Override
	protected abstract void paintComponent(Graphics gr);

	//------------------------------------------------------------------

	protected abstract double getValue(MouseEvent event);

	//------------------------------------------------------------------

	protected abstract void forceValue(double value);

	//------------------------------------------------------------------

	protected abstract double getKnobValue();

	//------------------------------------------------------------------

	protected abstract void setDragDeltaCoord(Point   point,
											  boolean centred);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (isEnabled())
		{
			String command = event.getActionCommand();

			if (command.equals(Command.DECREMENT_UNIT))
				onDecrementUnit();

			else if (command.equals(Command.INCREMENT_UNIT))
				onIncrementUnit();

			else if (command.equals(Command.DECREMENT_BLOCK))
				onDecrementBlock();

			else if (command.equals(Command.INCREMENT_BLOCK))
				onIncrementBlock();

			else if (command.equals(Command.DECREMENT_MAX))
				onDecrementMax();

			else if (command.equals(Command.INCREMENT_MAX))
				onIncrementMax();
		}
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
		if (isEnabled())
		{
			requestFocusInWindow();

			if (SwingUtilities.isLeftMouseButton(event))
			{
				Point point = event.getPoint();
				if (knobRect.contains(point))
				{
					setDragDeltaCoord(point, false);
					forceValue(getValue(event));
				}
				else
				{
					Rectangle rect = new Rectangle(BORDER_WIDTH, BORDER_WIDTH, width - 2 * BORDER_WIDTH,
												   height - 2 * BORDER_WIDTH);
					if (rect.contains(point))
					{
						setDragDeltaCoord(point, true);
						forceValue(getValue(event));
						setDragDeltaCoord(point, false);
					}
				}
			}
		}
	}

	//------------------------------------------------------------------

	public void mouseReleased(MouseEvent event)
	{
		if (isEnabled() && SwingUtilities.isLeftMouseButton(event) && isAdjusting())
		{
			setDragDeltaCoord(null, false);
			double value = getKnobValue();
			if (unitIncrement != 0.0)
				value = Math.rint(value / unitIncrement) * unitIncrement;
			forceValue(value);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseMotionListener interface
////////////////////////////////////////////////////////////////////////

	public void mouseDragged(MouseEvent event)
	{
		if (isEnabled() && SwingUtilities.isLeftMouseButton(event) && isAdjusting())
			setValue(getValue(event));
	}

	//------------------------------------------------------------------

	public void mouseMoved(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(width, height);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public double getValue()
	{
		return value;
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

	public void setValue(double value)
	{
		value = getBoundedValue(value);
		if (this.value != value)
			forceValue(value);
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

	public void onUnitIncrement(int numUnits)
	{
		incrementValue(numUnits * unitIncrement);
	}

	//------------------------------------------------------------------

	public void onBlockIncrement(int numBlocks)
	{
		incrementValue(numBlocks * blockIncrement);
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

	protected void fireStateChanged()
	{
		for (int i = changeListeners.size() - 1; i >= 0; i--)
		{
			if (changeEvent == null)
				changeEvent = new ChangeEvent(this);
			changeListeners.get(i).stateChanged(changeEvent);
		}
	}

	//------------------------------------------------------------------

	private void incrementValue(double increment)
	{
		setValue(value + increment);
	}

	//------------------------------------------------------------------

	private void onDecrementUnit()
	{
		incrementValue(-unitIncrement);
	}

	//------------------------------------------------------------------

	private void onIncrementUnit()
	{
		incrementValue(unitIncrement);
	}

	//------------------------------------------------------------------

	private void onDecrementBlock()
	{
		incrementValue(-blockIncrement);
	}

	//------------------------------------------------------------------

	private void onIncrementBlock()
	{
		incrementValue(blockIncrement);
	}

	//------------------------------------------------------------------

	private void onDecrementMax()
	{
		setValue(MIN_VALUE);
	}

	//------------------------------------------------------------------

	private void onIncrementMax()
	{
		setValue(MAX_VALUE);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	int						width;
	protected	int						height;
	protected	Rectangle				knobRect;
	protected	double					unitIncrement;
	protected	double					blockIncrement;
	protected	List<ChangeListener>	changeListeners;
	protected	ChangeEvent				changeEvent;
	protected	double					value;

}

//----------------------------------------------------------------------
