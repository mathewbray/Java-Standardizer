/*====================================================================*\

DoubleRangeBarPanel.java

Double range-bar panel class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import common.misc.DoubleRange;

//----------------------------------------------------------------------


// DOUBLE RANGE-BAR PANEL CLASS


public class DoubleRangeBarPanel
	extends RangeBarPanel
	implements ChangeListener
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// VERTICAL-BAR PANEL CLASS


	public static class Vertical
		extends DoubleRangeBarPanel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Vertical(String                labelStr,
						AbstractDoubleSpinner lowerBoundSpinner,
						AbstractDoubleSpinner upperBoundSpinner,
						int                   rangeBarExtent)
		{
			super(Orientation.VERTICAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  null);
		}

		//--------------------------------------------------------------

		public Vertical(String                labelStr,
						AbstractDoubleSpinner lowerBoundSpinner,
						AbstractDoubleSpinner upperBoundSpinner,
						int                   rangeBarExtent,
						String                key)
		{
			super(Orientation.VERTICAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  key);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// HORIZONTAL-BAR PANEL CLASS


	public static class Horizontal
		extends DoubleRangeBarPanel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Horizontal(String                labelStr,
						  AbstractDoubleSpinner lowerBoundSpinner,
						  AbstractDoubleSpinner upperBoundSpinner,
						  int                   rangeBarExtent)
		{
			super(Orientation.HORIZONTAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  null);
		}

		//--------------------------------------------------------------

		public Horizontal(String                labelStr,
						  AbstractDoubleSpinner lowerBoundSpinner,
						  AbstractDoubleSpinner upperBoundSpinner,
						  int                   rangeBarExtent,
						  String                key)
		{
			super(Orientation.HORIZONTAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  key);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DoubleRangeBarPanel(Orientation           orientation,
							   String                labelStr,
							   AbstractDoubleSpinner lowerBoundSpinner,
							   AbstractDoubleSpinner upperBoundSpinner,
							   int                   rangeBarExtent,
							   String                key)
	{
		// Call superclass constructor
		super(orientation);

		// Initialise instance fields
		this.lowerBoundSpinner = lowerBoundSpinner;
		this.upperBoundSpinner = upperBoundSpinner;
		if (rangeBarExtent > 0)
		{
			createRangeBar(rangeBarExtent);
			rangeBar.addChangeListener(this);
		}

		// Initialise bounds
		setLowerBound(denormaliseValue(0.0));
		setUpperBound(denormaliseValue(1.0));

		// Set layout manager
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridBag);

		int gridX = 0;

		// Panel: spinners
		spinnerPanel = new SpinnerPanel(lowerBoundSpinner, upperBoundSpinner, labelStr);
		lowerBoundSpinner.addChangeListener(this);
		upperBoundSpinner.addChangeListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(spinnerPanel, gbc);
		add(spinnerPanel);

		// Filler
		filler = GuiUtils.createFiller(MIN_FILLER_WIDTH, 1);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(filler, gbc);
		add(filler);

		// Button: range bar
		if (rangeBar != null)
		{
			createRangeBarButton();

			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 8);
			gridBag.setConstraints(rangeBarButton, gbc);
			add(rangeBarButton);
		}

		// Button: set lower bound to upper bound
		JButton setToUpperButton = new JButton(RangeIcon.SET_LOWER_TO_UPPER);
		setToUpperButton.setMargin(BUTTON_MARGINS);
		setToUpperButton.setToolTipText(SET_LOWER_TO_UPPER_TOOLTIP_STR);
		setToUpperButton.setActionCommand(Command.SET_LOWER_BOUND_TO_UPPER_BOUND);
		setToUpperButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(setToUpperButton, gbc);
		add(setToUpperButton);

		// Button: set upper bound to lower bound
		JButton setToLowerButton = new JButton(RangeIcon.SET_UPPER_TO_LOWER);
		setToLowerButton.setMargin(BUTTON_MARGINS);
		setToLowerButton.setToolTipText(SET_UPPER_TO_LOWER_TOOLTIP_STR);
		setToLowerButton.setActionCommand(Command.SET_UPPER_BOUND_TO_LOWER_BOUND);
		setToLowerButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 4, 0, 0);
		gridBag.setConstraints(setToLowerButton, gbc);
		add(setToLowerButton);

		// Add instance to map
		if (key != null)
			addInstance(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	public void stateChanged(ChangeEvent event)
	{
		Object eventSource = event.getSource();

		// Lower bound spinner
		if (eventSource == lowerBoundSpinner)
		{
			if (!changing)
				upperBoundSpinner.setMinimum(getLowerBound());
		}

		// Upper bound spinner
		else if (eventSource == upperBoundSpinner)
		{
			if (!changing)
				lowerBoundSpinner.setMaximum(getUpperBound());
		}

		// Range bar
		else if (eventSource == rangeBar)
		{
			switch (((RangeBar.RangeBarEvent)event).getBound())
			{
				case NONE:
					setLowerBound(denormaliseValue(rangeBar.getRange().lowerBound));
					setUpperBound(denormaliseValue(rangeBar.getRange().upperBound));
					break;

				case LOWER:
					setLowerBound(denormaliseValue(rangeBar.getRange().lowerBound));
					break;

				case UPPER:
					setUpperBound(denormaliseValue(rangeBar.getRange().upperBound));
					break;
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected void onShowRangeBar()
	{
		rangeBar.setRange(new DoubleRange(normaliseValue(getLowerBound()),
										  normaliseValue(getUpperBound())));
		showDialog();
	}

	//------------------------------------------------------------------

	@Override
	protected void onSetLowerBoundToUpperBound()
	{
		setLowerBound(getUpperBound());
	}

	//------------------------------------------------------------------

	@Override
	protected void onSetUpperBoundToLowerBound()
	{
		setUpperBound(getLowerBound());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public AbstractDoubleSpinner getLowerBoundSpinner()
	{
		return lowerBoundSpinner;
	}

	//------------------------------------------------------------------

	public AbstractDoubleSpinner getUpperBoundSpinner()
	{
		return upperBoundSpinner;
	}

	//------------------------------------------------------------------

	public double getLowerBound()
	{
		return lowerBoundSpinner.getDoubleValue();
	}

	//------------------------------------------------------------------

	public double getUpperBound()
	{
		return upperBoundSpinner.getDoubleValue();
	}

	//------------------------------------------------------------------

	public DoubleRange getRange()
	{
		return new DoubleRange(getLowerBound(), getUpperBound());
	}

	//------------------------------------------------------------------

	public void setLowerBound(double value)
	{
		lowerBoundSpinner.setDoubleValue(value);
	}

	//------------------------------------------------------------------

	public void setUpperBound(double value)
	{
		upperBoundSpinner.setDoubleValue(value);
	}

	//------------------------------------------------------------------

	public void setRange(DoubleRange range)
	{
		changing = true;
		setLowerBound(range.lowerBound);
		setUpperBound(range.upperBound);
		lowerBoundSpinner.setMaximum(getUpperBound());
		upperBoundSpinner.setMinimum(getLowerBound());
		changing = false;
	}

	//------------------------------------------------------------------

	public double normaliseValue(double value)
	{
		return value;
	}

	//------------------------------------------------------------------

	public double denormaliseValue(double value)
	{
		return value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	AbstractDoubleSpinner	lowerBoundSpinner;
	private	AbstractDoubleSpinner	upperBoundSpinner;

}

//----------------------------------------------------------------------
