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
public class Code
{
	private String fName;
	private List<String> fParents;
	private int fFrequency;
	
	public Code(String name)
	{
		fName = name;
		fParents = new ArrayList<String>();
	}
	
	public String getName()
	{
		return fName;
	}
	
	public List<String> getParents()
	{
		return fParents;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return fName + " : " + fParents.toString();
	}

	public void setFrequency(int frequency)
	{
		fFrequency = frequency;
	}

	public int getFrequency()
	{
		return fFrequency;
	}
}
