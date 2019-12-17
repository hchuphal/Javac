/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.providers;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

import ca.mcgill.cs.swevo.qualyzer.editors.pages.CodeEditorPage;

/**
 * Handles dropping of codes onto the treeViewer.
 */
public class TreeDropListener extends ViewerDropAdapter
{

	private static final String SPLIT = ":"; //$NON-NLS-1$
	private static final int TREE_DATA_SIZE = 2;
	private static final int TABLE_DATA_SIZE = 3;
	
	private Viewer fViewer;
	private CodeEditorPage fPage;
	private Node fTarget;
	private boolean fCopy;

	/**
	 * @param viewer
	 * @param codeEditorPage 
	 */
	public TreeDropListener(Viewer viewer, CodeEditorPage codeEditorPage)
	{
		super(viewer);
		fViewer = viewer;
		fPage = codeEditorPage;
	}

	/**
	 * Checks to see if we are copying and finds the target of the drop. If it is null, makes it the root.
	 */
	@Override
	public void drop(DropTargetEvent event)
	{
		fCopy = event.detail == DND.DROP_COPY;
		
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
	
	/**
	 * Performs the drop based on the data. The length of the data determines where the data is coming from 
	 * (the tree or the table). Parses the data and then creates, copies or moves any necessary nodes.
	 */
	@Override
	public boolean performDrop(Object data)
	{
		String info = (String) data;
		String[] values = info.split(SPLIT);
		
		if(fTarget != null)
		{
			if(values.length == TREE_DATA_SIZE)
			{
				Long id = Long.parseLong(values[0]);
				Node toMove = findNode(values[1], id);
				
				if(fCopy)
				{
					toMove = toMove.copy();
				}
				else
				{
					Node oldParent = toMove.getParent();
					oldParent.getChildren().remove(toMove.getPersistenceId());
				}
				
				toMove.setParent(fTarget);
				toMove.updatePaths();
				
				refreshEditor();
				fViewer.setSelection(new StructuredSelection(toMove), true);
				return true;
			}
			else if(values.length == TABLE_DATA_SIZE)
			{
				long childID = Long.parseLong(((String) data).split(":")[1]); //$NON-NLS-1$
				fTarget.addChild((String) data);
				Node child = fTarget.getChild(childID); 
				refreshEditor();
				fViewer.setSelection(new StructuredSelection(child), true);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Updates the frequencies and then tells the page to update the table and tree.
	 */
	private void refreshEditor()
	{
		Node root = (Node) fViewer.getInput();
		root.computeFreq();
		fPage.refresh();
		fPage.setDirty();
	}

	/**
	 * Verifies that the drop is allowed by checking for cycles and duplicates.
	 */
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType)
	{
		LocalSelectionTransfer sel = LocalSelectionTransfer.getTransfer();
		IStructuredSelection selection = (IStructuredSelection) sel.getSelection();
		
		Node nTarget = (Node) target;
		if(nTarget == null)
		{
			nTarget = (Node) fViewer.getInput();
		}
		
		String data = (String) selection.getFirstElement();
		String[] values = data.split(SPLIT);
		Long id = null;
		
		boolean valid = true;
		
		if(nTarget != null)
		{
			if(values.length == TABLE_DATA_SIZE)
			{
				id = Long.parseLong(values[1]);
				
				//if(hardCycleExists(nTarget, id, fPage.getTreeModel()))
				if(containsCycle(id, nTarget))
				{
					valid = false;
				}
			}
			else if(values.length == TREE_DATA_SIZE)
			{
				id = Long.parseLong(values[0]);
				
				Node node = findNode(values[1], id);
				
				if(depthFirstContainsCycle(node, nTarget))
				{
					valid = false;
				}
			}
			
			Node child = nTarget.getChild(id);
			if(child != null)
			{
				valid = false;
			}
			
			return valid && TextTransfer.getInstance().isSupportedType(transferType);
		}
		return false;
	}

	/**
	 * Get the node specified by the path and the id.
	 * @param path
	 * @param id
	 * @return
	 */
	private Node findNode(String path, Long id)
	{
		Node node = (Node) fViewer.getInput();
		for(String next : path.split("/")) //$NON-NLS-1$
		{
			node = node.getChild(Long.parseLong(next));
		}
		node = node.getChild(id);
		return node;
	}

	/**
	 * Checks a node and all of its children for the existence of a cycle with the target node.
	 * @param id
	 * @param nTarget
	 * @param string
	 * @return
	 */
	private boolean depthFirstContainsCycle(Node node, Node nTarget)
	{
		boolean hasCycle = false;
		
		hasCycle = hasCycle || containsCycle(node.getPersistenceId(), nTarget);
		
		for(Node child : node.getChildren().values())
		{
			hasCycle = hasCycle || depthFirstContainsCycle(child, nTarget);
		}
		
		return hasCycle;
	}

	/**
	 * Checks if the given node forms a cycle with the target node.
	 * @param id
	 * @param nTarget
	 */
	private boolean containsCycle(Long id, Node nTarget)
	{
		Node parent = nTarget;
		while(parent != null)
		{
			if(parent.getPersistenceId().equals(id))
			{
				return true;
			}
			parent = parent.getParent();
		}
		
		return false;
	}

}
