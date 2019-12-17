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
import static org.junit.Assert.assertNull;
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
import ca.mcgill.cs.swevo.qualyzer.editors.TranscriptEditor;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.TranscriptWizardPage;

/**
 * @author Jonathan Faubert
 *
 */
public class NewTranscriptHandlerTest
{
	private static final String PROJECT = "Project";
	private static final String INV = "Investigator";
	private static final String PART = "Participant";
	private static final String TRANSCRIPT = "Transcript";
	private Project fProject;
	private IProject wProject;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		Facade.getInstance().createParticipant(PART, "", fProject);
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void NewTranscriptTest()
	{
		TestUtil.setProjectExplorerSelection(wProject);
		
		NewTranscriptHandler handler = new NewTranscriptHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				QualyzerWizardDialog wizard = (QualyzerWizardDialog) dialog;
				
				TranscriptWizardPage page = (TranscriptWizardPage) wizard.getCurrentPage();
				
				page.getNameText().setText(TRANSCRIPT);
				page.getTable().select(0);
				
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
		
		assertEquals(fProject.getTranscripts().size(), 1);
		
		Transcript transcript = fProject.getTranscripts().get(0);
		
		assertEquals(transcript.getName(), TRANSCRIPT);
		assertNull(transcript.getAudioFile());
		assertEquals(transcript.getFileName(), TRANSCRIPT + ".rtf");
		
		Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
		assertEquals(lTranscript.getParticipants().size(), 1);
		assertEquals(lTranscript.getParticipants().get(0), fProject.getParticipants().get(0));
		
		assertEquals(fPage.getEditorReferences().length, 1);
		
		IEditorPart editor = fPage.getActiveEditor();
		assertEquals(editor.getClass(), TranscriptEditor.class);
		assertEquals(((TranscriptEditor) editor).getDocument(), transcript);
	}
}
