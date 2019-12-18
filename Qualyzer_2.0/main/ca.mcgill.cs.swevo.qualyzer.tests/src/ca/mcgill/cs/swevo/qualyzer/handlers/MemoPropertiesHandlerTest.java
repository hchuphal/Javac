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
import org.eclipse.jface.dialogs.Dialog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.MemoPropertiesDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class MemoPropertiesHandlerTest
{
	private static final String PROJECT = "Project";
	private static final String INV = "inv";
	private static final String MEMO = "memo";
	private static final String INV2 = "inv2";
	private Project fProject;
	private Memo fMemo;

	@Before
	public void setUp()
	{
		fProject = TestUtil.createProject(PROJECT, INV, MEMO);
		fMemo = fProject.getMemos().get(0);
		Facade.getInstance().createInvestigator(INV2, "", "", fProject, true);
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void memoPropertiesTest()
	{
		TestUtil.setProjectExplorerSelection(fMemo);
		
		MemoPropertiesHandler handler = new MemoPropertiesHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				MemoPropertiesDialog prop = (MemoPropertiesDialog) dialog;
				
				prop.getAuthorCombo().select(1);
				
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
		
		fProject = PersistenceManager.getInstance().getProject(PROJECT);
		fMemo = fProject.getMemos().get(0);
		
		assertEquals(fMemo.getAuthor(), fProject.getInvestigators().get(1));
	}
}
