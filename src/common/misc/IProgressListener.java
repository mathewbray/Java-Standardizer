/*====================================================================*\

IProgressListener.java

Progress listener interface.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// PROGRESS LISTENER INTERFACE


public interface IProgressListener
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	void setProgress(double fractionDone);

	//------------------------------------------------------------------

	boolean isTaskCancelled();

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------