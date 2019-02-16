/*====================================================================*\

SelectionList.java

Selection list class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Dimension;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

//----------------------------------------------------------------------


// SELECTION LIST CLASS


public class SelectionList<E>
	extends JList<E>
	implements FocusListener
{

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
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// SELECTION LIST CELL RENDERER CLASS


	protected static class CellRenderer<E>
		extends JLabel
		implements ListCellRenderer<E>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	DEFAULT_VERTICAL_MARGIN		= 2;
		private static final	int	DEFAULT_HORIZONTAL_MARGIN	= 4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		protected CellRenderer(int verticalMargin,
							   int horizontalMargin)
		{
			this.verticalMargin = verticalMargin;
			this.horizontalMargin = horizontalMargin;
			GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ListCellRenderer interface
	////////////////////////////////////////////////////////////////////

		public Component getListCellRendererComponent(JList<? extends E> list,
													  E                  value,
													  int                index,
													  boolean            isSelected,
													  boolean            cellHasFocus)
		{
			setBorder((cellHasFocus && (list.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION))
						? BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(Colours.List.FOCUSED_CELL_BORDER.getColour()),
							BorderFactory.createEmptyBorder(verticalMargin - 1, horizontalMargin - 1,
															verticalMargin - 1, horizontalMargin - 1))
						: BorderFactory.createEmptyBorder(verticalMargin, horizontalMargin,
														  verticalMargin, horizontalMargin));
			setBackground(isSelected ? list.isFocusOwner()
													? Colours.List.FOCUSED_SELECTION_BACKGROUND.getColour()
													: list.getSelectionBackground()
									 : list.getBackground());
			setForeground(isSelected ? list.isFocusOwner()
													? Colours.List.FOCUSED_SELECTION_FOREGROUND.getColour()
													: list.getSelectionForeground()
									 : list.getForeground());
			setText(value.toString());
			if (value instanceof ITooltipSource)
				setToolTipText(((ITooltipSource)value).getTooltip());
			return this;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getColumnWidth()
		{
			return GuiUtils.getCharWidth('0', getFontMetrics(getFont()));
		}

		//--------------------------------------------------------------

		public int getRowHeight()
		{
			return (2 * verticalMargin + getFontMetrics(getFont()).getHeight());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	int	verticalMargin;
		private	int	horizontalMargin;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public SelectionList(int numViewableColumns,
						 int numViewableRows)
	{
		_init(numViewableColumns, numViewableRows, CellRenderer.DEFAULT_VERTICAL_MARGIN,
			  CellRenderer.DEFAULT_HORIZONTAL_MARGIN);
	}

	//------------------------------------------------------------------

	public SelectionList(E[] items,
						 int numViewableColumns,
						 int numViewableRows)
	{
		super(items);
		_init(numViewableColumns, numViewableRows, CellRenderer.DEFAULT_VERTICAL_MARGIN,
			  CellRenderer.DEFAULT_HORIZONTAL_MARGIN);
	}

	//------------------------------------------------------------------

	public SelectionList(int numViewableColumns,
						 int numViewableRows,
						 int verticalMargin,
						 int horizontalMargin)
	{
		_init(numViewableColumns, numViewableRows, verticalMargin, horizontalMargin);
	}

	//------------------------------------------------------------------

	public SelectionList(E[] items,
						 int numViewableColumns,
						 int numViewableRows,
						 int verticalMargin,
						 int horizontalMargin)
	{
		super(items);
		_init(numViewableColumns, numViewableRows, verticalMargin, horizontalMargin);
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
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return ((numViewableColumns == 0)
						? super.getPreferredScrollableViewportSize()
						: new Dimension(numViewableColumns *
													((CellRenderer<?>)getCellRenderer()).getColumnWidth(),
										getVisibleRowCount() * getFixedCellHeight()));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private void _init(int numViewableColumns,
					   int numViewableRows,
					   int verticalMargin,
					   int horizontalMargin)
	{
		this.numViewableColumns = numViewableColumns;
		CellRenderer<E> cellRenderer = new CellRenderer<>(verticalMargin, horizontalMargin);
		setCellRenderer(cellRenderer);
		setFixedCellHeight(cellRenderer.getRowHeight());
		setVisibleRowCount(numViewableRows);
		setBackground(Colours.List.BACKGROUND.getColour());
		setSelectionBackground(Colours.List.SELECTION_BACKGROUND.getColour());
		setForeground(Colours.List.FOREGROUND.getColour());
		setSelectionForeground(Colours.List.SELECTION_FOREGROUND.getColour());
		addFocusListener(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int	numViewableColumns;

}

//----------------------------------------------------------------------
