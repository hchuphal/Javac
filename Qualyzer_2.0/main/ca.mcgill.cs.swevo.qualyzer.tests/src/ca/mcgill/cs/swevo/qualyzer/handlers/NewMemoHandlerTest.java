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
import ca.mcgill.cs.swevo.qualyzer.editors.MemoEditor;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewMemoPage;

/**
 * @author Jonathan Faubert
 *
 */
public class NewMemoHandlerTest
{
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	private static final String MEMO_NAME = "memoHere";
	
	private Project fProject;
	private IProject wProject;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		fPage.closeAllEditors(false);
		fProject = Facade.getInstance().createProject(PROJECT, INV, "", "");
		wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT);
	}
	
	@After
	public void tearDown()
	{
		Facade.getInstance().deleteProject(fProject);
	}
	
	@Test
	public void newMemoTest()
	{
		assertTrue(wProject.exists());
		TestUtil.setProjectExplorerSelection(wProject);
		
		NewMemoHandler handler = new NewMemoHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				QualyzerWizardDialog wizard = (QualyzerWizardDialog) dialog;
				
				NewMemoPage page = (NewMemoPage) wizard.getCurrentPage();
				
				page.getNameText().setText(MEMO_NAME);
				
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
		assertEquals(fProject.getMemos().size(), 1);
		
		Memo memo = fProject.getMemos().get(0);
		assertEquals(memo.getName(), MEMO_NAME);
		assertEquals(memo.getAuthor(), fProject.getInvestigators().get(0));
		assertEquals(memo.getCode(), null);
		assertEquals(memo.getTranscript(), null);
		
		assertEquals(fPage.getEditorReferences().length, 1);
		IEditorPart editor = fPage.getActiveEditor();
		
		assertEquals(editor.getClass(), MemoEditor.class);
		assertEquals(memo, ((MemoEditor) editor).getDocument());
		
		Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
		assertEquals(lMemo.getParticipants().size(), 0);
	}
}
