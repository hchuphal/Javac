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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class DeleteProjectHandlerTest
{

	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private Project fProject;
	private IProject wProject;
	
	@Before
	public void setUp()
	{
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
		
	}
	
	@After
	public void tearDown()
	{
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		if(fProject != null)
		{
			Facade.getInstance().deleteProject(fProject);
		}
	}
	
	@Test
	public void testDeleteProject()
	{
		TestUtil.setProjectExplorerSelection(wProject);
		
		DeleteProjectHandler handler = new DeleteProjectHandler();
		handler.setTesting(true);
		
		try
		{
			handler.execute(null);
		}
		catch (ExecutionException e)
		{
			fail();
		}
		
		assertTrue(!wProject.exists());
		assertNull(PersistenceManager.getInstance().getProject(PROJECT));
	}
}
