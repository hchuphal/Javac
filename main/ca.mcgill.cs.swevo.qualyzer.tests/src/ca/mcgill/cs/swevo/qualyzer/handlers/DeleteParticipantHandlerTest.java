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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class DeleteParticipantHandlerTest
{
	
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String PART_ID = "Participant";
	private Project fProject;
	private Participant fParticipant;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		fParticipant = Facade.getInstance().createParticipant(PART_ID, "", fProject);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void deleteParticipantTest()
	{
		TestUtil.setProjectExplorerSelection(fParticipant);
		
		assertTrue(!fProject.getParticipants().isEmpty());
		
		DeleteParticipantHandler handler = new DeleteParticipantHandler();
		handler.setTesting(true);
		
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		assertTrue(fProject.getParticipants().isEmpty());
	}
}
