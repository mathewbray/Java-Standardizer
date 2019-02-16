/*====================================================================*\

RangeBarPanel.java

Range bar panel base class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

//----------------------------------------------------------------------


// RANGE BAR PANEL BASE CLASS


public abstract class RangeBarPanel
	extends JPanel
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		char	LABEL_INDENT_PREFIX_CHAR	= '\t';
	public static final		String	LABEL_INDENT_PREFIX	= Character.toString(LABEL_INDENT_PREFIX_CHAR);

	public enum Orientation
	{
		VERTICAL,
		HORIZONTAL
	}

	protected static final	int	MIN_FILLER_WIDTH	= 8;

	protected static final	String	RANGE_BAR_TOOLTIP_STR			= "Display range bar";
	protected static final	String	SET_LOWER_TO_UPPER_TOOLTIP_STR	= "Set lower bound to upper bound";
	protected static final	String	SET_UPPER_TO_LOWER_TOOLTIP_STR	= "Set upper bound to lower bound";

	protected static final	Insets	BUTTON_MARGINS				= new Insets(1, 2, 1, 2);
	protected static final	Insets	BUTTON_MARGINS_VERTICAL		= new Insets(1, 2, 1, 2);
	protected static final	Insets	BUTTON_MARGINS_HORIZONTAL	= new Insets(1, 2, 1, 2);

	// Commands
	protected interface Command
	{
		String	SHOW_RANGE_BAR					= "showRangeBar";
		String	SET_LOWER_BOUND_TO_UPPER_BOUND	= "setLowerBoundToUpperBound";
		String	SET_UPPER_BOUND_TO_LOWER_BOUND	= "setUpperBoundToLowerBound";
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// SPINNER PANEL CLASS


	protected static class SpinnerPanel
		extends JPanel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		protected SpinnerPanel(JComponent lowerBoundSpinner,
							   JComponent upperBoundSpinner,
							   String     labelStr)
		{
			// Initialise instance fields
			this.lowerBoundSpinner = lowerBoundSpinner;

			// Set layout manager
			GridBagLayout gridBag = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			setLayout(gridBag);

			int gridX = 0;

			// Spinner: lower bound
			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(lowerBoundSpinner, gbc);
			add(lowerBoundSpinner);

			// Label
			if (labelStr != null)
			{
				label = new Label(labelStr);

				gbc.gridx = gridX++;
				gbc.gridy = 0;
				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 0.0;
				gbc.weighty = 0.0;
				gbc.anchor = GridBagConstraints.LINE_START;
				gbc.fill = GridBagConstraints.NONE;
				gbc.insets = new Insets(0, 0, 0, 0);
				gridBag.setConstraints(label, gbc);
				add(label);
			}

			// Spinner: upper bound
			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, (label == null) ? 6 : 0, 0, 0);
			gridBag.setConstraints(upperBoundSpinner, gbc);
			add(upperBoundSpinner);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private int getUpperBoundSpinnerX()
		{
			return (lowerBoundSpinner.getPreferredSize().width + label.getPreferredSize().width);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	JComponent	lowerBoundSpinner;
		private	Label		label;

	}

	//==================================================================


	// LABEL CLASS


	private static class Label
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MARGIN	= 4;
		private static final	int	INDENT	= 6;

		private static final	Color	BACKGROUND_COLOUR	= new Color(248, 232, 192);
		private static final	Color	TEXT_COLOUR			= Color.BLACK;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Label(String text)
		{
			// Initialise margin and alignment; find start of text
			margin = 2 * MARGIN;
			int index = 0;
			if (text.startsWith(LABEL_INDENT_PREFIX))
			{
				rightAligned = true;
				while (true)
				{
					if (text.charAt(index) != LABEL_INDENT_PREFIX_CHAR)
						break;
					++index;
					margin += INDENT;
				}
			}
			this.text = text.substring(index);

			// Initialise width and height of text
			GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
			FontMetrics fontMetrics = getFontMetrics(getFont());
			textWidth = fontMetrics.stringWidth(text);
			textHeight = fontMetrics.getAscent() + fontMetrics.getDescent();

			// Set component attributes
			if (!rightAligned)
				setBackground(BACKGROUND_COLOUR);
			setOpaque(false);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(margin + textWidth, textHeight);
		}

		//--------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Draw background
			Rectangle rect = gr.getClipBounds();
			gr.setColor(getBackground());
			gr.fillRect(rect.x, rect.y, rect.width, rect.height);

			// Set rendering hints for text antialiasing and fractional metrics
			TextRendering.setHints((Graphics2D)gr);

			// Draw text
			FontMetrics fontMetrics = gr.getFontMetrics();
			int width = getWidth();
			int x = rightAligned ? width - (textWidth + MARGIN) : (margin + 1) / 2;
			int y = fontMetrics.getAscent();
			gr.setColor(TEXT_COLOUR);
			gr.drawString(text, x, y);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	text;
		private	boolean	rightAligned;
		private	int		margin;
		private	int		textWidth;
		private	int		textHeight;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected RangeBarPanel(Orientation orientation)
	{
		this.orientation = orientation;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void removeInstances(String key)
	{
		instanceMap.remove(key);
	}

	//------------------------------------------------------------------

	public static void align(String key)
	{
		List<RangeBarPanel> instances = instanceMap.get(key);
		if (instances != null)
		{
			// Fix up width of label between spinners
			int maxWidth = 0;
			for (RangeBarPanel panel : instances)
			{
				if (panel.spinnerPanel.label != null)
				{
					int width = panel.spinnerPanel.getUpperBoundSpinnerX();
					if (maxWidth < width)
						maxWidth = width;
				}
			}
			if (maxWidth > 0)
			{
				for (RangeBarPanel panel : instances)
				{
					if (panel.spinnerPanel.label != null)
						panel.spinnerPanel.label.margin +=
													maxWidth - panel.spinnerPanel.getUpperBoundSpinnerX();
				}
			}

			// Fix up width of filler between upper-bound spinner and range-bar button
			for (RangeBarPanel panel : instances)
			{
				int width = panel.spinnerPanel.getPreferredSize().width;
				if (maxWidth < width)
					maxWidth = width;
			}
			for (RangeBarPanel panel : instances)
			{
				int width = MIN_FILLER_WIDTH + maxWidth - panel.spinnerPanel.getPreferredSize().width;
				Dimension fillerSize = new Dimension(width, 1);
				panel.filler.changeShape(fillerSize, fillerSize, fillerSize);
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract void onShowRangeBar();

	//------------------------------------------------------------------

	protected abstract void onSetLowerBoundToUpperBound();

	//------------------------------------------------------------------

	protected abstract void onSetUpperBoundToLowerBound();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.SHOW_RANGE_BAR))
			onShowRangeBar();

		else if (command.equals(Command.SET_LOWER_BOUND_TO_UPPER_BOUND))
			onSetLowerBoundToUpperBound();

		else if (command.equals(Command.SET_UPPER_BOUND_TO_LOWER_BOUND))
			onSetUpperBoundToLowerBound();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setIncrements(double unitIncrement,
							  double blockIncrement)
	{
		rangeBar.setUnitIncrement(unitIncrement);
		rangeBar.setBlockIncrement(blockIncrement);
	}

	//------------------------------------------------------------------

	protected void createRangeBar(int extent)
	{
		switch (orientation)
		{
			case VERTICAL:
				rangeBar = new VerticalRangeBar(VerticalRangeBar.extentToHeight(extent));
				break;

			case HORIZONTAL:
				rangeBar = new HorizontalRangeBar(HorizontalRangeBar.extentToWidth(extent));
				break;
		}
	}

	//------------------------------------------------------------------

	protected void createRangeBarButton()
	{
		switch (orientation)
		{
			case VERTICAL:
				rangeBarButton = new JButton(RangeIcon.RANGE_VERTICAL);
				rangeBarButton.setMargin(BUTTON_MARGINS_VERTICAL);
				break;

			case HORIZONTAL:
				rangeBarButton = new JButton(RangeIcon.RANGE_HORIZONTAL);
				rangeBarButton.setMargin(BUTTON_MARGINS_HORIZONTAL);
				break;
		}
		rangeBarButton.setToolTipText(RANGE_BAR_TOOLTIP_STR);
		rangeBarButton.setActionCommand(Command.SHOW_RANGE_BAR);
		rangeBarButton.addActionListener(this);
	}

	//------------------------------------------------------------------

	protected void showDialog()
	{
		Window window = GuiUtils.getWindow(this);
		switch (orientation)
		{
			case VERTICAL:
			{
				RangeBarDialog dialog = new RangeBarDialog.Vertical(window, rangeBar);
				Rectangle rect = new Rectangle(rangeBarButton.getLocationOnScreen(),
											   rangeBarButton.getSize());
				dialog.setLocation(GuiUtils.getComponentLocation(dialog, rect));
				dialog.setVisible(true);
				break;
			}

			case HORIZONTAL:
			{
				RangeBarDialog dialog = new RangeBarDialog.Horizontal(window, rangeBar);
				Point location = spinnerPanel.lowerBoundSpinner.getLocationOnScreen();
				location.y += spinnerPanel.lowerBoundSpinner.getHeight() - 1;
				dialog.setLocation(GuiUtils.getComponentLocation(dialog, location));
				dialog.setVisible(true);
				break;
			}
		}
	}

	//------------------------------------------------------------------

	protected void addInstance(String key)
	{
		List<RangeBarPanel> instances = instanceMap.get(key);
		if (instances == null)
		{
			instances = new ArrayList<>();
			instanceMap.put(key, instances);
		}
		instances.add(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Map<String, List<RangeBarPanel>>	instanceMap	=   new Hashtable<>();

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	Orientation		orientation;
	protected	SpinnerPanel	spinnerPanel;
	protected	JButton			rangeBarButton;
	protected	RangeBar		rangeBar;
	protected	Box.Filler		filler;
	protected	boolean			changing;

}

//----------------------------------------------------------------------
