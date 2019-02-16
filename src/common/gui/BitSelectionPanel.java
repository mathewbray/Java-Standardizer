/*====================================================================*\

BitSelectionPanel.java

Bit selection panel class.

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
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import common.misc.KeyAction;
import common.misc.NumberUtils;
import common.misc.StringUtils;

//----------------------------------------------------------------------


// BIT SELECTION PANEL CLASS


public class BitSelectionPanel
	extends JComponent
	implements ActionListener, FocusListener, MouseListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	NUM_ROWS	= 1;

	private static final	int	HORIZONTAL_MARGIN		= 2;
	private static final	int	INNER_VERTICAL_MARGIN	= 2;
	private static final	int	OUTER_VERTICAL_MARGIN	= 2;
	private static final	int	GRID_LINE_WIDTH			= 1;

	private static final	Color	BACKGROUND_COLOUR					= new Color(254, 254, 250);
	private static final	Color	TEXT_COLOUR							= Colours.FOREGROUND;
	private static final	Color	BORDER_COLOUR						= new Color(176, 184, 176);
	private static final	Color	FOCUSED_BORDER_COLOUR				= Color.BLACK;
	private static final	Color	SELECTION_BACKGROUND_COLOUR			= new Color(200, 224, 200);
	private static final	Color	FOCUSED_SELECTION_BACKGROUND_COLOUR	= new Color(248, 216, 144);

	// Commands
	private interface Command
	{
		String	TOGGLE_SELECTED		= "toggleSelected";
		String	SELECT_LEFT_UNIT	= "selectLeftUnit";
		String	SELECT_RIGHT_UNIT	= "selectRightUnit";
		String	SELECT_LEFT_MAX		= "selectLeftMax";
		String	SELECT_RIGHT_MAX	= "selectRightMax";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair
		(
			KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
			Command.TOGGLE_SELECTED
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

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public BitSelectionPanel(int numBits,
							 int bitMask)
	{
		this(numBits, Integer.SIZE, bitMask);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public BitSelectionPanel(int numBits,
							 int maxNumSelectedBits,
							 int bitMask)
	{
		// Validate arguments
		if ((numBits <= 0) || (numBits > Integer.SIZE) || (maxNumSelectedBits < 0))
			throw new IllegalArgumentException();

		// Set font
		GuiUtils.setAppFont(Constants.FontKey.MAIN, this);

		// Initialise instance fields
		this.numBits = numBits;
		this.maxNumSelectedBits = maxNumSelectedBits;
		this.bitMask = bitMask;
		FontMetrics fontMetrics = getFontMetrics(getFont());
		String str = StringUtils.createCharString('0',
												  NumberUtils.getNumDecDigitsInt(numBits - 1));
		cellWidth = GRID_LINE_WIDTH + 2 * HORIZONTAL_MARGIN + fontMetrics.stringWidth(str);
		cellHeight = GRID_LINE_WIDTH + 2 * INNER_VERTICAL_MARGIN + fontMetrics.getAscent() +
																				fontMetrics.getDescent();

		// Set component attributes
		setOpaque(true);
		setFocusable(true);

		// Add commands to action map
		KeyAction.create(this, JComponent.WHEN_FOCUSED, this, KEY_COMMANDS);

		// Add listeners
		addFocusListener(this);
		addMouseListener(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.TOGGLE_SELECTED))
			onToggleSelected();

		else if (command.equals(Command.SELECT_LEFT_UNIT))
			onSelectLeftUnit();

		else if (command.equals(Command.SELECT_RIGHT_UNIT))
			onSelectRightUnit();

		else if (command.equals(Command.SELECT_LEFT_MAX))
			onSelectLeftMax();

		else if (command.equals(Command.SELECT_RIGHT_MAX))
			onSelectRightMax();
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
		if (SwingUtilities.isLeftMouseButton(event))
		{
			int x = event.getX();
			int y = event.getY();
			if ((x >= 0) && (x < numBits * cellWidth) && (y >= 0) && (y < NUM_ROWS * cellHeight))
			{
				requestFocusInWindow();
				setActiveIndex(numBits - x / cellWidth - 1);
				onToggleSelected();
			}
		}
	}

	//------------------------------------------------------------------

	public void mouseReleased(MouseEvent event)
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
		return new Dimension(GRID_LINE_WIDTH + numBits * cellWidth,
							 2 * OUTER_VERTICAL_MARGIN + GRID_LINE_WIDTH + NUM_ROWS * cellHeight);
	}

	//------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics gr)
	{
		// Create copy of graphics context
		gr = gr.create();

		// Draw background
		Rectangle rect = gr.getClipBounds();
		gr.setColor(getBackground());
		gr.fillRect(rect.x, rect.y, rect.width, rect.height);

		// Draw cell backgrounds
		int x = GRID_LINE_WIDTH;
		int y = OUTER_VERTICAL_MARGIN + GRID_LINE_WIDTH;
		for (int i = numBits - 1; i >= 0; i--)
		{
			gr.setColor(((bitMask & 1 << i) == 0) ? BACKGROUND_COLOUR
												  : isFocusOwner() ? FOCUSED_SELECTION_BACKGROUND_COLOUR
																   : SELECTION_BACKGROUND_COLOUR);
			gr.fillRect(x, y, cellWidth - 1, cellHeight - 1);
			x += cellWidth;
		}

		// Draw horizontal grid lines
		gr.setColor(BORDER_COLOUR);
		int x1 = 0;
		int x2 = getWidth() - 1;
		y = OUTER_VERTICAL_MARGIN;
		for (int i = 0; i <= NUM_ROWS; i++)
		{
			gr.drawLine(x1, y, x2, y);
			y += cellHeight;
		}

		// Draw vertical grid lines
		x = 0;
		int y1 = OUTER_VERTICAL_MARGIN;
		int y2 = y1 + cellHeight;
		for (int i = 0; i <= numBits; i++)
		{
			gr.drawLine(x, y1, x, y2);
			x += cellWidth;
		}

		// Set rendering hints for text antialiasing and fractional metrics
		TextRendering.setHints((Graphics2D)gr);

		// Draw text
		gr.setColor(TEXT_COLOUR);
		FontMetrics fontMetrics = gr.getFontMetrics();
		x = GRID_LINE_WIDTH;
		y = OUTER_VERTICAL_MARGIN + GRID_LINE_WIDTH + INNER_VERTICAL_MARGIN + fontMetrics.getAscent();
		for (int i = numBits - 1; i >= 0; i--)
		{
			String str = Integer.toString(i);
			int strWidth = fontMetrics.stringWidth(str);
			gr.drawString(str, x + (cellWidth - strWidth) / 2, y);
			x += cellWidth;
		}

		// Draw focus indicator
		if (isFocusOwner())
		{
			((Graphics2D)gr).setStroke(GuiUtils.getBasicDash());
			gr.setColor(FOCUSED_BORDER_COLOUR);
			x = GRID_LINE_WIDTH + (numBits - activeIndex - 1) * cellWidth;
			y = OUTER_VERTICAL_MARGIN + GRID_LINE_WIDTH;
			gr.drawRect(x, y, cellWidth - 2, cellHeight - 2);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getBitMask()
	{
		return bitMask;
	}

	//------------------------------------------------------------------

	private void setActiveIndex(int index)
	{
		if (activeIndex != index)
		{
			activeIndex = index;
			repaint();
		}
	}

	//------------------------------------------------------------------

	private void onToggleSelected()
	{
		int oldBitMask = bitMask;
		int mask = 1 << activeIndex;
		if ((bitMask & mask) == 0)
		{
			if (Integer.bitCount(bitMask) < maxNumSelectedBits)
				bitMask |= mask;
		}
		else
			bitMask &= ~mask;
		if (bitMask != oldBitMask)
			repaint();
	}

	//------------------------------------------------------------------

	private void onSelectLeftUnit()
	{
		setActiveIndex(Math.min(activeIndex + 1, numBits - 1));
	}

	//------------------------------------------------------------------

	private void onSelectRightUnit()
	{
		setActiveIndex(Math.max(0, activeIndex - 1));
	}

	//------------------------------------------------------------------

	private void onSelectLeftMax()
	{
		setActiveIndex(numBits - 1);
	}

	//------------------------------------------------------------------

	private void onSelectRightMax()
	{
		setActiveIndex(0);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int	numBits;
	private	int	maxNumSelectedBits;
	private	int	bitMask;
	private	int	activeIndex;
	private	int	cellWidth;
	private	int	cellHeight;

}

//----------------------------------------------------------------------
