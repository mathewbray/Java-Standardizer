/*====================================================================*\

SpinnerSliderPanel.java

Spinner and slider panel base class.

\*====================================================================*/


// PACKAGE


package common.gui;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JPanel;

//----------------------------------------------------------------------


// SPINNER AND SLIDER PANEL BASE CLASS


public abstract class SpinnerSliderPanel
	extends JPanel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	protected static final	int	MIN_FILLER_WIDTH	= 6;

	protected static final	Insets	DEFAULT_BUTTON_MARGINS	= new Insets(1, 4, 1, 4);

	protected static final	String	DEFAULT_STR	= "Default";

	// Commands
	protected interface Command
	{
		String	SET_DEFAULT_VALUE	= "setDefaultValue";
	}

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// FOCUS TRAVERSAL POLICY CLASS


	private class PanelFocusTraversalPolicy
		extends FocusTraversalPolicy
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private PanelFocusTraversalPolicy()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Component getDefaultComponent(Container container)
		{
			return components.get(0);
		}

		//--------------------------------------------------------------

		public Component getFirstComponent(Container container)
		{
			return components.get(0);
		}

		//--------------------------------------------------------------

		public Component getLastComponent(Container container)
		{
			return components.get(components.size() - 1);
		}

		//--------------------------------------------------------------

		public Component getComponentBefore(Container container,
											Component component)
		{
			int index = components.indexOf(component) - 1;
			return ((index < 0) ? null : components.get(index));
		}

		//--------------------------------------------------------------

		public Component getComponentAfter(Container container,
										   Component component)
		{
			int index = components.indexOf(component) + 1;
			return ((index >= components.size()) ? null : components.get(index));
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected SpinnerSliderPanel()
	{
		components = new ArrayList<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void removeInstances(String key)
	{
		instanceMap.remove(key);
	}

	//------------------------------------------------------------------

	public static void align(String key)
	{
		List<SpinnerSliderPanel> instances = instanceMap.get(key);
		if (instances != null)
		{
			int maxWidth = 0;
			for (SpinnerSliderPanel panel : instances)
			{
				int width = panel.getPreferredSpinnerWidth();
				if (maxWidth < width)
					maxWidth = width;
			}

			for (SpinnerSliderPanel panel : instances)
			{
				int width = MIN_FILLER_WIDTH + maxWidth - panel.getPreferredSpinnerWidth();
				Dimension fillerSize = new Dimension(width, 1);
				panel.filler.changeShape(fillerSize, fillerSize, fillerSize);
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	protected abstract int getPreferredSpinnerWidth();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public HorizontalSlider getSlider()
	{
		return slider;
	}

	//------------------------------------------------------------------

	public void setUnitIncrement(double increment)
	{
		slider.setUnitIncrement(increment);
	}

	//------------------------------------------------------------------

	public void setBlockIncrement(double increment)
	{
		slider.setBlockIncrement(increment);
	}

	//------------------------------------------------------------------

	protected void addInstance(String key)
	{
		List<SpinnerSliderPanel> instances = instanceMap.get(key);
		if (instances == null)
		{
			instances = new ArrayList<>();
			instanceMap.put(key, instances);
		}
		instances.add(this);
	}

	//------------------------------------------------------------------

	protected void setFocusTraversalPolicy()
	{
		setFocusTraversalPolicy(new PanelFocusTraversalPolicy());
		setFocusTraversalPolicyProvider(true);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Map<String, List<SpinnerSliderPanel>>	instanceMap	=   new Hashtable<>();

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	boolean				adjusting;
	protected	Box.Filler			filler;
	protected	HorizontalSlider	slider;
	protected	List<Component>		components;

}

//----------------------------------------------------------------------
