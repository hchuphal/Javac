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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;

/**
 * Creates the Drag data so that the drop listener knows the data comes from the table.
 */
public class TableDragListener implements DragSourceListener
{

	/**
	 * 
	 */
	private static final String SPLIT = ":"; //$NON-NLS-1$
	private final TableViewer fViewer;
	
	/**
	 * 
	 * @param viewer
	 */
	public TableDragListener(TableViewer viewer)
	{
		fViewer = viewer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragFinished(DragSourceEvent event)
	{
	}

	/**
	 * Build the drag data.
	 */
	@Override
	public void dragSetData(DragSourceEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		CodeTableRow row = (CodeTableRow) selection.getFirstElement();
		
		if(TextTransfer.getInstance().isSupportedType(event.dataType))
		{
			event.data = row.getName() + SPLIT + row.getPersistenceId() + SPLIT + row.getFrequency();
		}
	}

	/**
	 * Defines the SelectionTransfer so that ValidateDrop can work.
	 */
	@Override
	public void dragStart(DragSourceEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		CodeTableRow row = (CodeTableRow) selection.getFirstElement();
		String data = row.getName() + SPLIT + row.getPersistenceId() + SPLIT + row.getFrequency();
		
		LocalSelectionTransfer tran = LocalSelectionTransfer.getTransfer();
		tran.setSelection(new StructuredSelection(data));
	}

}
