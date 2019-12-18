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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.editors.CodeEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.TranscriptEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.providers.WrapperCode;

/**
 * @author Jonathan Faubert
 *
 */
public class OpenAllHandlerTest
{

	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String PART = "Particip";
	private static final String TRANSCRIPT = "Transcript";
	
	private Project fProject;
	private IWorkbenchPage fPage;
	private Investigator fInves;
	private Participant fPartic;
	private Transcript fTranscript;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		
		fProject = TestUtil.createProject(PROJECT, INV, PART, TRANSCRIPT);
		fInves = fProject.getInvestigators().get(0);
		fPartic = fProject.getParticipants().get(0);
		fTranscript = fProject.getTranscripts().get(0);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testOpenInvestigator()
	{
		TestUtil.setProjectExplorerSelection(fInves);
		
		OpenAllHandler handler = new OpenAllHandler();
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		assertEquals(fPage.getEditorReferences().length, 1);
		IEditorPart editor = fPage.getActiveEditor();
		assertTrue(editor instanceof InvestigatorFormEditor);
		
		InvestigatorEditorInput input = new InvestigatorEditorInput(fInves);
		
		assertEquals(editor.getEditorInput().getName(), input.getName());
	}
	
	@Test
	public void testOpenParticipant()
	{
		TestUtil.setProjectExplorerSelection(fPartic);
		
		OpenAllHandler handler = new OpenAllHandler();
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		assertEquals(fPage.getEditorReferences().length, 1);
		
		IEditorPart editor = fPage.getActiveEditor();
		assertTrue(editor instanceof ParticipantFormEditor);
		
		ParticipantEditorInput input = new ParticipantEditorInput(fPartic);
		assertEquals(editor.getEditorInput().getName(), input.getName());
	}
	
	@Test
	public void testOpenTranscript()
	{
		TestUtil.setProjectExplorerSelection(fTranscript);
		
		OpenAllHandler handler = new OpenAllHandler();
		
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		assertEquals(fPage.getEditorReferences().length, 1);
		
		IEditorPart editor = fPage.getActiveEditor();
		
		assertTrue(editor instanceof TranscriptEditor);
		
	}
	
	@Test
	public void testOpenMemo()
	{
		
	}
	
	@Test
	public void testOpenCodes()
	{
		WrapperCode codeNode = new WrapperCode(fProject);
		TestUtil.setProjectExplorerSelection(codeNode);
		
		OpenAllHandler handler = new OpenAllHandler();
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		assertEquals(fPage.getEditorReferences().length, 1);
		
		IEditorPart editor = fPage.getActiveEditor();
		
		assertTrue(editor instanceof CodeEditor);
		assertEquals(editor.getEditorInput().getName(), new CodeEditorInput(fProject).getName());
	}
	
	@Test
	public void testOpenAll()
	{
		Object[] selection = new Object[]{fInves, fPartic, fTranscript, new WrapperCode(fProject)};
		TestUtil.setProjectExplorerSelection(selection);
		
		OpenAllHandler handler = new OpenAllHandler();
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		assertEquals(fPage.getEditorReferences().length, 4);
		
		boolean iFound = false, pFound = false, cFound = false, tFound = false;
		int count = 0;
		
		for(IEditorReference ref : fPage.getEditorReferences())
		{
			IEditorPart editor = ref.getEditor(true);
			boolean found = false;
			
			if(editor instanceof InvestigatorFormEditor)
			{
				found = true;
				iFound = true;
				count++;
			}
			else if(editor instanceof ParticipantFormEditor)
			{
				found = true;
				pFound = true;
				count++;
			}
			else if(editor instanceof TranscriptEditor)
			{
				found = true;
				tFound = true;
				count++;
			}
			else if(editor instanceof CodeEditor)
			{
				found = true;
				cFound = true;
				count++;
			}
			
			assertTrue(found);
		}
		
		assertTrue(iFound && pFound && cFound && tFound);
		assertTrue(count == 4);
	}
}
