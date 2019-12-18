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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.ProjectPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * @author Jonathan Faubert
 *
 */
public class ProjectPropertiesTest
{

	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String INV2 = "OtherInv";
	private Project fProject;
	private IProject wProject;
	
	
	@Before
	public void setUp()
	{
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		Facade.getInstance().createInvestigator(INV2, "", "", fProject, true);
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void projectPropertiesTest()
	{
		TestUtil.setProjectExplorerSelection(wProject);
		
		ProjectPropertiesHandler handler = new ProjectPropertiesHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				ProjectPropertiesDialog prop = (ProjectPropertiesDialog) dialog;
				
				prop.getInvestigatorCombo().select(1);
				
				prop.okPressed();
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
		
		String name = "";
		try
		{
			name = FileUtil.getProjectProperty(wProject, FileUtil.ACTIVE_INV);
		}
		catch (CoreException e)
		{
			fail();
		}
		
		assertEquals(name, INV2);
		
	}
}
