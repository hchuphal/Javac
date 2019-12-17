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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeTableInput.CodeTableRow;

/**
 * Defines the filter on the table which filters out all the elements that are in the tree.
 */
public class TableFilter extends ViewerFilter
{
	private TreeModel fTree;
	
	/**
	 * Constructor.
	 * @param tree
	 */
	public TableFilter(TreeModel tree)
	{
		fTree = tree;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(
	 * org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		CodeTableRow row = (CodeTableRow) element;
		
		return !fTree.isInHierarchy(row.getCode());
	}

}
