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
import ca.mcgill.cs.swevo.qualyzer.dialogs.NewCodeDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.CodeEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.CodeEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;

/**
 * @author Jonathan Faubert
 *
 */
public class NewCodeHandlerTest
{
	/**
	 * 
	 */
	private static final String CODE_DESCRIPTION = "Code Description";
	/**
	 * 
	 */
	private static final String CODE_NAME = "Code name";
	private static final String PROJECT = "Project";
	private static final String INV = "Inv";
	
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
	public void testNewCode()
	{
		assertTrue(wProject.exists());
		TestUtil.setProjectExplorerSelection(wProject);
		
		NewCodeHandler handler = new NewCodeHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				NewCodeDialog newCode = (NewCodeDialog) dialog;
				
				newCode.getNameText().setText(CODE_NAME);
				newCode.getDescriptionText().setText(CODE_DESCRIPTION);
				
				newCode.okPressed();
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
		assertEquals(fProject.getCodes().size(), 1);
		Code code = fProject.getCodes().get(0);
		
		assertEquals(code.getCodeName(), CODE_NAME);
		assertEquals(code.getDescription(), CODE_DESCRIPTION);
		
		assertEquals(fPage.getEditorReferences().length, 1);
		
		IEditorPart editor = fPage.getActiveEditor();
		
		assertEquals(editor.getClass(), CodeEditor.class);
		CodeEditorInput input = new CodeEditorInput(code.getProject());
		assertEquals(editor.getEditorInput().getName(), input.getName());
		
		
	}
}
