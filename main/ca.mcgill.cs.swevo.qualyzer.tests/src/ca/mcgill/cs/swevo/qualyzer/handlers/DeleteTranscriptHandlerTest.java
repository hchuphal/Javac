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

import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.TranscriptDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * @author Jonathan Faubert
 *
 */
public class DeleteTranscriptHandlerTest
{
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String PART = "Partic";
	private static final String TRANSCRIPT = "TranscriptName";
	
	private Project fProject;
	private Transcript fTranscript;
	
	
	@Before
	public void setUp()
	{
		fProject = TestUtil.createProject(PROJECT, INV, PART, TRANSCRIPT);
		fTranscript = fProject.getTranscripts().get(0);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testDeleteTranscript()
	{
		TestUtil.setProjectExplorerSelection(fTranscript);
		
		DeleteTranscriptHandler handler = new DeleteTranscriptHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				TranscriptDeleteDialog delete = (TranscriptDeleteDialog) dialog;
				
				
				
				delete.okPressed();
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
		
		assertTrue(fProject.getTranscripts().isEmpty());
		assertEquals(fProject.getInvestigators().size(), 1);
		assertEquals(fProject.getParticipants().size(), 1);
		
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT).getFolder("transcripts");
		File file = new File(folder.getLocation() + File.separator + fTranscript.getFileName());
		assertFalse(file.exists());
		
	}
}
