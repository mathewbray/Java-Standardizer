/*====================================================================*\

EnvelopeEvaluator.java

Envelope evaluator class.

\*====================================================================*/


// PACKAGE


package common.envelope;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.misc.EnvelopeNode;
import common.misc.IEvaluable;

//----------------------------------------------------------------------


// ENVELOPE EVALUATOR CLASS


public class EnvelopeEvaluator
	implements IEvaluable
{

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// NODE CLASS


	private static class Node
		extends EnvelopeNode
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Node(double x,
					 double y)
		{
			super(x, y);
		}

		//--------------------------------------------------------------

		private Node(EnvelopeNode node)
		{
			super(node);
		}

		//--------------------------------------------------------------

		private Node(Envelope.SimpleNode node)
		{
			super(node.x, node.y);
		}

		//--------------------------------------------------------------

		private Node(Envelope.CompoundNode node,
					 int                   index)
		{
			super(node.x, node.y[index]);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public EnvelopeEvaluator(Envelope.Simple envelope)
	{
		kind = envelope.getKind();

		nodes = new ArrayList<>();
		for (int i = 0; i < envelope.getNumNodes(); i++)
			nodes.add(new Node((Envelope.SimpleNode)envelope.getNode(i)));

		initEvaluation();
	}

	//------------------------------------------------------------------

	public EnvelopeEvaluator(Envelope.Compound envelope,
							 int               bandIndex)
	{
		kind = envelope.getKind();

		nodes = new ArrayList<>();
		for (int i = 0; i < envelope.getNumNodes(); i++)
			nodes.add(new Node((Envelope.CompoundNode)envelope.getNode(i), bandIndex));

		initEvaluation();
	}

	//------------------------------------------------------------------

	public EnvelopeEvaluator(Envelope.Kind      kind,
							 List<EnvelopeNode> nodes)
	{
		this.kind = kind;
		this.nodes = new ArrayList<>(nodes);

		Collections.sort(this.nodes);

		initEvaluation();
	}

	//------------------------------------------------------------------

	public EnvelopeEvaluator(List<Envelope.SimpleNode> nodes,
							 Envelope.Kind             kind)
	{
		this.kind = kind;
		this.nodes = new ArrayList<>();
		for (Envelope.SimpleNode node : nodes)
			this.nodes.add(new Node(node));

		Collections.sort(this.nodes);

		initEvaluation();
	}

	//------------------------------------------------------------------

	public EnvelopeEvaluator(List<Envelope.CompoundNode> nodes,
							 int                         bandIndex,
							 Envelope.Kind               kind)
	{
		this.kind = kind;
		this.nodes = new ArrayList<>();
		for (Envelope.CompoundNode node : nodes)
			this.nodes.add(new Node(node, bandIndex));

		Collections.sort(this.nodes);

		initEvaluation();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IEvaluable interface
////////////////////////////////////////////////////////////////////////

	public void initEvaluation()
	{
		nodeIndex = 0;
		evalX = -1.0;
		switch (kind)
		{
			case CUBIC_SPLINE_A:
			{
				EnvelopeNode node = nodes.get(nodeIndex++);
				x0 = node.x;
				y0 = node.y;
				node = nodes.get(nodeIndex++);
				x1 = node.x;
				y1 = node.y;
				node = (nodeIndex < nodes.size()) ? nodes.get(nodeIndex++)
												  : new EnvelopeNode(x1 + (x1 - x0), y1 + (y1 - y0));
				x2 = node.x;
				y2 = node.y;

				double dx10 = x1 - x0;
				double dx20 = x2 - x0;
				double dx21 = x2 - x1;
				double dx10_2 = dx10 * dx10;
				double dx10_3 = dx10_2 * dx10;
				double dy10 = y1 - y0;
				double dy20 = y2 - y0;
				double dy21 = y2 - y1;

				m0 = dy10 / dx10 - (dx10 / dx20) * (dy21 / dx21 - dy10 / dx10);
				m1 = dy20 / dx20;
				a = (m0 + m1) / dx10_2 - 2.0 * dy10 / dx10_3;
				b = 3 * dy10 / dx10_2 - (2.0 * m0 + m1) / dx10;
				break;
			}

			case CUBIC_SPLINE_B:
			{
				EnvelopeNode node = nodes.get(nodeIndex++);
				x0 = node.x;
				y0 = node.y;
				node = nodes.get(nodeIndex++);
				x1 = node.x;
				y1 = node.y;
				node = (nodeIndex < nodes.size()) ? nodes.get(nodeIndex++)
												  : new EnvelopeNode(x1 + (x1 - x0), y1 + (y1 - y0));
				x2 = node.x;
				y2 = node.y;

				double dx10 = x1 - x0;
				double dx20 = x2 - x0;
				double dx21 = x2 - x1;
				double dx10_2 = dx10 * dx10;
				double dx20_2 = dx20 * dx20;
				double dy10 = y1 - y0;
				double dy21 = y2 - y1;

				m0 = dy10 / dx10 - dx10 / dx20 * (dy21 / dx21 - dy10 / dx10);
				a = dy21 / (dx21 * dx20_2) - dy10 * (dx10 + dx20) / (dx10_2 * dx20_2) + m0 / (dx10 * dx20);
				b = dy10 / dx10_2 - a * dx10 - m0 / dx10;
				break;
			}

			default:
				// do nothing
				break;
		}
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public double evaluate(double x)
	{
		if ((x < Envelope.Node.MIN_X) || (x > Envelope.Node.MAX_X))
			throw new IllegalArgumentException();

		if (x != evalX)
		{
			evalX = x;
			switch (kind)
			{
				case LINEAR:
				{
					EnvelopeNode node = nodes.get(nodeIndex);
					double x0 = node.x;
					double y0 = node.y;
					node = nodes.get(nodeIndex + 1);
					while (x > node.x)
					{
						x0 = node.x;
						y0 = node.y;
						++nodeIndex;
						node = nodes.get(nodeIndex + 1);
					}
					evalY = (node.x == x0) ? 0.5 * (y0 + node.y)
										   : y0 + (x - x0) * (node.y - y0) / (node.x - x0);
					break;
				}

				case CUBIC_SEGMENT:
				{
					EnvelopeNode node = nodes.get(nodeIndex);
					double x0 = node.x;
					double y0 = node.y;
					node = nodes.get(nodeIndex + 1);
					while (x > node.x)
					{
						x0 = node.x;
						y0 = node.y;
						++nodeIndex;
						node = nodes.get(nodeIndex + 1);
					}
					double x1 = node.x;
					if (x0 == x1)
						evalY = 0.5 * (y0 + node.y);
					else
					{
						double a = 2.0 * (node.y - y0) /
													(x0 * x0 * (x0 - 3.0 * x1) - x1 * x1 * (x1 - 3.0 * x0));
						double b = -1.5 * a * (x0 + x1);
						double c = 3.0 * a * x0 * x1;
						double d = y0 + 0.5 * a * x0 * x0 * (x0 - 3.0 * x1);
						evalY = ((a * x + b) * x + c) * x + d;
					}
					break;
				}

				case CUBIC_SPLINE_A:
				{
					while (x >= x1)
					{
						x0 = x1;
						y0 = y1;

						x1 = x2;
						y1 = y2;

						EnvelopeNode node = (nodeIndex < nodes.size())
													? nodes.get(nodeIndex++)
													: new EnvelopeNode(x1 + (x1 - x0), y1 + (y1 - y0));
						x2 = node.x;
						y2 = node.y;

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
					}
					double dx = x - x0;
					evalY = ((a * dx + b) * dx + m0) * dx + y0;
					break;
				}

				case CUBIC_SPLINE_B:
				{
					while ((x >= x1) && (nodeIndex < nodes.size()))
					{
						double prevDx10 = x1 - x0;

						x0 = x1;
						y0 = y1;

						x1 = x2;
						y1 = y2;

						EnvelopeNode node = nodes.get(nodeIndex++);
						x2 = node.x;
						y2 = node.y;

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
						a = dy21 / (dx21 * dx20_2) - dy10 * (dx10 + dx20) / (dx10_2 * dx20_2) + m0 /
																							(dx10 * dx20);
						b = dy10 / dx10_2 - a * dx10 - m0 / dx10;
					}
					double dx = x - x0;
					evalY = ((a * dx + b) * dx + m0) * dx + y0;
					break;
				}
			}
		}

		return evalY;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	Envelope.Kind		kind;
	private	List<EnvelopeNode>	nodes;
	private	int					nodeIndex;
	private	double				evalX;
	private	double				evalY;
	private	double				x0;
	private	double				x1;
	private	double				x2;
	private	double				y0;
	private	double				y1;
	private	double				y2;
	private	double				m0;
	private	double				m1;
	private	double				a;
	private	double				b;

}

//----------------------------------------------------------------------
