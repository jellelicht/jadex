package jadex.backup.resource;


/**
 *  Struct for posting information about the progress of
 *  ongoing backup processes.
 */
public class BackupEvent
{
//	public static final String FILE_UPDATE_START = "file_update_start";
	
	/** Event type for information about a download progress. */
	public static final String DOWNLOAD_STATE = "download_state";
	
//	public static final String FILE_UPDATE_END = "file_update_end";
	
//	public static final String FILE_UPDATE_ERROR = "file_update_error";
	
	public static final String ERROR = "error";
	
	public static final String FILE_STATE = "file_state";



	//-------- attributes --------
	
	/** The event type. */
	protected String	type;
	
	/** The local file info. */
	protected FileMetaInfo localfi;
	
	/** The remote file info. */
	protected FileMetaInfo remotefi;
	
//	/** The current progress state of the file (0..1) or -1 if atomic event. */
	protected Object details;
	
	//-------- constructors --------
	
	/**
	 *  Create a new backup event.
	 */
	public BackupEvent()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a new backup event.
	 */
	public BackupEvent(String type, FileMetaInfo remotefi)
	{
		this(type, null, remotefi, null);
	}
	
	/**
	 *  Create a new backup event.
	 */
	public BackupEvent(String type, FileMetaInfo localfi, FileMetaInfo remotefi, Object details)
	{
		setType(type);
		this.localfi	= localfi;
		this.remotefi	= remotefi;
		this.details = details;
	}

	//-------- methods --------
	
	/**
	 *  Get the type.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 *  Set the type.
	 */
	public void setType(String type)
	{
		if(type==null || !(DOWNLOAD_STATE.equals(type) || ERROR.equals(type) || FILE_STATE.equals(type)))
			throw new IllegalArgumentException("Unknown type: "+type);
		this.type = type;
	}

	/**
	 *  Get the file.
	 */
	public FileMetaInfo getLocalFile()
	{
		return localfi;
	}
	
	/**
	 *  Set the file.
	 */
	public void setLocalFile(FileMetaInfo fi)
	{
		this.localfi = fi;
	}
	
	/**
	 *  Get the remote file.
	 */
	public FileMetaInfo getRemoteFile()
	{
		return remotefi;
	}
	
	/**
	 *  Set the remote file.
	 */
	public void setRemoteFile(FileMetaInfo fi)
	{
		this.remotefi = fi;
	}

	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public Object getDetails()
	{
		return details;
	}

	/**
	 *  Set the details.
	 *  @param details The details to set.
	 */
	public void setDetails(Object details)
	{
		this.details = details;
	}

	/**
	 * 
	 */
	public String toString()
	{
		return "BackupEvent [type=" + type + ", file=" + (remotefi!=null ? remotefi.getPath() : "null") + ", details="+ details + "]";
	}

//	public String toString()
//	{
//		return type + ": " + file.getPath() + (progress>=0 ? progress<1 ? " ("+(int)(progress*100)+"%)" : " (done)" : ""); 
//	}
}
