/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthélémy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.dialogs.ReportIssueDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;
import ca.mcgill.cs.swevo.qualyzer.editors.NullTester;

/**
 * Displays a dialog with a text box and a button to copy the error log to the clipboard.
 *
 */
public class ReportIssueHandler extends AbstractHandler implements ITestableHandler
{
	private static Logger gLogger = LoggerFactory.getLogger(ReportIssueHandler.class);

	private boolean fTesting = false;
	private IDialogTester fTester = new NullTester();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		try
		{
			
			ReportIssueDialog dialog = new ReportIssueDialog(shell);
			dialog.setBlockOnOpen(!fTesting);
			dialog.open();
			fTester.execute(dialog);
		}
		// CSOFF:
		catch(Exception e)
		{
			gLogger.error("Error while opening Report Issue Dialog", e); //$NON-NLS-1$
			MessageDialog.openError(shell, MessagesClient.getString(
					"handlers.ReportIssueHandler.openError", "ca.mcgill.cs.swevo.qualyzer.handlers.messages"), e.getMessage()); //$NON-NLS-1$
		}
		// CSON:
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
