/*====================================================================*\

DoubleSpinnerSliderPanel.java

Double spinner and slider panel class.

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

import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//----------------------------------------------------------------------


// DOUBLE SPINNER AND SLIDER PANEL CLASS


public class DoubleSpinnerSliderPanel
	extends SpinnerSliderPanel
	implements ActionListener, ChangeListener
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public DoubleSpinnerSliderPanel(double       value,
									double       minValue,
									double       maxValue,
									double       stepSize,
									int          maxLength,
									NumberFormat format,
									boolean      signed,
									int          sliderWidth,
									int          sliderHeight,
									int          sliderKnobWidth,
									Double       defaultValue)
	{
		this(value, minValue, maxValue, stepSize, maxLength, format, signed, sliderWidth, sliderHeight,
			 sliderKnobWidth, defaultValue, null);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public DoubleSpinnerSliderPanel(double       value,
									double       minValue,
									double       maxValue,
									double       stepSize,
									int          maxLength,
									NumberFormat format,
									boolean      signed,
									int          sliderWidth,
									int          sliderHeight,
									int          sliderKnobWidth,
									Double       defaultValue,
									String       key)
	{
		// Validate arguments
		if (!signed && (value < 0.0))
			throw new IllegalArgumentException();

		// Set layout manager
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gridBag);

		int gridX = 0;

		// Spinner
		spinner = new DoubleSpinner(value, minValue, maxValue, stepSize, maxLength, format, signed);
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
				updateValue(spinner.getDoubleValue());
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

	public DoubleSpinner getSpinner()
	{
		return spinner;
	}

	//------------------------------------------------------------------

	/**
	 * @throws NumberFormatException
	 */

	public double getValue()
	{
		return spinner.getDoubleValue();
	}

	//------------------------------------------------------------------

	public void setValue(double value)
	{
		spinner.setDoubleValue(value);
	}

	//------------------------------------------------------------------

	public void setDefaultValue(double value)
	{
		defaultValue = value;
	}

	//------------------------------------------------------------------

	public double normaliseValue(double value)
	{
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		double minValue = (Double)model.getMinimum();
		double maxValue = (Double)model.getMaximum();
		return ((value - minValue) / (maxValue - minValue));
	}

	//------------------------------------------------------------------

	public double denormaliseValue(double value)
	{
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		double minValue = (Double)model.getMinimum();
		double maxValue = (Double)model.getMaximum();
		return (minValue + value * (maxValue - minValue));
	}

	//------------------------------------------------------------------

	protected void valueUpdated(double value)
	{
		// may be overridden in subclass
	}

	//------------------------------------------------------------------

	private void updateValue(double value)
	{
		adjusting = true;
		spinner.setDoubleValue(value);
		slider.setValue(normaliseValue(value));
		valueUpdated(value);
		adjusting = false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	double			defaultValue;
	private	DoubleSpinner	spinner;

}

//----------------------------------------------------------------------
