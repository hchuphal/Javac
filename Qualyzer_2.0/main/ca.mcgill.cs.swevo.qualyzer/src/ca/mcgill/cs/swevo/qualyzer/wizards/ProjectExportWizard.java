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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;

import ca.mcgill.cs.swevo.qualyzer.wizards.pages.ProjectExportWizardPage;

/**
 * The Wizard the handles exporting projects.
 * This was copied from the Eclipse export wizard.
 *
 */
@SuppressWarnings("restriction")
public class ProjectExportWizard extends Wizard implements IExportWizard
{
	private IStructuredSelection fSelection;

	private ProjectExportWizardPage fMainPage;

	/**
	 * Creates a wizard for exporting workspace resources to a zip file.
	 */
	public ProjectExportWizard()
	{
		IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault().getDialogSettings();
		IDialogSettings section = workbenchSettings.getSection("ProjectExportWizard"); //$NON-NLS-1$
		if (section == null)
		{
			section = workbenchSettings.addNewSection("ProjectExportWizard"); //$NON-NLS-1$
		}
		setDialogSettings(section);
	}

	@Override
	public void addPages()
	{
		super.addPages();
		fMainPage = new ProjectExportWizardPage(fSelection);
		addPage(fMainPage);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		this.fSelection = currentSelection;
		List selectedResources = IDE.computeSelectedResources(currentSelection);
		if (!selectedResources.isEmpty())
		{
			this.fSelection = new StructuredSelection(selectedResources);
		}

		setWindowTitle(DataTransferMessages.DataTransfer_export);
		setDefaultPageImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor(
				"wizban/exportzip_wiz.png")); //$NON-NLS-1$
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish()
	{
		return fMainPage.finish();
	}
}
