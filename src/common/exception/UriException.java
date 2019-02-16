/*====================================================================*\

UriException.java

URI exception class.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.net.URI;

//----------------------------------------------------------------------


// URI EXCEPTION CLASS


public class UriException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UriException(AppException.IId id,
						URI              uri)
	{
		super(id);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException.IId id,
						URI              uri,
						Throwable        cause)
	{
		super(id, cause);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException.IId id,
						URI              uri,
						String...        substitutionStrs)
	{
		super(id, substitutionStrs);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException.IId id,
						URI              uri,
						Throwable        cause,
						String...        substitutionStrs)
	{
		super(id, cause, substitutionStrs);
		this.uri = uri;
	}

	//------------------------------------------------------------------

	public UriException(AppException exception,
						URI          uri)
	{
		this(exception.getId(), uri, exception.getCause(), exception.getSubstitutionStrings());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getPrefix()
	{
		return ((uri == null) ? null : uri + "\n");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public URI getUri()
	{
		return uri;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	URI	uri;

}

//----------------------------------------------------------------------
