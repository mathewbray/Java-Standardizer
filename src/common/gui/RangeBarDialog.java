/*====================================================================*\

RangeBarDialog.java

Range bar dialog box class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import common.misc.KeyAction;

//----------------------------------------------------------------------


// RANGE BAR DIALOG BOX CLASS


public abstract class RangeBarDialog
	extends JDialog
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	Color	BORDER_COLOUR	= new Color(224, 96, 32);

	// Commands
	private interface Command
	{
		String	CLOSE	= "close";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
									 Command.CLOSE)
	};

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// VERTICAL-BAR DIALOG CLASS


	public static class Vertical
		extends RangeBarDialog
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Vertical(Window   owner,
						RangeBar rangeBar)
		{
			// Call superclass constructor
			super(owner, rangeBar);

			// Set attributes
			rangeBar.setBorderColour(BORDER_COLOUR);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		protected void addComponents(JPanel             panel,
									 RangeBar           rangeBar,
									 JButton            closeButton,
									 GridBagLayout      gridBag,
									 GridBagConstraints gbc)
		{
			// Close button
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(closeButton, gbc);
			panel.add(closeButton);

			// Range bar
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(-1, 0, 0, 0);
			gridBag.setConstraints(rangeBar, gbc);
			panel.add(rangeBar);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// HORIZONTAL-BAR DIALOG CLASS


	public static class Horizontal
		extends RangeBarDialog
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Horizontal(Window   owner,
						  RangeBar rangeBar)
		{
			// Call superclass constructor
			super(owner, rangeBar);

			// Set attributes
			rangeBar.setBorderColour(BORDER_COLOUR);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		protected void addComponents(JPanel             panel,
									 RangeBar           rangeBar,
									 JButton            closeButton,
									 GridBagLayout      gridBag,
									 GridBagConstraints gbc)
		{
			// Range bar
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(rangeBar, gbc);
			panel.add(rangeBar);

			// Close button
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.insets = new Insets(0, -1, 0, 0);
			gridBag.setConstraints(closeButton, gbc);
			panel.add(closeButton);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLOSE BUTTON CLASS


	private static class CloseButton
		extends JButton
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MARGIN	= 2;

		private static final	Color	BACKGROUND_COLOUR			= Colours.BACKGROUND;
		private static final	Color	ACTIVE_BACKGROUND_COLOUR	= Colours.FOCUSED_SELECTION_BACKGROUND;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private CloseButton()
		{
			super(RangeIcon.CROSS);
			setBorder(null);
			setFocusable(false);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(2 * MARGIN + RangeIcon.CROSS.getIconWidth(),
								 2 * MARGIN + RangeIcon.CROSS.getIconHeight());
		}

		//--------------------------------------------------------------

		@Override
		protected void paintComponent(Graphics gr)
		{
			// Get dimensions
			int width = getWidth();
			int height = getHeight();

			// Fill interior
			boolean active = (isSelected() != getModel().isArmed());
			gr.setColor(active ? ACTIVE_BACKGROUND_COLOUR : BACKGROUND_COLOUR);
			gr.fillRect(1, 1, width - 2, height - 2);

			// Draw icon
			Icon icon = getIcon();
			icon.paintIcon(this, gr, (width - icon.getIconWidth()) / 2,
						   (height - icon.getIconHeight()) / 2);

			// Draw border
			gr.setColor(BORDER_COLOUR);
			gr.drawRect(0, 0, width - 1, height - 1);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private RangeBarDialog(Window   owner,
						   RangeBar rangeBar)
	{

		// Call superclass constructor
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);


		//----  Main panel

		// Button: close
		JButton closeButton = new CloseButton();
		closeButton.setActionCommand(Command.CLOSE);
		closeButton.addActionListener(this);

		// Add components to panel
		GridBagLayout gridBag = new GridBagLayout();
		JPanel mainPanel = new JPanel(gridBag);
		addComponents(mainPanel, rangeBar, closeButton, gridBag, new GridBagConstraints());

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, this, KEY_COMMANDS);


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Omit frame from dialog box
		setUndecorated(true);

		// Dispose of window when it is closed
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract void addComponents(JPanel             panel,
										  RangeBar           rangeBar,
										  JButton            closeButton,
										  GridBagLayout      gridBag,
										  GridBagConstraints gbc);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		if (event.getActionCommand().equals(Command.CLOSE))
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
