/*====================================================================*\

VerticalRangeBar.java

Vertical range bar class.

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


// VERTICAL RANGE BAR CLASS


public class VerticalRangeBar
	extends RangeBar
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_HEIGHT	= 32;

	private static final	int	MARKER_WIDTH		= 7;
	private static final	int	MARKER_AREA_WIDTH	= 9;
	private static final	int	CENTRAL_AREA_WIDTH	= 10;
	private static final	int	WIDTH				= 2 * BORDER_WIDTH + 2 * MARKER_AREA_WIDTH + 2 +
																						CENTRAL_AREA_WIDTH;
	private static final	int	END_REGION_HEIGHT	= 5;
	private static final	int	MIN_RANGE_Y			= BORDER_WIDTH + END_REGION_HEIGHT;
	private static final	int	EXTENT_ADJUSTMENT	= 2 * BORDER_WIDTH + 2 * END_REGION_HEIGHT + 1;

	// Icons
	private static final	ImageIcon	MARKER_LEFT		= new ImageIcon(ImageData.MARKER_LEFT);
	private static final	ImageIcon	MARKER_RIGHT	= new ImageIcon(ImageData.MARKER_RIGHT);

	private interface ImageData
	{
		// Image: rangeBarMarkerL-7x11.png
		byte[]	MARKER_LEFT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0B,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB3, (byte)0x90, (byte)0x97,
			(byte)0xA8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3B, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0x60, (byte)0x60, (byte)0x60, (byte)0xF8,
			(byte)0x0F, (byte)0xC4, (byte)0x0F, (byte)0x80, (byte)0x58, (byte)0xE1, (byte)0xFF, (byte)0xFF,
			(byte)0xFF, (byte)0x0C, (byte)0xC8, (byte)0x98, (byte)0x01, (byte)0x2A, (byte)0x89, (byte)0x55,
			(byte)0x01, (byte)0xB2, (byte)0x24, (byte)0x86, (byte)0x02, (byte)0x74, (byte)0x49, (byte)0x14,
			(byte)0x05, (byte)0xD8, (byte)0x24, (byte)0xE1, (byte)0x0A, (byte)0x70, (byte)0x49, (byte)0x82,
			(byte)0xF0, (byte)0x01, (byte)0x92, (byte)0x75, (byte)0xE2, (byte)0xB4, (byte)0x13, (byte)0xA7,
			(byte)0x6B, (byte)0x71, (byte)0xFA, (byte)0x13, (byte)0x6B, (byte)0x08, (byte)0x01, (byte)0x00,
			(byte)0x0A, (byte)0xE2, (byte)0x6A, (byte)0x61, (byte)0xEA, (byte)0x56, (byte)0x79, (byte)0x8F,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44,
			(byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// Image: rangeBarMarkerR-7x11.png
		byte[]	MARKER_RIGHT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0B,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB3, (byte)0x90, (byte)0x97,
			(byte)0xA8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x34, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0xDA, (byte)0x63, (byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x3F,
			(byte)0x03, (byte)0x32, (byte)0x06, (byte)0x02, (byte)0x05, (byte)0x20, (byte)0x7E, (byte)0x00,
			(byte)0xC4, (byte)0x78, (byte)0x24, (byte)0x90, (byte)0x25, (byte)0x31, (byte)0x24, (byte)0x60,
			(byte)0x92, (byte)0x58, (byte)0x25, (byte)0x20, (byte)0x18, (byte)0xA7, (byte)0x04, (byte)0x58,
			(byte)0xF2, (byte)0x00, (byte)0x0E, (byte)0x09, (byte)0x02, (byte)0x3A, (byte)0xF1, (byte)0xDA,
			(byte)0x49, (byte)0xD0, (byte)0xB5, (byte)0x04, (byte)0xFD, (byte)0x89, (byte)0x4D, (byte)0x01,
			(byte)0x00, (byte)0x3C, (byte)0xE8, (byte)0x6A, (byte)0x61, (byte)0xB3, (byte)0x97, (byte)0x93,
			(byte)0x3E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E,
			(byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public VerticalRangeBar(int height)
	{
		this(height, DEFAULT_RANGE);
	}

	//------------------------------------------------------------------

	public VerticalRangeBar(int         height,
							DoubleRange range)
	{
		// Initialise instance fields
		this.height = Math.max(MIN_HEIGHT, height);
		changeListeners = new ArrayList<>();
		activeBound = Bound.NONE;
		unitIncrement = 1.0 / (double)heightToExtent(height);
		blockIncrement = 10.0 * unitIncrement;

		// Set range
		setRange(range);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int heightToExtent(int height)
	{
		return (height - EXTENT_ADJUSTMENT);
	}

	//------------------------------------------------------------------

	public static int extentToHeight(int extent)
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
		return new Dimension(WIDTH, height);
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

		// Draw vertical lines that bound central area
		gr.setColor(INNER_BORDER_COLOUR);
		int x1 = BORDER_WIDTH + MARKER_AREA_WIDTH;
		int x2 = width - BORDER_WIDTH - MARKER_AREA_WIDTH - 1;
		int y1 = 1;
		int y2 = height - 2;
		gr.drawLine(x1, y1, x1, y2);
		gr.drawLine(x2, y1, x2, y2);

		// Draw end regions
		++x1;
		gr.setColor(END_REGION_COLOUR);
		gr.fillRect(x1, y1, CENTRAL_AREA_WIDTH, END_REGION_HEIGHT + 1);
		gr.fillRect(x1, y2 - END_REGION_HEIGHT, CENTRAL_AREA_WIDTH, END_REGION_HEIGHT + 1);

		// Fill central area
		gr.setColor(RANGE_BACKGROUND_COLOUR);
		gr.fillRect(x1, y1 + END_REGION_HEIGHT + 1, CENTRAL_AREA_WIDTH,
					y2 - y1 - 2 * END_REGION_HEIGHT - 1);

		// Draw range area
		gr.setColor(RANGE_COLOUR);
		gr.fillRect(x1, rangeY2, CENTRAL_AREA_WIDTH, rangeY1 - rangeY2 + 1);

		// Draw markers
		if (isFocusOwner())
		{
			gr.drawImage(MARKER_LEFT.getImage(), BORDER_WIDTH + MARKER_AREA_WIDTH - MARKER_WIDTH,
						 rangeY1 - END_REGION_HEIGHT, null);
			gr.drawImage(MARKER_RIGHT.getImage(), WIDTH - BORDER_WIDTH - MARKER_AREA_WIDTH,
						 rangeY2 - END_REGION_HEIGHT, null);
		}
		else
		{
			gr.setColor(MARKER_COLOUR);
			int x = BORDER_WIDTH + MARKER_AREA_WIDTH - MARKER_WIDTH;
			gr.drawLine(x, rangeY1, x + MARKER_WIDTH - 1, rangeY1);
			x = WIDTH - BORDER_WIDTH - MARKER_AREA_WIDTH;
			gr.drawLine(x, rangeY2, x + MARKER_WIDTH - 1, rangeY2);
		}

		// Draw border
		gr.setColor(borderColour);
		gr.drawRect(0, 0, width - 1, height - 1);
	}

	//------------------------------------------------------------------

	@Override
	protected void setActiveBound(MouseEvent event)
	{
			int x = event.getX();
			activeBound = (x < BORDER_WIDTH + MARKER_AREA_WIDTH + 1 + CENTRAL_AREA_WIDTH / 2) ? Bound.LOWER
																							  : Bound.UPPER;
	}

	//------------------------------------------------------------------

	@Override
	protected DoubleRange getRange(MouseEvent event)
	{
		DoubleRange range = null;
		switch (activeBound)
		{
			case LOWER:
				range = getRange(event.getY(), rangeY2);
				break;

			case UPPER:
				range = getRange(rangeY1, event.getY());
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
			double extent = (double)heightToExtent(height);
			rangeY1 = BORDER_WIDTH + END_REGION_HEIGHT +
													(int)Math.round((1.0 - range.lowerBound) * extent);
			rangeY2 = BORDER_WIDTH + END_REGION_HEIGHT +
													(int)Math.round((1.0 - range.upperBound) * extent);
			repaint();
			fireStateChanged(bound);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private DoubleRange getRange(int y1,
								 int y2)
	{
		y1 = Math.min(Math.max(MIN_RANGE_Y, y1), height - MIN_RANGE_Y - 1);
		if (y1 < rangeY2)
			y1 = rangeY2;

		y2 = Math.min(Math.max(MIN_RANGE_Y, y2), height - MIN_RANGE_Y - 1);
		if (y2 > rangeY1)
			y2 = rangeY1;

		double extent = (double)heightToExtent(height);
		return new DoubleRange(1.0 - (double)(y1 - BORDER_WIDTH - END_REGION_HEIGHT) / extent,
							   1.0 - (double)(y2 - BORDER_WIDTH - END_REGION_HEIGHT) / extent);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int	height;
	private	int	rangeY1;
	private	int	rangeY2;

}

//----------------------------------------------------------------------
