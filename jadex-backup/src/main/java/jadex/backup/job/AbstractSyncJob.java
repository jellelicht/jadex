package jadex.backup.job;

import jadex.backup.resource.BackupResource;
import jadex.backup.resource.IBackupResource;
import jadex.backup.swing.SyncJobPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;

import java.io.File;

/**
 * 
 */
public class AbstractSyncJob extends Job
{
	//-------- attributes --------
	
	/** The global resource. */
	protected String gres;
	
	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public AbstractSyncJob()
	{
	}
	
	/**
	 *  Create a new job.
	 */
	public AbstractSyncJob(String id, String name, String gres)
	{
		super(id, name);
		this.gres = gres;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the globalResource.
	 *  @return The globalResource.
	 */
	public String getGlobalResource()
	{
		return gres;
	}

	/**
	 *  Set the globalResource.
	 *  @param globalResource The globalResource to set.
	 */
	public void setGlobalResource(String globalResource)
	{
		this.gres = globalResource;
	}
	
//		/**
//		 *  Get the details about a job.
//		 *  @return The details.
//		 */
//		public String getDetails()
//		{
//			StringBuffer ret = new StringBuffer(lres);
//			if(gres!=null)
//				ret.append(", id: ").append(gres);
//			return ret.toString();
//		}
	
	/**
	 *  Get the agent type.
	 */
	public String getAgentType()
	{
		return "jadex/backup/job/processing/SyncJobProcessingAgent.class";
	}

	/** 
	 * 
	 */
	public String toString()
	{
		return "AbstractSyncJob(gres=" + gres + ", id=" + id + ", name="+ name + ")";
	}
	
}
