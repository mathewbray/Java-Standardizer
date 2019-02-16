/*====================================================================*\

TaskProgressDialog.java

Task progress dialog box class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import common.exception.AppException;

import common.misc.IProgressListener;
import common.misc.KeyAction;
import common.misc.NumberUtils;
import common.misc.StringUtils;
import common.misc.Task;
import common.misc.TextUtils;

//----------------------------------------------------------------------


// TASK PROGRESS DIALOG BOX CLASS


public class TaskProgressDialog
	extends JDialog
	implements ActionListener, IProgressListener, IProgressView
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	INFO_FIELD_WIDTH	= 480;

	private static final	int	PROGRESS_BAR_WIDTH		= INFO_FIELD_WIDTH;
	private static final	int	PROGRESS_BAR_HEIGHT		= 15;
	private static final	int	PROGRESS_BAR_MAX_VALUE	= 10000;

	private static final	String	TIME_ELAPSED_STR	= "Time elapsed:";
	private static final	String	TIME_REMAINING_STR	= "Estimated time remaining:";

	// Commands
	private interface Command
	{
		String	CLOSE	= "close";
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// INFORMATION FIELD CLASS


	private static class InfoField
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private InfoField()
		{
			GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
			setPreferredSize(new Dimension(INFO_FIELD_WIDTH,
										   getFontMetrics(getFont()).getHeight()));
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Draw background
			gr.setColor(getBackground());
			gr.fillRect(0, 0, getWidth(), getHeight());

			// Draw text
			if (text != null)
			{
				// Set rendering hints for text antialiasing and fractional metrics
				TextRendering.setHints((Graphics2D)gr);

				// Draw text
				gr.setColor(Color.BLACK);
				gr.drawString(text, 0, gr.getFontMetrics().getAscent());
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void setText(String text)
		{
			if (!StringUtils.equal(text, this.text))
			{
				this.text = text;
				repaint();
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	text;

	}

	//==================================================================


	// TIME FIELD CLASS


	private static class TimeField
		extends JComponent
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MIN_TIME	= 0;
		private static final	int	MAX_TIME	= 100 * 60 * 60 * 1000 - 1;

		private static final	String	SEPARATOR_STR		= ":";
		private static final	String	PROTOTYPE_STR		= "00" + SEPARATOR_STR + "00" +
																					SEPARATOR_STR + "00";
		private static final	String	OUT_OF_RANGE_STR	= "--";

		private static final	Color	TEXT_COLOUR	= new Color(0, 0, 144);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TimeField()
		{
			GuiUtils.setAppFont(Constants.FontKey.MAIN, this);
			FontMetrics fontMetrics = getFontMetrics(getFont());
			setPreferredSize(new Dimension(fontMetrics.stringWidth(PROTOTYPE_STR),
										   fontMetrics.getAscent() + fontMetrics.getDescent()));
			setOpaque(true);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Create copy of graphics context
			gr = gr.create();

			// Draw background
			gr.setColor(getBackground());
			gr.fillRect(0, 0, getWidth(), getHeight());

			// Draw text
			if (text != null)
			{
				// Set rendering hints for text antialiasing and fractional metrics
				TextRendering.setHints((Graphics2D)gr);

				// Draw text
				FontMetrics fontMetrics = gr.getFontMetrics();
				gr.setColor(TEXT_COLOUR);
				gr.drawString(text, getWidth() - fontMetrics.stringWidth(text),
							  fontMetrics.getAscent());
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public void setTime(int milliseconds)
		{
			String str = OUT_OF_RANGE_STR;
			if ((milliseconds >= MIN_TIME) && (milliseconds <= MAX_TIME))
			{
				int seconds = milliseconds / 1000;
				int minutes = seconds / 60;
				int hours = minutes / 60;
				str = ((hours == 0) ? Integer.toString(minutes)
									: Integer.toString(hours) + SEPARATOR_STR +
												NumberUtils.uIntToDecString(minutes % 60, 2, '0')) +
									SEPARATOR_STR + NumberUtils.uIntToDecString(seconds % 60, 2, '0');
			}
			setText(str);
		}

		//--------------------------------------------------------------

		public void setText(String text)
		{
			if (!StringUtils.equal(text, this.text))
			{
				this.text = text;
				repaint();
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	text;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// SET INFO CLASS


	private class DoSetInfo
		implements Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DoSetInfo(String str,
						  File   file)
		{
			text = (file == null) ? str : getText(str, getPathname(file), getFileSeparatorChar());
		}

		//--------------------------------------------------------------

		private DoSetInfo(String str,
						  URL    url)
		{
			text = (url == null) ? str : getText(str, url.toString(), '/');
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			infoField.setText(text);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String getText(String str,
							   String pathname,
							   char   separatorChar)
		{
			str = (str == null) ? "" : str + " ";
			FontMetrics fontMetrics = infoField.getFontMetrics(infoField.getFont());
			int maxWidth = infoField.getWidth() - fontMetrics.stringWidth(str);
			return (str + TextUtils.getLimitedWidthPathname(pathname, fontMetrics, maxWidth,
															separatorChar));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	text;

	}

	//==================================================================


	// SET PROGRESS CLASS


	private class DoSetProgress
		implements Runnable
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	UPDATE_INTERVAL	= 500;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DoSetProgress(int    index,
							  double value)
		{
			this.index = index;
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Runnable interface
	////////////////////////////////////////////////////////////////////

		public void run()
		{
			if (value < 0.0)
			{
				progressBars[index].setIndeterminate(true);

				if (timeElapsedField != null)
				{
					timeElapsedField.setText(null);
					timeRemainingField.setText(null);
				}
			}
			else
			{
				if (progressBars[index].isIndeterminate())
					progressBars[index].setIndeterminate(false);
				progressBars[index].setValue((int)Math.round(value * (double)PROGRESS_BAR_MAX_VALUE));

				if (index == timeProgressIndex)
				{
					if (value == 0.0)
					{
						startTime = System.currentTimeMillis();
						timeElapsedField.setTime(0);
						timeRemainingField.setText(null);
						updateTime = startTime + UPDATE_INTERVAL;
					}
					else
					{
						long currentTime = System.currentTimeMillis();
						if (currentTime >= updateTime)
						{
							long timeElapsed = currentTime - startTime;
							timeElapsedField.setTime((int)timeElapsed);
							timeRemainingField.setTime((int)Math.round((1.0 / value - 1.0) *
																			(double)timeElapsed) + 500);
							updateTime = currentTime + UPDATE_INTERVAL;
						}
					}
				}
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	int		index;
		private	double	value;

	}

	//==================================================================


	// DEFERRED OUTPUT CLASS


	private class DeferredOutput
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DeferredOutput()
		{
			progresses = new double[progressBars.length];
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void init(String str,
						  File   file,
						  URL    url)
		{
			this.str = str;
			this.file = file;
			this.url = url;
		}

		//--------------------------------------------------------------

		private void write()
		{
			if (url == null)
				new DoSetInfo(str, file).run();
			else
				new DoSetInfo(str, url).run();
			for (int i = 0; i < progresses.length; i++)
			{
				if (progresses[i] > 0.0)
					new DoSetProgress(i, 0.0).run();
				new DoSetProgress(i, progresses[i]).run();
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String		str;
		private	File		file;
		private	URL			url;
		private	double[]	progresses;

	}

	//==================================================================


	// WINDOW EVENT HANDLER CLASS


	private class WindowEventHandler
		extends WindowAdapter
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private WindowEventHandler()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void windowOpened(WindowEvent event)
		{
			synchronized (deferredOutputLock)
			{
				if (deferredOutput != null)
					deferredOutput.write();
			}
		}

		//--------------------------------------------------------------

		@Override
		public void windowClosing(WindowEvent event)
		{
			if (isVisible())
				location = getLocation();
			if (stopped)
				dispose();
			else
				Task.setCancelled(true);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected TaskProgressDialog(Window  owner,
								 String  titleStr,
								 Task    task,
								 int     delay,
								 int     numProgressBars,
								 int     timeProgressIndex,
								 boolean canCancel)
		throws AppException
	{

		// Call superclass constructor
		super(owner, titleStr, Dialog.ModalityType.APPLICATION_MODAL);

		// Set icons
		if (owner != null)
			setIconImages(owner.getIconImages());

		// Initialise instance fields
		deferredOutputLock = new Object();
		this.timeProgressIndex = timeProgressIndex;


		//----  Info field

		infoField = new InfoField();


		//----  Progress bars

		progressBars = new JProgressBar[numProgressBars];
		for (int i = 0; i < progressBars.length; i++)
		{
			progressBars[i] = new JProgressBar(0, PROGRESS_BAR_MAX_VALUE);
			progressBars[i].setPreferredSize(new Dimension(PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT));
		}


		//----  Time panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		int gridY = 0;

		boolean hasTimeFields = timeProgressIndex >= 0;
		JPanel timePanel = null;
		if (hasTimeFields)
		{
			timePanel = new JPanel(gridBag);

			// Label: time elapsed
			JLabel timeElapsedLabel = new FLabel(TIME_ELAPSED_STR);

			gbc.gridx = 0;
			gbc.gridy = gridY;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(timeElapsedLabel, gbc);
			timePanel.add(timeElapsedLabel);

			// Field: time elapsed
			timeElapsedField = new TimeField();

			gbc.gridx = 1;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 4, 0, 0);
			gridBag.setConstraints(timeElapsedField, gbc);
			timePanel.add(timeElapsedField);

			// Label: time remaining
			JLabel timeRemainingLabel = new FLabel(TIME_REMAINING_STR);

			gbc.gridx = 0;
			gbc.gridy = gridY;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(1, 0, 0, 0);
			gridBag.setConstraints(timeRemainingLabel, gbc);
			timePanel.add(timeRemainingLabel);

			// Field: time remaining
			timeRemainingField = new TimeField();

			gbc.gridx = 1;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(1, 4, 0, 0);
			gridBag.setConstraints(timeRemainingField, gbc);
			timePanel.add(timeRemainingField);
		}


		//----  Button panel

		JPanel buttonPanel = null;
		if (canCancel)
		{
			buttonPanel = new JPanel(new GridLayout(1, 0, 0, 0));

			// Button: cancel
			cancelButton = new FButton(Constants.CANCEL_STR);
			cancelButton.setActionCommand(Command.CLOSE);
			cancelButton.addActionListener(this);
			buttonPanel.add(cancelButton);
		}


		//----  Bottom panel

		JComponent bottomPanel = null;
		if (hasTimeFields && canCancel)
		{
			bottomPanel = new JPanel(gridBag);

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(timePanel, gbc);
			bottomPanel.add(timePanel);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 24, 0, 0);
			gridBag.setConstraints(buttonPanel, gbc);
			bottomPanel.add(buttonPanel);
		}
		else if (hasTimeFields && !canCancel)
			bottomPanel = timePanel;
		else if (!hasTimeFields && canCancel)
			bottomPanel = buttonPanel;

		if (bottomPanel == null)
			bottomPanel = GuiUtils.createFiller();
		else
			bottomPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));


		//----  Main panel

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

		gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(8, 2, 6, 2);
		gridBag.setConstraints(infoField, gbc);
		mainPanel.add(infoField);

		for (int i = 0; i < progressBars.length; i++)
		{
			gbc.gridx = 0;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(6, 0, 0, 0);
			gridBag.setConstraints(progressBars[i], gbc);
			mainPanel.add(progressBars[i]);
		}

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = hasTimeFields ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
		gbc.insets = new Insets(3, 0, 0, 0);
		gridBag.setConstraints(bottomPanel, gbc);
		mainPanel.add(bottomPanel);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE, this);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window opening and closing
		addWindowListener(new WindowEventHandler());

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(location);

		// Set default button
		getRootPane().setDefaultButton(cancelButton);

		// Start task
		Task.setProgressView(this);
		Task.setException(null, true);
		Task.setCancelled(false);
		task.start();

		// Delay before making dialog visible
		long endTime = System.currentTimeMillis() + delay;
		while (!stopped)
		{
			if (System.currentTimeMillis() >= endTime)
			{
				setVisible(!stopped);
				break;
			}
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// ignore
			}
		}

		// Throw any exception from task thread
		Task.throwIfException();

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(Command.CLOSE))
			onClose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IProgressView interface
////////////////////////////////////////////////////////////////////////

	public void setInfo(String str)
	{
		setInfo(str, (File)null);
	}

	//------------------------------------------------------------------

	public void setInfo(String str,
						File   file)
	{
		if (isVisible())
			SwingUtilities.invokeLater(new DoSetInfo(str, file));
		else
		{
			synchronized (deferredOutputLock)
			{
				if (deferredOutput == null)
					deferredOutput = new DeferredOutput();
				deferredOutput.init(str, file, null);
			}
		}
	}

	//------------------------------------------------------------------

	public void setInfo(String str,
						URL    url)
	{
		if (isVisible())
			SwingUtilities.invokeLater(new DoSetInfo(str, url));
		else
		{
			synchronized (deferredOutputLock)
			{
				if (deferredOutput == null)
					deferredOutput = new DeferredOutput();
				deferredOutput.init(str, null, url);
			}
		}
	}

	//------------------------------------------------------------------

	public void setProgress(int    index,
							double value)
	{
		if (isVisible())
			SwingUtilities.invokeLater(new DoSetProgress(index, value));
		else
		{
			synchronized (deferredOutputLock)
			{
				if (deferredOutput == null)
					deferredOutput = new DeferredOutput();
				deferredOutput.progresses[index] = value;
			}
		}
	}

	//------------------------------------------------------------------

	public void waitForIdle()
	{
		EventQueue eventQueue = getToolkit().getSystemEventQueue();
		while (eventQueue.peekEvent() != null)
		{
			// do nothing
		}
	}

	//------------------------------------------------------------------

	public void close()
	{
		stopped = true;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (isVisible())
					dispatchEvent(new WindowEvent(TaskProgressDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IProgressListener interface
////////////////////////////////////////////////////////////////////////

	public void setProgress(double fractionDone)
	{
		setProgress(0, fractionDone);
	}

	//------------------------------------------------------------------

	public boolean isTaskCancelled()
	{
		return Task.isCancelled();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	protected int getNumProgressBars()
	{
		return progressBars.length;
	}

	//------------------------------------------------------------------

	protected String getPathname(File file)
	{
		String pathname = null;
		try
		{
			try
			{
				pathname = file.getCanonicalPath();
			}
			catch (Exception e)
			{
				pathname = file.getAbsolutePath();
			}
		}
		catch (SecurityException e)
		{
			pathname = file.getPath();
		}
		return pathname;
	}

	//------------------------------------------------------------------

	protected char getFileSeparatorChar()
	{
		return File.separatorChar;
	}

	//------------------------------------------------------------------

	private void onClose()
	{
		cancelButton.setEnabled(false);
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Point	location;

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private volatile	boolean	stopped;

	private	DeferredOutput		deferredOutput;
	private	Object				deferredOutputLock;
	private	int					timeProgressIndex;
	private	long				startTime;
	private	long				updateTime;
	private	InfoField			infoField;
	private	JProgressBar[]		progressBars;
	private	TimeField			timeElapsedField;
	private	TimeField			timeRemainingField;
	private	JButton				cancelButton;

}

//----------------------------------------------------------------------
