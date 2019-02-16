/*====================================================================*\

ArrowButton.java

Arrow button class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;

//----------------------------------------------------------------------


// ARROW BUTTON CLASS


public class ArrowButton
	extends JButton
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum Direction
	{
		UP,
		DOWN,
		LEFT,
		RIGHT
	}

	public enum Active
	{
		PRESSED,
		ARMED
	}

	public static final		Color	BORDER_COLOUR				= new Color(144, 152, 144);
	public static final		Color	FOCUSED_BORDER_COLOUR		= Color.BLACK;
	public static final		Color	BACKGROUND_COLOUR			= new Color(184, 216, 184);
	public static final		Color	ACTIVE_BACKGROUND_COLOUR	= Colours.FOCUSED_SELECTION_BACKGROUND;
	public static final		Color	DISABLED_BACKGROUND_COLOUR	= new Color(208, 208, 208);
	public static final		Color	FOREGROUND_COLOUR			= Colours.FOREGROUND;
	public static final		Color	DISABLED_FOREGROUND_COLOUR	= new Color(144, 144, 144);

	private static final	int	MIN_WIDTH	= 1;
	private static final	int	MAX_WIDTH	= (1 << 15) - 1;

	private static final	int	MIN_HEIGHT	= 1;
	private static final	int	MAX_HEIGHT	= (1 << 15) - 1;

	private static final	int	MIN_ARROW_SIZE	= 1;
	private static final	int	MAX_ARROW_SIZE	= (1 << 15) - 1;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * @throws IllegalArgumentException
	 */

	public ArrowButton(int width,
					   int height,
					   int arrowSize)
	{
		this(width, height, arrowSize, null, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public ArrowButton(int       width,
					   int       height,
					   int       arrowSize,
					   Direction direction)
	{
		this(width, height, arrowSize, direction, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public ArrowButton(int     width,
					   int     height,
					   int     arrowSize,
					   boolean bar)
	{
		this(width, height, arrowSize, null, bar);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public ArrowButton(int       width,
					   int       height,
					   int       arrowSize,
					   Direction direction,
					   boolean   bar)
	{
		// Validate arguments
		if ((width < MIN_WIDTH) || (width > MAX_WIDTH) || (height < MIN_HEIGHT) || (height > MAX_HEIGHT) ||
			 (arrowSize < MIN_ARROW_SIZE) || (arrowSize > MAX_ARROW_SIZE))
			throw new IllegalArgumentException();

		// Initialise instance fields
		this.width = width;
		this.height = height;
		this.arrowSize = arrowSize;
		this.direction = direction;
		this.bar = bar;
		active = Active.ARMED;

		// Set attributes
		setBorder(null);
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

	@Override
	protected void paintComponent(Graphics gr)
	{
		// Create copy of graphics context
		gr = gr.create();

		// Fill background
		Rectangle rect = gr.getClipBounds();
		gr.setColor(isEnabled() ? isActive() ? ACTIVE_BACKGROUND_COLOUR
											 : BACKGROUND_COLOUR
								: DISABLED_BACKGROUND_COLOUR);
		gr.fillRect(rect.x, rect.y, rect.width, rect.height);

		// Draw arrow
		if (direction != null)
		{
			gr.setColor(isEnabled() ? FOREGROUND_COLOUR : DISABLED_FOREGROUND_COLOUR);
			switch (direction)
			{
				case UP:
				{
					int x1 = (width - 1) / 2;
					int x2 = x1;
					int y = (height - arrowSize) / 2;
					if (bar)
						y += 2;
					for (int i = 0; i < arrowSize; i++)
					{
						gr.drawLine(x1, y, x2, y);
						--x1;
						++x2;
						++y;
					}

					if (bar)
					{
						y -= arrowSize + 3;
						++x1;
						--x2;
						gr.drawLine(x1, y, x2, y);
						++y;
						gr.drawLine(x1, y, x2, y);
					}
					break;
				}

				case DOWN:
				{
					int x1 = (width - 1) / 2;
					int x2 = x1;
					int y = height - 1 - (height - arrowSize) / 2;
					if (bar)
						y -= 2;
					for (int i = 0; i < arrowSize; i++)
					{
						gr.drawLine(x1, y, x2, y);
						--x1;
						++x2;
						--y;
					}

					if (bar)
					{
						y += arrowSize + 3;
						++x1;
						--x2;
						gr.drawLine(x1, y, x2, y);
						--y;
						gr.drawLine(x1, y, x2, y);
					}
					break;
				}

				case LEFT:
				{
					int x = (width - arrowSize) / 2;
					if (bar)
						x += 2;
					int y1 = (height - 1) / 2;
					int y2 = y1;
					for (int i = 0; i < arrowSize; i++)
					{
						gr.drawLine(x, y1, x, y2);
						++x;
						--y1;
						++y2;
					}

					if (bar)
					{
						x -= arrowSize + 3;
						++y1;
						--y2;
						gr.drawLine(x, y1, x, y2);
						++x;
						gr.drawLine(x, y1, x, y2);
					}
					break;
				}

				case RIGHT:
				{
					int x = width - 1 - (width - arrowSize) / 2;
					if (bar)
						x -= 2;
					int y1 = (height - 1) / 2;
					int y2 = y1;
					for (int i = 0; i < arrowSize; i++)
					{
						gr.drawLine(x, y1, x, y2);
						--x;
						--y1;
						++y2;
					}

					if (bar)
					{
						x += arrowSize + 3;
						++y1;
						--y2;
						gr.drawLine(x, y1, x, y2);
						--x;
						gr.drawLine(x, y1, x, y2);
					}
					break;
				}
			}
		}

		// Draw border
		gr.setColor(BORDER_COLOUR);
		gr.drawRect(0, 0, width - 1, height - 1);
		if (isFocusOwner())
		{
			((Graphics2D)gr).setStroke(Constants.BASIC_DASH);
			gr.setColor(FOCUSED_BORDER_COLOUR);
			gr.drawRect(1, 1, width - 3, height - 3);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Direction getDirection()
	{
		return direction;
	}

	//------------------------------------------------------------------

	public boolean isBar()
	{
		return bar;
	}

	//------------------------------------------------------------------

	public boolean isActive()
	{
		if (active != null)
		{
			switch (active)
			{
				case PRESSED:
					return getModel().isPressed();

				case ARMED:
					return getModel().isArmed();
			}
		}
		return false;
	}

	//------------------------------------------------------------------

	public void setDirection(Direction direction)
	{
		if (this.direction != direction)
		{
			this.direction = direction;
			repaint();
		}
	}

	//------------------------------------------------------------------

	public void setActive(Active active)
	{
		if (this.active != active)
		{
			this.active = active;
			repaint();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int			width;
	private	int			height;
	private	int			arrowSize;
	private	Direction	direction;
	private	boolean		bar;
	private	Active		active;

}

//----------------------------------------------------------------------
