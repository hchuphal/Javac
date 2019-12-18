
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 */

/**
 * @author Jonathan Faubert
 *
 */
public class TreeModel
{
	private static final String EMPTY = "";
	private static final String SLASH = "/";
	
	private static Map<String, TreeModel> gModels = new HashMap<String, TreeModel>();
	
	private Project fProject;
	private List<Code> fLocalList;
	private Node fTreeRoot;
	
	private class Node
	{	
		private Node fParent;
		private List<Node> fChildren;
		private String fPathToRoot;
		private String fCodeName;
		
		/**
		 * Build the root node.
		 */
		public Node()
		{
			fParent = null;
			fChildren = new ArrayList<Node>();
			fPathToRoot = EMPTY;
			fCodeName = "";
		}
		
		/**
		 * Build a child node.
		 * @param parent
		 * @param codeName
		 */
		public Node(Node parent, String codeName)
		{
			fParent = parent;
			fChildren = new ArrayList<Node>();
			if(parent != fTreeRoot)
			{
				fPathToRoot = EMPTY;
				if(!parent.fPathToRoot.isEmpty())
				{
					fPathToRoot = parent.fPathToRoot + SLASH;
				}
				fPathToRoot += parent.fCodeName;
			}
			else
			{
				fPathToRoot = EMPTY;
			}
			fCodeName = codeName;			
		}
		
		public String getPathToRoot()
		{
			return fPathToRoot;
		}
		
		public String getCodeName()
		{
			return fCodeName;
		}
		
		public List<Node> getChildren()
		{
			return fChildren;
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
			String output = EMPTY;
			
			for(int i = 0; i < fPathToRoot.split(SLASH).length; i++)
			{
				if(!fPathToRoot.isEmpty())
				{
					output += "\t";
				}
			}
			
			output += fCodeName + "\n";
			
			for(Node child : fChildren)
			{	
				output += child.toString(); 
			}
			
			return output;
		}
	}
	
	private TreeModel(Project project)
	{
		fProject = project;
		fLocalList = new ArrayList<Code>();
		fTreeRoot = new Node();
		
		for(Code code : fProject.getCodes())
		{
			Code localCode = new Code(code.getName());
			for(String path : code.getParents())
			{
				localCode.getParents().add(path);
			}
			fLocalList.add(localCode);
		}
		
		buildModel();
	}
	
	private void buildModel()
	{
		for(Code code : fLocalList)
		{
			Node node = new Node(fTreeRoot, code.getName());
			fTreeRoot.getChildren().add(node);
		}
		
		Iterator<Code> iCode = fLocalList.iterator();
		while(iCode.hasNext())
		{
			Code code = iCode.next();
			if(code.getParents().isEmpty())
			{
				iCode.remove();
			}
		}
		
		int length = 1;
		while(!fLocalList.isEmpty())
		{
			iCode = fLocalList.iterator();
			while(iCode.hasNext())
			{
				Code code = iCode.next();
				Iterator<String> iString = code.getParents().iterator();
				while(iString.hasNext())
				{
					String path = iString.next();
					String[] pathNodes = path.split(SLASH);
					if(pathNodes.length == length)
					{
						Node parent = fTreeRoot;
						for(String nextNode : pathNodes)
						{
							for(Node child : parent.getChildren())
							{
								if(child.getCodeName().equals(nextNode))
								{
									parent = child;
									break;
								}
							}
						}
						
						Node node = new Node(parent, code.getName());
						parent.getChildren().add(node);
						iString.remove();
					}
				}
				
				if(code.getParents().isEmpty())
				{
					iCode.remove();
				}
			}
			length++;
		}
	}
	
	public static TreeModel getTreeModel(Project project)
	{
		TreeModel model = gModels.get(project.getName());
		if(model == null)
		{
			model = new TreeModel(project);
			gModels.put(project.getName(), model);
		}
		return model;
	}
	
	public Node getRoot()
	{
		return fTreeRoot;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return fTreeRoot.toString();
	}
	
	public void updatePaths()
	{
		for(Code code : fProject.getCodes())
		{
			code.getParents().clear();
		}
		
		depthFirstUpdate(fTreeRoot);
		
	}
	
	private void depthFirstUpdate(Node node)
	{
		for(Code code : fProject.getCodes())
		{
			if(code.getName().equals(node.getCodeName()))
			{
				String pathToRoot = node.getPathToRoot();
				if(!pathToRoot.isEmpty())
				{
					code.getParents().add(pathToRoot);
				}
				break;
			}
		}
		
		for(Node child : node.getChildren())
		{
			depthFirstUpdate(child);
		}
	}
}
