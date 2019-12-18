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
package ca.mcgill.cs.swevo.qualyzer.providers;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;


/**
 * Creates the data when dragging from the tree.
 */
public class TreeDragListener implements DragSourceListener
{
	/**
	 * 
	 */
	private static final String SPLIT = ":"; //$NON-NLS-1$
	private TreeViewer fViewer;
	
	/**
	 * 
	 * @param viewer
	 */
	public TreeDragListener(TreeViewer viewer)
	{
		fViewer = viewer;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Builds the drag data so that the drop listener knows it's coming from the tree and can build the node.
	 */
	@Override
	public void dragSetData(DragSourceEvent event)
	{
		if(TextTransfer.getInstance().isSupportedType(event.dataType))
		{
			IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
			Node node = (Node) selection.getFirstElement();
			
			String data = node.getPersistenceId() + SPLIT + node.getPathToRoot();
			event.data = data;
		}
	}

	/**
	 * Sets the selection transfer so that the validate drop method can work.
	 */
	@Override
	public void dragStart(DragSourceEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		Node node = (Node) selection.getFirstElement();
		String data = node.getPersistenceId() + SPLIT + node.getPathToRoot();
		
		LocalSelectionTransfer tran = LocalSelectionTransfer.getTransfer();
		tran.setSelection(new StructuredSelection(data));

	}

}
