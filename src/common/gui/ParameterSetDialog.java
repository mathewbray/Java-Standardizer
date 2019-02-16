/*====================================================================*\

ParameterSetDialog.java

Parameter set dialog box class.

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

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.exception.AppException;

import common.misc.KeyAction;
import common.misc.ParameterSet;
import common.misc.ParameterSetList;

//----------------------------------------------------------------------


// PARAMETER SET DIALOG BOX CLASS


public class ParameterSetDialog<E extends ParameterSet>
	extends JDialog
	implements ActionListener, ChangeListener, DocumentListener, ListSelectionListener,
			   SingleSelectionList.IModelListener
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	protected static final	int	SELECTION_LIST_NUM_ROWS		= 16;
	protected static final	int	SELECTION_LIST_NUM_COLUMNS	= 40;

	private static final	int	NAME_FIELD_NUM_COLUMNS	= 20;

	private static final	String	NAME_STR	= "Name";
	private static final	String	ADD_STR		= "Add";
	private static final	String	DELETE_STR	= "Delete";
	private static final	String	LOAD_STR	= "Load";
	private static final	String	UPDATE_STR	= "Update";
	private static final	String	RESET_STR	= "Reset";
	private static final	String	ERROR_STR	= "Error";
	private static final	String	CANCEL_STR	= "Cancel";

	private static final	String	READ_TITLE_STR		= "Read parameter set";
	private static final	String	WRITE_TITLE_STR		= "Write parameter set";
	private static final	String	ADD_TITLE_STR		= "Add parameter set";
	private static final	String	DELETE_TITLE_STR	= "Delete parameter set";
	private static final	String	UPDATE_TITLE_STR	= "Update parameter set";
	private static final	String	RESET_TITLE_STR		= "Reset parameters";

	private static final	String	PARAMETER_SET_STR			= "Parameter set: ";
	private static final	String	DELETE_MESSAGE_STR			= "Do you want to delete the parameter " +
																	"set?";
	private static final	String	ADD_REPLACE_MESSAGE_STR		= "A parameter set with this name  " +
																	"already exists.\nDo you want to " +
																	"replace it?";
	private static final	String	UPDATE_REPLACE_MESSAGE_STR	= "Do you want to replace the parameter " +
																	"set with the current parameters?";
	private static final	String	RESET_MESSAGE_STR			= "Do you want to reset all parameters " +
																	"to their default values?";

	private static final	String	ADD_TOOLTIP_STR		= "Add the current set of parameters to the list";
	private static final	String	DELETE_TOOLTIP_STR	= "Delete the selected parameter set from the list";
	private static final	String	LOAD_TOOLTIP_STR	= "Load the selected parameter set";
	private static final	String	UPDATE_TOOLTIP_STR	= "Update the selected parameter set with the " +
															"current parameters";
	private static final	String	RESET_TOOLTIP_STR	= "Reset all parameters to their default values";

	private static final	String[]	DC_OPTION_STRS	= { DELETE_STR, CANCEL_STR };
	private static final	String[]	RC_OPTION_STRS	= { "Replace", CANCEL_STR };
	private static final	String[]	XC_OPTION_STRS	= { "Reset", CANCEL_STR };

	// Commands
	private interface Command
	{
		String	ADD						= "add";
		String	DELETE					= "delete";
		String	LOAD					= "load";
		String	LOAD_ACCEPT				= "loadAccept";
		String	UPDATE					= "update";
		String	RESET					= "reset";
		String	MOVE_PARAMETER_SET_UP	= "moveParameterSetUp";
		String	MOVE_PARAMETER_SET_DOWN	= "moveParameterSetDown";
		String	MOVE_PARAMETER_SET		= "moveParameterSet";
		String	ACCEPT					= "accept";
		String	CLOSE					= "close";
	}

	private static final	Map<String, String>	COMMAND_MAP;

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


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
			readParamSetList();
		}

		//--------------------------------------------------------------

		@Override
		public void windowClosing(WindowEvent event)
		{
			onClose();
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected ParameterSetDialog(Window              owner,
								 String              titleStr,
								 E                   params,
								 E                   defaultParams,
								 ParameterSetList<E> paramSetList,
								 File                file)
	{
		this(owner, titleStr, params, defaultParams, paramSetList, file, null, false);
	}

	//------------------------------------------------------------------

	protected ParameterSetDialog(Window              owner,
								 String              titleStr,
								 E                   params,
								 E                   defaultParams,
								 ParameterSetList<E> paramSetList,
								 File                file,
								 File                dtdDirectory,
								 boolean             writeDtd)
	{

		// Call superclass constructor
		super(owner, titleStr, Dialog.ModalityType.APPLICATION_MODAL);

		// Set icons
		if (owner != null)
			setIconImages(owner.getIconImages());

		// Initialise instance fields
		this.params = params;
		this.defaultParams = defaultParams;
		this.paramSetList = paramSetList;
		this.file = file;
		this.dtdDirectory = dtdDirectory;
		this.writeDtd = writeDtd;


		//----  Selection list

		selectionList = createSelectionList(paramSetList);
		selectionList.addActionListener(this);
		selectionList.addListSelectionListener(this);
		selectionList.addModelListener(this);
		KeyAction.create(selectionList, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Command.LOAD, this);

		// Scroll pane: selection list
		selectionListScrollPane = new JScrollPane(selectionList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		selectionListScrollPane.getVerticalScrollBar().getModel().addChangeListener(this);
		selectionListScrollPane.getVerticalScrollBar().setFocusable(false);

		selectionList.setViewport(selectionListScrollPane.getViewport());


		//----  Name panel

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JPanel namePanel = new JPanel(gridBag);
		GuiUtils.setPaddedLineBorder(namePanel);

		// Label: name
		JLabel nameLabel = new FLabel(NAME_STR);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = Constants.COMPONENT_INSETS;
		gridBag.setConstraints(nameLabel, gbc);
		namePanel.add(nameLabel);

		// Field: name
		nameField = new FTextField(NAME_FIELD_NUM_COLUMNS);
		nameField.setActionCommand(Command.ADD);
		nameField.addActionListener(this);
		nameField.getDocument().addDocumentListener(this);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = Constants.COMPONENT_INSETS;
		gridBag.setConstraints(nameField, gbc);
		namePanel.add(nameField);


		//----  Button panel, edit buttons

		JPanel editButtonPanel = new JPanel(new GridLayout(0, 2, 8, 8));
		editButtonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// Button: add
		addButton = new FButton(ADD_STR);
		addButton.setToolTipText(ADD_TOOLTIP_STR);
		addButton.setActionCommand(Command.ADD);
		addButton.addActionListener(this);
		editButtonPanel.add(addButton);

		// Button: delete
		deleteButton = new FButton(DELETE_STR + Constants.ELLIPSIS_STR);
		deleteButton.setToolTipText(DELETE_TOOLTIP_STR);
		deleteButton.setActionCommand(Command.DELETE);
		deleteButton.addActionListener(this);
		editButtonPanel.add(deleteButton);

		// Button: load
		loadButton = new FButton(LOAD_STR);
		loadButton.setToolTipText(LOAD_TOOLTIP_STR);
		loadButton.setActionCommand(Command.LOAD);
		loadButton.addActionListener(this);
		editButtonPanel.add(loadButton);

		// Button: update
		updateButton = new FButton(UPDATE_STR + Constants.ELLIPSIS_STR);
		updateButton.setToolTipText(UPDATE_TOOLTIP_STR);
		updateButton.setActionCommand(Command.UPDATE);
		updateButton.addActionListener(this);
		editButtonPanel.add(updateButton);


		//----  Button panel, reset button

		JPanel resetButtonPanel = null;
		if (defaultParams != null)
		{
			resetButtonPanel = new JPanel(new GridLayout(0, 1, 8, 8));
			resetButtonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

			// Button: reset
			JButton resetButton = new FButton(RESET_STR + Constants.ELLIPSIS_STR);
			resetButton.setToolTipText(RESET_TOOLTIP_STR);
			resetButton.setActionCommand(Command.RESET);
			resetButton.addActionListener(this);
			resetButtonPanel.add(resetButton);

			// Update preferred size of buttons
			List<Component> buttons = new ArrayList<>();
			for (int i = 0; i < editButtonPanel.getComponentCount(); i++)
				buttons.add(editButtonPanel.getComponent(i));
			buttons.add(resetButton);
			GuiUtils.updatePreferredSize(buttons);
		}


		//----  Button panel, close buttons

		JPanel closeButtonPanel = new JPanel(new GridLayout(1, 0, 8, 0));
		closeButtonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// Button: OK
		JButton okButton = new FButton(Constants.OK_STR);
		okButton.setActionCommand(Command.ACCEPT);
		okButton.addActionListener(this);
		closeButtonPanel.add(okButton);

		// Button: cancel
		JButton cancelButton = new FButton(Constants.CANCEL_STR);
		cancelButton.setActionCommand(Command.CLOSE);
		cancelButton.addActionListener(this);
		closeButtonPanel.add(cancelButton);


		//----  Control panel

		JPanel controlPanel = new JPanel(gridBag);

		int gridY = 0;

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(namePanel, gbc);
		controlPanel.add(namePanel);

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(4, 0, 0, 0);
		gridBag.setConstraints(editButtonPanel, gbc);
		controlPanel.add(editButtonPanel);

		if (resetButtonPanel != null)
		{
			gbc.gridx = 0;
			gbc.gridy = gridY++;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0, 0, 0, 0);
			gridBag.setConstraints(resetButtonPanel, gbc);
			controlPanel.add(resetButtonPanel);
		}

		gbc.gridx = 0;
		gbc.gridy = gridY++;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(16, 0, 4, 0);
		gridBag.setConstraints(closeButtonPanel, gbc);
		controlPanel.add(closeButtonPanel);


		//----  Main panel

		JPanel mainPanel = new JPanel(gridBag);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		gridBag.setConstraints(selectionListScrollPane, gbc);
		mainPanel.add(selectionListScrollPane);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, 3, 0, 0);
		gridBag.setConstraints(controlPanel, gbc);
		mainPanel.add(controlPanel);

		// Add commands to action map
		KeyAction.create(mainPanel, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						 KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Command.CLOSE, this);

		// Update components
		updateComponents();


		//----  Window

		// Set content pane
		setContentPane(mainPanel);

		// Dispose of window explicitly
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		// Handle window events
		addWindowListener(new WindowEventHandler());

		// Prevent dialog from being resized
		setResizable(false);

		// Resize dialog to its preferred size
		pack();

		// Set location of dialog box
		Point location = locations.get(paramSetList.getApplicationKey());
		if (location == null)
			location = GuiUtils.getComponentLocation(this, owner);
		setLocation(location);

		// Set default button
		getRootPane().setDefaultButton(okButton);

		// Set focus
		nameField.requestFocusInWindow();

		// Show dialog
		setVisible(true);

	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static <E extends ParameterSet> E showDialog(Component           parent,
														String              titleStr,
														E                   params,
														E                   defaultParams,
														ParameterSetList<E> paramSetList,
														File                file)
	{
		return new ParameterSetDialog<>(GuiUtils.getWindow(parent), titleStr, params, defaultParams,
										paramSetList, file).getParameterSet();
	}

	//------------------------------------------------------------------

	public static <E extends ParameterSet> E showDialog(Component           parent,
														String              titleStr,
														E                   params,
														E                   defaultParams,
														ParameterSetList<E> paramSetList,
														File                file,
														File                dtdDirectory,
														boolean             writeDtd)
	{
		return new ParameterSetDialog<>(GuiUtils.getWindow(parent), titleStr, params, defaultParams,
										paramSetList, file, dtdDirectory, writeDtd).getParameterSet();
	}

	//------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private static <E extends ParameterSet> E createCopy(E paramSet)
		throws AppException
	{
		return (E)paramSet.create();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ActionListener interface
////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent event)
	{
		try
		{
			String command = event.getActionCommand();
			if (COMMAND_MAP.containsKey(command))
				command = COMMAND_MAP.get(command);

			if (command.equals(Command.ADD))
				onAdd();

			else if (command.equals(Command.DELETE))
				onDelete();

			else if (command.equals(Command.LOAD))
				onLoad();

			else if (command.equals(Command.LOAD_ACCEPT))
				onLoadAccept();

			else if (command.equals(Command.UPDATE))
				onUpdate();

			else if (command.equals(Command.RESET))
				onReset();

			else if (command.equals(Command.MOVE_PARAMETER_SET_UP))
				onMoveParameterSetUp();

			else if (command.equals(Command.MOVE_PARAMETER_SET_DOWN))
				onMoveParameterSetDown();

			else if (command.equals(Command.MOVE_PARAMETER_SET))
				onMoveParameterSet();

			else if (command.equals(Command.ACCEPT))
				onAccept();

			else if (command.equals(Command.CLOSE))
				onClose();
		}
		catch (AppException e)
		{
			JOptionPane.showMessageDialog(this, e, ERROR_STR, JOptionPane.ERROR_MESSAGE);
		}

		updateComponents();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ChangeListener interface
////////////////////////////////////////////////////////////////////////

	public void stateChanged(ChangeEvent event)
	{
		if (!selectionListScrollPane.getVerticalScrollBar().getValueIsAdjusting() &&
			 !selectionList.isDragging())
			selectionList.snapViewPosition();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : DocumentListener interface
////////////////////////////////////////////////////////////////////////

	public void changedUpdate(DocumentEvent event)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	public void insertUpdate(DocumentEvent event)
	{
		updateComponents();
	}

	//------------------------------------------------------------------

	public void removeUpdate(DocumentEvent event)
	{
		updateComponents();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ListSelectionListener interface
////////////////////////////////////////////////////////////////////////

	public void valueChanged(ListSelectionEvent event)
	{
		if (!event.getValueIsAdjusting())
			updateComponents();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : SingleSelectionList.IModelListener interface
////////////////////////////////////////////////////////////////////////

	public void modelChanged(SingleSelectionList.ModelEvent event)
	{
		listChanged = true;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	protected E getParameterSet()
	{
		return (accepted ? params : null);
	}

	//------------------------------------------------------------------

	protected SingleSelectionList<E> createSelectionList(ParameterSetList<E> paramSetList)
	{
		SingleSelectionList<E> selectionList = new SingleSelectionList<>(SELECTION_LIST_NUM_COLUMNS,
																		 SELECTION_LIST_NUM_ROWS,
																		 GuiUtils.getAppFont(Constants.FontKey.MAIN),
																		 paramSetList);
		selectionList.setRowHeight(selectionList.getRowHeight() + 2);
		return selectionList;
	}

	//------------------------------------------------------------------

	private void updateComponents()
	{
		boolean isSelection = selectionList.isSelection();
		boolean isParamSet = (params != null);

		nameField.setEditable(isParamSet);
		addButton.setEnabled(isParamSet && (nameField.getDocument().getLength() > 0));
		deleteButton.setEnabled(isSelection);
		loadButton.setEnabled(isSelection);
		updateButton.setEnabled(isParamSet && isSelection);
	}

	//------------------------------------------------------------------

	private void readParamSetList()
	{
		try
		{
			paramSetList.read(file, dtdDirectory);
			if (!selectionList.isEmpty())
			{
				selectionList.setSelectedIndex(0);
				selectionList.requestFocusInWindow();
			}
			selectionList.repaint();
			updateComponents();
		}
		catch (AppException e)
		{
			JOptionPane.showMessageDialog(this, e, READ_TITLE_STR, JOptionPane.ERROR_MESSAGE);
		}
	}

	//------------------------------------------------------------------

	private void writeParamSetList()
	{
		try
		{
			paramSetList.write(file, writeDtd);
		}
		catch (AppException e)
		{
			JOptionPane.showMessageDialog(this, e, WRITE_TITLE_STR, JOptionPane.ERROR_MESSAGE);
		}
	}

	//------------------------------------------------------------------

	private String getMessage(String name,
							  String str)
	{
		StringBuilder buffer = new StringBuilder(128);
		buffer.append(PARAMETER_SET_STR);
		buffer.append(name);
		buffer.append('\n');
		buffer.append(str);
		return buffer.toString();
	}

	//------------------------------------------------------------------

	private void onAdd()
		throws AppException
	{
		String name = nameField.getText();
		if (!name.isEmpty())
		{
			int index = paramSetList.find(name);
			if (index < 0)
			{
				params.setName(name);
				paramSetList.addElement(paramSetList.getNumElements(), createCopy(params));
				listChanged = true;
				selectionList.setSelectedIndex(paramSetList.find(name));
				selectionList.repaint();
			}
			else
			{
				selectionList.setSelectedIndex(index);
				if (JOptionPane.showOptionDialog(this, getMessage(name, ADD_REPLACE_MESSAGE_STR),
												 ADD_TITLE_STR, JOptionPane.OK_CANCEL_OPTION,
												 JOptionPane.WARNING_MESSAGE, null, RC_OPTION_STRS,
												 RC_OPTION_STRS[1]) == JOptionPane.OK_OPTION)
				{
					params.setName(name);
					paramSetList.setElement(index, createCopy(params));
					listChanged = true;
				}
			}
		}
	}

	//------------------------------------------------------------------

	private void onDelete()
	{
		int index = selectionList.getSelectedIndex();
		if (index >= 0)
		{
			String name = paramSetList.getElement(index).getName();
			if (JOptionPane.showOptionDialog(this, getMessage(name, DELETE_MESSAGE_STR),
											 DELETE_TITLE_STR, JOptionPane.OK_CANCEL_OPTION,
											 JOptionPane.QUESTION_MESSAGE, null, DC_OPTION_STRS,
											 DC_OPTION_STRS[1]) == JOptionPane.OK_OPTION)
			{
				paramSetList.removeElement(index);
				if ((params != null) && name.equals(params.getName()))
					params.setName(null);
				listChanged = true;
				selectionList.repaint();
			}
		}
	}

	//------------------------------------------------------------------

	private void onLoad()
		throws AppException
	{
		int index = selectionList.getSelectedIndex();
		if (index >= 0)
		{
			params = createCopy(paramSetList.getElement(index));
			nameField.setText(params.getName());
			nameField.setCaretPosition(params.getName().length());
		}
	}

	//------------------------------------------------------------------

	private void onLoadAccept()
		throws AppException
	{
		if (selectionList.getSelectedIndex() >= 0)
		{
			onLoad();
			onAccept();
		}
	}

	//------------------------------------------------------------------

	private void onUpdate()
		throws AppException
	{
		int index = selectionList.getSelectedIndex();
		if (index >= 0)
		{
			String name = paramSetList.getElement(index).getName();
			if (JOptionPane.showOptionDialog(this, getMessage(name, UPDATE_REPLACE_MESSAGE_STR),
											 UPDATE_TITLE_STR, JOptionPane.OK_CANCEL_OPTION,
											 JOptionPane.QUESTION_MESSAGE, null, RC_OPTION_STRS,
											 RC_OPTION_STRS[1]) == JOptionPane.OK_OPTION)
			{
				params.setName(name);
				paramSetList.setElement(index, createCopy(params));
				listChanged = true;
			}
		}
	}

	//------------------------------------------------------------------

	private void onReset()
		throws AppException
	{
		if (JOptionPane.showOptionDialog(this, RESET_MESSAGE_STR, RESET_TITLE_STR,
										 JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										 XC_OPTION_STRS, XC_OPTION_STRS[1]) == JOptionPane.OK_OPTION)
			params = createCopy(defaultParams);
	}

	//------------------------------------------------------------------

	private void onMoveParameterSetUp()
	{
		int index = selectionList.getSelectedIndex();
		selectionList.moveElement(index, index - 1);
	}

	//------------------------------------------------------------------

	private void onMoveParameterSetDown()
	{
		int index = selectionList.getSelectedIndex();
		selectionList.moveElement(index, index + 1);
	}

	//------------------------------------------------------------------

	private void onMoveParameterSet()
	{
		int fromIndex = selectionList.getSelectedIndex();
		int toIndex = selectionList.getDragEndIndex();
		if (toIndex > fromIndex)
			--toIndex;
		selectionList.moveElement(fromIndex, toIndex);
	}

	//------------------------------------------------------------------

	private void onAccept()
	{
		accepted = true;
		if (listChanged)
			writeParamSetList();
		onClose();
	}

	//------------------------------------------------------------------

	private void onClose()
	{
		locations.put(paramSetList.getApplicationKey(), getLocation());
		setVisible(false);
		dispose();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Map<String, Point>	locations	= new Hashtable<>();

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		COMMAND_MAP = new HashMap<>();
		COMMAND_MAP.put(SingleSelectionList.Command.EDIT_ELEMENT,      Command.LOAD_ACCEPT);
		COMMAND_MAP.put(SingleSelectionList.Command.DELETE_ELEMENT,    Command.DELETE);
		COMMAND_MAP.put(SingleSelectionList.Command.DELETE_EX_ELEMENT, Command.DELETE);
		COMMAND_MAP.put(SingleSelectionList.Command.MOVE_ELEMENT_UP,   Command.MOVE_PARAMETER_SET_UP);
		COMMAND_MAP.put(SingleSelectionList.Command.MOVE_ELEMENT_DOWN, Command.MOVE_PARAMETER_SET_DOWN);
		COMMAND_MAP.put(SingleSelectionList.Command.DRAG_ELEMENT,      Command.MOVE_PARAMETER_SET);
	}

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	boolean					accepted;
	private	boolean					listChanged;
	private	E						params;
	private	E						defaultParams;
	private	ParameterSetList<E>		paramSetList;
	private	File					file;
	private	File					dtdDirectory;
	private	boolean					writeDtd;
	private	SingleSelectionList<E>	selectionList;
	private	JScrollPane				selectionListScrollPane;
	private	JTextField				nameField;
	private	JButton					addButton;
	private	JButton					deleteButton;
	private	JButton					loadButton;
	private	JButton					updateButton;

}

//----------------------------------------------------------------------
