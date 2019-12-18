/**
 * 
 */
package treeviewertest.providers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import treeviewertest.model.Node;

/**
 * @author Jonathan Faubert
 *
 */
public class TreeDropListener extends ViewerDropAdapter
{

	private final Viewer fViewer;
	private Node fTarget;
	
	/**
	 * @param viewer
	 */
	public TreeDropListener(Viewer viewer)
	{
		super(viewer);
		this.fViewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	@Override
	public void drop(DropTargetEvent event)
	{
		Node target = (Node) determineTarget(event);
		Node root = (Node) fViewer.getInput();
		
		if(target != null)
		{
			fTarget = target;
		}
		else
		{
			fTarget = root;
		}
		
		super.drop(event);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
	 */
	@Override
	public boolean performDrop(Object data)
	{
		String name = ((String) data).split(":")[0];
		
		if(fTarget != null)
		{
			Node child = fTarget.getChild(name);
			if(child != null)
			{
				return false;
			}
			
			Node parent = fTarget;
			while(parent != null)
			{
				if(parent.getCodeName().equals(name))
				{
					return false;
				}
				parent = parent.getParent();
			}
			
			fTarget.addChild((String) data);
			Node root = (Node) fViewer.getInput();
			root.computeFreq();
			fViewer.refresh();
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData)
	 */
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType)
	{
		
		
		if(TextTransfer.getInstance().isSupportedType(transferType))
		{
			return true;
		}
		return false;
	}

}
