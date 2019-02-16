/*====================================================================*\

Task.java

Task class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

import common.exception.AppException;
import common.exception.TaskCancelledException;

import common.gui.IProgressView;

//----------------------------------------------------------------------


// TASK CLASS


public abstract class Task
	implements Runnable
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected Task()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static synchronized boolean isException()
	{
		return (exception != null);
	}

	//------------------------------------------------------------------

	public static synchronized AppException getException()
	{
		return exception;
	}

	//------------------------------------------------------------------

	public static synchronized boolean isCancelled()
	{
		return cancelled;
	}

	//------------------------------------------------------------------

	public static synchronized boolean isExceptionOrCancelled()
	{
		return ((exception != null) || cancelled);
	}

	//------------------------------------------------------------------

	public static synchronized IProgressView getProgressView()
	{
		return progressView;
	}

	//------------------------------------------------------------------

	public static synchronized void setException(AppException exception,
												 boolean      overwrite)
	{
		if (overwrite || (Task.exception == null))
			Task.exception = exception;
	}

	//------------------------------------------------------------------

	public static synchronized void setCancelled(boolean cancelled)
	{
		Task.cancelled = cancelled;
	}

	//------------------------------------------------------------------

	public static synchronized void setProgressView(IProgressView progressView)
	{
		Task.progressView = progressView;
	}

	//------------------------------------------------------------------

	public static synchronized int getNumThreads()
	{
		return threads.size();
	}

	//------------------------------------------------------------------

	public static synchronized int addThread(Thread thread)
	{
		threads.add(thread);
		return threads.size();
	}

	//------------------------------------------------------------------

	public static synchronized void removeThread()
	{
		threads.remove(Thread.currentThread());
		if (threads.isEmpty())
		{
			if (progressView != null)
			{
				progressView.close();
				progressView = null;
			}
		}
	}

	//------------------------------------------------------------------

	public static synchronized void throwIfException()
		throws AppException
	{
		if (exception != null)
			throw exception;
	}

	//------------------------------------------------------------------

	public static synchronized void throwIfCancelled()
		throws TaskCancelledException
	{
		if (cancelled)
			throw new TaskCancelledException();
	}

	//------------------------------------------------------------------

	public static synchronized void throwIfExceptionOrCancelled()
		throws AppException
	{
		throwIfException();
		throwIfCancelled();
	}

	//------------------------------------------------------------------

	public static synchronized void interrupt()
	{
		for (Thread thread : threads)
			thread.interrupt();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean start()
	{
		if (cancelled)
			return false;
		Thread thread = new Thread(this, "app-" + getClass().getName() + "-" + threadId++);
		if (addThread(thread) == 1)
			primary = true;
		thread.start();
		return true;
	}

	//------------------------------------------------------------------

	public boolean isPrimary()
	{
		return primary;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	AppException	exception;
	private static	boolean			cancelled;
	private static	IProgressView	progressView;
	private static	int				threadId;
	private static	List<Thread>	threads		= new ArrayList<>();

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	boolean	primary;

}

//----------------------------------------------------------------------
