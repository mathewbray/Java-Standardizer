/*====================================================================*\

EnvelopeNodeValueDialog.java

Envelope node value dialog box class.

\*====================================================================*/


// PACKAGE


package common.envelope;

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
import java.awt.event.WindowEvent;

import java.awt.geom.Point2D;

import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import common.gui.DoubleSpinner;
import common.gui.FButton;
import common.gui.FLabel;
import common.gui.GuiUtils;

import common.misc.DoubleRange;
import common.misc.KeyAction;

//----------------------------------------------------------------------


// ENVELOPE NODE VALUE DIALOG BOX CLASS


public class EnvelopeNodeValueDialog
	extends JDialog
	implements ActionListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	Insets	BUTTON_MARGINS	= new Insets(2, 4, 2, 4);

	private static final	String	X_STR	= "x";
	private static final	String	Y_STR	= "y";

	// Commands
	private interface Command
	{
		String	ACCEPT	= "accept";
		String	CLOSE	= "close";
	}

	private static final	KeyAction.KeyCommandPair[]	KEY_COMMANDS	=
	{
		new KeyAction.KeyCommandPair(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE)
		};

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// NODE VALUE INTERFACE


	public interface INodeValueEditor
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		double getNodeValue();

		//--------------------------------------------------------------

		void setNodeValue(double value);

		//--------------------------------------------------------------

		void setMinimumValue(double minValue);

		//--------------------------------------------------------------

		void setMaximumValue(double maxValue);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// COORDINATE SPINNER CLASS


	public static class CoordinateSpinner
		extends DoubleSpinner
		implements INodeValueEditor
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public CoordinateSpinner(double       stepSize,
								 int          maxLength,
								 NumberFormat format)

		{
			super(0.0, 0.0, 1.0, stepSize, maxLength, format);
			GuiUtils.setAppFont(Constants.FontKey.TEXT_FIELD, this);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : INodeValueEditor interface
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws NumberFormatException
		 */

		public double getNodeValue()
		{
			return getDoubleValue();
		}

		//--------------------------------------------------------------

		public void setNodeValue(double value)
		{
			setDoubleValue(value);
		}

		//--------------------------------------------------------------

		public void setMinimumValue(double minValue)
		{
			setMinimum(minValue);
		}

		//--------------------------------------------------------------

		public void setMaximumValue(double maxValue)
		{
			setMaximum(maxValue);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private EnvelopeNodeValueDialog(Window          owner,
									EnvelopeView    envelopeView,
									Envelope.NodeId nodeId)
	{

		// Call superclass constructor
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);

		// Initialise instance fields
		xRange = envelopeView.getNodeXMinMax(nodeId);
		yRange = envelopeView.getNodeYMinMax(nodeId);


		//----  Control panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel controlPanel = new JPanel(gridBag);

		int gridY = 0;

		// Label: x
		JLabel xLabel = new FLabel(X_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = Constants.COMPONENT_INSETS;
		gridBag.setConstraints(xLabel, gbc);
		controlPanel.add(xLabel);

		// Spinner: x
		Envelope.Node node = envelopeView.getNode(nodeId);

		xSpinner = envelopeView.createXSpinner();
		if (xSpinner instanceof INodeValueEditor)
		{
			((INodeValueEditor)xSpinner).setMinimumValue(xRange.lowerBound);
			((INodeValueEditor)xSpinner).setMaximumValue(xRange.upperBound);
			((INodeValueEditor)xSpinner).setNodeValue(node.x);
		}
		xSpinner.setEnabled(!node.fixedX);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = Constants.COMPONENT_INSETS;
		gridBag.setConstraints(xSpinner, gbc);
		controlPanel.add(xSpinner);

		// Label: y
		JLabel yLabel = new FLabel(Y_STR);

		gbc.gridx = 0;
		gbc.gridy = gridY;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = Constants.COMPONENT_INSETS;
		gridBag.setConstraints(yLabel, gbc);
		controlPanel.add(yLabel);

		// Spinner: y
		double y = 0.0;
		boolean enabled = false;
		if (node instanceof Envelope.SimpleNode)
		{
			Envelope.SimpleNode n = (Envelope.SimpleNode)node;
			y = n.y;
			enabled = !n.fixedY;
		}
		if (node instanceof Envelope.CompoundNode)
		{
			Envelope.CompoundNode n = (Envelope.CompoundNode)node;
			y = n.y[nodeId.bandIndex];
			enabled = !n.isFixedY(nodeId.bandIndex);
		}

		ySpinner = envelopeView.createYSpinner();
		if (ySpinner instanceof INodeValueEditor)
		{
			((INodeValueEditor)ySpinner).setMinimumValue(yRange.lowerBound);
			((INodeValueEditor)ySpinner).setMaximumValue(yRange.upperBound);
			((INodeValueEditor)ySpinner).setNodeValue(y);
		}
		ySpinner.setEnabled(enabled);

		gbc.gridx = 1;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = Constants.COMPONENT_INSETS;
		gridBag.setConstraints(ySpinner, gbc);
		controlPanel.add(ySpinner);


		//----  Button panel

		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 4, 0));

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

		JPanel mainPanel = new JPanel(gridBag);
		GuiUtils.setPaddedRaisedBevelBorder(mainPanel, 3, 4, 4, 4);

		gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		mainPanel.add(controlPanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 0, 0, 0);
		gridBag.setConstraints(buttonPanel, gbc);
		mainPanel.add(buttonPanel);

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

		// Set location of dialog box
		Point point = envelopeView.nodeToPoint(nodeId);
		Point location = new Point(point.x + 1, point.y + 1);
		SwingUtilities.convertPointToScreen(location, envelopeView);
		setLocation(GuiUtils.getComponentLocation(this, location));

		// Set default button
		getRootPane().setDefaultButton(okButton);

		// Set focus
		if (xSpinner.isEnabled())
			xSpinner.requestFocusInWindow();
		else
			ySpinner.requestFocusInWindow();

		// Show dialog
		setVisible(true);

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Point2D.Double showDialog(Component       parent,
											EnvelopeView    envelopeView,
											Envelope.NodeId nodeId)
	{
		return new EnvelopeNodeValueDialog(GuiUtils.getWindow(parent), envelopeView, nodeId).
																							getNodePoint();
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

	private Point2D.Double getNodePoint()
	{
		Point2D.Double point = null;
		if (accepted)
		{
			if ((xSpinner instanceof INodeValueEditor) && (ySpinner instanceof INodeValueEditor))
				point = new Point2D.Double(((INodeValueEditor)xSpinner).getNodeValue(),
										   ((INodeValueEditor)ySpinner).getNodeValue());
		}
		return point;
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
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	boolean		accepted;
	private	JSpinner	xSpinner;
	private	JSpinner	ySpinner;
	private	DoubleRange	xRange;
	private	DoubleRange	yRange;

}

//----------------------------------------------------------------------
