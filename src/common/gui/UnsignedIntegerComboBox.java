/*====================================================================*\

UnsignedIntegerComboBox.java

Unsigned integer combo box class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import common.textfield.IntegerField;

//----------------------------------------------------------------------


// UNSIGNED INTEGER COMBO BOX CLASS


public class UnsignedIntegerComboBox
	extends JComboBox<Integer>
	implements FocusListener, PopupMenuListener
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// DEFAULT COMPARATOR CLASS


	private static class DefaultComparator
		implements Comparator<Integer>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DefaultComparator()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Comparator interface
	////////////////////////////////////////////////////////////////////

		@Override
		public int compare(Integer value1,
						   Integer value2)
		{
			return value1.compareTo(value2);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// EDITOR CLASS


	private class Editor
		extends IntegerField.Unsigned
		implements ComboBoxEditor
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	VERTICAL_MARGIN		= 1;
		private static final	int	HORIZONTAL_MARGIN	= 4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Editor(int maxLength)
		{
			super(maxLength);
			setBorder(BorderFactory.createEmptyBorder(VERTICAL_MARGIN, HORIZONTAL_MARGIN,
													  VERTICAL_MARGIN, HORIZONTAL_MARGIN));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ComboBoxEditor interface
	////////////////////////////////////////////////////////////////////

		public Component getEditorComponent()
		{
			return this;
		}

		//--------------------------------------------------------------

		public Object getItem()
		{
			return getText();
		}

		//--------------------------------------------------------------

		public void setItem(Object obj)
		{
			setText((obj == null) ? null : obj.toString());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void processKeyEvent(KeyEvent event)
		{
			if ((event.getID() == KeyEvent.KEY_PRESSED) && (event.getKeyCode() == KeyEvent.VK_ENTER) &&
				 (event.getModifiersEx() == 0))
			{
				event.consume();
				accept();
			}
			super.processKeyEvent(event);
		}

		//--------------------------------------------------------------

		@Override
		protected int getColumnWidth()
		{
			return GuiUtils.getCharWidth('0', getFontMetrics(getFont()));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getFieldWidth()
		{
			return (getColumns() * getColumnWidth());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UnsignedIntegerComboBox(int maxLength)
	{
		// Set font and border
		GuiUtils.setAppFont(Constants.FontKey.TEXT_FIELD, this);
		GuiUtils.setPaddedLineBorder(this, 1);

		// Set editor
		Editor editor = new Editor(maxLength);
		setEditor(editor);
		setEditable(true);

		// Set renderer
		setRenderer(new ComboBoxRenderer<>(this, editor.getFieldWidth()));

		// Add listeners
		editor.addFocusListener(this);
		addPopupMenuListener(this);
	}

	//------------------------------------------------------------------

	public UnsignedIntegerComboBox(int           maxLength,
								   int           maxNumItems,
								   List<Integer> items)
	{
		// Initialise combo box
		this(maxLength);

		// Initialise instance fields
		this.maxNumItems = maxNumItems;

		// Set items
		int numItems = items.size();
		if ((maxNumItems > 0) && (numItems > maxNumItems))
			numItems = maxNumItems;
		for (int i = 0; i < numItems; i++)
			addItem(items.get(i));
	}

	//------------------------------------------------------------------

	public UnsignedIntegerComboBox(int           maxLength,
								   int           maxNumItems,
								   List<Integer> items,
								   int           selectedItem)
	{
		this(maxLength, maxNumItems, items);
		if (selectedItem >= 0)
			setSelectedItem(selectedItem);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Comparator<Integer> getDefaultComparator()
	{
		return defaultComparator;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : FocusListener interface
////////////////////////////////////////////////////////////////////////

	public void focusGained(FocusEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void focusLost(FocusEvent event)
	{
		updateList();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : PopupMenuListener interface
////////////////////////////////////////////////////////////////////////

	public void popupMenuCanceled(PopupMenuEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void popupMenuWillBecomeInvisible(PopupMenuEvent event)
	{
		updateList();
	}

	//------------------------------------------------------------------

	public void popupMenuWillBecomeVisible(PopupMenuEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean isEmpty()
	{
		return ((Editor)getEditor()).isEmpty();
	}

	//------------------------------------------------------------------

	public int getValue()
	{
		return ((Editor)getEditor()).getValue();
	}

	//------------------------------------------------------------------

	public List<Integer> getItems()
	{
		List<Integer> items = new ArrayList<>();
		for (int i = 0; i < getItemCount(); i++)
			items.add(getItemAt(i));
		return items;
	}

	//------------------------------------------------------------------

	public List<Integer> getItems(boolean updateList)
	{
		if (updateList)
			updateList();
		return getItems();
	}

	//------------------------------------------------------------------

	public void setItems(int[] items)
	{
		removeAllItems();
		for (int item : items)
			addItem(item);
		setSelectedIndex((items.length == 0) ? -1 : 0);
	}

	//------------------------------------------------------------------

	public void setItems(List<Integer> items)
	{
		removeAllItems();
		for (Integer item : items)
			addItem(item);
		setSelectedIndex(items.isEmpty() ? -1 : 0);
	}

	//------------------------------------------------------------------

	public void setComparator(Comparator<Integer> comparator)
	{
		if ((comparator == null) ? (this.comparator != null) : !comparator.equals(this.comparator))
		{
			this.comparator = comparator;
			updateList();
		}
	}

	//------------------------------------------------------------------

	public void setDefaultComparator()
	{
		setComparator(defaultComparator);
	}

	//------------------------------------------------------------------

	public void updateList()
	{
		try
		{
			int value = ((Editor)getEditor()).getValue();
			if (comparator == null)
			{
				insertItemAt(value, 0);
				for (int i = 1; i < getItemCount(); i++)
				{
					if (getItemAt(i) == value)
					{
						removeItemAt(i);
						break;
					}
				}
			}
			else
			{
				int index = Collections.binarySearch(getItems(), value, comparator);
				if (index < 0)
					insertItemAt(value, -index - 1);
			}
			if (maxNumItems > 0)
			{
				while (getItemCount() > maxNumItems)
					removeItemAt(getItemCount() - 1);
			}
			setSelectedItem(value);
		}
		catch (NumberFormatException e)
		{
			// ignore
		}
	}

	//------------------------------------------------------------------

	private void accept()
	{
		if (isPopupVisible())
		{
			if (isEmpty())
				getEditor().setItem(getSelectedItem());
			setPopupVisible(false);
		}
		else
			updateList();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	DefaultComparator	defaultComparator	= new DefaultComparator();

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int					maxNumItems;
	private	Comparator<Integer>	comparator;

}

//----------------------------------------------------------------------
