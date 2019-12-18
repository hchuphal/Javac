/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Name - Initial Contribution
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.editors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.TestUtil;
import ca.mcgill.cs.swevo.qualyzer.dialogs.CodeChooserDialog;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
public class MarkTextActionTest
{

	public final static String PROJECT_NAME = "p1";
	
	private Project fProject;
	private Transcript fTranscript;
	private IWorkbenchPage fPage;
	
	@Before
	public void setUp()
	{
		fProject = TestUtil.createProject(PROJECT_NAME, "i1", "p1", "t1");
		fTranscript = fProject.getTranscripts().get(0);
		fPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
	}
	
	@After
	public void tearDown()
	{
		Facade facade = Facade.getInstance();
		facade.deleteProject(fProject);
	}
	
	/**
	 * 
	 * Does something.
	 *
	 */
	@Test
	public void testNewCode()
	{
		RTFEditor rtfEditor = (RTFEditor) ResourcesUtil.openEditor(fPage, fTranscript);
		assertNotNull(rtfEditor);
		MarkTextAction action = (MarkTextAction)rtfEditor.getMarkTextAction();
		action.setWindowsBlock(false);
		action.setTester(new IDialogTester()
		{
			
			@Override
			public void execute(Dialog dialog)
			{
				CodeChooserDialog cDialog = (CodeChooserDialog)dialog;
				cDialog.getCodeName().setText("c1");
				cDialog.okPressed();
			}
		});
		
		rtfEditor.getSelectionProvider().setSelection(new TextSelection(10, 5));
		action.run();
		rtfEditor.doSave(new NullProgressMonitor());
		
		Project project = PersistenceManager.getInstance().getProject(PROJECT_NAME);
		assertTrue(TestUtil.codeExists(project, "c1"));
		
		rtfEditor.close(true);
	}
}
