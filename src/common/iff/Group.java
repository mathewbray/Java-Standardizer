/*====================================================================*\

Group.java

Abstract IFF group class.

\*====================================================================*/


// PACKAGE


package common.iff;

//----------------------------------------------------------------------


// IMPORTS


import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//----------------------------------------------------------------------


// ABSTRACT IFF GROUP CLASS


public abstract class Group
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	int	SIZE_SIZE	= 4;
	public static final	int	HEADER_SIZE	= IffId.SIZE + SIZE_SIZE + IffId.SIZE;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected Group()
	{
		groupId = new IffId();
		typeId = new IffId();
		chunks = new Vector<>();
	}

	//------------------------------------------------------------------

	protected Group(IffId groupId,
					IffId typeId)
	{
		this.groupId = groupId;
		this.typeId = typeId;
		chunks = new ArrayList<>();
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	protected Group(byte[] headerBytes)
	{
		this();
		groupId.set(headerBytes);
		typeId.set(headerBytes, Chunk.HEADER_SIZE);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract int putHeader(byte[] buffer);

	//------------------------------------------------------------------

	public abstract int putHeader(byte[] buffer,
								  int    offset);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public IffId getGroupId()
	{
		return groupId;
	}

	//------------------------------------------------------------------

	public IffId getTypeId()
	{
		return typeId;
	}

	//------------------------------------------------------------------

	public List<Chunk> getChunks()
	{
		return chunks;
	}

	//------------------------------------------------------------------

	public int getSize()
	{
		int size = IffId.SIZE;
		for (Chunk chunk : chunks)
			size += chunk.getExtent();
		return size;
	}

	//------------------------------------------------------------------

	public int getExtent()
	{
		return (HEADER_SIZE + getSize());
	}

	//------------------------------------------------------------------

	public void setGroupId(IffId id)
	{
		groupId = id;
	}

	//------------------------------------------------------------------

	public void setTypeId(IffId id)
	{
		typeId = id;
	}

	//------------------------------------------------------------------

	public void init(IffId  groupId,
					 IffId  typeId)
	{
		this.groupId = groupId;
		this.typeId = typeId;
		chunks.clear();
	}

	//------------------------------------------------------------------

	public void addChunk(Chunk chunk)
	{
		chunks.add(chunk);
	}

	//------------------------------------------------------------------

	public void put(byte[] buffer)
	{
		put(buffer, 0);
	}

	//------------------------------------------------------------------

	public void put(byte[] buffer,
					int    offset)
	{
		putHeader(buffer, offset);
		offset += HEADER_SIZE;
		for (Chunk chunk : chunks)
		{
			chunk.put(buffer, offset);
			offset += chunk.getExtent();
		}
	}

	//------------------------------------------------------------------

	public void write(OutputStream outStream)
		throws IOException
	{
		byte header[] = new byte[HEADER_SIZE];
		putHeader(header);
		outStream.write(header);
		for (Chunk chunk : chunks)
			chunk.write(outStream);
	}

	//------------------------------------------------------------------

	public void write(DataOutput dataOutput)
		throws IOException
	{
		byte header[] = new byte[HEADER_SIZE];
		putHeader(header);
		dataOutput.write(header);
		for (Chunk chunk : chunks)
			chunk.write(dataOutput);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	IffId		groupId;
	protected	IffId		typeId;
	protected	List<Chunk>	chunks;

}

//----------------------------------------------------------------------
