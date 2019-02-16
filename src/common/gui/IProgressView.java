/*====================================================================*\

ProgressView.java

Progress view interface.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

//----------------------------------------------------------------------


// PROGRESS VIEW INTERFACE


public interface IProgressView
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	void setInfo(String str);

	//------------------------------------------------------------------

	void setInfo(String str,
				 File   file);

	//------------------------------------------------------------------

	void setProgress(int    index,
					 double value);

	//------------------------------------------------------------------

	void waitForIdle();

	//------------------------------------------------------------------

	void close();

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------