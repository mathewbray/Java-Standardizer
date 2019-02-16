/*====================================================================*\

FComboBox.java

Combo box class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JComboBox;

//----------------------------------------------------------------------


// COMBO BOX CLASS


public class FComboBox<E>
	extends JComboBox<E>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FComboBox()
	{
		_init();
	}

	//------------------------------------------------------------------

	public FComboBox(E[] items)
	{
		super(items);
		_init();
	}

	//------------------------------------------------------------------

	public FComboBox(Iterable<E> items)
	{
		super();
		_init();
		addItems(items);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public E getSelectedValue()
	{
		int index = getSelectedIndex();
		return ((index < 0) ? null : getItemAt(index));
	}

	//------------------------------------------------------------------

	public void setSelectedValue(E value)
	{
		setSelectedItem(value);
	}

	//------------------------------------------------------------------

	public void addItems(Iterable<E> items)
	{
		for (E item : items)
			addItem(item);
	}

	//------------------------------------------------------------------

	private void _init()
	{
		GuiUtils.setAppFont(Constants.FontKey.COMBO_BOX, this);
		setRenderer(new ComboBoxRenderer<>(this));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
