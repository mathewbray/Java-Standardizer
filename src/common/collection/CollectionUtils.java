/*====================================================================*\

CollectionUtils.java

Class: collection utility methods.

\*====================================================================*/


// PACKAGE


package common.collection;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: COLLECTION UTILITY METHODS


public class CollectionUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private CollectionUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isNullOrEmpty(Collection<?> collection)
	{
		return (collection == null) || collection.isEmpty();
	}

	//------------------------------------------------------------------

	public static List<Integer> intArrayToList(int[] values)
	{
		List<Integer> outValues = new ArrayList<>();
		for (int value : values)
			outValues.add(value);
		return outValues;
	}

	//------------------------------------------------------------------

	public static int[] intListToArray(List<Integer> values)
	{
		int[] outValues = new int[values.size()];
		for (int i = 0; i < outValues.length; i++)
			outValues[i] = values.get(i);
		return outValues;
	}

	//------------------------------------------------------------------

	public static List<Long> longArrayToList(long[] values)
	{
		List<Long> outValues = new ArrayList<>();
		for (long value : values)
			outValues.add(value);
		return outValues;
	}

	//------------------------------------------------------------------

	public static long[] longListToArray(List<Long> values)
	{
		long[] outValues = new long[values.size()];
		for (int i = 0; i < outValues.length; i++)
			outValues[i] = values.get(i);
		return outValues;
	}

	//------------------------------------------------------------------

	public static List<Double> doubleArrayToList(double[] values)
	{
		List<Double> outValues = new ArrayList<>();
		for (double value : values)
			outValues.add(value);
		return outValues;
	}

	//------------------------------------------------------------------

	public static double[] doubleListToArray(List<Double> values)
	{
		double[] outValues = new double[values.size()];
		for (int i = 0; i < outValues.length; i++)
			outValues[i] = values.get(i);
		return outValues;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
