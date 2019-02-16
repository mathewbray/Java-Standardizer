/*====================================================================*\

IntegerSpinnerSliderPanel.java

Integer spinner and slider panel class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//----------------------------------------------------------------------


// INTEGER SPINNER AND SLIDER PANEL CLASS


public class IntegerSpinnerSliderPanel
	extends SpinnerSliderPanel
	implements ActionListener, ChangeListener
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public IntegerSpinnerSliderPanel(int     value,
									 int     minValue,
									 int     maxValue,
									 int     maxLength,
									 boolean signed,
									 int     sliderWidth,
									 int     sliderHeight,
									 int     sliderKnobWidth,
									 Integer defaultValue)
	{
		this(value, minValue, maxValue, maxLength, signed, sliderWidth, sliderHeight, sliderKnobWidth,
			 defaultValue, null);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public IntegerSpinnerSliderPanel(int     value,
									 int     minValue,
									 int     maxValue,
									 int     maxLength,
									 boolean signed,
									 int     sliderWidth,
									 int     sliderHeight,
									 int     sliderKnobWidth,
									 Integer defaultValue,
									 String  key)
	{
		// Validate arguments
		if (!signed && (value < 0))
			throw new IllegalArgumentException();

		// Set layout manager
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridBag);

		int gridX = 0;

		// Spinner
		spinner = new IntegerSpinner(value, minValue, maxValue, maxLength, signed);
		components.add(spinner.getEditor());
		GuiUtils.setAppFont(Constants.FontKey.TEXT_FIELD, spinner);
		spinner.addChangeListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(spinner, gbc);
		add(spinner);

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

		// Horizontal slider
		slider = new FlatHorizontalSlider(sliderWidth, sliderHeight, sliderKnobWidth,
										  normaliseValue(value));
		components.add(slider);
		slider.addChangeListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(slider, gbc);
		add(slider);

		// Default button
		if (defaultValue != null)
		{
			this.defaultValue = defaultValue;

			JButton defaultButton = new FButton(DEFAULT_STR);
			components.add(defaultButton);
			defaultButton.setMargin(DEFAULT_BUTTON_MARGINS);
			defaultButton.setActionCommand(Command.SET_DEFAULT_VALUE);
			defaultButton.addActionListener(this);

			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 6, 0, 0);
			gridBag.setConstraints(defaultButton, gbc);
			add(defaultButton);
		}

		// Add instance to map
		if (key != null)
			addInstance(key);

		// Set focus traversal policy
		setFocusTraversalPolicy();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(Command.SET_DEFAULT_VALUE))
			setValue(defaultValue);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	public void stateChanged(ChangeEvent event)
	{
		Object eventSource = event.getSource();

		// Spinner
		if (eventSource == spinner)
		{
			if (!adjusting)
				updateValue(spinner.getIntValue());
		}

		// Slider
		else if (eventSource == slider)
		{
			if (!adjusting)
				updateValue(denormaliseValue(slider.getValue()));
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected int getPreferredSpinnerWidth()
	{
		return spinner.getPreferredSize().width;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public IntegerSpinner getSpinner()
	{
		return spinner;
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 */

	public int getValue()
	{
		return spinner.getIntValue();
	}

	//------------------------------------------------------------------

	public void setValue(int value)
	{
		spinner.setIntValue(value);
	}

	//------------------------------------------------------------------

	public void setDefaultValue(int value)
	{
		defaultValue = value;
	}

	//------------------------------------------------------------------

	public double normaliseValue(int value)
	{
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		int minValue = (Integer)model.getMinimum();
		int maxValue = (Integer)model.getMaximum();
		return ((double)(value - minValue) / (double)(maxValue - minValue));
	}

	//------------------------------------------------------------------

	public int denormaliseValue(double value)
	{
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		int minValue = (Integer)model.getMinimum();
		int maxValue = (Integer)model.getMaximum();
		return (minValue + (int)Math.round(value * (double)(maxValue - minValue)));
	}

	//------------------------------------------------------------------

	protected void valueUpdated(int value)
	{
		// may be overridden in subclass
	}

	//------------------------------------------------------------------

	private void updateValue(int value)
	{
		adjusting = true;
		spinner.setIntValue(value);
		slider.setValue(normaliseValue(value));
		valueUpdated(value);
		adjusting = false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int				defaultValue;
	private	IntegerSpinner	spinner;

}

//----------------------------------------------------------------------
