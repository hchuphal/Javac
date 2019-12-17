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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.QualyzerWizardDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.AddInvestigatorPage;

/**
 * @author Jonathan Faubert
 *
 */
public class AddInvestigatorHandlerTest
{
	/**
	 * 
	 */
	private static final String PROJECT = "Project";
	/**
	 * 
	 */
	private static final String INSTITUTE = "mcgill";
	/**
	 * 
	 */
	private static final String FULL = "jonathan";
	/**
	 * 
	 */
	private static final String NAME = "jon";
	private Project fProject;
	private IProject wProject;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = TestUtil.createProject(PROJECT, "first", "other", "trans");
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testAddInvestigator()
	{
		assertTrue(wProject.exists());
		TestUtil.setProjectExplorerSelection(wProject);
		
		AddInvestigatorHandler handler = new AddInvestigatorHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester(){

			@Override
			public void execute(Dialog dialog)
			{
				IWizardPage wizardPage = ((WizardDialog) dialog).getCurrentPage();
				
				AddInvestigatorPage page = (AddInvestigatorPage) wizardPage;
				
				page.getNicknameText().setText(NAME);
				page.getFullNameText().setText(FULL);
				page.getInstitutionText().setText(INSTITUTE);
				
				((QualyzerWizardDialog) dialog).finishPressed();
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
		
		assertEquals(fProject.getInvestigators().size(), 2);
		Investigator test = fProject.getInvestigators().get(1);
		assertEquals(test.getNickName(), NAME);
		assertEquals(test.getFullName(), FULL);
		assertEquals(test.getInstitution(), INSTITUTE);
		
		InvestigatorEditorInput input = new InvestigatorEditorInput(test);
		IEditorPart editor = fPage.getActiveEditor();
		assertEquals(editor.getClass(), InvestigatorFormEditor.class);
		assertEquals(editor.getEditorInput().getName(), input.getName());
		
	}
}
