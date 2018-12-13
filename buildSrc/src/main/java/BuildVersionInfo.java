import java.util.Properties;

/**
 *  Struct for build version information.
 */
public class BuildVersionInfo
{
	//-------- constants --------
	
	/** Properties prefix for util/commons/version.properties. */
	public static final String	PROPS_PREFIX	= "jadex_build_";
	
	//-------- attributes --------
	
	/** Major version number to be incremented on incompatible API changes. */
    public final int	major;
    
	/** Minor version number to be incremented on backwards compatible API changes. */
    public final int	minor;

	/** Patch version number to be incremented on internal changes. */
    public final int	patch;
    
	/** The repo branch which was built (if any). */
    public final String	branch;

	/** The time stamp of the latest commit (for clean builds) or of the build itself (for snapshot builds). */
    public final String	timestamp;
    
    /** The commit hash of the latest commit (if built from cloned repo). */
    public final String	commit;
    
    /** Flag to indicate a non-clean build (i.e. with local modifications not under version control). */
    public final boolean	snapshot;

    /** Flag to indicate a release (i.e. a matching tag exists in git). */
    public final boolean	release;

    //-------- constructors --------
    
    /**
     *  Create a version info object.
     *
     * @param major	Major version number to be incremented on incompatible API changes.
     * @param minor	Minor version number to be incremented on backwards compatible API changes.
     * @param patch	Patch version number to be incremented on internal changes.
     * @param branch	The repo branch which was built (if any).
     * @param timestamp	The time stamp of the latest commit (for clean builds) or of the build itself (for snapshot builds).
     * @param commit	The commit hash of the latest commit (if built from cloned repo).
     * @param snapshot	Flag to indicate a non-clean build (i.e. with local modifications not under version control).
     * @param release	Flag to indicate a release (i.e. a matching tag exists in git).
     */
    public BuildVersionInfo(int major, int minor, int patch,
    	String branch, String timestamp, String commit,
    	boolean snapshot, boolean release)
    {
        this.major	= major;
        this.minor	= minor;
        this.patch	= patch;
        this.branch	= branch;
        this.timestamp	= timestamp;
        this.commit	= commit;
        this.snapshot	= snapshot;
        this.release	= release;
    }

    /**
     *  Version name as it is used to name artifacts.
     */
    @Override
    public String toString()
    {
    	StringBuffer	ret	= new StringBuffer();
    	ret.append(major).append(".").append(minor).append(".").append(patch);
    	if(!release)
    		ret.append("-beta");
    	if(branch!=null && !branch.isEmpty())
    		ret.append("-").append(branch);
    	if(snapshot)
    		ret.append("-SNAPSHOT");
    	else if(!release)
    		ret.append("-").append(timestamp);
    	return ret.toString();
    }
    
    /**
     *  Version info as text in name-value pairs on separate lines
     *  for appending all version info to a properties file.
     */
    public String	getPropertiesString()
    {
    	return PROPS_PREFIX + "version\t= " + toString() + "\n"
		+  PROPS_PREFIX + "major\t= " + major + "\n"
		+  PROPS_PREFIX + "minor\t= " + minor + "\n"
		+  PROPS_PREFIX + "patch\t= " + patch + "\n"
		+  PROPS_PREFIX + "branch\t= " + branch + "\n"
		+  PROPS_PREFIX + "timestamp\t= " + timestamp + "\n"
		+  PROPS_PREFIX + "commit\t= " + commit + "\n"
		+  PROPS_PREFIX + "snapshot\t= " + snapshot + "\n"
		+  PROPS_PREFIX + "release\t= " + release + "\n";
    }

    /**
     *  Read build version info from properties.
     *  @param props	The properties as produced by {@link #getPropertiesString()}.
     */
    public static BuildVersionInfo	fromProperties(Properties props)
    {
    	int major	= Integer.parseInt(props.getProperty(PROPS_PREFIX+"major"));
    	int minor	= Integer.parseInt(props.getProperty(PROPS_PREFIX+"minor"));
    	int patch	= Integer.parseInt(props.getProperty(PROPS_PREFIX+"patch"));
    	String branch	= props.getProperty(PROPS_PREFIX+"branch");
    	String timestamp	= props.getProperty(PROPS_PREFIX+"timestamp");
    	String commit	= props.getProperty(PROPS_PREFIX+"commit");
    	boolean	snapshot	= Boolean.parseBoolean(props.getProperty(PROPS_PREFIX+"snapshot"));
    	boolean	release	= Boolean.parseBoolean(props.getProperty(PROPS_PREFIX+"release"));
    	
    	return new BuildVersionInfo(major, minor, patch, branch, timestamp, commit, snapshot, release);
    }

    /**
     *  Check if two version info objects are the same except for the timestamp
     */
    public boolean equalsIgnoreTimestamp(BuildVersionInfo i2)
    {
		boolean	ret	= major==i2.major
			&& minor==i2.minor
			&& patch==i2.patch
			&& snapshot==i2.snapshot
			&& release==i2.release
			&& branch.equals(i2.branch)
			&& commit.equals(i2.commit);
		
		System.out.println("equalsIgnoreTimestamp "+this+" vs. "+i2+" = "+ret);
		return ret;
    }
    
    @Override
    public boolean equals(Object obj)
    {
    	if(obj==this)
    		return true;
    	if(obj instanceof BuildVersionInfo)
    	{
    		BuildVersionInfo	i2	= (BuildVersionInfo) obj;
    		return equalsIgnoreTimestamp(i2)
				&& timestamp.equals(i2.timestamp);
    	}
    	else
    	{
    		return false;
    	}
    }
    
    @Override
    public int hashCode() {
    	int hash	= 31;
    	hash	*= major +31;
    	hash	*= minor +31;
    	hash	*= patch +31;
    	hash	*= branch.hashCode() +31;
    	hash	*= commit.hashCode() +31;
    	hash	*= timestamp.hashCode() +31;
    	hash	*= snapshot ? 31 : 13;
    	hash	*= release ? 31 : 13;
    	return hash;
    }
}