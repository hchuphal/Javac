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

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * The wizard for importing a project into the workspace. Mostly copied from the Eclipse import wizard.
 * Slight changes made to performFinish() to deal with recreating folders and rolling back non-Qualyzer 
 * projects that get imported.
 *
 */
@SuppressWarnings("restriction")
public class ProjectImportWizard extends Wizard implements IImportWizard
{
	
	private static final String IMPORT_ERROR = MessagesClient.getString(
			"wizards.ProjectImportWizard.importError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient"); //$NON-NLS-1$
	private static final String EXTERNAL_PROJECT_SECTION = "ProjectImportWizard"; //$NON-NLS-1$
	private WizardProjectsImportPage fMainPage;
	private IStructuredSelection fCurrentSelection = null;
	private String fInitialPath = null;

	/**
	 * Constructor for TestImportWizard.
	 */
	public ProjectImportWizard()
	{
		this(null);
	}

	/**
	 * Constructor for ProjectImportWizard.
	 * 
	 * @param initialPath
	 *            Default path for wizard to import
	 */
	public ProjectImportWizard(String initialPath)
	{
		super();
		this.fInitialPath = initialPath;
		setNeedsProgressMonitor(true);
		IDialogSettings workbenchSettings = IDEWorkbenchPlugin.getDefault().getDialogSettings();

		IDialogSettings wizardSettings = workbenchSettings.getSection(EXTERNAL_PROJECT_SECTION);
		if (wizardSettings == null)
		{
			wizardSettings = workbenchSettings.addNewSection(EXTERNAL_PROJECT_SECTION);
		}
		setDialogSettings(wizardSettings);
	}

	@Override
	public void addPages()
	{
		super.addPages();
		fMainPage = new WizardProjectsImportPage(
				"wizardExternalProjectsPage", fInitialPath, fCurrentSelection); //$NON-NLS-1$
//		fMainPage.setMessage(MessagesClient.getString("wizards.ProjectImportWizard.title"), IMessageProvider.INFORMATION);
		addPage(fMainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection)
	{
		setWindowTitle(MessagesClient.getString("wizards.ProjectImportWizard.title", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient"));
		setDefaultPageImageDescriptor(IDEWorkbenchPlugin.getIDEImageDescriptor(
				"wizban/importproj_wiz.png")); //$NON-NLS-1$
		this.fCurrentSelection = currentSelection;
	}

	@Override
	public boolean performCancel()
	{
		fMainPage.performCancel();
		return true;
	}

	@Override
	public boolean performFinish()
	{
		boolean toReturn =  fMainPage.createProjects();
		ArrayList<IProject> toDelete = new ArrayList<IProject>();
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			/*For each project that was imported:
			 * Verify that it is a Qualyzer Project,
			 * Recreate it's sub-folders.
			 * If it was not then show an error message and delete it from the workspace.
			 */
			PersistenceManager.getInstance().refreshManager(project);
			Project qProject = PersistenceManager.getInstance().getProject(project.getName());
			if(qProject != null)
			{
				try
				{
					FileUtil.refreshSubFolders(project);
					FileUtil.renewTimestamps(project);
				}
				catch(QualyzerException e)
				{
					MessageDialog.openError(getShell(),	IMPORT_ERROR, e.getMessage()); 
				}
			}
			else
			{
				MessageDialog.openError(getShell(), IMPORT_ERROR, MessagesClient.getString(
						"wizards.ProjectImportWizard.couldNotImport", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient") + project.getName() + //$NON-NLS-1$
						MessagesClient.getString("wizards.ProjectImportWizard.invalidProject", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient")); //$NON-NLS-1$
				toDelete.add(project);
			}
		}
		for(IProject wProject : toDelete)
		{
			try
			{
				wProject.delete(true, new NullProgressMonitor());
			}
			catch(CoreException e)
			{
				MessageDialog.openError(getShell(), IMPORT_ERROR, 
						MessagesClient.getString("wizards.ProjectImportWizard.deleteFailed", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient")); //$NON-NLS-1$
			}
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		view.getCommonViewer().refresh();
		return toReturn;
	}

}
