/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     McGill University - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameProjectDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class RenameProjectHandlerTest
{
	
	private static final String OTHER_PROJECT = "otherProject";
	private static final String PROJECT_NAME = "project";
	
	private Project fProject;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(true);
		fProject = TestUtil.createProject(PROJECT_NAME, "inves", "p1", "t1");
	}
	
	@After
	public void tearDown()
	{
		fProject = PersistenceManager.getInstance().getProject(OTHER_PROJECT);
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testBasicRename()
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(fProject.getFolderName());
		assertTrue(project.exists());
		
		TestUtil.setProjectExplorerSelection(project);
				
		RenameProjectHandler handler = new RenameProjectHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester(){

			@Override
			public void execute(Dialog dialog)
			{
				RenameProjectDialog rename = (RenameProjectDialog) dialog;
				rename.getNameField().setText(OTHER_PROJECT);
				rename.okPressed();
			}
			
		});
		
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		project = root.getProject(PROJECT_NAME);
		assertFalse(project.exists());
		
		project = root.getProject(OTHER_PROJECT);
		assertTrue(project.exists());
		
		fProject = PersistenceManager.getInstance().getProject(OTHER_PROJECT);
		
		assertEquals(fProject.getInvestigators().size(), 1);
		assertEquals(fProject.getParticipants().size(), 1);
		assertEquals(fProject.getTranscripts().size(), 1);
		assertEquals(fPage.getEditorReferences().length, 0);
		
	}

	
	
}
