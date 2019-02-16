/*====================================================================*\

EnvelopeNode.java

Envelope node class.

\*====================================================================*/


// PACKAGE


package common.misc;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

import common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// ENVELOPE NODE CLASS


public class EnvelopeNode
	implements Cloneable, Comparable<EnvelopeNode>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public EnvelopeNode()
	{
	}

	//------------------------------------------------------------------

	public EnvelopeNode(double x,
						double y)
	{
		this.x = x;
		this.y = y;
	}

	//------------------------------------------------------------------

	public EnvelopeNode(EnvelopeNode node)
	{
		x = node.x;
		y = node.y;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static List<EnvelopeNode> deepCopy(List<EnvelopeNode> nodes)
	{
		List<EnvelopeNode> outNodes = new ArrayList<>();
		for (EnvelopeNode node : nodes)
			outNodes.add(node.clone());
		return outNodes;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Comparable interface
////////////////////////////////////////////////////////////////////////

	@Override
	public int compareTo(EnvelopeNode other)
	{
		return Double.compare(x, other.x);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public EnvelopeNode clone()
	{
		try
		{
			return (EnvelopeNode)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return (Double.toString(x) + ", " + Double.toString(y));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public boolean equals(EnvelopeNode node)
	{
		return ((node != null) && (x == node.x) && (y == node.y));
	}

	//------------------------------------------------------------------

	public void set(double x,
					double y)
	{
		this.x = x;
		this.y = y;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	public	double	x;
	public	double	y;

}

//----------------------------------------------------------------------
