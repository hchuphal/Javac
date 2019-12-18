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

import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.RenameMemoDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class RenameMemoHandlerTest
{
	private static final String MEMO = "Memo";
	private static final String PROJECT = "Project";
	private static final String INV = "investigator";
	private static final String NEW_NAME = "otherMemo";
	
	private Project fProject;
	private Memo fMemo;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp() //TODO make the memo file
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = TestUtil.createProject(PROJECT, INV, MEMO);
		fMemo = fProject.getMemos().get(0);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void renameMemoTest()
	{
		TestUtil.setProjectExplorerSelection(fMemo);
		
		RenameMemoHandler handler = new RenameMemoHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				RenameMemoDialog memoDialog = (RenameMemoDialog) dialog;
				
				memoDialog.getNameText().setText(NEW_NAME);
				
				memoDialog.okPressed();
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
		
		assertEquals(fProject.getMemos().size(), 1);
		fMemo = fProject.getMemos().get(0);
		
		assertEquals(fMemo.getName(), NEW_NAME);
		assertEquals(fMemo.getFileName(), NEW_NAME+".rtf");
		assertEquals(fMemo.getAuthor(), fProject.getInvestigators().get(0));
		
		Memo lMemo = Facade.getInstance().forceMemoLoad(fMemo);
		assertTrue(lMemo.getParticipants().isEmpty());
		
		String path = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName()).getLocation().toString();
		path += File.separator + "memos" + File.separator + fMemo.getFileName();
		
		File memoFile = new File(path);
		assertTrue(memoFile.exists());
		
	}
}
