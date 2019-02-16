/*====================================================================*\

ComboBoxRenderer.java

Combo box renderer class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

//----------------------------------------------------------------------


// COMBO BOX RENDERER CLASS


public class ComboBoxRenderer<E>
	extends JComponent
	implements ListCellRenderer<E>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	TOP_MARGIN		= 1;
	private static final	int	BOTTOM_MARGIN	= TOP_MARGIN;
	private static final	int	LEFT_MARGIN		= 3;
	private static final	int	RIGHT_MARGIN	= 5;

	private static final	Color	BORDER_COLOUR	= Color.BLACK;

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// TOOLTIP SOURCE INTERFACE


	public interface ITooltipSource
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		String getTooltip();

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ComboBoxRenderer(JComboBox<E> comboBox)
	{
		setOpaque(false);
		setFocusable(false);
		this.comboBox = comboBox;
	}

	//------------------------------------------------------------------

	public ComboBoxRenderer(JComboBox<E> comboBox,
							int          maxTextWidth)
	{
		this(comboBox);
		this.maxTextWidth = maxTextWidth;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListCellRenderer interface
////////////////////////////////////////////////////////////////////////

	public Component getListCellRendererComponent(JList<? extends E> list,
												  E                  value,
												  int                index,
												  boolean            isSelected,
												  boolean            cellHasFocus)
	{
		isListItem = (index >= 0);
		setBackground(isSelected ? (selectionBackgroundColour == null) ? list.getSelectionBackground()
																	   : selectionBackgroundColour
								 : (backgroundColour == null) ? list.getBackground()
															  : backgroundColour);
		setForeground(isSelected ? (selectionForegroundColour == null) ? list.getSelectionForeground()
																	   : selectionForegroundColour
								 : (foregroundColour == null) ? list.getForeground()
															  : foregroundColour);
		setFont(list.getFont());
		text = (value == null) ? "" : value.toString();
		FontMetrics fontMetrics = getFontMetrics(getFont());
		textWidth = fontMetrics.stringWidth(text);
		if ((maxTextWidth > 0) && (textWidth > maxTextWidth))
			reduceTextWidth(fontMetrics);
		textHeight = fontMetrics.getHeight();
		if (value instanceof ITooltipSource)
			setToolTipText(((ITooltipSource)value).getTooltip());

		return this;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getPreferredSize()
	{
		int width = (maxTextWidth > 0) ? maxTextWidth : textWidth;
		return new Dimension(LEFT_MARGIN + width + RIGHT_MARGIN, TOP_MARGIN + textHeight + BOTTOM_MARGIN);
	}

	//------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics gr)
	{
		// Create copy of graphics context
		gr = gr.create();

		// Fill background
		if (isBackgroundSet())
		{
			Rectangle rect = gr.getClipBounds();
			gr.setColor(getBackground());
			gr.fillRect(rect.x, rect.y, rect.width, rect.height);
		}

		// Set rendering hints for text antialiasing and fractional metrics
		TextRendering.setHints((Graphics2D)gr);

		// Draw text
		FontMetrics fontMetrics = gr.getFontMetrics();
		gr.setColor(getForeground());
		gr.drawString(text, LEFT_MARGIN, TOP_MARGIN + fontMetrics.getAscent());

		// Draw border
		if (!isListItem && (comboBox != null) && comboBox.isFocusOwner())
		{
			((Graphics2D)gr).setStroke(Constants.BASIC_DASH);
			gr.setColor(BORDER_COLOUR);
			gr.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setDefaultColours()
	{
		backgroundColour = Colours.BACKGROUND;
		foregroundColour = Colours.FOREGROUND;
		selectionBackgroundColour = Colours.FOCUSED_SELECTION_BACKGROUND;
		selectionForegroundColour = Colours.FOCUSED_SELECTION_FOREGROUND;
	}

	//------------------------------------------------------------------

	public void setBackgroundColour(Color colour)
	{
		backgroundColour = colour;
	}

	//------------------------------------------------------------------

	public void setForegroundColour(Color colour)
	{
		foregroundColour = colour;
	}

	//------------------------------------------------------------------

	public void setSelectionBackgroundColour(Color colour)
	{
		selectionBackgroundColour = colour;
	}

	//------------------------------------------------------------------

	public void setSelectionForegroundColour(Color colour)
	{
		selectionForegroundColour = colour;
	}

	//------------------------------------------------------------------

	protected void reduceTextWidth(FontMetrics fontMetrics)
	{
		int maxWidth = maxTextWidth - fontMetrics.stringWidth(Constants.ELLIPSIS_STR);
		char[] chars = text.toCharArray();
		int length = chars.length;
		while ((length > 0) && (textWidth > maxWidth))
			textWidth -= fontMetrics.charWidth(chars[--length]);
		text = new String(chars, 0, length) + Constants.ELLIPSIS_STR;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	JComboBox<E>	comboBox;
	protected	int				maxTextWidth;
	protected	int				textWidth;
	protected	int				textHeight;
	protected	String			text;
	protected	boolean			isListItem;
	protected	Color			backgroundColour;
	protected	Color			foregroundColour;
	protected	Color			selectionBackgroundColour;
	protected	Color			selectionForegroundColour;

}

//----------------------------------------------------------------------
