/*====================================================================*\

KeyValuePair.java

Class: key-value pair.

\*====================================================================*/


// PACKAGE


package common.tuple;

//----------------------------------------------------------------------


// CLASS: KEY-VALUE PAIR


public class KeyValuePair<K extends Comparable<K>, V>
	implements Comparable<KeyValuePair<K, V>>
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public KeyValuePair(K key,
						V value)
	{
		if ((key == null) || (value == null))
			throw new IllegalArgumentException();

		this.key = key;
		this.value = value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : Comparable interface
////////////////////////////////////////////////////////////////////////

	public int compareTo(KeyValuePair<K, V> entry)
	{
		return key.compareTo(entry.key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof KeyValuePair<?, ?>)
		{
			KeyValuePair<?, ?> kvPair = (KeyValuePair<?, ?>)obj;
			return (key.equals(kvPair.key) && value.equals(kvPair.value));
		}
		return false;
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		return (key.hashCode() * 31 + value.hashCode());
	}

	//------------------------------------------------------------------

	@Override
	public String toString()
	{
		return (key.toString() + " = " + value.toString());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public K getKey()
	{
		return key;
	}

	//------------------------------------------------------------------

	public V getValue()
	{
		return value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	private	K	key;
	private	V	value;

}

//----------------------------------------------------------------------
