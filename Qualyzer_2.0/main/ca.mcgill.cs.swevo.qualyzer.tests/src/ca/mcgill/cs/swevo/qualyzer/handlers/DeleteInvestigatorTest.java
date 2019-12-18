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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class DeleteInvestigatorTest
{
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String INV_ID = "OtherInvestigator";
	private Project fProject;
	private Investigator fInvestigator;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		fInvestigator = Facade.getInstance().createInvestigator(INV_ID, "", "", fProject, true);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void deleteParticipantTest()
	{
		TestUtil.setProjectExplorerSelection(fInvestigator);
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		assertEquals(fProject.getInvestigators().size(), 2);
		
		DeleteInvestigatorHandler handler = new DeleteInvestigatorHandler();
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
		assertEquals(fProject.getInvestigators().size(), 1);
		
		Investigator investigator = fProject.getInvestigators().get(0);
		assertEquals(investigator.getNickName(), INV);
	}
}
