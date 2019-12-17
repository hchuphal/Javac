package treeviewertest.model;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
	private static final String SLASH = "/";
	
	private static Map<String, TreeModel> gModels = new HashMap<String, TreeModel>();
	
	private Project fProject;
	private LinkedList<Node> fNewLocalList;
	private Node fTreeRoot;
	
	private TreeModel(Project project)
	{
		fProject = project;
		fNewLocalList = new LinkedList<Node>();
		fTreeRoot = new Node();
		
		for(Code code : fProject.getCodes())
		{
			for(String path : code.getParents())
			{
				insertIntoList(code, path);
			}
		}
				
		buildModel();
		
		aggregateFreqs();
	}
	
	/**
	 * 
	 */
	private void aggregateFreqs()
	{
		for(Node child : fTreeRoot.getChildren().values())
		{
			child.computeFreq();
		}
		
	}

	private void buildModel()
	{	
		Iterator<Node> iNode = fNewLocalList.iterator();
		while(iNode.hasNext())
		{
			Node node = iNode.next();
			if(node.getPathToRoot().equals(SLASH))
			{
				node.setParent(fTreeRoot);
				iNode.remove();
			}
		}
		
		for(Node node : fNewLocalList)
		{
			String path = node.getPathToRoot();
			String[] pathNodes = path.split(SLASH);
			
			Node parent = fTreeRoot;
			for(String nextNode : pathNodes)
			{
				parent = parent.getChild(nextNode);
			}
			
			node.setParent(parent);
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
				else if(!node.getChildren().isEmpty())
				{
					code.getParents().add("/");
				}
				break;
			}
		}
		
		for(Node child : node.getChildren().values())
		{
			depthFirstUpdate(child);
		}
	}
	
	private void insertIntoList(Code code, String pathToRoot)
	{
		Node node = new Node(code, pathToRoot);
		
		if(pathToRoot.split(SLASH).length == 0)
		{
			fNewLocalList.addFirst(node);
		}
		else
		{
			int index = binarySearch(pathToRoot.split(SLASH).length, 0, fNewLocalList.size() - 1);
			fNewLocalList.add(index, node);
		}
	}

	/**
	 * @param length
	 * @param i
	 * @param j
	 * @return
	 */
	private int binarySearch(int length, int start, int end)
	{
		if(end < start)
		{
			return 0;
		}
		
		if(start == end)
		{
			Node node = fNewLocalList.get(start);
			if(node.getPathToRoot().split(SLASH).length >= length)
			{
				return start;
			}
			else
			{
				return start + 1;
			}
		}
		else
		{
			int index = (start + end)/2;
			Node node = fNewLocalList.get(index);
			int nodeLength = node.getPathToRoot().split(SLASH).length;
			if(nodeLength == length)
			{
				return index;
			}
			else if(nodeLength > length)
			{
				if(index == end)
				{
					index--;
				}
				return binarySearch(length, start, index);
			}
			else
			{
				if(index == start)
				{
					index++;
				}
				return binarySearch(length, index, end);
			}
		}
	}
}
