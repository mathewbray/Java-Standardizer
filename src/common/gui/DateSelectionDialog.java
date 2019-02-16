/*====================================================================*\

DateSelectionDialog.java

Date selection dialog box class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Dialog;
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

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import common.misc.Date;
import common.misc.KeyAction;

//----------------------------------------------------------------------


// DATE SELECTION DIALOG CLASS


public class DateSelectionDialog
	extends JDialog
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	Insets	BUTTON_MARGINS	= new Insets(1, 2, 1, 2);

	// Commands
	private interface Command
	{
		String	ACCEPT	= "accept";
		String	CLOSE	= "close";
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// ACCEPT ACTION CLASS


	private class AcceptAction
		extends AbstractAction
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private AcceptAction()
		{
			putValue(Action.ACTION_COMMAND_KEY, Command.ACCEPT);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ActionListener interface
	////////////////////////////////////////////////////////////////////

		public void actionPerformed(ActionEvent event)
		{
			DateSelectionDialog.this.actionPerformed(event);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DateSelectionDialog(Window  owner,
							   Point   location,
							   Date    date,
							   boolean showAdjacentMonths,
							   String  key)
	{
		this(owner, location, date, 0, showAdjacentMonths, key);
	}

	//------------------------------------------------------------------

	public DateSelectionDialog(Window  owner,
							   Point   location,
							   Date    date,
							   int     firstDayOfWeek,
							   boolean showAdjacentMonths,
							   String  key)
	{

		// Call superclass constructor
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);

		// Initialise instance fields
		this.key = key;


		//----  Date selection panel

		dateSelectionPanel = new DateSelectionPanel(date, firstDayOfWeek, showAdjacentMonths);
		dateSelectionPanel.setAcceptAction(new AcceptAction());


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 8, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		// Button: OK
		JButton okButton = new FButton(Constants.OK_STR);
		okButton.setMargin(BUTTON_MARGINS);
		okButton.setActionCommand(Command.ACCEPT);
		okButton.addActionListener(this);
		buttonPanel.add(okButton);

		// Button: cancel
		JButton cancelButton = new FButton(Constants.CANCEL_STR);
		cancelButton.setMargin(BUTTON_MARGINS);
		cancelButton.setActionCommand(Command.CLOSE);
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);


		//----  Main panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createLineBorder(Colours.LINE_BORDER));

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(-1, -1, 0, -1);
		gridBag.setConstraints(dateSelectionPanel, gbc);
		mainPanel.add(dateSelectionPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		mainPanel.add(buttonPanel);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE, this);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Omit frame from dialog box
		setUndecorated(true);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window closing
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent event)
			{
				onClose();
			}
		});

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
		if (location == null)
			location = locations.get(key);
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(GuiUtils.getComponentLocation(this, location));

		// Set default button
		getRootPane().setDefaultButton(okButton);

		// Set focus
		dateSelectionPanel.requestFocusInWindow();

		// Show dialog
		setVisible(true);

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Date showDialog(Component parent,
								  Point     location,
								  Date      date,
								  boolean   showAdjacentMonths,
								  String    key)
	{
		return showDialog(parent, location, date, 0, showAdjacentMonths, key);
	}

	//------------------------------------------------------------------

	public static Date showDialog(Component parent,
								  Point     location,
								  Date      date,
								  int       firstDayOfWeek,
								  boolean   showAdjacentMonths,
								  String    key)
	{
		return new DateSelectionDialog(GuiUtils.getWindow(parent), location, date, firstDayOfWeek,
									   showAdjacentMonths, key).getDate();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();

		if (command.equals(Command.ACCEPT))
			onAccept();

		else if (command.equals(Command.CLOSE))
			onClose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Date getDate()
	{
		return (accepted ? dateSelectionPanel.getDate() : null);
	}

	//------------------------------------------------------------------

	private void onAccept()
	{
		accepted = true;
		onClose();
	}

	//------------------------------------------------------------------

	private void onClose()
	{
		locations.put(key, getLocation());
		setVisible(false);
		dispose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Map<String, Point>	locations	= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	String				key;
	private	boolean				accepted;
	private	DateSelectionPanel	dateSelectionPanel;

}

//----------------------------------------------------------------------
