/*====================================================================*\

EnvelopeScrollPane.java

Envelope scroll pane class.

\*====================================================================*/


// PACKAGE


package common.envelope;

//----------------------------------------------------------------------


// IMPORTS


import javax.swing.JScrollPane;

//----------------------------------------------------------------------


// ENVELOPE SCROLL PANE CLASS


public class EnvelopeScrollPane
	extends JScrollPane
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public EnvelopeScrollPane(EnvelopeView envelopeView)
	{
		super(envelopeView, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		setColumnHeaderView(envelopeView.getXScale());
		setRowHeaderView(envelopeView.getYScale());
		setCorner(JScrollPane.UPPER_LEFT_CORNER, envelopeView.getUpperLeftCorner());
		setCorner(JScrollPane.LOWER_LEFT_CORNER, envelopeView.getLowerLeftCorner());

		envelopeView.setViewport(getViewport());

		getHorizontalScrollBar().setFocusable(false);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
