/**
 * 
 */
package treeviewertest.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import treeviewertest.model.Node;

/**
 * @author Jonathan Faubert
 *
 */
public class TreeModelContentProvider implements ITreeContentProvider
{

	private TreeViewer fViewer;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement)
	{
		Node node = (Node) parentElement;
		
		if(node.getChildren().isEmpty())
		{
			return new Object[0];
		}
		else
		{
			return node.getChildren().values().toArray();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element)
	{
		Node node = (Node) element;
		
		return node.getParent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element)
	{
		Node node = (Node) element;
		
		return !node.getChildren().isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement)
	{
		return ((Node) inputElement).getChildren().values().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		fViewer = (TreeViewer) viewer;
		
//	    if(oldInput != null) {
//	        removeListenerFrom((Node)oldInput);
//	    }
//	    if(newInput != null) {
//	       addListenerTo((Node)newInput);


	}

}
