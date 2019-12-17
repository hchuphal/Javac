/**
 * 
 */
package treeviewertest.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author  Jonathan Faubert
 */
public class Node
{	
	private static final String EMPTY = "";
	private static final String SLASH = "/";
	
	private Node fParent;
	private LinkedHashMap<String, Node> fChildren;
	private String fPathToRoot;
	private String fCodeName;
	private int fLocalFreq;
	private int fAggrFreq;
	
	/**
	 * Build the root node.
	 */
	public Node()
	{
		fParent = null;
		fChildren = new LinkedHashMap<String, Node>();
		fPathToRoot = EMPTY;
		fCodeName = "";
		fLocalFreq = -1;
		fAggrFreq = -1;
	}
	
	/**
	 * Build a child node.
	 * @param parent
	 * @param codeName
	 */
	public Node(Node parent, Code code)
	{
		this.fParent = parent;
		this.fParent.getChildren().put(code.getName(), this);
		this.fChildren = new LinkedHashMap<String, Node>();
		this.fPathToRoot = EMPTY;
		
		if(!parent.getCodeName().equals(EMPTY))
		{
			if(!(parent.getPathToRoot().equals(SLASH)))
			{
				this.fPathToRoot = parent.getPathToRoot() + SLASH;
			}
			this.fPathToRoot += parent.getCodeName();
		}
		else
		{
			this.fPathToRoot = SLASH;
		}
		
		this.fCodeName = code.getName();
		this.fLocalFreq = code.getFrequency();
		this.fAggrFreq = fLocalFreq;
	}
	
	public Node(Code code, String pathToRoot)
	{
		fParent = null;
		fChildren = new LinkedHashMap<String, Node>();
		fPathToRoot = pathToRoot;
		fCodeName = code.getName();
		fLocalFreq = code.getFrequency();
		fAggrFreq = fLocalFreq;
	}
	
	public void setParent(Node node)
	{
		fParent = node;
		fParent.getChildren().put(fCodeName, this);
	}
	
	public String getPathToRoot()
	{
		return fPathToRoot;
	}
	
	public String getCodeName()
	{
		return fCodeName;
	}
	
	public Map<String, Node> getChildren()
	{
		return fChildren;
	}
	
	public Node getChild(String name)
	{
		return fChildren.get(name);
	}
	
	public Node getParent()
	{
		return fParent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
//		String output = EMPTY;
//		
//		for(int i = 0; i < fPathToRoot.split(SLASH).length; i++)
//		{
//			if(!fPathToRoot.isEmpty())
//			{
//				output += "\t";
//			}
//		}
//		
//		output += fCodeName + "\n";
//		
//		for(Node child : fChildren)
//		{	
//			output += child.toString(); 
//		}
//		
//		return output;
		
		return fCodeName + ":" + fPathToRoot;
	}

	public int computeFreq()
	{
		fAggrFreq = fLocalFreq;
		
		for(Node child : fChildren.values())
		{
			fAggrFreq += child.computeFreq();
		}
		
		return fAggrFreq;
	}
	
	public int getAggragateFreq()
	{
		return fAggrFreq;
	}
	
	public int getLocalFreq()
	{
		return fLocalFreq;
	}

	/**
	 * @param data
	 */
	public void addChild(String data)
	{
		String[] values = data.split(":");
		if(values.length != 2)
		{
			return;
		}
		
		Code code = new Code(values[0]);
		code.setFrequency(Integer.parseInt(values[1]));
		new Node(this, code);
		
	}
}