/*====================================================================*\

DateSelectionPanel.java

Date selection panel class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;

import java.util.Calendar;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import common.misc.Date;
import common.misc.DateUtils;
import common.misc.KeyAction;
import common.misc.ModernCalendar;

//----------------------------------------------------------------------


// DATE SELECTION PANEL CLASS


public class DateSelectionPanel
	extends JPanel
	implements ActionListener, MouseListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_YEAR	= 1600;
	public static final		int	MAX_YEAR	= 3999;

	private static final	int	MIN_MONTH	= 0;
	private static final	int	MAX_MONTH	= 11;

	private static final	int	MIN_DAY	= 0;

	private static final	int	NUM_DAYS_IN_WEEK	= 7;

	private static final	int	MIN_MONTH_NAME_LENGTH	= 3;
	private static final	int	MIN_DAY_NAME_LENGTH		= 2;

	private static final	int	MONTH_LABEL_HORIZONTAL_MARGIN	= 4;
	private static final	int	MONTH_LABEL_VERTICAL_MARGIN		= 1;

	private static final	Color	MONTH_BACKGROUND_COLOUR	= new Color(232, 208, 156);

	private static final	String	PREVIOUS_MONTH_TOOLTIP_STR	= "Previous month (PageUp)";
	private static final	String	NEXT_MONTH_TOOLTIP_STR		= "Next month (PageDown)";
	private static final	String	PREVIOUS_YEAR_TOOLTIP_STR	= "Previous year (Ctrl+PageUp)";
	private static final	String	NEXT_YEAR_TOOLTIP_STR		= "Next year (Ctrl+PageDown)";

	private static final	String	PROTOTYPE_YEAR_STR	= "0000";

	// Icons
	private static final	ImageIcon	ANGLE_SINGLE_LEFT_ICON	=
															new ImageIcon(ImageData.ANGLE_SINGLE_LEFT);
	private static final	ImageIcon	ANGLE_SINGLE_RIGHT_ICON	=
															new ImageIcon(ImageData.ANGLE_SINGLE_RIGHT);
	private static final	ImageIcon	ANGLE_DOUBLE_LEFT_ICON	=
															new ImageIcon(ImageData.ANGLE_DOUBLE_LEFT);
	private static final	ImageIcon	ANGLE_DOUBLE_RIGHT_ICON	=
															new ImageIcon(ImageData.ANGLE_DOUBLE_RIGHT);

	// Commands
	private interface Command
	{
		String	PREVIOUS_MONTH		= "previousMonth";
		String	NEXT_MONTH			= "nextMonth";
		String	PREVIOUS_YEAR		= "previousYear";
		String	NEXT_YEAR			= "nextYear";
		String	EDIT_MONTH_YEAR		= "editMonthYear";

		String	SELECT_UP_UNIT		= "selectUpUnit";
		String	SELECT_DOWN_UNIT	= "selectDownUnit";
		String	SELECT_UP_MAX		= "selectUpMax";
		String	SELECT_DOWN_MAX		= "selectDownMax";
		String	SELECT_LEFT_UNIT	= "selectLeftUnit";
		String	SELECT_RIGHT_UNIT	= "selectRightUnit";
		String	SELECT_LEFT_MAX		= "selectLeftMax";
		String	SELECT_RIGHT_MAX	= "selectRightMax";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
			Command.PREVIOUS_MONTH
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
			Command.NEXT_MONTH
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_DOWN_MASK),
			Command.PREVIOUS_YEAR
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_DOWN_MASK),
			Command.NEXT_YEAR
		),
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK),
			Command.EDIT_MONTH_YEAR
		)
	};

	// Image data
	private interface ImageData
	{
		// Image: angleSingleL-13x10.png
		byte[]	ANGLE_SINGLE_LEFT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6F, (byte)0xEE, (byte)0xD4,
			(byte)0xC4, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4D, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x3F,
			(byte)0x03, (byte)0x2E, (byte)0xCC, (byte)0xC0, (byte)0x10, (byte)0x9F, (byte)0x07, (byte)0xC4,
			(byte)0x8F, (byte)0x81, (byte)0xD8, (byte)0x0E, (byte)0x45, (byte)0x1C, (byte)0xB7, (byte)0x86,
			(byte)0x84, (byte)0x02, (byte)0x20, (byte)0xFE, (byte)0x0F, (byte)0xC1, (byte)0xF1, (byte)0xCE,
			(byte)0x04, (byte)0x35, (byte)0xA1, (byte)0x6A, (byte)0x48, (byte)0x28, (byte)0xC0, (byte)0x90,
			(byte)0x27, (byte)0x55, (byte)0x03, (byte)0x86, (byte)0x26, (byte)0xA0, (byte)0xA2, (byte)0x5C,
			(byte)0x42, (byte)0x1A, (byte)0xA8, (byte)0xA3, (byte)0x89, (byte)0x2C, (byte)0xE7, (byte)0x91,
			(byte)0x1D, (byte)0x10, (byte)0x38, (byte)0x34, (byte)0x7A, (byte)0x10, (byte)0xA5, (byte)0x09,
			(byte)0xA2, (byte)0x31, (byte)0x31, (byte)0x07, (byte)0x1A, (byte)0xB9, (byte)0xD6, (byte)0xC8,
			(byte)0xE2, (byte)0x00, (byte)0xC0, (byte)0x03, (byte)0x3A, (byte)0xE5, (byte)0x5A, (byte)0xC2,
			(byte)0x4D, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45,
			(byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// Image: angleSingleR-13x10.png
		byte[]	ANGLE_SINGLE_RIGHT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6F, (byte)0xEE, (byte)0xD4,
			(byte)0xC4, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x51, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x3F,
			(byte)0x03, (byte)0x0C, (byte)0x33, (byte)0x30, (byte)0xC4, (byte)0x5B, (byte)0x03, (byte)0xF1,
			(byte)0x63, (byte)0x06, (byte)0x86, (byte)0xC4, (byte)0x1C, (byte)0x64, (byte)0x71, (byte)0x74,
			(byte)0x8C, (byte)0xCA, (byte)0x61, (byte)0x48, (byte)0xF0, (byte)0x00, (byte)0xE2, (byte)0xFF,
			(byte)0x50, (byte)0x5C, (byte)0x40, (byte)0x94, (byte)0x26, (byte)0xA8, (byte)0xC6, (byte)0x02,
			(byte)0x42, (byte)0x1A, (byte)0xB1, (byte)0x9B, (byte)0x44, (byte)0x40, (byte)0x23, (byte)0x6E,
			(byte)0x77, (byte)0xA3, (byte)0x6A, (byte)0xCC, (byte)0xA5, (byte)0x8D, (byte)0x26, (byte)0x92,
			(byte)0x9D, (byte)0x47, (byte)0x72, (byte)0x40, (byte)0x00, (byte)0xE3, (byte)0xC8, (byte)0x99,
			(byte)0xE4, (byte)0x20, (byte)0x07, (byte)0x6A, (byte)0xB2, (byte)0x83, (byte)0x44, (byte)0x6E,
			(byte)0x7C, (byte)0x1E, (byte)0xBE, (byte)0xC8, (byte)0x05, (byte)0x00, (byte)0xEE, (byte)0xE7,
			(byte)0x3A, (byte)0xE5, (byte)0x77, (byte)0x71, (byte)0xF5, (byte)0xAD, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42,
			(byte)0x60, (byte)0x82
		};

		// Image: angleDoubleL-13x10.png
		byte[]	ANGLE_DOUBLE_LEFT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6F, (byte)0xEE, (byte)0xD4,
			(byte)0xC4, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x52, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x3F,
			(byte)0x03, (byte)0x32, (byte)0x66, (byte)0x60, (byte)0x48, (byte)0x90, (byte)0x00, (byte)0xE2,
			(byte)0x13, (byte)0x40, (byte)0xBC, (byte)0x1E, (byte)0x97, (byte)0x18, (byte)0x36, (byte)0x0D,
			(byte)0xD7, (byte)0x81, (byte)0x18, (byte)0xC8, (byte)0x89, (byte)0x3F, (byte)0x8C, (byte)0x53,
			(byte)0x0C, (byte)0xBB, (byte)0x06, (byte)0x30, (byte)0x2D, (byte)0x81, (byte)0x4D, (byte)0x0C,
			(byte)0xAE, (byte)0x89, (byte)0x14, (byte)0x0D, (byte)0x10, (byte)0xF5, (byte)0x0C, (byte)0xB1,
			(byte)0x62, (byte)0x98, (byte)0x1A, (byte)0x30, (byte)0xC5, (byte)0x50, (byte)0xBD, (byte)0x41,
			(byte)0x8E, (byte)0x26, (byte)0xB2, (byte)0x9C, (byte)0x47, (byte)0x76, (byte)0x40, (byte)0xE0,
			(byte)0xD0, (byte)0x78, (byte)0x02, (byte)0xA7, (byte)0x18, (byte)0xF6, (byte)0xC8, (byte)0x8D,
			(byte)0x3F, (byte)0x8C, (byte)0x19, (byte)0xB9, (byte)0x08, (byte)0x31, (byte)0x00, (byte)0x1A,
			(byte)0xBE, (byte)0xD1, (byte)0xC7, (byte)0x52, (byte)0x41, (byte)0xE0, (byte)0xF8, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE,
			(byte)0x42, (byte)0x60, (byte)0x82
		};

		// Image: angleDoubleR-13x10.png
		byte[]	ANGLE_DOUBLE_RIGHT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6F, (byte)0xEE, (byte)0xD4,
			(byte)0xC4, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5B, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x3F,
			(byte)0x03, (byte)0x03, (byte)0x43, (byte)0xC2, (byte)0x7A, (byte)0x06, (byte)0x86, (byte)0xF8,
			(byte)0xC3, (byte)0x40, (byte)0x5A, (byte)0x02, (byte)0xC4, (byte)0xC7, (byte)0x25, (byte)0x06,
			(byte)0x97, (byte)0x83, (byte)0x2A, (byte)0x38, (byte)0x01, (byte)0xC4, (byte)0x40, (byte)0x46,
			(byte)0xC2, (byte)0x75, (byte)0x98, (byte)0x22, (byte)0x6C, (byte)0x62, (byte)0xE8, (byte)0x9A,
			(byte)0x24, (byte)0xA0, (byte)0x92, (byte)0x70, (byte)0x45, (byte)0xD8, (byte)0xC4, (byte)0x50,
			(byte)0x34, (byte)0x91, (byte)0xAA, (byte)0x11, (byte)0xD5, (byte)0xAD, (byte)0x18, (byte)0x8A,
			(byte)0x62, (byte)0xC5, (byte)0xB0, (byte)0x8A, (byte)0x51, (byte)0xA4, (byte)0x89, (byte)0x64,
			(byte)0xE7, (byte)0x91, (byte)0x15, (byte)0x10, (byte)0xD0, (byte)0xF8, (byte)0x40, (byte)0x0B,
			(byte)0x72, (byte)0x4C, (byte)0x31, (byte)0xF4, (byte)0x20, (byte)0x5F, (byte)0x0F, (byte)0x8D,
			(byte)0x17, (byte)0xB4, (byte)0xC8, (byte)0x45, (byte)0x15, (byte)0x83, (byte)0x61, (byte)0x00,
			(byte)0x11, (byte)0x56, (byte)0xD1, (byte)0xC7, (byte)0xD0, (byte)0x16, (byte)0xFF, (byte)0x39,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44,
			(byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// NAVIGATION BUTTON CLASS


	private static class NavigationButton
		extends JButton
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	HORIZONTAL_MARGIN	= 4;
		private static final	int	VERTICAL_MARGIN		= 4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private NavigationButton(ImageIcon icon)
		{
			super(icon);
			setBorder(null);
			setBackground(null);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(2 * HORIZONTAL_MARGIN + getIcon().getIconWidth(),
								 2 * VERTICAL_MARGIN + getIcon().getIconHeight());
		}

		//--------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Get dimensions
			int width = getWidth();
			int height = getHeight();

			// Fill interior
			gr.setColor(isEnabled() ? (isSelected() != getModel().isArmed())
																	? ArrowButton.ACTIVE_BACKGROUND_COLOUR
																	: ArrowButton.BACKGROUND_COLOUR
									: getBackground());
			gr.fillRect(0, 0, width, height);

			// Draw icon
			if (isEnabled())
				getIcon().paintIcon(this, gr, HORIZONTAL_MARGIN, VERTICAL_MARGIN);

			// Draw border
			gr.setColor(ArrowButton.BORDER_COLOUR);
			gr.drawRect(0, 0, width - 1, height - 1);
			if (isFocusOwner())
			{
				((Graphics2D)gr).setStroke(GuiUtils.getBasicDash());
				gr.setColor(ArrowButton.FOCUSED_BORDER_COLOUR);
				gr.drawRect(1, 1, width - 3, height - 3);
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// DAY SELECTION PANEL CLASS


	private static class DaySelectionPanel
		extends JComponent
		implements ActionListener, FocusListener, MouseListener, MouseMotionListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MIN_NUM_DAYS	= 28;
		private static final	int	MAX_NUM_DAYS	= 31;

		private static final	int	NUM_COLUMNS	= 7;

		private static final	int	BORDER_WIDTH	= 1;

		private static final	int	HORIZONTAL_MARGIN	= 3;
		private static final	int	VERTICAL_MARGIN		= 2;

		private static final	Color	HEADER_BACKGROUND_COLOUR			= new Color(240, 224, 176);
		private static final	Color	BACKGROUND1_COLOUR					= Colours.BACKGROUND;
		private static final	Color	BACKGROUND2_COLOUR					= new Color(228, 236, 224);
		private static final	Color	SELECTION_BACKGROUND_COLOUR			= new Color(216, 216, 232);
		private static final	Color	FOCUSED_SELECTION_BACKGROUND_COLOUR	= Colours.FOCUSED_SELECTION_BACKGROUND;
		private static final	Color	SELECTION_BORDER_COLOUR				= new Color(144, 144, 176);
		private static final	Color	FOCUSED_SELECTION_BORDER_COLOUR		= Colours.FOCUSED_CELL_BORDER;
		private static final	Color	TEXT_COLOUR							= Colours.FOREGROUND;
		private static final	Color	DISABLED_TEXT_COLOUR				= new Color(176, 176, 176);
		private static final	Color	BORDER_COLOUR						= Colours.LINE_BORDER;

		private static final	String	PROTOTYPE_DAY_STR	= "00";

		private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
		{
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				Command.SELECT_UP_UNIT
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				Command.SELECT_DOWN_UNIT
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_DOWN_MASK),
				Command.SELECT_UP_MAX
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_DOWN_MASK),
				Command.SELECT_DOWN_MAX
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				Command.SELECT_LEFT_UNIT
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				Command.SELECT_RIGHT_UNIT
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
				Command.SELECT_LEFT_MAX
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
				Command.SELECT_RIGHT_MAX
			)
		};

	////////////////////////////////////////////////////////////////////
	//  Member classes : non-inner classes
	////////////////////////////////////////////////////////////////////


		// POP-UP COMPONENT CLASS


		private static class PopUpComponent
			extends JComponent
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			private static final	Color	TEXT_COLOUR			= Color.BLACK;
			private static final	Color	BACKGROUND_COLOUR	= new Color(255, 248, 192);
			private static final	Color	BORDER_COLOUR		= new Color(224, 176, 128);

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private PopUpComponent(String text,
								   int    height)
			{
				this.text = text;
				this.height = height;
				GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
				width = 2 * HORIZONTAL_MARGIN + getFontMetrics(getFont()).stringWidth(text);
				setOpaque(true);
				setFocusable(false);
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(width, height);
			}

			//----------------------------------------------------------

			@Override
			protected void paintComponent(Graphics gr)
			{
				// Create copy of graphics context
				gr = gr.create();

				// Fill interior
				gr.setColor(BACKGROUND_COLOUR);
				gr.fillRect(0, 0, width, height);

				// Set rendering hints for text antialiasing and fractional metrics
				TextRendering.setHints((Graphics2D)gr);

				// Draw text
				gr.setColor(TEXT_COLOUR);
				gr.drawString(text, HORIZONTAL_MARGIN,
							  GuiUtils.getBaselineOffset(height, gr.getFontMetrics()));

				// Draw border
				gr.setColor(BORDER_COLOUR);
				gr.drawRect(0, 0, width - 1, height - 1);
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance fields
		////////////////////////////////////////////////////////////////

			private	String	text;
			private	int		width;
			private	int		height;

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws IllegalArgumentException
		 */

		private DaySelectionPanel(int     dayOffset,
								  int     numDays,
								  int     selectedDay,
								  int     prevNumDays,
								  int     firstDayOfWeek,
								  boolean fullHeight)
		{
			// Initialise instance fields
			init(dayOffset, numDays, selectedDay, prevNumDays);
			this.firstDayOfWeek = firstDayOfWeek;
			this.fullHeight = fullHeight;

			// Get day names and set length of names in header
			dayNames = DateUtils.getDayNames(getDefaultLocale());
			dayNameLength = MIN_DAY_NAME_LENGTH - 1;
			boolean done = false;
			while (!done)
			{
				++dayNameLength;
				for (int i = 0; i < dayNames.size() - 1; i++)
				{
					String str = getDayString(i, dayNameLength);
					int j = i + 1;
					while (j < dayNames.size())
					{
						if (str.equals(getDayString(j, dayNameLength)))
							break;
						++j;
					}
					if (j == dayNames.size())
					{
						done = true;
						break;
					}
				}
			}

			// Get maximum width of day names and day numbers
			GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
			FontMetrics fontMetrics = getFontMetrics(getFont());
			int maxStrWidth = fontMetrics.stringWidth(PROTOTYPE_DAY_STR);
			for (int i = 0; i < dayNames.size(); i++)
			{
				int strWidth = fontMetrics.stringWidth(getDayString(i, dayNameLength));
				if (maxStrWidth < strWidth)
					maxStrWidth = strWidth;
			}

			// Initialise remaining instance fields
			columnWidth = 2 * HORIZONTAL_MARGIN + maxStrWidth;
			rowHeight = 2 * VERTICAL_MARGIN + fontMetrics.getAscent() + fontMetrics.getDescent();
			titleHeight = rowHeight;
			dayMousePressed = -1;
			dayMouseReleased = -1;

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

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ActionListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void actionPerformed(ActionEvent event)
		{
			String command = event.getActionCommand();

			if (command.equals(Command.SELECT_UP_UNIT))
				onSelectUpUnit();

			else if (command.equals(Command.SELECT_DOWN_UNIT))
				onSelectDownUnit();

			else if (command.equals(Command.SELECT_UP_MAX))
				onSelectUpMax();

			else if (command.equals(Command.SELECT_DOWN_MAX))
				onSelectDownMax();

			else if (command.equals(Command.SELECT_LEFT_UNIT))
				onSelectLeftUnit();

			else if (command.equals(Command.SELECT_RIGHT_UNIT))
				onSelectRightUnit();

			else if (command.equals(Command.SELECT_LEFT_MAX))
				onSelectLeftMax();

			else if (command.equals(Command.SELECT_RIGHT_MAX))
				onSelectRightMax();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : FocusListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void focusGained(FocusEvent event)
		{
			repaint();
		}

		//--------------------------------------------------------------

		@Override
		public void focusLost(FocusEvent event)
		{
			repaint();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : MouseListener interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void mouseClicked(MouseEvent event)
		{
			if (SwingUtilities.isLeftMouseButton(event) && (event.getClickCount() > 1) &&
				 (dayMousePressed >= 0) && (dayMousePressed == dayMouseReleased) && (acceptAction != null))
				acceptAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
															 (String)acceptAction.
																	getValue(Action.ACTION_COMMAND_KEY)));
		}

		//--------------------------------------------------------------

		@Override
		public void mouseEntered(MouseEvent event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		@Override
		public void mouseExited(MouseEvent event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

		@Override
		public void mousePressed(MouseEvent event)
		{
			requestFocusInWindow();

			if (SwingUtilities.isLeftMouseButton(event))
			{
				showPopUp(event);

				dayMousePressed = getDay(event);
				setSelection(dayMousePressed);
			}
		}

		//--------------------------------------------------------------

		@Override
		public void mouseReleased(MouseEvent event)
		{
			if (SwingUtilities.isLeftMouseButton(event))
			{
				hidePopUp();

				dayMouseReleased = getDay(event);
				setSelection(dayMouseReleased);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : MouseMotionListener interface
	////////////////////////////////////////////////////////////////////

		public void mouseDragged(MouseEvent event)
		{
			if (SwingUtilities.isLeftMouseButton(event))
				setSelection(getDay(event));
		}

		//--------------------------------------------------------------

		public void mouseMoved(MouseEvent event)
		{
			// do nothing
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(2 * BORDER_WIDTH + NUM_COLUMNS * columnWidth,
								 2 * BORDER_WIDTH + titleHeight + getNumPanelRows() * rowHeight);
		}

		//--------------------------------------------------------------

		@Override
		public void doLayout()
		{
			// Call superclass method
			super.doLayout();

			// Widen columns if preferred width of panel is less than its actual width
			int width = getWidth();
			while (getPreferredSize().width < width)
				++columnWidth;

			// Lay out ancestor window again if width of this panel has changed
			int preferredWidth = getPreferredSize().width;
			if (width != preferredWidth)
			{
				setSize(preferredWidth, getHeight());
				SwingUtilities.getWindowAncestor(this).pack();
			}
		}

		//--------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Get dimensions
			int width = getWidth();
			int height = getHeight();

			// Fill header
			int x = BORDER_WIDTH;
			int y = BORDER_WIDTH;
			gr.setColor(HEADER_BACKGROUND_COLOUR);
			gr.fillRect(x, y, width, titleHeight);

			// Fill column backgrounds
			y += titleHeight;
			for (int i = 0; i < NUM_COLUMNS; i++)
			{
				gr.setColor(((i % 2) == 0) ? BACKGROUND1_COLOUR : BACKGROUND2_COLOUR);
				gr.fillRect(x, y, columnWidth, height - BORDER_WIDTH - y);
				x += columnWidth;
			}

			// Draw background and border of selected cell
			if (selectedDay >= 0)
			{
				x = getCellX(selectedDay);
				y = getCellY(selectedDay);
				gr.setColor(isFocusOwner() ? FOCUSED_SELECTION_BACKGROUND_COLOUR
										   : SELECTION_BACKGROUND_COLOUR);
				gr.fillRect(x, y, columnWidth, rowHeight);
				gr.setColor(isFocusOwner() ? FOCUSED_SELECTION_BORDER_COLOUR : SELECTION_BORDER_COLOUR);
				gr.drawRect(x, y, columnWidth - 1, rowHeight - 1);
			}

			// Set rendering hints for text antialiasing and fractional metrics
			TextRendering.setHints((Graphics2D)gr);

			// Draw header text
			FontMetrics fontMetrics = gr.getFontMetrics();
			int ascent = fontMetrics.getAscent();
			x = BORDER_WIDTH;
			y = BORDER_WIDTH + VERTICAL_MARGIN + ascent;
			gr.setColor(TEXT_COLOUR);
			for (int i = 0; i < NUM_DAYS_IN_WEEK; i++)
			{
				String str = getDayString(i, dayNameLength);
				gr.drawString(str, x + (columnWidth - fontMetrics.stringWidth(str)) / 2, y);
				x += columnWidth;
			}

			// Draw text of all day numbers
			int strWidth = fontMetrics.stringWidth(PROTOTYPE_DAY_STR);
			int xOffset = BORDER_WIDTH + (columnWidth - strWidth) / 2 + strWidth;
			int startDay = (prevNumDays < 0) ? 0 : -dayOffset;
			int endDay = (prevNumDays < 0) ? numDays : startDay + getNumPanelRows() * NUM_COLUMNS;
			for (int day = startDay; day < endDay; day++)
			{
				gr.setColor(((day >= 0) && (day < numDays)) ? TEXT_COLOUR : DISABLED_TEXT_COLOUR);
				int dayIndex = (day < 0) ? day + prevNumDays
										 : (day < numDays)
												? day
												: day - numDays;
				String str = Integer.toString(dayIndex + 1);
				x = xOffset + getColumnForDay(day) * columnWidth - fontMetrics.stringWidth(str);
				y = BORDER_WIDTH + titleHeight + getRowForDay(day) * rowHeight + VERTICAL_MARGIN + ascent;
				gr.drawString(str, x, y);
			}

			// Draw border
			gr.setColor(BORDER_COLOUR);
			gr.drawRect(0, 0, width - 1, height - 1);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getSelectedDay()
		{
			return selectedDay;
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public void setMonth(int dayOffset,
							 int numDays,
							 int selectedDay,
							 int prevNumDays)
		{
			// Set instance fields
			init(dayOffset, numDays, selectedDay, prevNumDays);

			// Redraw panel
			revalidate();
			repaint();
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		private void init(int dayOffset,
						  int numDays,
						  int selectedDay,
						  int prevNumDays)
		{
			// Validate arguments
			if ((dayOffset < 0) || (dayOffset >= NUM_COLUMNS)
				|| (numDays < MIN_NUM_DAYS) || (numDays > MAX_NUM_DAYS)
				|| (selectedDay >= numDays))
				throw new IllegalArgumentException();

			// Set instance fields
			this.dayOffset = ((prevNumDays < 0) || (dayOffset > 0)) ? dayOffset : dayOffset + NUM_COLUMNS;
			this.numDays = numDays;
			this.selectedDay = (selectedDay < 0) ? -1 : selectedDay;
			this.prevNumDays = prevNumDays;
			numRows = (this.dayOffset + numDays + NUM_COLUMNS - 1) / NUM_COLUMNS;
		}

		//--------------------------------------------------------------

		private int getNumPanelRows()
		{
			return (fullHeight ? MAX_NUM_DAYS / NUM_COLUMNS + Math.min(MAX_NUM_DAYS % NUM_COLUMNS, 2)
							   : numRows);
		}

		//--------------------------------------------------------------

		private String getDayString(int index,
									int length)
		{
			int i = firstDayOfWeek + index - Calendar.SUNDAY;
			if (i >= NUM_DAYS_IN_WEEK)
				i -= NUM_DAYS_IN_WEEK;
			String str = dayNames.get(i);
			if ((length > 0) && (length < str.length()))
				str = str.substring(0, length);
			return str;
		}

		//--------------------------------------------------------------

		private void incrementSelectionColumn(int increment)
		{
			int day = (selectedDay < 0) ? 0 : selectedDay;
			int column = Math.min(Math.max(0, getColumnForDay(day) + increment), NUM_COLUMNS - 1);
			day = rowColumnToDay(getRowForDay(day), column);
			day = Math.min(Math.max(MIN_DAY, day), numDays - 1);
			setSelection(day);
		}

		//--------------------------------------------------------------

		private void incrementSelectionRow(int increment)
		{
			int day = (selectedDay < 0) ? 0 : selectedDay;
			int row = Math.min(Math.max(0, getRowForDay(day) + increment), numRows - 1);
			day = rowColumnToDay(row, getColumnForDay(day));
			while (day < MIN_DAY)
				day += NUM_COLUMNS;
			while (day >= numDays)
				day -= NUM_COLUMNS;
			setSelection(day);
		}

		//--------------------------------------------------------------

		private int getCellX(int day)
		{
			return (BORDER_WIDTH + (day + dayOffset) % NUM_COLUMNS * columnWidth);
		}

		//--------------------------------------------------------------

		private int getCellY(int day)
		{
			return (BORDER_WIDTH + titleHeight + ((day + dayOffset) / NUM_COLUMNS) * rowHeight);
		}

		//--------------------------------------------------------------

		private int getRowForDay(int day)
		{
			return ((day + dayOffset) / NUM_COLUMNS);
		}

		//--------------------------------------------------------------

		private int getColumnForDay(int day)
		{
			return ((day + dayOffset) % NUM_COLUMNS);
		}

		//--------------------------------------------------------------

		private int rowColumnToDay(int row,
								   int column)
		{
			return (row * NUM_COLUMNS + column - dayOffset);
		}

		//--------------------------------------------------------------

		private void showPopUp(MouseEvent event)
		{
			int x = event.getX();
			int x1 = BORDER_WIDTH;
			int x2 = x1 + NUM_COLUMNS * columnWidth;
			int y = event.getY();
			int y1 = BORDER_WIDTH;
			int y2 = y1 + titleHeight;
			if ((x >= x1) && (x < x2) && (y >= y1) && (y < y2))
			{
				int index = (x - x1) / columnWidth;
				int strWidth = getFontMetrics(getFont()).stringWidth(getDayString(index, dayNameLength));
				x = x1 + index * columnWidth + (columnWidth - strWidth) / 2 - HORIZONTAL_MARGIN;
				PopUpComponent popUpComponent = new PopUpComponent(getDayString(index, 0), titleHeight);
				int popUpWidth = popUpComponent.getPreferredSize().width;
				Point location = new Point(x, y1);
				SwingUtilities.convertPointToScreen(location, this);
				Rectangle screen = GuiUtils.getVirtualScreenBounds(this);
				x = Math.min(location.x, screen.x + screen.width - popUpWidth);
				popUp = PopupFactory.getSharedInstance().getPopup(this, popUpComponent, x, location.y);
				popUp.show();
			}
		}

		//--------------------------------------------------------------

		private void hidePopUp()
		{
			if (popUp != null)
			{
				popUp.hide();
				popUp = null;
			}
		}

		//--------------------------------------------------------------

		private int getDay(MouseEvent event)
		{
			int day = -1;
			int x = event.getX();
			int x1 = BORDER_WIDTH;
			int x2 = x1 + NUM_COLUMNS * columnWidth;
			int y = event.getY();
			int y1 = BORDER_WIDTH + titleHeight;
			int y2 = y1 + getNumPanelRows() * rowHeight;
			if ((x >= x1) && (x < x2) && (y >= y1) && (y < y2))
			{
				int column = (x - x1) / columnWidth;
				int row = (y - y1) / rowHeight;
				day = rowColumnToDay(row, column);
				if ((day < 0) || (day >= numDays))
					day = -1;
			}
			return day;
		}

		//--------------------------------------------------------------

		private void setSelection(int day)
		{
			if ((day >= 0) && (day != selectedDay))
			{
				selectedDay = day;
				repaint();
			}
		}

		//--------------------------------------------------------------

		private void onSelectUpUnit()
		{
			incrementSelectionRow(-1);
		}

		//--------------------------------------------------------------

		private void onSelectDownUnit()
		{
			incrementSelectionRow(1);
		}

		//--------------------------------------------------------------

		private void onSelectUpMax()
		{
			incrementSelectionRow(-numRows);
		}

		//--------------------------------------------------------------

		private void onSelectDownMax()
		{
			incrementSelectionRow(numRows);
		}

		//--------------------------------------------------------------

		private void onSelectLeftUnit()
		{
			incrementSelectionColumn(-1);
		}

		//--------------------------------------------------------------

		private void onSelectRightUnit()
		{
			incrementSelectionColumn(1);
		}

		//--------------------------------------------------------------

		private void onSelectLeftMax()
		{
			incrementSelectionColumn(-NUM_COLUMNS);
		}

		//--------------------------------------------------------------

		private void onSelectRightMax()
		{
			incrementSelectionColumn(NUM_COLUMNS);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	int				dayOffset;
		private	int				numDays;
		private	int				selectedDay;
		private	int				firstDayOfWeek;
		private	Action			acceptAction;
		private	boolean			fullHeight;
		private	int				prevNumDays;
		private	int				numRows;
		private	int				columnWidth;
		private	int				rowHeight;
		private	int				titleHeight;
		private	int				dayMousePressed;
		private	int				dayMouseReleased;
		private	List<String>	dayNames;
		private	int				dayNameLength;
		private	Popup			popUp;

	}

	//==================================================================


	// MONTH-YEAR DIALOG BOX CLASS


	private static class MonthYearDialog
		extends JDialog
		implements ActionListener
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	YEAR_FIELD_LENGTH	= 4;

		private static final	Insets	BUTTON_MARGINS	= new Insets(1, 4, 1, 4);

		private static final	Color	BACKGROUND_COLOUR	= new Color(252, 240, 200);
		private static final	Color	BORDER_COLOUR		= new Color(224, 128, 64);

		// Commands
		private interface Command
		{
			String	ACCEPT	= "accept";
			String	CLOSE	= "close";
		}

		private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
		{
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK),
				Command.ACCEPT
			),
			new KeyAction.KeyCommandPair
			(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				Command.CLOSE
			)
		};

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private MonthYearDialog(Window       owner,
								int          month,
								int          year,
								List<String> monthStrs)
		{

			// Call superclass constructor
			super(owner, Dialog.ModalityType.APPLICATION_MODAL);


			//----  Control panel

			GridBagLayout gridBag = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();

			JPanel controlPanel = new JPanel(gridBag);
			controlPanel.setBackground(BACKGROUND_COLOUR);

			int gridX = 0;

			// Combo box: months
			monthComboBox = new FComboBox<>();
			for (String str : monthStrs)
				monthComboBox.addItem(str);
			monthComboBox.setSelectedIndex(month);

			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(monthComboBox, gbc);
			controlPanel.add(monthComboBox);

			// Spinner: year
			yearSpinner = new FIntegerSpinner(year, MIN_YEAR, MAX_YEAR, YEAR_FIELD_LENGTH);

			gbc.gridx = gridX++;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.insets = new Insets(0, 1, 0, 0);
			gridBag.setConstraints(yearSpinner, gbc);
			controlPanel.add(yearSpinner);


			//----  Button panel

			JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 4, 0));
			buttonPanel.setBackground(BACKGROUND_COLOUR);
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

			// Button: OK
			JButton okButton = new FButton(Constants.OK_STR);
			okButton.setMargin(BUTTON_MARGINS);
			okButton.setActionCommand(Command.ACCEPT);
			okButton.addActionListener(this);
			buttonPanel.add(okButton);

			// Button: cancel
			JButton cancelButton = new FButton(Constants.CANCEL_STR);
			cancelButton.setMargin(BUTTON_MARGINS);
			cancelButton.setActionCommand(Command.CLOSE);
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);


			//----  Main panel

			JPanel mainPanel = new JPanel(gridBag);
			mainPanel.setBackground(BACKGROUND_COLOUR);
			GuiUtils.setPaddedLineBorder(mainPanel, 1, BORDER_COLOUR);

			int gridY = 0;

			gbc.gridx = 0;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(controlPanel, gbc);
			mainPanel.add(controlPanel);

			gbc.gridx = 0;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(1, 0, 0, 0);
			gridBag.setConstraints(buttonPanel, gbc);
			mainPanel.add(buttonPanel);

			// Add commands to action map
			KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, KEY_COMMANDS);


			//----  Window

			// Set content pane
			setContentPane(mainPanel);

			// Omit frame from dialog box
			setUndecorated(true);

			// Dispose of window when it is closed
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			// Prevent dialog from being resized
			setResizable(false);

			// Resize dialog to its preferred size
			pack();

		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ActionListener interface
	////////////////////////////////////////////////////////////////////

		public void actionPerformed(ActionEvent event)
		{
			String command = event.getActionCommand();

			if (command.equals(Command.ACCEPT))
				onAccept();

			else if (command.equals(Command.CLOSE))
				onClose();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void onAccept()
		{
			accepted = true;
			onClose();
		}

		//--------------------------------------------------------------

		private void onClose()
		{
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	boolean				accepted;
		private	JComboBox<String>	monthComboBox;
		private	FIntegerSpinner		yearSpinner;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public DateSelectionPanel(Date    date,
							  boolean showAdjacentMonths)
	{
		this(date.year, date.month, date.day, 0, showAdjacentMonths);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public DateSelectionPanel(Date    date,
							  int     firstDayOfWeek,
							  boolean showAdjacentMonths)
	{
		this(date.year, date.month, date.day, firstDayOfWeek, showAdjacentMonths);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public DateSelectionPanel(int     year,
							  int     month,
							  int     day,
							  boolean showAdjacentMonths)
	{
		this(year, month, day, 0, showAdjacentMonths);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public DateSelectionPanel(int     year,
							  int     month,
							  int     day,
							  int     firstDayOfWeek,
							  boolean showAdjacentMonths)
	{
		// Validate arguments
		if ((year < MIN_YEAR) || (year > MAX_YEAR)
			|| (month < MIN_MONTH) || (month > MAX_MONTH)
			|| (firstDayOfWeek < 0) || (firstDayOfWeek > Calendar.SATURDAY))
			throw new IllegalArgumentException();

		// Initialise instance fields
		calendar = new ModernCalendar(year, month, 1);
		if ((day < MIN_DAY) || (day >= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
			throw new IllegalArgumentException();
		this.firstDayOfWeek = (firstDayOfWeek == 0) ? calendar.getFirstDayOfWeek() : firstDayOfWeek;
		this.showAdjacentMonths = showAdjacentMonths;

		// Set names of months and lengths of names
		monthNames = DateUtils.getMonthNames(getDefaultLocale());
		monthNameLengths = new int[monthNames.size()];
		for (int i = 0; i < monthNames.size(); i++)
		{
			int length = MIN_MONTH_NAME_LENGTH - 1;
			boolean done = false;
			while (!done)
			{
				++length;
				String str0 = monthNames.get(i);
				if (length < str0.length())
					str0 = str0.substring(0, length);
				int j = 0;
				while (j < monthNames.size())
				{
					if (i != j)
					{
						String str1 = monthNames.get(j);
						if (length < str1.length())
							str1 = str1.substring(0, length);
						if (str1.equals(str0))
							break;
					}
					++j;
				}
				if (j == monthNames.size())
					done = true;
			}
			monthNameLengths[i] = length;
		}


		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);
		controlPanel.setBackground(MONTH_BACKGROUND_COLOUR);
		controlPanel.setBorder(BorderFactory.createLineBorder(Colours.LINE_BORDER));

		int gridX = 0;

		// Button: previous year
		previousYearButton = new NavigationButton(ANGLE_DOUBLE_LEFT_ICON);
		previousYearButton.setToolTipText(PREVIOUS_YEAR_TOOLTIP_STR);
		previousYearButton.setActionCommand(Command.PREVIOUS_YEAR);
		previousYearButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 1, 0, 0);
		gridBag.setConstraints(previousYearButton, gbc);
		controlPanel.add(previousYearButton);

		// Button: previous month
		previousMonthButton = new NavigationButton(ANGLE_SINGLE_LEFT_ICON);
		previousMonthButton.setToolTipText(PREVIOUS_MONTH_TOOLTIP_STR);
		previousMonthButton.setActionCommand(Command.PREVIOUS_MONTH);
		previousMonthButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(previousMonthButton, gbc);
		controlPanel.add(previousMonthButton);

		// Label: month and year
		monthLabel = new FLabel("");
		monthLabel.setFont(monthLabel.getFont().deriveFont(Font.BOLD));
		monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
		FontMetrics fontMetrics = monthLabel.getFontMetrics(monthLabel.getFont());
		int width = 0;
		for (int i = 0; i < monthNames.size(); i++)
		{
			int strWidth = fontMetrics.stringWidth(getMonthString(i));
			if (width < strWidth)
				width = strWidth;
		}
		width += fontMetrics.stringWidth(" " + PROTOTYPE_YEAR_STR);
		monthLabel.setPreferredSize(new Dimension(2 * MONTH_LABEL_HORIZONTAL_MARGIN + width,
												  2 * MONTH_LABEL_VERTICAL_MARGIN +
																				fontMetrics.getAscent() +
																				fontMetrics.getDescent()));
		monthLabel.setText(getMonthString(year, month));
		monthLabel.addMouseListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 1, 0, 0);
		gridBag.setConstraints(monthLabel, gbc);
		controlPanel.add(monthLabel);

		// Button: next month
		nextMonthButton = new NavigationButton(ANGLE_SINGLE_RIGHT_ICON);
		nextMonthButton.setToolTipText(NEXT_MONTH_TOOLTIP_STR);
		nextMonthButton.setActionCommand(Command.NEXT_MONTH);
		nextMonthButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 1, 0, 0);
		gridBag.setConstraints(nextMonthButton, gbc);
		controlPanel.add(nextMonthButton);

		// Button: next year
		nextYearButton = new NavigationButton(ANGLE_DOUBLE_RIGHT_ICON);
		nextYearButton.setToolTipText(NEXT_YEAR_TOOLTIP_STR);
		nextYearButton.setActionCommand(Command.NEXT_YEAR);
		nextYearButton.addActionListener(this);

		gbc.gridx = gridX++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 1, 0, 0);
		gridBag.setConstraints(nextYearButton, gbc);
		controlPanel.add(nextYearButton);


		//----  Day selection panel

		daySelectionPanel = new DaySelectionPanel(getDayOffset(), getDaysInMonth(), day,
												  showAdjacentMonths ? getDaysInPrevMonth() : -1,
												  this.firstDayOfWeek, true);


		//----  Outer panel

		setLayout(gridBag);

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		add(controlPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(-1, 0, 0, 0);
		gridBag.setConstraints(daySelectionPanel, gbc);
		add(daySelectionPanel);

		// Add commands to action map
		KeyAction.create(this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, KEY_COMMANDS);

		// Update buttons
		updateButtons();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.PREVIOUS_MONTH))
			onPreviousMonth();

		else if (command.equals(Command.PREVIOUS_YEAR))
			onPreviousYear();

		else if (command.equals(Command.NEXT_MONTH))
			onNextMonth();

		else if (command.equals(Command.NEXT_YEAR))
			onNextYear();

		else if (command.equals(Command.EDIT_MONTH_YEAR))
			onEditMonthYear();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : MouseListener interface
////////////////////////////////////////////////////////////////////////

	@Override
	public void mouseClicked(MouseEvent event)
	{
		if (SwingUtilities.isLeftMouseButton(event))
			onEditMonthYear();
	}

	//------------------------------------------------------------------

	@Override
	public void mouseEntered(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mouseExited(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mousePressed(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	@Override
	public void mouseReleased(MouseEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean requestFocusInWindow()
	{
		return daySelectionPanel.requestFocusInWindow();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Date getDate()
	{
		int day = daySelectionPanel.getSelectedDay();
		return ((day < 0) ? null
						  : new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day));
	}

	//------------------------------------------------------------------

	public void setAcceptAction(Action action)
	{
		daySelectionPanel.acceptAction = action;
	}

	//------------------------------------------------------------------

	private void updateButtons()
	{
		previousMonthButton.setEnabled(canDecrementMonth());
		previousYearButton.setEnabled(canDecrementYear());
		nextMonthButton.setEnabled(canIncrementMonth());
		nextYearButton.setEnabled(canIncrementYear());
	}

	//------------------------------------------------------------------

	private boolean canDecrementMonth()
	{
		return ((calendar.get(Calendar.MONTH) > MIN_MONTH) || (calendar.get(Calendar.YEAR) > MIN_YEAR));
	}

	//------------------------------------------------------------------

	private boolean canIncrementMonth()
	{
		return ((calendar.get(Calendar.MONTH) < MAX_MONTH) || (calendar.get(Calendar.YEAR) < MAX_YEAR));
	}

	//------------------------------------------------------------------

	private boolean canDecrementYear()
	{
		return (calendar.get(Calendar.YEAR) > MIN_YEAR);
	}

	//------------------------------------------------------------------

	private boolean canIncrementYear()
	{
		return (calendar.get(Calendar.YEAR) < MAX_YEAR);
	}

	//------------------------------------------------------------------

	private int getDayOffset()
	{
		int offset = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;
		if (offset < 0)
			offset += NUM_DAYS_IN_WEEK;
		return offset;
	}

	//------------------------------------------------------------------

	private int getDaysInMonth()
	{
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	//------------------------------------------------------------------

	private int getDaysInPrevMonth()
	{
		ModernCalendar calendar = new ModernCalendar();
		calendar.setTimeInMillis(this.calendar.getTimeInMillis());
		calendar.roll(Calendar.MONTH, false);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	//------------------------------------------------------------------

	private String getMonthString(int index)
	{
		String str = monthNames.get(index);
		if (monthNameLengths[index] < str.length())
			str = str.substring(0, monthNameLengths[index]);
		return str;
	}

	//------------------------------------------------------------------

	private String getMonthString(int year,
								  int month)
	{
		return (getMonthString(month) + " " + Integer.toString(year));
	}

	//------------------------------------------------------------------

	private void setMonth(int year,
						  int month)
	{
		calendar = new ModernCalendar(year, month, 1);
		int daysInMonth = getDaysInMonth();
		int selectedDay = Math.min(daySelectionPanel.getSelectedDay(), daysInMonth - 1);
		daySelectionPanel.setMonth(getDayOffset(), daysInMonth, selectedDay,
								   showAdjacentMonths ? getDaysInPrevMonth() : -1);
		monthLabel.setText(getMonthString(year, month));
		updateButtons();
	}

	//------------------------------------------------------------------

	private void onPreviousMonth()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (month == MIN_MONTH)
		{
			if (year > MIN_YEAR)
				setMonth(year - 1, MAX_MONTH);
		}
		else
			setMonth(year, month - 1);
	}

	//------------------------------------------------------------------

	private void onPreviousYear()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (year > MIN_YEAR)
			setMonth(year - 1, month);
	}

	//------------------------------------------------------------------

	private void onNextMonth()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (month == MAX_MONTH)
		{
			if (year < MAX_YEAR)
				setMonth(year + 1, MIN_MONTH);
		}
		else
			setMonth(year, month + 1);
	}

	//------------------------------------------------------------------

	private void onNextYear()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (year < MAX_YEAR)
			setMonth(year + 1, month);
	}

	//------------------------------------------------------------------

	private void onEditMonthYear()
	{
		MonthYearDialog dialog = new MonthYearDialog(SwingUtilities.getWindowAncestor(this),
													 calendar.get(Calendar.MONTH),
													 calendar.get(Calendar.YEAR), monthNames);
		Point location = getLocationOnScreen();
		location.x += (getWidth() - dialog.getWidth()) / 2;
		dialog.setLocation(GuiUtils.getComponentLocation(dialog, location));
		dialog.setVisible(true);

		if (dialog.accepted)
			setMonth(dialog.yearSpinner.getIntValue(), dialog.monthComboBox.getSelectedIndex());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	Calendar			calendar;
	private	int					firstDayOfWeek;
	private	boolean				showAdjacentMonths;
	private	List<String>		monthNames;
	private	int[]				monthNameLengths;
	private	NavigationButton	previousMonthButton;
	private	NavigationButton	previousYearButton;
	private	NavigationButton	nextMonthButton;
	private	NavigationButton	nextYearButton;
	private	JLabel				monthLabel;
	private	DaySelectionPanel	daySelectionPanel;

}

//----------------------------------------------------------------------
