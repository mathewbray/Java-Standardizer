/*====================================================================*\

EditList.java

Edit list class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.LinkedList;

//----------------------------------------------------------------------


// EDIT LIST CLASS


public class EditList<T>
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// EDIT LIST ELEMENT CLASS


	public static abstract class Element<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		protected Element()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		public abstract String getText();

		//--------------------------------------------------------------

		public abstract void undo(T model);

		//--------------------------------------------------------------

		public abstract void redo(T model);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public EditList(int maxLength)
	{
		this.maxLength = maxLength;
		elements = new LinkedList<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Element<T> getUndo()
	{
		return (canUndo() ? elements.get(currentIndex - 1) : null);
	}

	//------------------------------------------------------------------

	public Element<T> getRedo()
	{
		return (canRedo() ? elements.get(currentIndex) : null);
	}

	//------------------------------------------------------------------

	public Element<T> removeUndo()
	{
		return (canUndo() ? elements.get(--currentIndex) : null);
	}

	//------------------------------------------------------------------

	public Element<T> removeRedo()
	{
		return (canRedo() ? elements.get(currentIndex++) : null);
	}

	//------------------------------------------------------------------

	public boolean canUndo()
	{
		return (currentIndex > 0);
	}

	//------------------------------------------------------------------

	public boolean canRedo()
	{
		return (currentIndex < elements.size());
	}

	//------------------------------------------------------------------

	public boolean isEmpty()
	{
		return elements.isEmpty();
	}

	//------------------------------------------------------------------

	public boolean isChanged()
	{
		return (currentIndex != unchangedIndex);
	}

	//------------------------------------------------------------------

	public void setChanged()
	{
		unchangedIndex = -1;
	}

	//------------------------------------------------------------------

	public void add(Element<T> edit)
	{
		// Remove redos
		while (elements.size() > currentIndex)
			elements.removeLast();

		// Preserve changed status if unchanged state cannot be recovered
		if (unchangedIndex > currentIndex)
			unchangedIndex = -1;

		// Remove oldest edits while list is full
		while (elements.size() >= maxLength)
		{
			elements.removeFirst();
			if (--unchangedIndex < 0)
				unchangedIndex = -1;
			if (--currentIndex < 0)
				currentIndex = 0;
		}

		// Add new edit
		elements.add(edit);
		++currentIndex;
	}

	//------------------------------------------------------------------

	public void clear()
	{
		elements.clear();
		unchangedIndex = currentIndex = 0;
	}

	//------------------------------------------------------------------

	public void reset()
	{
		while (elements.size() > currentIndex)
			elements.removeLast();

		unchangedIndex = currentIndex;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	int						maxLength;
	private	int						currentIndex;
	private	int						unchangedIndex;
	private	LinkedList<Element<T>>	elements;

}

//----------------------------------------------------------------------
