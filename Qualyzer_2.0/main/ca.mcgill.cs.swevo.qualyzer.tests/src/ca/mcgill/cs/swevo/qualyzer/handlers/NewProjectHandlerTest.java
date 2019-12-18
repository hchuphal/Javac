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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.dialogs.QualyzerWizardDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewProjectPage;

/**
 * @author Jonathan Faubert
 *
 */
public class NewProjectHandlerTest
{
	/**
	 * 
	 */
	private static final String INSTITUTION = "institution";

	/**
	 * 
	 */
	private static final String FULL_NAME = "full name";

	/**
	 * 
	 */
	private static final String INVESTIGATOR = "Investigator";

	private static final String PROJECT = "Project";
	
	private IWorkbenchPage fPage;
	private Project fProject;
	private IProject wProject;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = null;
		wProject = null;
	}
	
	@After
	public void tearDown()
	{
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
		
		if(fProject != null && wProject.exists())
		{
			Facade.getInstance().deleteProject(fProject);
		}
	}
	
	@Test
	public void testNewProject()
	{
		NewProjectHandler handler = new NewProjectHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				QualyzerWizardDialog wizard = (QualyzerWizardDialog) dialog;
				
				NewProjectPage page = (NewProjectPage) wizard.getCurrentPage();
				
				page.getProjectNameText().setText(PROJECT);
				page.getNickNameText().setText(INVESTIGATOR);
				page.getFullNameText().setText(FULL_NAME);
				page.getInstitutionText().setText(INSTITUTION);
				
				wizard.finishPressed();
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
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
		
		assertNotNull(fProject);
		assertNotNull(wProject);
		assertTrue(wProject.exists());
		
		assertEquals(fProject.getName(), PROJECT);
		assertEquals(fProject.getFolderName(), PROJECT);
		assertEquals(fProject.getInvestigators().size(), 1);
		assertEquals(fProject.getCodes().size(), 0);
		assertEquals(fProject.getParticipants().size(), 0);
		assertEquals(fProject.getTranscripts().size(), 0);
		assertEquals(fProject.getMemos().size(), 0);
		
		Investigator inves = fProject.getInvestigators().get(0);
		
		assertEquals(inves.getNickName(), INVESTIGATOR);
		assertEquals(inves.getFullName(), FULL_NAME);
		assertEquals(inves.getInstitution(), INSTITUTION);
		
		assertEquals(fPage.getEditorReferences().length, 0);
		
	}
	
}
