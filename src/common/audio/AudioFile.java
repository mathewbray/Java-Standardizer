/*====================================================================*\

AudioFile.java

Audio file class.

\*====================================================================*/


// PACKAGE


package common.audio;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.List;

import common.exception.AppException;
import common.exception.FileException;

import common.iff.Chunk;
import common.iff.ChunkFilter;
import common.iff.FormFile;
import common.iff.Group;
import common.iff.IffException;
import common.iff.IffId;

import common.misc.IByteDataInputStream;
import common.misc.IByteDataOutputStream;
import common.misc.IByteDataSource;
import common.misc.IDataInput;
import common.misc.IDoubleDataInputStream;
import common.misc.IDoubleDataOutputStream;
import common.misc.IDoubleDataSource;
import common.misc.IStringKeyed;

//----------------------------------------------------------------------


// AUDIO FILE CLASS


public abstract class AudioFile
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	protected static final	int	MAX_BITS_PER_SAMPLE		= 32;

	protected static final	int	DEFAULT_NUM_CHANNELS	= 2;
	protected static final	int	DEFAULT_BITS_PER_SAMPLE	= 16;
	protected static final	int	DEFAULT_SAMPLE_RATE		= 44100;

	protected enum SampleFormat
	{
		NONE,
		INTEGER,
		DOUBLE
	}

	private static final	String	NUM_CHANNELS_STR	= "Number of channels";
	private static final	String	BITS_PER_SAMPLE_STR	= "Bits per sample";
	private static final	String	SAMPLE_RATE_STR		= "Sample rate";
	private static final	String	HERTZ_STR			= " Hz";
	private static final	String	EQUALS_STR			= " = ";

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// AUDIO FILE KIND


	public enum Kind
		implements IStringKeyed
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		AIFF
		(
			"aiff",
			"AIFF",
			AiffFile.IFF_GROUP_ID,
			AiffFile.AIFF_TYPE_ID,
			FilenameSuffixes.AIFF
		)
		{
			@Override
			public AudioFile createFile(File file)
			{
				return new AiffFile(file);
			}

			//----------------------------------------------------------

			@Override
			public AudioFile createFile(File file,
										int  numChannels,
										int  bitsPerSample,
										int  sampleRate)
			{
				return new AiffFile(file, numChannels, bitsPerSample, sampleRate);
			}

			//----------------------------------------------------------
		},

		WAVE
		(
			"wave",
			"WAVE",
			WaveFile.RIFF_GROUP_ID,
			WaveFile.WAVE_TYPE_ID,
			FilenameSuffixes.WAVE
		)
		{
			@Override
			public AudioFile createFile(File file)
			{
				return new WaveFile(file);
			}

			//----------------------------------------------------------

			@Override
			public AudioFile createFile(File file,
										int  numChannels,
										int  bitsPerSample,
										int  sampleRate)
			{
				return new WaveFile(file, numChannels, bitsPerSample, sampleRate);
			}

			//----------------------------------------------------------
		};

		private interface FilenameSuffixes
		{
			String[]	AIFF	= { ".aif", ".aiff" };
			String[]	WAVE	= { ".wav", ".wave" };
		}

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Kind(String   key,
					 String   text,
					 IffId    groupId,
					 IffId    typeId,
					 String[] filenameSuffixes)
		{
			this.key = key;
			this.text = text;
			this.groupId = groupId;
			this.typeId = typeId;
			this.filenameSuffixes = filenameSuffixes;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

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

		public static Kind forId(IffId groupId,
								 IffId typeId)
		{
			for (Kind value : values())
			{
				if (value.groupId.equals(groupId) && value.typeId.equals(typeId))
					return value;
			}
			return null;
		}

		//--------------------------------------------------------------

		public static Kind forFile(File file)
			throws AppException
		{
			Kind kind = forFilename(file.getName());
			if (kind == null)
				kind = getFileKind(file);
			return kind;
		}

		//--------------------------------------------------------------

		public static Kind forFilename(String filename)
		{
			int index = filename.lastIndexOf('.');
			if (index >= 0)
			{
				String suffix = filename.substring(index);
				for (Kind value : values())
				{
					for (String filenameSuffix : value.filenameSuffixes)
					{
						if (filenameSuffix.equals(suffix))
							return value;
					}
				}
			}
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		public abstract AudioFile createFile(File file);

		//--------------------------------------------------------------

		public abstract AudioFile createFile(File file,
											 int  numChannels,
											 int  bitsPerSample,
											 int  sampleRate);

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

		private	String		key;
		private	String		text;
		private	IffId		groupId;
		private	IffId		typeId;
		private	String[]	filenameSuffixes;

	}

	//==================================================================


	// ERROR IDENTIFIERS


	protected enum ErrorId
		implements AppException.IId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		FILE_DOES_NOT_EXIST
		("The file does not exist."),

		FAILED_TO_OPEN_FILE
		("Failed to open the file."),

		FAILED_TO_CLOSE_FILE
		("Failed to close the file."),

		FAILED_TO_LOCK_FILE
		("Failed to lock the file."),

		ERROR_READING_FILE
		("An error occurred when reading the file."),

		FILE_ACCESS_NOT_PERMITTED
		("Access to the file was not permitted."),

		FILE_IS_NOT_OPEN
		("The file is not open."),

		MALFORMED_FILE
		("The file is malformed."),

		ILLEGAL_CHUNK_ID
		("The file contains an illegal chunk identifier."),

		NO_DATA_CHUNK
		("The file does not have a data chunk.");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private ErrorId(String message)
		{
			this.message = message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : AppException.IId interface
	////////////////////////////////////////////////////////////////////

		public String getMessage()
		{
			return message;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance fields
	////////////////////////////////////////////////////////////////////

		private	String	message;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected AudioFile(File file)
	{
		this(file, DEFAULT_NUM_CHANNELS, DEFAULT_BITS_PER_SAMPLE, DEFAULT_SAMPLE_RATE);
	}

	//------------------------------------------------------------------

	protected AudioFile(File file,
						int  numChannels,
						int  bitsPerSample,
						int  sampleRate)
	{
		this.file = file;
		this.numChannels = numChannels;
		this.bitsPerSample = bitsPerSample;
		this.sampleRate = sampleRate;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int bitsPerSampleToBytesPerSample(int bitsPerSample)
	{
		return (bitsPerSample + 7 >> 3);
	}

	//------------------------------------------------------------------

	public static double getMaxInputSampleValue(int bytesPerSample)
	{
		return (double)(1 << ((bytesPerSample << 3) - 1));
	}

	//------------------------------------------------------------------

	public static double getMaxOutputSampleValue(int bytesPerSample)
	{
		return (double)((1 << ((bytesPerSample << 3) - 1)) - 1);
	}

	//------------------------------------------------------------------

	public static Kind getDefaultFileKind()
	{
		return defaultFileKind;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public static void setDefaultFileKind(Kind fileKind)
	{
		if (fileKind == null)
			throw new IllegalArgumentException();
		defaultFileKind = fileKind;
	}

	//------------------------------------------------------------------

	public static Kind getFileKind(File file)
		throws AppException
	{
		// Test for file
		if (!file.isFile())
			throw new FileException(ErrorId.FILE_DOES_NOT_EXIST, file);

		// Read file
		Kind fileKind = null;
		RandomAccessFile raFile = null;
		try
		{
			// Open file for random access, read only
			try
			{
				raFile = new RandomAccessFile(file, "r");
			}
			catch (FileNotFoundException e)
			{
				throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file, e);
			}
			catch (SecurityException e)
			{
				throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
			}

			// Lock file
			try
			{
				if (raFile.getChannel().tryLock(0, Long.MAX_VALUE, true) == null)
					throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file, e);
			}

			// Read group header
			try
			{
				if (raFile.length() >= Group.HEADER_SIZE)
				{
					byte[] buffer = new byte[Group.HEADER_SIZE];
					raFile.readFully(buffer);
					fileKind = Kind.forId(new IffId(buffer), new IffId(buffer, Chunk.HEADER_SIZE));
				}
			}
			catch (IllegalArgumentException e)
			{
				// do nothing
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.ERROR_READING_FILE, file, e);
			}

			// Close random access file
			try
			{
				raFile.close();
				raFile = null;
			}
			catch (IOException e)
			{
				throw new FileException(ErrorId.FAILED_TO_CLOSE_FILE, file, e);
			}
		}
		catch (AppException e)
		{
			// Close random access file
			try
			{
				if (raFile != null)
					raFile.close();
			}
			catch (IOException e1)
			{
				// ignore
			}

			// Rethrow exception
			throw e;
		}
		return fileKind;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract void addChunks(List<Chunk> chunks)
		throws ClassCastException;

	//------------------------------------------------------------------

	public abstract void read(FormFile.IChunkReader chunkReader)
		throws AppException;

	//------------------------------------------------------------------

	public abstract int read(double[] buffer,
							 int      offset,
							 int      length)
		throws AppException;

	//------------------------------------------------------------------

	protected abstract Object read(SampleFormat sampleFormat,
								   int          bytesPerSample,
								   Object       outStream,
								   ChunkFilter  filter)
		throws AppException;

	//------------------------------------------------------------------

	protected abstract int readGroupHeader()
		throws AppException;

	//------------------------------------------------------------------

	protected abstract void write(IDataInput      sampleDataInput,
								  IDataInput.Kind inputKind)
		throws AppException;

	//------------------------------------------------------------------

	protected abstract int getChunkSize(byte[] buffer,
										int    offset);

	//------------------------------------------------------------------

	protected abstract IffId getDataChunkId();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return (NUM_CHANNELS_STR + EQUALS_STR + numChannels + "\n" +
					BITS_PER_SAMPLE_STR + EQUALS_STR + bitsPerSample + "\n" +
					SAMPLE_RATE_STR + EQUALS_STR + sampleRate + HERTZ_STR);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public File getFile()
	{
		return file;
	}

	//------------------------------------------------------------------

	public int getNumChannels()
	{
		return numChannels;
	}

	//------------------------------------------------------------------

	public int getBitsPerSample()
	{
		return bitsPerSample;
	}

	//------------------------------------------------------------------

	public int getSampleRate()
	{
		return sampleRate;
	}

	//------------------------------------------------------------------

	public int getNumSampleFrames()
	{
		return numSampleFrames;
	}

	//------------------------------------------------------------------

	public int getBytesPerSample()
	{
		return bitsPerSampleToBytesPerSample(bitsPerSample);
	}

	//------------------------------------------------------------------

	public int getBytesPerSampleFrame()
	{
		return (numChannels * bitsPerSampleToBytesPerSample(bitsPerSample));
	}

	//------------------------------------------------------------------

	public void setNumChannels(int numChannels)
	{
		this.numChannels = numChannels;
	}

	//------------------------------------------------------------------

	public void setBitsPerSample(int bitsPerSample)
	{
		this.bitsPerSample = bitsPerSample;
	}

	//------------------------------------------------------------------

	public void setSampleRate(int sampleRate)
	{
		this.sampleRate = sampleRate;
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public void open()
		throws AppException
	{
		// Test whether random access file is already open
		if (raFile != null)
			throw new IllegalStateException();

		// Open file for random access, read only
		try
		{
			sampleDataOffset = 0;
			raFile = new RandomAccessFile(file, "r");
		}
		catch (FileNotFoundException e)
		{
			throw new FileException(ErrorId.FAILED_TO_OPEN_FILE, file, e);
		}
		catch (SecurityException e)
		{
			throw new FileException(ErrorId.FILE_ACCESS_NOT_PERMITTED, file, e);
		}

		// Lock file
		try
		{
			if (raFile.getChannel().tryLock(0, Long.MAX_VALUE, true) == null)
				throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file);
		}
		catch (Exception e)
		{
			throw new FileException(ErrorId.FAILED_TO_LOCK_FILE, file, e);
		}

		// Read and test group header
		readGroupHeader();
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public void close()
		throws AppException
	{
		// Test whether random access file is open
		if (raFile == null)
			throw new IllegalStateException();

		// Close random-access file
		try
		{
			raFile.close();
			raFile = null;
		}
		catch (IOException e)
		{
			throw new FileException(ErrorId.FAILED_TO_CLOSE_FILE, file, e);
		}
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalArgumentException
	 */

	public void seekSampleFrame(int index)
		throws AppException
	{
		// Validate sample frame index
		if (index >= numSampleFrames)
			throw new IllegalArgumentException();

		// Test whether random-access file is open
		if (raFile == null)
			throw new FileException(ErrorId.FILE_IS_NOT_OPEN, file);

		// Seek sample frame
		try
		{
			// Set offset to data chunk
			if (sampleDataOffset == 0)
			{
				if (findChunk(getDataChunkId()) < 0)
					throw new FileException(ErrorId.NO_DATA_CHUNK, file);
				sampleDataOffset = raFile.getFilePointer();
			}

			// Seek sample frame
			raFile.seek(sampleDataOffset + index * getBytesPerSampleFrame());
		}
		catch (IOException e)
		{
			throw new FileException(ErrorId.ERROR_READING_FILE, file, e);
		}
	}

	//------------------------------------------------------------------

	public int read(byte[] buffer)
		throws AppException
	{
		return read(buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public int read(byte[] buffer,
					int    offset,
					int    length)
		throws AppException
	{
		// Test whether random-access file is open
		if (raFile == null)
			throw new FileException(ErrorId.FILE_IS_NOT_OPEN, file);

		// Read from random-access file
		try
		{
			return raFile.read(buffer, offset, length);
		}
		catch (IOException e)
		{
			throw new FileException(ErrorId.ERROR_READING_FILE, file, e);
		}
	}

	//------------------------------------------------------------------

	public int read(double[] buffer)
		throws AppException
	{
		return read(buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public void readAttributes()
		throws AppException
	{
		read(SampleFormat.NONE, 0, null, null);
	}

	//------------------------------------------------------------------

	public byte[] readInteger(ChunkFilter filter)
		throws AppException
	{
		return (byte[])read(SampleFormat.INTEGER, 0, null, filter);
	}

	//------------------------------------------------------------------

	public byte[] readInteger16(ChunkFilter filter)
		throws AppException
	{
		return (byte[])read(SampleFormat.INTEGER, 2, null, filter);
	}

	//------------------------------------------------------------------

	public byte[] readInteger24(ChunkFilter filter)
		throws AppException
	{
		return (byte[])read(SampleFormat.INTEGER, 3, null, filter);
	}

	//------------------------------------------------------------------

	public byte[] readInteger32(ChunkFilter filter)
		throws AppException
	{
		return (byte[])read(SampleFormat.INTEGER, 4, null, filter);
	}

	//------------------------------------------------------------------

	public double[] readDouble(ChunkFilter filter)
		throws AppException
	{
		return (double[])read(SampleFormat.DOUBLE, 0, null, filter);
	}

	//------------------------------------------------------------------

	public void readInteger(IByteDataOutputStream outStream,
							ChunkFilter           filter)
		throws AppException
	{
		read(SampleFormat.INTEGER, 0, outStream, filter);
	}

	//------------------------------------------------------------------

	public void readInteger16(IByteDataOutputStream outStream,
							  ChunkFilter           filter)
		throws AppException
	{
		read(SampleFormat.INTEGER, 2, outStream, filter);
	}

	//------------------------------------------------------------------

	public void readInteger24(IByteDataOutputStream outStream,
							  ChunkFilter           filter)
		throws AppException
	{
		read(SampleFormat.INTEGER, 3, outStream, filter);
	}

	//------------------------------------------------------------------

	public void readInteger32(IByteDataOutputStream outStream,
							  ChunkFilter           filter)
		throws AppException
	{
		read(SampleFormat.INTEGER, 4, outStream, filter);
	}

	//------------------------------------------------------------------

	public void readDouble(IDoubleDataOutputStream outStream,
						   ChunkFilter             filter)
		throws AppException
	{
		read(SampleFormat.DOUBLE, 0, outStream, filter);
	}

	//------------------------------------------------------------------

	public void write(IByteDataInputStream sampleDataStream)
		throws AppException
	{
		write(sampleDataStream, IDataInput.Kind.BYTE_STREAM);
	}

	//------------------------------------------------------------------

	public void write(IByteDataSource sampleDataSource)
		throws AppException
	{
		write(sampleDataSource, IDataInput.Kind.BYTE_SOURCE);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public void write(IDoubleDataInputStream sampleDataStream)
		throws AppException
	{
		if ((bitsPerSample != 16) && (bitsPerSample != 24))
			throw new IllegalStateException();
		write(sampleDataStream, IDataInput.Kind.DOUBLE_STREAM);
	}

	//------------------------------------------------------------------

	/**
	 * @throws IllegalStateException
	 */

	public void write(IDoubleDataSource sampleDataSource)
		throws AppException
	{
		if ((bitsPerSample != 16) && (bitsPerSample != 24))
			throw new IllegalStateException();
		write(sampleDataSource, IDataInput.Kind.DOUBLE_SOURCE);
	}

	//------------------------------------------------------------------

	protected int findChunk(IffId id)
		throws AppException
	{
		// Search for chunk
		try
		{
			// Seek start of file
			raFile.seek(0);

			// Read group header
			int groupSize = readGroupHeader();

			// Initialise variables
			long groupOffset = IffId.SIZE;
			byte[] buffer = new byte[Chunk.HEADER_SIZE];

			// Search for chunk with specified ID
			while (groupOffset < groupSize)
			{
				// Seek next chunk
				raFile.seek(Chunk.HEADER_SIZE + groupOffset);

				// Test whether chunk header extends beyond end of group
				if (groupOffset + Chunk.HEADER_SIZE > groupSize)
					throw new FileException(ErrorId.MALFORMED_FILE, file);

				// Read chunk header
				raFile.readFully(buffer);

				// Get chunk ID and size
				IffId chunkId = null;
				try
				{
					chunkId = new IffId(buffer);
				}
				catch (IllegalArgumentException e)
				{
					throw new FileException(ErrorId.ILLEGAL_CHUNK_ID, file);
				}
				int chunkSize = getChunkSize(buffer, IffId.SIZE);

				// Test whether chunk extends beyond end of group
				groupOffset += Chunk.HEADER_SIZE;
				if (groupOffset + chunkSize > groupSize)
					throw new IffException(ErrorId.MALFORMED_FILE, file, chunkId);

				// Test for target chunk
				if (chunkId.equals(id))
					return chunkSize;

				// Increment group offset
				groupOffset += chunkSize;
				if ((chunkSize & 1) != 0)
					++groupOffset;
			}

			// Indicate chunk not found
			return -1;
		}
		catch (IOException e)
		{
			throw new FileException(ErrorId.ERROR_READING_FILE, file, e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class fields
////////////////////////////////////////////////////////////////////////

	private static	Kind	defaultFileKind	= Kind.WAVE;

////////////////////////////////////////////////////////////////////////
//  Instance fields
////////////////////////////////////////////////////////////////////////

	protected	File				file;
	protected	int					numChannels;
	protected	int					bitsPerSample;
	protected	int					sampleRate;
	protected	int					numSampleFrames;
	protected	long				sampleDataOffset;
	protected	RandomAccessFile	raFile;

}

//----------------------------------------------------------------------
