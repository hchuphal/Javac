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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.dialogs.ProjectPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * Displays the project properties, including the location and active investigator. 
 * Saves any changes made to the active investigator.
 *
 */
public class ProjectPropertiesHandler extends AbstractHandler implements ITestableHandler
{
	private static Logger gLogger = LoggerFactory.getLogger(ProjectPropertiesHandler.class);
	
	private boolean fTesting = false;
	private IDialogTester fTester = new NullTester();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		CommonNavigator view = (CommonNavigator) page.findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		ISelection selection = view.getCommonViewer().getSelection();
		
		if(selection != null && selection instanceof IStructuredSelection)
		{
			IStructuredSelection struct = (IStructuredSelection) selection;
			Object element = struct.getFirstElement();
			if(element instanceof IProject)
			{
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				Project project = ResourcesUtil.getProject(element);
				ProjectPropertiesDialog dialog = new ProjectPropertiesDialog(shell, project);
				dialog.setBlockOnOpen(!fTesting);
				dialog.open();
				fTester.execute(dialog);
				try
				{
					if(dialog.getReturnCode() == Window.OK)
					{
						IProject wProject = (IProject) element;
						try
						{
							FileUtil.setProjectProperty(wProject, FileUtil.ACTIVE_INV, dialog.getInvestigator());
							
							Facade facade = Facade.getInstance();
							facade.getListenerManager().notifyProjectListeners(ChangeType.MODIFY, project, facade);
						}
						catch (CoreException e)
						{
							gLogger.error("Unable to set Active Investigator", e); //$NON-NLS-1$
							MessageDialog.openError(shell, MessagesClient.getString(
									"handlers.ProjectPropertiesHandler.fileAccessError", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), //$NON-NLS-1$
									MessagesClient.getString("handlers.ProjectPropertiesHandler.errorMessage", "ca.mcgill.cs.swevo.qualyzer.handlers.messages")); //$NON-NLS-1$
						}
					}
				}
				catch(QualyzerException e)
				{
					MessageDialog.openError(shell, MessagesClient.getString(
							"handlers.ProjectPropertiesHandler.fileAccessError", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), e.getMessage()); //$NON-NLS-1$
				}
			}
		}
		
		return null;
	}
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#getTester()
	 */
	@Override
	public IDialogTester getTester()
	{
		return fTester;
	}
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#isWindowsBlock()
	 */
	@Override
	public boolean isTesting()
	{
		return fTesting;
	}
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setTester(
	 * ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester)
	 */
	@Override
	public void setTester(IDialogTester tester)
	{
		fTester = tester;
	}
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setWindowsBlock(boolean)
	 */
	@Override
	public void setTesting(boolean windowsBlock)
	{
		fTesting = windowsBlock;
	}

}
