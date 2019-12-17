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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.internal.wizards.datatransfer.ArchiveFileExportOperation;
import org.eclipse.ui.internal.wizards.datatransfer.WizardArchiveFileResourceExportPage1;

/**
 * Extends the WizardArchiveFileResourceExportPage1 in order to force a refresh of all
 *  the resources before trying to access them.
 */
@SuppressWarnings("restriction")
public class ProjectExportWizardPage extends WizardArchiveFileResourceExportPage1
{

	/**
	 * @param selection
	 */
	public ProjectExportWizardPage(IStructuredSelection selection)
	{
		super(selection);
		setMessage(MessagesClient.getString("wizards.pages.ProjectExportWizardPage.title", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"), IMessageProvider.INFORMATION);
		//Save the dirty editors here. No longer done later, since it doesn't work there for some reason.
		saveDirtyEditors();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.wizards.datatransfer.WizardArchiveFileResourceExportPage1#finish()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean finish()
	{
		List resourcesToExport = getWhiteCheckedResources();
		
		try
		{
			for(Object obj : resourcesToExport)
			{
				if(obj instanceof IResource)
				{
					((IResource) obj).refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
			}
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}
		
        if (!ensureTargetIsValid()) 
        {
			return false;
		}

        // about to invoke the operation so save our state
        saveWidgetValues();

        return executeExportOperation(new ArchiveFileExportOperation(null,
                resourcesToExport, getDestinationValue()));
	}

}
