/*====================================================================*\

Envelope.java

Envelope class.

\*====================================================================*/


// PACKAGE


package common.envelope;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.exception.UnexpectedRuntimeException;

import common.misc.IntegerRange;
import common.misc.IStringKeyed;

//----------------------------------------------------------------------


// ENVELOPE CLASS


public abstract class Envelope
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int	MIN_NUM_NODES	= 0;
	public static final		int	MAX_NUM_NODES	= 1 << 14;  // 16384

	// Default values
	private static final	Color	DEFAULT_SEGMENT_COLOUR	= Color.BLUE;
	private static final	Color	DEFAULT_NODE_COLOUR		= Color.BLACK;

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENVELOPE KIND


	public enum Kind
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		LINEAR
		(
			"linear",
			"Linear"
		),

		CUBIC_SEGMENT
		(
			"cubicSegment",
			"Cubic segment"
		),

		CUBIC_SPLINE_A
		(
			"cubicSplineA",
			"Cubic spline A"
		),

		CUBIC_SPLINE_B
		(
			"cubicSplineB",
			"Cubic spline B"
		);

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Kind(String key,
					 String text)
		{
			this.key = key;
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Kind get(int index)
		{
			return (((index >= 0) && (index < values().length)) ? values()[index] : null);
		}

		//--------------------------------------------------------------

		public static Kind forKey(String key)
		{
			for (Kind value : values())
			{
				if (value.key.equals(key))
					return value;
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IStringKeyed interface
	////////////////////////////////////////////////////////////////////

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// NODE CLASS


	public abstract static class Node
		implements Cloneable, Comparable<Node>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		public static final		double	MIN_X	= 0.0;
		public static final		double	MAX_X	= 1.0;

		public static final		double	MIN_Y	= 0.0;
		public static final		double	MAX_Y	= 1.0;

		public static final		int	WIDTH		= 5;
		public static final		int	HEIGHT		= WIDTH;
		public static final		int	HALF_WIDTH	= WIDTH / 2;
		public static final		int	HALF_HEIGHT	= HEIGHT / 2;

		private static final	int	CAPTURE_WIDTH		= 7;
		private static final	int	CAPTURE_HEIGHT		= CAPTURE_WIDTH;
		private static final	int	CAPTURE_HALF_WIDTH	= CAPTURE_WIDTH / 2;
		private static final	int	CAPTURE_HALF_HEIGHT	= CAPTURE_HEIGHT / 2;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Node()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static Rectangle getRectangle(Point point)
		{
			return new Rectangle(point.x - HALF_WIDTH, point.y - HALF_HEIGHT, WIDTH, HEIGHT);
		}

		//--------------------------------------------------------------

		public static Rectangle getCaptureRectangle(Point point)
		{
			return new Rectangle(point.x - CAPTURE_HALF_WIDTH, point.y - CAPTURE_HALF_HEIGHT,
								 CAPTURE_WIDTH, CAPTURE_HEIGHT);
		}

		//--------------------------------------------------------------

		public static Point2D.Double pointToNodePoint(Point point,
													  int   width,
													  int   height)
		{
			double x = 0.0;
			if (--width > 0)
				x = applyNormalBounds((double)(point.x - EnvelopeView.LEFT_MARGIN) / (double)width);
			double y = 0.0;
			if (--height > 0)
				y = applyNormalBounds((double)(height - point.y + EnvelopeView.TOP_MARGIN) /
																						(double)height);
			return new Point2D.Double(x, y);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		public abstract boolean isFixed(int bandIndex);

		//--------------------------------------------------------------

		public abstract boolean isPartiallyFixed(int bandIndex);

		//--------------------------------------------------------------

		public abstract Point toPoint(int width,
									  int height,
									  int bandIndex);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : Comparable interface
	////////////////////////////////////////////////////////////////////

		@Override
		public int compareTo(Node other)
		{
			return Double.compare(x, other.x);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public Node clone()
		{
			try
			{
				return (Node)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new UnexpectedRuntimeException(e);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public double getY(int bandIndex)
		{
			if (this instanceof SimpleNode)
				return ((SimpleNode)this).y;
			if (this instanceof CompoundNode)
				return ((CompoundNode)this).y[bandIndex];
			return Double.NaN;
		}

		//--------------------------------------------------------------

		public void setY(int    bandIndex,
						 double y)
		{
			if (this instanceof SimpleNode)
				((SimpleNode)this).y = y;
			if (this instanceof CompoundNode)
				((CompoundNode)this).y[bandIndex] = y;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	double	x;
		public	boolean	fixedX;

	}

	//==================================================================


	// SIMPLE NODE CLASS


	public static class SimpleNode
		extends Node
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws IllegalArgumentException
		 */

		public SimpleNode(double x,
						  double y)
		{
			this(x, y, false, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public SimpleNode(double  x,
						  double  y,
						  boolean fixedX)
		{
			this(x, y, fixedX, false);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public SimpleNode(double  x,
						  double  y,
						  boolean fixedX,
						  boolean fixedY)
		{
			if ((x < MIN_X) || (x > MAX_X))
				throw new IllegalArgumentException();
			if ((y < MIN_Y) || (y > MAX_Y))
				throw new IllegalArgumentException();

			this.x = x;
			this.y = y;
			this.fixedX = fixedX;
			this.fixedY = fixedY;
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public SimpleNode(SimpleNode node)
		{
			this(node.x, node.y, node.fixedX, node.fixedY);
		}

		//--------------------------------------------------------------

		public SimpleNode(Point point,
						  int   width,
						  int   height)
		{
			if (--width > 0)
				x = applyNormalBounds((double)(point.x - EnvelopeView.LEFT_MARGIN) / (double)width);
			if (--height > 0)
				y = applyNormalBounds((double)(height - point.y + EnvelopeView.TOP_MARGIN) /
																						(double)height);
		}

		//--------------------------------------------------------------

		protected SimpleNode()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static List<SimpleNode> copyList(List<Node> nodes)
		{
			List<SimpleNode> outNodes = new ArrayList<>();
			for (Node node : nodes)
				outNodes.add((SimpleNode)node);
			return outNodes;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public SimpleNode clone()
		{
			return (SimpleNode)super.clone();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public boolean isFixed(int bandIndex)
		{
			return (fixedX && fixedY);
		}

		//--------------------------------------------------------------

		public boolean isPartiallyFixed(int bandIndex)
		{
			return (fixedX || fixedY);
		}

		//--------------------------------------------------------------

		public Point toPoint(int width,
							 int height,
							 int bandIndex)
		{
			--width;
			--height;
			return new Point(EnvelopeView.LEFT_MARGIN + (int)Math.round(x * (double)width),
							 EnvelopeView.TOP_MARGIN + height - (int)Math.round(y * (double)height));
		}

		//--------------------------------------------------------------

		public boolean equals(SimpleNode node)
		{
			return ((node != null) && (x == node.x) && (y == node.y));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	double	y;
		public	boolean	fixedY;

	}

	//==================================================================


	// COMPOUND NODE CLASS


	public static class CompoundNode
		extends Node
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MAX_NUM_ELEMENTS	= 32;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * @throws IllegalArgumentException
		 */

		public CompoundNode(double   x,
							double[] y)
		{
			this(x, y, false, 0);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public CompoundNode(double   x,
							double[] y,
							boolean  fixedX)
		{
			this(x, y, fixedX, 0);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public CompoundNode(double   x,
							double[] y,
							boolean  fixedX,
							int      fixedY)
		{
			if ((x < MIN_X) || (x > MAX_X) || (y.length > MAX_NUM_ELEMENTS))
				throw new IllegalArgumentException();
			for (int i = 0; i < y.length; i++)
			{
				if ((y[i] < MIN_Y) || (y[i] > MAX_Y))
					throw new IllegalArgumentException();
			}

			this.x = x;
			this.y = y.clone();
			this.fixedX = fixedX;
			this.fixedY = fixedY;
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public CompoundNode(CompoundNode node)
		{
			this(node.x, node.y, node.fixedX, node.fixedY);
		}

		//--------------------------------------------------------------

		protected CompoundNode()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static List<CompoundNode> copyList(List<Node> nodes)
		{
			List<CompoundNode> outNodes = new ArrayList<>();
			for (Node node : nodes)
				outNodes.add((CompoundNode)node);
			return outNodes;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public CompoundNode clone()
		{
			CompoundNode copy = (CompoundNode)super.clone();
			copy.y = y.clone();
			return copy;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public boolean isFixedY(int bandIndex)
		{
			return ((fixedY & 1 << bandIndex) != 0);
		}

		//--------------------------------------------------------------

		public boolean isFixed(int bandIndex)
		{
			return (fixedX && isFixedY(bandIndex));
		}

		//--------------------------------------------------------------

		public boolean isPartiallyFixed(int bandIndex)
		{
			return (fixedX || isFixedY(bandIndex));
		}

		//--------------------------------------------------------------

		public Point toPoint(int width,
							 int height,
							 int bandIndex)
		{
			--width;
			--height;
			return new Point(EnvelopeView.LEFT_MARGIN + (int)Math.round(x * (double)width),
							 EnvelopeView.TOP_MARGIN + height - (int)Math.round(y[bandIndex] *
																						(double)height));
		}

		//--------------------------------------------------------------

		public boolean equals(CompoundNode node)
		{
			return ((node != null) && (x == node.x) && Arrays.equals(y, node.y));
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	double[]	y;
		public	int			fixedY;

	}

	//==================================================================


	// NODE IDENTIFIER CLASS


	public static class NodeId
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public NodeId()
		{
		}

		//--------------------------------------------------------------

		public NodeId(int envelopeIndex,
					  int bandIndex,
					  int nodeIndex)
		{
			this.envelopeIndex = envelopeIndex;
			this.bandIndex = bandIndex;
			this.nodeIndex = nodeIndex;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public NodeId clone()
		{
			try
			{
				return (NodeId)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new UnexpectedRuntimeException(e);
			}
		}

		//--------------------------------------------------------------

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof NodeId)
			{
				NodeId id = (NodeId)obj;
				return ((envelopeIndex == id.envelopeIndex) && (bandIndex == id.bandIndex) &&
						 (nodeIndex == id.nodeIndex));
			}
			return false;
		}

		//--------------------------------------------------------------

		@Override
		public int hashCode()
		{
			return (envelopeIndex << 20 | bandIndex << 10 | nodeIndex);
		}

		//--------------------------------------------------------------

		@Override
		public String toString()
		{
			return new String(envelopeIndex + ", " + bandIndex + ", " + nodeIndex);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		public	int	envelopeIndex;
		public	int	bandIndex;
		public	int	nodeIndex;

	}

	//==================================================================


	// SIMPLE ENVELOPE CLASS


	public static class Simple
		extends Envelope
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Simple(Kind kind)
		{
			this(kind, DEFAULT_SEGMENT_COLOUR, DEFAULT_NODE_COLOUR, null);
		}

		//--------------------------------------------------------------

		public Simple(Kind  kind,
					  Color segmentColour,
					  Color nodeColour)
		{
			this(kind, segmentColour, nodeColour, null);
		}

		//--------------------------------------------------------------

		public Simple(Kind   kind,
					  Color  segmentColour,
					  Color  nodeColour,
					  String name)
		{
			super(kind);
			setBandMask(1);
			this.segmentColour = segmentColour;
			this.nodeColour = nodeColour;
			this.name = name;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getNumBands()
		{
			return 1;
		}

		//--------------------------------------------------------------

		public Color getSegmentColour(int bandIndex)
		{
			return segmentColour;
		}

		//--------------------------------------------------------------

		public Color getNodeColour(int bandIndex)
		{
			return nodeColour;
		}

		//--------------------------------------------------------------

		public String getName(int bandIndex)
		{
			return name;
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public void setNodes(List<? extends Node> nodes,
							 boolean              loop,
							 boolean              forceLoop)
		{
			int numNodes = nodes.size();
			if (!getNumNodesRange().contains(numNodes))
				throw new IllegalArgumentException();
			if (loop)
			{
				if (numNodes < 2)
					throw new IllegalArgumentException();
				SimpleNode node0 = (SimpleNode)nodes.get(0);
				SimpleNode node1 = (SimpleNode)nodes.get(numNodes - 1);
				if ((node0.x != Node.MIN_X) || !node0.fixedX || (node1.x != Node.MAX_X) || !node1.fixedX)
					throw new IllegalArgumentException();
				if (forceLoop)
				{
					node1.y = node0.y;
					node1.fixedY = node0.fixedY;
				}
				else
				{
					if ((node1.y != node0.y) || (node1.fixedY != node0.fixedY))
						throw new IllegalArgumentException();
				}
			}

			double prevX = -1.0;
			for (int i = 0; i < numNodes; i++)
			{
				SimpleNode node = (SimpleNode)nodes.get(i);
				if ((node.x < Node.MIN_X) || (node.x > Node.MAX_X) || (node.x - prevX < getMinDeltaX()))
					throw new IllegalArgumentException();
				prevX = node.x;

				if ((node.y < Node.MIN_Y) || (node.y > Node.MAX_Y))
					throw new IllegalArgumentException();
			}

			getNodes().clear();
			for (Node node : nodes)
				getNodes().add(node);

			setLoop(loop);
		}

		//--------------------------------------------------------------

		public void setSegmentColour(int   bandIndex,
									 Color colour)
		{
			segmentColour = colour;
		}

		//--------------------------------------------------------------

		public void setNodeColour(int   bandIndex,
								  Color colour)
		{
			nodeColour = colour;
		}

		//--------------------------------------------------------------

		public void setName(int    bandIndex,
							String name)
		{
			this.name = name;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	Color	segmentColour;
		private	Color	nodeColour;
		private	String	name;

	}

	//==================================================================


	// COMPOUND ENVELOPE CLASS


	public static class Compound
		extends Envelope
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public Compound(Kind kind,
						int  numBands)
		{
			super(kind);
			setBandMask((1 << numBands) - 1);
			segmentColours = new Color[numBands];
			Arrays.fill(segmentColours, DEFAULT_SEGMENT_COLOUR);
			nodeColours = new Color[numBands];
			Arrays.fill(nodeColours, DEFAULT_NODE_COLOUR);
			names = new String[numBands];
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public Compound(Kind    kind,
						Color[] segmentColours,
						Color[] nodeColours)
		{
			this(kind, segmentColours, nodeColours, new String[segmentColours.length]);
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public Compound(Kind     kind,
						Color[]  segmentColours,
						Color[]  nodeColours,
						String[] names)
		{
			super(kind);
			int numBands = segmentColours.length;
			if ((nodeColours.length != numBands) || (names.length != numBands))
				throw new IllegalArgumentException();
			setBandMask((1 << numBands) - 1);
			this.segmentColours = segmentColours.clone();
			this.nodeColours = nodeColours.clone();
			this.names = names.clone();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getNumBands()
		{
			return segmentColours.length;
		}

		//--------------------------------------------------------------

		public Color getSegmentColour(int bandIndex)
		{
			return segmentColours[bandIndex];
		}

		//--------------------------------------------------------------

		public Color getNodeColour(int bandIndex)
		{
			return nodeColours[bandIndex];
		}

		//--------------------------------------------------------------

		public String getName(int bandIndex)
		{
			return names[bandIndex];
		}

		//--------------------------------------------------------------

		/**
		 * @throws IllegalArgumentException
		 */

		public void setNodes(List<? extends Node> nodes,
							 boolean              loop,
							 boolean              forceLoop)
		{
			int numNodes = nodes.size();
			if (!getNumNodesRange().contains(numNodes))
				throw new IllegalArgumentException();
			if (loop)
			{
				if (numNodes < 2)
					throw new IllegalArgumentException();
				CompoundNode node0 = (CompoundNode)nodes.get(0);
				CompoundNode node1 = (CompoundNode)nodes.get(numNodes - 1);
				if ((node0.x != Node.MIN_X) || !node0.fixedX || (node1.x != Node.MAX_X) || !node1.fixedX)
					throw new IllegalArgumentException();
				if (forceLoop)
				{
					node1.y = node0.y.clone();
					node1.fixedY = node0.fixedY;
				}
				else
				{
					if (!node1.y.equals(node0.y) || (node1.fixedY != node0.fixedY))
						throw new IllegalArgumentException();
				}
			}

			double prevX = -1.0;
			for (int i = 0; i < numNodes; i++)
			{
				CompoundNode node = (CompoundNode)nodes.get(i);
				if ((node.x < Node.MIN_X) || (node.x > Node.MAX_X) || (node.x - prevX < getMinDeltaX()))
					throw new IllegalArgumentException();
				prevX = node.x;

				double prevY = 0.0;
				for (int j = 0; j < getNumBands(); j++)
				{
					double y = node.y[j];
					if ((y < Node.MIN_Y) || (y > Node.MAX_Y) || (y < prevY))
						throw new IllegalArgumentException();
					prevY = y;
				}
			}

			getNodes().clear();
			for (Node node : nodes)
				getNodes().add(node);

			setLoop(loop);
		}

		//--------------------------------------------------------------

		public void setSegmentColour(int   bandIndex,
									 Color colour)
		{
			segmentColours[bandIndex] = colour;
		}

		//--------------------------------------------------------------

		public void setNodeColour(int   bandIndex,
								  Color colour)
		{
			nodeColours[bandIndex] = colour;
		}

		//--------------------------------------------------------------

		public void setName(int    bandIndex,
							String name)
		{
			names[bandIndex] = name;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	Color[]		segmentColours;
		private	Color[]		nodeColours;
		private	String[]	names;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private Envelope(Kind kind)
	{
		this.kind = kind;
		numNodesRange = new IntegerRange(MIN_NUM_NODES, MAX_NUM_NODES);
		nodes = new ArrayList<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static double applyNormalBounds(double value)
	{
		return Math.min(Math.max(0.0, value), 1.0);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract int getNumBands();

	//------------------------------------------------------------------

	public abstract Color getSegmentColour(int bandIndex);

	//------------------------------------------------------------------

	public abstract Color getNodeColour(int bandIndex);

	//------------------------------------------------------------------

	public abstract String getName(int bandIndex);

	//------------------------------------------------------------------

	public abstract void setNodes(List<? extends Node> nodes,
								  boolean              loop,
								  boolean              forceLoop);

	//------------------------------------------------------------------

	public abstract void setSegmentColour(int   bandIndex,
										  Color colour);

	//------------------------------------------------------------------

	public abstract void setNodeColour(int   bandIndex,
									   Color colour);

	//------------------------------------------------------------------

	public abstract void setName(int    bandIndex,
								 String name);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Kind getKind()
	{
		return kind;
	}

	//------------------------------------------------------------------

	public IntegerRange getNumNodesRange()
	{
		return numNodesRange;
	}

	//------------------------------------------------------------------

	public List<Node> getNodes()
	{
		return nodes;
	}

	//------------------------------------------------------------------

	public int getNumNodes()
	{
		return nodes.size();
	}

	//------------------------------------------------------------------

	public Node getNode(int index)
	{
		return nodes.get(index);
	}

	//------------------------------------------------------------------

	public int getBandMask()
	{
		return bandMask;
	}

	//------------------------------------------------------------------

	public boolean isLoop()
	{
		return loop;
	}

	//------------------------------------------------------------------

	public double getMinDeltaX()
	{
		return minDeltaX;
	}

	//------------------------------------------------------------------

	public void setKind(Kind kind)
	{
		this.kind = kind;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public void setNumNodesRange(IntegerRange range)
	{
		if ((range.lowerBound < MIN_NUM_NODES) || (range.lowerBound > MAX_NUM_NODES) ||
			 (range.upperBound < MIN_NUM_NODES) || (range.upperBound > MAX_NUM_NODES) ||
			 (range.upperBound < range.lowerBound))
			throw new IllegalArgumentException();
		numNodesRange = range.clone();
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public void setNodes(List<? extends Node> nodes)
	{
		setNodes(nodes, false, false);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public void setNodes(List<? extends Node> nodes,
						 boolean              loop)
	{
		setNodes(nodes, loop, false);
	}

	//------------------------------------------------------------------

	public void setNode(int  index,
						Node node)
	{
		nodes.set(index, node);
	}

	//------------------------------------------------------------------

	public void setBandMask(int mask)
	{
		bandMask = mask;
	}

	//------------------------------------------------------------------

	public void setLoop(boolean loop)
	{
		this.loop = loop;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public void setMinDeltaX(double minDeltaX)
	{
		if ((minDeltaX < 0.0) || (minDeltaX >= 1.0))
			throw new IllegalArgumentException();
		this.minDeltaX = minDeltaX;
	}

	//------------------------------------------------------------------

	public boolean isValidNodeX(double x)
	{
		for (Node node : nodes)
		{
			if (Math.abs(node.x - x) < minDeltaX)
				return false;
		}
		return true;
	}

	//------------------------------------------------------------------

	public boolean hasDiscreteSegments()
	{
		switch (kind)
		{
			case LINEAR:
			case CUBIC_SEGMENT:
				return true;

			default:
				return false;
		}
	}

	//------------------------------------------------------------------

	public void drawSegments(Graphics gr,
							 int      bandIndex,
							 int      startIndex,
							 int      endIndex,
							 int      width,
							 int      height)
	{
		gr.setColor(getSegmentColour(bandIndex));

		switch (kind)
		{
			case LINEAR:
			{
				int index = startIndex;
				Node node = getNode(index);
				Point point = node.toPoint(width, height, bandIndex);
				while (++index <= endIndex)
				{
					int x0 = point.x;
					int y0 = point.y;
					node = getNode(index);
					point = node.toPoint(width, height, bandIndex);
					gr.drawLine(x0, y0, point.x, point.y);
				}
				break;
			}

			case CUBIC_SEGMENT:
			{
				int index = startIndex;

				double x0 = 0.0;
				double y0 = 0.0;
				Point p0 = null;

				Node node = getNode(index);
				double x1 = node.x;
				double y1 = node.getY(bandIndex);
				Point p1 = node.toPoint(width, height, bandIndex);

				int hMinus1 = height - 1;
				double factor = 1.0 / (double)(width - 1);

				while (++index <= endIndex)
				{
					x0 = x1;
					y0 = y1;

					node = getNode(index);
					x1 = node.x;
					y1 = node.getY(bandIndex);

					p0 = p1;
					p1 = node.toPoint(width, height, bandIndex);

					if (p0.x == p1.x)
						gr.drawLine(p0.x, p0.y, p1.x, p1.y);
					else
					{
						double a = 2.0 * (y1 - y0) / (x0 * x0 * (x0 - 3.0 * x1) - x1 * x1 *
																						(x1 - 3.0 * x0));
						double b = -1.5 * a * (x0 + x1);
						double c = 3.0 * a * x0 * x1;
						double d = y0 + 0.5 * a * x0 * x0 * (x0 - 3.0 * x1);

						int prevY = p0.y;
						for (int i = p0.x + 1; i < p1.x; i++)
						{
							double x = (double)(i - EnvelopeView.LEFT_MARGIN) * factor;
							double y = ((a * x + b) * x + c) * x + d;
							int currY = EnvelopeView.TOP_MARGIN + hMinus1 -
																	(int)Math.round(y * (double)hMinus1);
							gr.drawLine(i - 1, prevY, i, currY);
							prevY = currY;
						}
						gr.drawLine(p1.x - 1, prevY, p1.x, p1.y);
					}
				}
				break;
			}

			case CUBIC_SPLINE_A:
			{
				int index = 0;

				double x0 = 0.0;
				double y0 = 0.0;
				Point p0 = null;

				Node node = getNode(index);
				double x1 = node.x;
				double y1 = node.getY(bandIndex);
				Point p1 = node.toPoint(width, height, bandIndex);

				node = getNode(++index);
				double x2 = node.x;
				double y2 = node.getY(bandIndex);
				Point p2 = node.toPoint(width, height, bandIndex);

				double a = 0.0;
				double b = 0.0;
				double m0 = 0.0;
				double m1 = Double.NaN;

				int hMinus1 = height - 1;
				double factor = 1.0 / (double)(width - 1);

				while (++index - 2 < endIndex)
				{
					x0 = x1;
					y0 = y1;

					x1 = x2;
					y1 = y2;

					if (index < nodes.size())
					{
						node = getNode(index);
						x2 = node.x;
						y2 = node.getY(bandIndex);
					}
					else
					{
						x2 = x1 + (x1 - x0);
						y2 = y1 + (y1 - y0);
						node = node.clone();
						node.x = x2;
						node.setY(bandIndex, y2);
					}

					double dx10 = x1 - x0;
					double dx20 = x2 - x0;
					double dx10_2 = dx10 * dx10;
					double dx10_3 = dx10_2 * dx10;
					double dy10 = y1 - y0;
					double dy20 = y2 - y0;

					m0 = Double.isNaN(m1)
										? dy10 / dx10 - dx10 / dx20 * ((y2 - y1) / (x2 - x1) - dy10 / dx10)
										: m1;
					m1 = dy20 / dx20;

					a = (m0 + m1) / dx10_2 - 2.0 * dy10 / dx10_3;
					b = 3 * dy10 / dx10_2 - (2.0 * m0 + m1) / dx10;

					p0 = p1;
					p1 = p2;
					p2 = node.toPoint(width, height, bandIndex);

					if (index >= startIndex)
					{
						int prevY = p0.y;
						for (int i = p0.x + 1; i < p1.x; i++)
						{
							double dx = (double)(i - EnvelopeView.LEFT_MARGIN) * factor - x0;
							double y = ((a * dx + b) * dx + m0) * dx + y0;
							int currY = EnvelopeView.TOP_MARGIN + hMinus1 -
																	(int)Math.round(y * (double)hMinus1);
							gr.drawLine(i - 1, prevY, i, currY);
							prevY = currY;
						}
						gr.drawLine(p1.x - 1, prevY, p1.x, p1.y);
					}
				}
				break;
			}

			case CUBIC_SPLINE_B:
			{
				int index = 0;

				double x0 = 0.0;
				double y0 = 0.0;
				Point p0 = null;

				Node node = getNode(index);
				double x1 = node.x;
				double y1 = node.getY(bandIndex);
				Point p1 = node.toPoint(width, height, bandIndex);

				node = getNode(++index);
				double x2 = node.x;
				double y2 = node.getY(bandIndex);
				Point p2 = node.toPoint(width, height, bandIndex);

				double a = 0.0;
				double b = 0.0;
				double m0 = Double.NaN;

				int hMinus1 = height - 1;
				double factor = 1.0 / (double)(width - 1);

				while (++index - 2 < endIndex)
				{
					if ((p0 == null) || (index < nodes.size()))
					{
						double prevDx10 = x1 - x0;

						x0 = x1;
						y0 = y1;

						x1 = x2;
						y1 = y2;

						if (index < nodes.size())
						{
							node = getNode(index);
							x2 = node.x;
							y2 = node.getY(bandIndex);
						}
						else
						{
							x2 = x1 + (x1 - x0);
							y2 = y1 + (y1 - y0);
							node = node.clone();
							node.x = x2;
							node.setY(bandIndex, y2);
						}

						double dx10 = x1 - x0;
						double dx20 = x2 - x0;
						double dx21 = x2 - x1;
						double dx10_2 = dx10 * dx10;
						double dx20_2 = dx20 * dx20;
						double dy10 = y1 - y0;
						double dy21 = y2 - y1;
						if (Double.isNaN(m0))
							m0 = dy10 / dx10 - dx10 / dx20 * (dy21 / dx21 - dy10 / dx10);
						else
							m0 += (3.0 * a * prevDx10 + 2.0 * b) * prevDx10;
						a = dy21 / (dx21 * dx20_2) - dy10 * (dx10 + dx20) / (dx10_2 * dx20_2) +
																						m0 / (dx10 * dx20);
						b = dy10 / dx10_2 - a * dx10 - m0 / dx10;
					}

					p0 = p1;
					p1 = p2;
					p2 = node.toPoint(width, height, bandIndex);

					if (index >= startIndex)
					{
						int prevY = p0.y;
						for (int i = p0.x + 1; i < p1.x; i++)
						{
							double dx = (double)(i - EnvelopeView.LEFT_MARGIN) * factor - x0;
							double y = ((a * dx + b) * dx + m0) * dx + y0;
							int currY = EnvelopeView.TOP_MARGIN + hMinus1 -
																	(int)Math.round(y * (double)hMinus1);
							gr.drawLine(i - 1, prevY, i, currY);
							prevY = currY;
						}
						gr.drawLine(p1.x - 1, prevY, p1.x, p1.y);
					}
				}
				break;
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	Kind			kind;
	private	IntegerRange	numNodesRange;
	private	List<Node>		nodes;
	private	int				bandMask;
	private	boolean			loop;
	private	double			minDeltaX;

}

//----------------------------------------------------------------------
