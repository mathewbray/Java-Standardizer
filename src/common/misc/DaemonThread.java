/*====================================================================*\

DaemonThread.java

Class: daemon thread.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// CLASS: DAEMON THREAD


public class DaemonThread
	extends Thread
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DaemonThread()
	{
		// Set attributes
		setDaemon(true);
	}

	//------------------------------------------------------------------

	public DaemonThread(Runnable runnable)
	{
		// Call superclass constructor
		super(runnable);

		// Set attributes
		setDaemon(true);
	}

	//------------------------------------------------------------------

	public DaemonThread(Runnable runnable,
						String   name)
	{
		// Call superclass constructor
		super(runnable, name);

		// Set attributes
		setDaemon(true);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static DaemonThread create()
	{
		return new DaemonThread();
	}

	//------------------------------------------------------------------

	public static DaemonThread create(Runnable runnable)
	{
		return new DaemonThread(runnable);
	}

	//------------------------------------------------------------------

	public static DaemonThread create(Runnable runnable,
									  String   name)
	{
		return new DaemonThread(runnable, name);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
