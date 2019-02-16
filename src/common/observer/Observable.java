/*====================================================================*\

Observable.java

Class: observable object.

\*====================================================================*/


// PACKAGE


package common.observer;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: OBSERVABLE OBJECT


public abstract class Observable<T>
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: OBSERVABLE OBJECT, IDENTITY TEST


	public static class Identity<T>
		extends Observable<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Identity()
		{
		}

		//--------------------------------------------------------------

		public Identity(T initialValue)
		{
			super(initialValue);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected boolean isChanged(T oldValue,
									T newValue)
		{
			return (newValue != oldValue);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: OBSERVABLE OBJECT, EQUALITY TEST


	public static class Equality<T>
		extends Observable<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Equality()
		{
		}

		//--------------------------------------------------------------

		public Equality(T initialValue)
		{
			super(initialValue);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected boolean isChanged(T oldValue,
									T newValue)
		{
			return (newValue == null) ? (oldValue != null) : !newValue.equals(oldValue);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected Observable()
	{
		observers = new ArrayList<>();
	}

	//------------------------------------------------------------------

	protected Observable(T initialValue)
	{
		this();
		value = initialValue;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract boolean isChanged(T oldValue,
										 T newValue);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public T get()
	{
		return value;
	}

	//------------------------------------------------------------------

	public void set(T value)
	{
		// Get old value
		T oldValue = this.value;

		// Set new value
		this.value = value;

		// If value has changed, notify observers
		if (isChanged(oldValue, value))
		{
			for (int i = observers.size() - 1; i >= 0; i--)
				observers.get(i).changed(this, oldValue, value);
		}
	}

	//------------------------------------------------------------------

	public void addObserver(IObserver<T> observer)
	{
		observers.add(observer);
	}

	//------------------------------------------------------------------

	public boolean removeObserver(IObserver<T> observer)
	{
		return observers.remove(observer);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	T					value;
	private	List<IObserver<T>>	observers;

}

//----------------------------------------------------------------------
