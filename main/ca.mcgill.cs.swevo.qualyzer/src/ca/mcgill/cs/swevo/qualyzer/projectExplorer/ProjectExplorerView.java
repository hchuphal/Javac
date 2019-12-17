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
package ca.mcgill.cs.swevo.qualyzer.projectExplorer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.navigator.extensions.NavigatorViewerDescriptor;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.INavigatorViewerDescriptor;


/**
 * Extends the CommonNavigator in order to hide some of its buttons.
 */
@SuppressWarnings("restriction")
public class ProjectExplorerView extends CommonNavigator
{
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.navigator.CommonNavigator#createCommonViewer(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected CommonViewer createCommonViewer(Composite aParent)
	{
		CommonViewer viewer = super.createCommonViewer(aParent);
		
		NavigatorViewerDescriptor desc = (NavigatorViewerDescriptor) 
			viewer.getNavigatorContentService().getViewerDescriptor();
		
		CommonViewerUtil.setProperty(desc, 
				INavigatorViewerDescriptor.PROP_HIDE_AVAILABLE_CUSTOMIZATIONS_DIALOG, "true"); //$NON-NLS-1$
		CommonViewerUtil.setProperty(desc, INavigatorViewerDescriptor.PROP_HIDE_LINK_WITH_EDITOR_ACTION,
				"true"); //$NON-NLS-1$
		
		return viewer;
	}
}
