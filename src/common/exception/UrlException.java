/*====================================================================*\

UrlException.java

URL exception class.

\*====================================================================*/


// PACKAGE


package common.exception;

//----------------------------------------------------------------------


// IMPORTS


import java.net.URL;

//----------------------------------------------------------------------


// URL EXCEPTION CLASS


public class UrlException
	extends AppException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public UrlException(AppException.IId id,
						URL              url)
	{
		super(id);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException.IId id,
						URL              url,
						Throwable        cause)
	{
		super(id, cause);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException.IId id,
						URL              url,
						String...        substitutionStrs)
	{
		super(id, substitutionStrs);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException.IId id,
						URL              url,
						Throwable        cause,
						String...        substitutionStrs)
	{
		super(id, cause, substitutionStrs);
		this.url = url;
	}

	//------------------------------------------------------------------

	public UrlException(AppException exception,
						URL          url)
	{
		this(exception.getId(), url, exception.getCause(), exception.getSubstitutionStrings());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getPrefix()
	{
		return ((url == null) ? null : url + "\n");
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public URL getUrl()
	{
		return url;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	URL	url;

}

//----------------------------------------------------------------------
