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
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;
import ca.mcgill.cs.swevo.qualyzer.wizards.ProjectImportWizard;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 *
 */
public class ImportProjectHandler extends AbstractHandler implements ITestableHandler
{

	private IDialogTester fTester = new NullTester();
	private boolean fTesting = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
			
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ProjectImportWizard wizard = new ProjectImportWizard();
		WizardDialog wDialog = new WizardDialog(shell, wizard);
		wDialog.create();
		wDialog.setMessage(MessagesClient.getString("wizards.ProjectImportWizard.title", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), IMessageProvider.INFORMATION);
		wDialog.setBlockOnOpen(!fTesting);
		wDialog.open();
		fTester.execute(wDialog);
			
		return null;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#getTester()
	 */
	@Override
	public IDialogTester getTester()
	{
		return fTester;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#isWindowsBlock()
	 */
	@Override
	public boolean isTesting()
	{
		return fTesting;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setTester(
	 * ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester)
	 */
	@Override
	public void setTester(IDialogTester tester)
	{
		fTester = tester;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.handlers.ITestableHandler#setWindowsBlock(boolean)
	 */
	@Override
	public void setTesting(boolean windowsBlock)
	{
		fTesting = windowsBlock;
	}
}
