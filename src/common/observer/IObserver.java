/*====================================================================*\

IObserver.java

Interface: observer of observable object.

\*====================================================================*/


// PACKAGE


package common.observer;

//----------------------------------------------------------------------


// INTERFACE: OBSERVER OF OBSERVABLE OBJECT


@FunctionalInterface
public interface IObserver<T>
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Notifies this observer that the value of the specified observable object has changed.
	 * @param observable  the observable object that has changed.
	 * @param oldValue    the old value of {@code observable}.
	 * @param newValue    the new value of {@code observable}.
	 */

	void changed(Observable<T> observable,
				 T             oldValue,
				 T             newValue);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
