package treeviewertest.model;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author Jonathan Faubert
 *
 */
public class Project
{
	private String fName;
	private List<Code> fCodes;
	
	public Project(String name)
	{
		fName = name;
		fCodes = new ArrayList<Code>();
	}
	
	public List<Code> getCodes()
	{
		return fCodes;
	}
	
	public String getName()
	{
		return fName;
	}
}
