/*====================================================================*\

IntegerRangeBarPanel.java

Integer range-bar panel class.

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
import common.misc.IntegerRange;

//----------------------------------------------------------------------


// INTEGER RANGE-BAR PANEL CLASS


public class IntegerRangeBarPanel
	extends RangeBarPanel
	implements ChangeListener
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// VERTICAL-BAR PANEL CLASS


	public static class Vertical
		extends IntegerRangeBarPanel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Vertical(String                 labelStr,
						AbstractIntegerSpinner lowerBoundSpinner,
						AbstractIntegerSpinner upperBoundSpinner,
						int                    rangeBarExtent)
		{
			super(Orientation.VERTICAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  null);
		}

		//--------------------------------------------------------------

		public Vertical(String                 labelStr,
						AbstractIntegerSpinner lowerBoundSpinner,
						AbstractIntegerSpinner upperBoundSpinner,
						int                    rangeBarExtent,
						String                 key)
		{
			super(Orientation.VERTICAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  key);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// HORIZONTAL-BAR PANEL CLASS


	public static class Horizontal
		extends IntegerRangeBarPanel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Horizontal(String                 labelStr,
						  AbstractIntegerSpinner lowerBoundSpinner,
						  AbstractIntegerSpinner upperBoundSpinner,
						  int                    rangeBarExtent)
		{
			super(Orientation.HORIZONTAL, labelStr, lowerBoundSpinner, upperBoundSpinner, rangeBarExtent,
				  null);
		}

		//--------------------------------------------------------------

		public Horizontal(String                 labelStr,
						  AbstractIntegerSpinner lowerBoundSpinner,
						  AbstractIntegerSpinner upperBoundSpinner,
						  int                    rangeBarExtent,
						  String                 key)
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

	public IntegerRangeBarPanel(Orientation            orientation,
								String                 labelStr,
								AbstractIntegerSpinner lowerBoundSpinner,
								AbstractIntegerSpinner upperBoundSpinner,
								int                    rangeBarExtent,
								String                 key)
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
			switch (((VerticalRangeBar.RangeBarEvent)event).getBound())
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

	public AbstractIntegerSpinner getLowerBoundSpinner()
	{
		return lowerBoundSpinner;
	}

	//------------------------------------------------------------------

	public AbstractIntegerSpinner getUpperBoundSpinner()
	{
		return upperBoundSpinner;
	}

	//------------------------------------------------------------------

	public int getLowerBound()
	{
		return lowerBoundSpinner.getIntValue();
	}

	//------------------------------------------------------------------

	public int getUpperBound()
	{
		return upperBoundSpinner.getIntValue();
	}

	//------------------------------------------------------------------

	public IntegerRange getRange()
	{
		return new IntegerRange(getLowerBound(), getUpperBound());
	}

	//------------------------------------------------------------------

	public void setLowerBound(int value)
	{
		lowerBoundSpinner.setIntValue(value);
	}

	//------------------------------------------------------------------

	public void setUpperBound(int value)
	{
		upperBoundSpinner.setIntValue(value);
	}

	//------------------------------------------------------------------

	public void setRange(IntegerRange range)
	{
		changing = true;
		setLowerBound(range.lowerBound);
		setUpperBound(range.upperBound);
		lowerBoundSpinner.setMaximum(getUpperBound());
		upperBoundSpinner.setMinimum(getLowerBound());
		changing = false;
	}

	//------------------------------------------------------------------

	public double normaliseValue(int value)
	{
		return (double)value;
	}

	//------------------------------------------------------------------

	public int denormaliseValue(double value)
	{
		return (int)Math.round(value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	AbstractIntegerSpinner	lowerBoundSpinner;
	private	AbstractIntegerSpinner	upperBoundSpinner;

}

//----------------------------------------------------------------------
