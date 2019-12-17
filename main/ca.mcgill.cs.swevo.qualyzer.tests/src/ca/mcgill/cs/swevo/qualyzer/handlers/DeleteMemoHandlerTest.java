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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.MemoDeleteDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class DeleteMemoHandlerTest
{
	
	private static final String PROJECT = "Project";
	private static final String MEMO = "MemoName";
	private static final String INV = "Inv";
	
	private Project fProject;
	private Memo fMemo;

	@Before
	public void setUp()
	{
		fProject = TestUtil.createProject(PROJECT, INV, MEMO);
		fMemo = fProject.getMemos().get(0);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void testDeleteMemo()
	{
		TestUtil.setProjectExplorerSelection(fMemo);
		
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT).getFolder("memos");
		
		DeleteMemoHandler handler = new DeleteMemoHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				MemoDeleteDialog delete = (MemoDeleteDialog) dialog;
				
				delete.getCheckBox().setSelection(false);
				
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
		assertNotNull(fProject);
		assertTrue(fProject.getMemos().isEmpty());
		
		assertEquals(fProject.getInvestigators().size(), 1);
		assertFalse(folder.getFile(MEMO + ".rtf").exists());
		
	}
}
