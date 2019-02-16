/*====================================================================*\

HorizontalRangeBar.java

Horizontal range bar class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import common.misc.DoubleRange;

//----------------------------------------------------------------------


// HORIZONTAL RANGE BAR CLASS


public class HorizontalRangeBar
	extends RangeBar
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_WIDTH	= 32;

	private static final	int	MARKER_HEIGHT		= 7;
	private static final	int	MARKER_AREA_HEIGHT	= 9;
	private static final	int	CENTRAL_AREA_HEIGHT	= 10;
	private static final	int	HEIGHT				= 2 * BORDER_WIDTH + 2 * MARKER_AREA_HEIGHT + 2 +
																						CENTRAL_AREA_HEIGHT;
	private static final	int	END_REGION_WIDTH	= 5;
	private static final	int	MIN_RANGE_X			= BORDER_WIDTH + END_REGION_WIDTH;
	private static final	int	EXTENT_ADJUSTMENT	= 2 * BORDER_WIDTH + 2 * END_REGION_WIDTH + 1;

	// Icons
	private static final	ImageIcon	MARKER_TOP		= new ImageIcon(ImageData.MARKER_TOP);
	private static final	ImageIcon	MARKER_BOTTOM	= new ImageIcon(ImageData.MARKER_BOTTOM);

	private interface ImageData
	{
		// Image: rangeBarMarkerT-11x7.png
		byte[]	MARKER_TOP	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xDE, (byte)0x6E, (byte)0xB7,
			(byte)0x5D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3C, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0x60, (byte)0x60, (byte)0x60, (byte)0xF8,
			(byte)0x4F, (byte)0x02, (byte)0x66, (byte)0x78, (byte)0x40, (byte)0xA4, (byte)0x42, (byte)0x90,
			(byte)0x3A, (byte)0x06, (byte)0x05, (byte)0x22, (byte)0x34, (byte)0x3C, (byte)0x00, (byte)0xAB,
			(byte)0xFB, (byte)0xFF, (byte)0xFF, (byte)0x3F, (byte)0x21, (byte)0x0D, (byte)0x60, (byte)0x85,
			(byte)0x60, (byte)0x75, (byte)0x20, (byte)0x02, (byte)0x8F, (byte)0x06, (byte)0xB8, (byte)0x42,
			(byte)0x14, (byte)0xC5, (byte)0x58, (byte)0x34, (byte)0xA0, (byte)0x28, (byte)0xC4, (byte)0x50,
			(byte)0x8C, (byte)0xA4, (byte)0xE1, (byte)0x00, (byte)0xBA, (byte)0x42, (byte)0x10, (byte)0x06,
			(byte)0x00, (byte)0xF5, (byte)0x96, (byte)0x6A, (byte)0x61, (byte)0xF9, (byte)0xB7, (byte)0x59,
			(byte)0x15, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E,
			(byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// Image: rangeBarMarkerB-11x7.png
		byte[]	MARKER_BOTTOM	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xDE, (byte)0x6E, (byte)0xB7,
			(byte)0x5D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3A, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x3F,
			(byte)0x03, (byte)0x32, (byte)0x06, (byte)0x02, (byte)0x05, (byte)0x20, (byte)0x3E, (byte)0x00,
			(byte)0xA2, (byte)0x31, (byte)0xE4, (byte)0xB0, (byte)0x28, (byte)0x7C, (byte)0x00, (byte)0xC4,
			(byte)0xFF, (byte)0xA1, (byte)0xB4, (byte)0x02, (byte)0x56, (byte)0xC5, (byte)0x68, (byte)0x0A,
			(byte)0xFF, (byte)0x63, (byte)0xD3, (byte)0x80, (byte)0x4F, (byte)0x21, (byte)0x86, (byte)0x06,
			(byte)0x42, (byte)0x0A, (byte)0x51, (byte)0x34, (byte)0x30, (byte)0x10, (byte)0xA1, (byte)0x10,
			(byte)0x59, (byte)0x03, (byte)0x51, (byte)0x0A, (byte)0xC1, (byte)0x18, (byte)0x00, (byte)0xA8,
			(byte)0x7F, (byte)0x6A, (byte)0x61, (byte)0xE3, (byte)0x85, (byte)0xEB, (byte)0xED, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE,
			(byte)0x42, (byte)0x60, (byte)0x82
		};
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public HorizontalRangeBar(int width)
	{
		this(width, DEFAULT_RANGE);
	}

	//------------------------------------------------------------------

	public HorizontalRangeBar(int         width,
							  DoubleRange range)
	{
		// Initialise instance fields
		this.width = Math.max(MIN_WIDTH, width);
		changeListeners = new ArrayList<>();
		activeBound = Bound.NONE;
		unitIncrement = 1.0 / (double)widthToExtent(width);
		blockIncrement = 10.0 * unitIncrement;

		// Set range
		setRange(range);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int widthToExtent(int width)
	{
		return (width - EXTENT_ADJUSTMENT);
	}

	//------------------------------------------------------------------

	public static int extentToWidth(int extent)
	{
		return (extent + EXTENT_ADJUSTMENT);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(width, HEIGHT);
	}

	//------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics gr)
	{
		// Draw background
		Rectangle rect = gr.getClipBounds();
		gr.setColor(BACKGROUND_COLOUR);
		gr.fillRect(rect.x, rect.y, rect.width, rect.height);

		// Get dimensions
		int width = getWidth();
		int height = getHeight();

		// Draw horizontal lines that bound central area
		gr.setColor(INNER_BORDER_COLOUR);
		int x1 = 1;
		int x2 = width - 2;
		int y1 = BORDER_WIDTH + MARKER_AREA_HEIGHT;
		int y2 = height - BORDER_WIDTH - MARKER_AREA_HEIGHT - 1;
		gr.drawLine(x1, y1, x2, y1);
		gr.drawLine(x1, y2, x2, y2);

		// Draw end regions
		++y1;
		gr.setColor(END_REGION_COLOUR);
		gr.fillRect(x1, y1, END_REGION_WIDTH + 1, CENTRAL_AREA_HEIGHT);
		gr.fillRect(x2 - END_REGION_WIDTH, y1, END_REGION_WIDTH + 1, CENTRAL_AREA_HEIGHT);

		// Fill central area
		gr.setColor(RANGE_BACKGROUND_COLOUR);
		gr.fillRect(x1 + END_REGION_WIDTH + 1, y1, x2 - x1 - 2 * END_REGION_WIDTH - 1,
					CENTRAL_AREA_HEIGHT);

		// Draw range area
		gr.setColor(RANGE_COLOUR);
		gr.fillRect(rangeX1, y1, rangeX2 - rangeX1 + 1, CENTRAL_AREA_HEIGHT);

		// Draw markers
		if (isFocusOwner())
		{
			gr.drawImage(MARKER_TOP.getImage(), rangeX2 - END_REGION_WIDTH,
						 BORDER_WIDTH + MARKER_AREA_HEIGHT - MARKER_HEIGHT, null);
			gr.drawImage(MARKER_BOTTOM.getImage(), rangeX1 - END_REGION_WIDTH,
						 HEIGHT - BORDER_WIDTH - MARKER_AREA_HEIGHT, null);
		}
		else
		{
			gr.setColor(MARKER_COLOUR);
			int y = BORDER_WIDTH + MARKER_AREA_HEIGHT - MARKER_HEIGHT;
			gr.drawLine(rangeX2, y, rangeX2, y + MARKER_HEIGHT - 1);
			y = HEIGHT - BORDER_WIDTH - MARKER_AREA_HEIGHT;
			gr.drawLine(rangeX1, y, rangeX1, y + MARKER_HEIGHT - 1);
		}

		// Draw border
		gr.setColor(borderColour);
		gr.drawRect(0, 0, width - 1, height - 1);
	}

	//------------------------------------------------------------------

	@Override
	protected void setActiveBound(MouseEvent event)
	{
		int y = event.getY();
		activeBound = (y < BORDER_WIDTH + MARKER_AREA_HEIGHT + 1 + CENTRAL_AREA_HEIGHT / 2) ? Bound.UPPER
																							: Bound.LOWER;
	}

	//------------------------------------------------------------------

	@Override
	protected DoubleRange getRange(MouseEvent event)
	{
		DoubleRange range = null;
		switch (activeBound)
		{
			case LOWER:
				range = getRange(event.getX(), rangeX2);
				break;

			case UPPER:
				range = getRange(rangeX1, event.getX());
				break;

			case NONE:
				// do nothing
				break;
		}
		return range;
	}

	//------------------------------------------------------------------

	@Override
	protected void setRange(DoubleRange range,
							Bound       bound)
	{
		range = getBoundedRange(range);
		if (range.upperBound < range.lowerBound)
			range.upperBound = range.lowerBound;
		if ((this.range == null) || !this.range.equals(range))
		{
			this.range = range;
			double extent = (double)widthToExtent(width);
			rangeX1 = BORDER_WIDTH + END_REGION_WIDTH + (int)Math.round(range.lowerBound * extent);
			rangeX2 = BORDER_WIDTH + END_REGION_WIDTH + (int)Math.round(range.upperBound * extent);
			repaint();
			fireStateChanged(bound);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private DoubleRange getRange(int x1,
								 int x2)
	{
		x1 = Math.min(Math.max(MIN_RANGE_X, x1), width - MIN_RANGE_X - 1);
		if (x1 > rangeX2)
			x1 = rangeX2;

		x2 = Math.min(Math.max(MIN_RANGE_X, x2), width - MIN_RANGE_X - 1);
		if (x2 < rangeX1)
			x2 = rangeX1;

		double extent = (double)widthToExtent(width);
		return new DoubleRange((double)(x1 - BORDER_WIDTH - END_REGION_WIDTH) / extent,
							   (double)(x2 - BORDER_WIDTH - END_REGION_WIDTH) / extent);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int	width;
	private	int	rangeX1;
	private	int	rangeX2;

}

//----------------------------------------------------------------------
