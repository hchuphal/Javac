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
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.QualyzerWizardDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.AddParticipantPage;

/**
 * @author Jonathan Faubert
 *
 */
public class AddParticipantHandlerTest
{
	private static String PROJECT = "Project";
	private static String INV = "Inv";
	
	private static String P_ID = "Part";
	private static String FULL = "Name here";
	
	private Project fProject;
	private IProject wProject;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testAddPaticipant()
	{
		TestUtil.setProjectExplorerSelection(wProject);
		
		AddParticipantHandler handler = new AddParticipantHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				QualyzerWizardDialog wizard = (QualyzerWizardDialog) dialog;
				
				AddParticipantPage page = (AddParticipantPage) wizard.getCurrentPage();
				
				page.getIDText().setText(P_ID);
				page.getFullnameText().setText(FULL);
				
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
		
		assertEquals(fProject.getParticipants().size(), 1);
		Participant part = fProject.getParticipants().get(0);
		assertEquals(part.getParticipantId(), P_ID);
		assertEquals(part.getFullName(), FULL);
		
		assertEquals(fPage.getEditorReferences().length, 1);
		
		IEditorPart editor = fPage.getActiveEditor();
		assertEquals(editor.getClass(), ParticipantFormEditor.class);
		
		ParticipantEditorInput input = new ParticipantEditorInput(part);
		assertEquals(editor.getEditorInput().getName(), input.getName());
	}
	
	

}
