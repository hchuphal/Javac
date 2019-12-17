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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.dnd.TextTransfer;
import org.junit.Test;

import ca.mcgill.cs.swevo.qualyzer.dialogs.ReportIssueDialog;
import ca.mcgill.cs.swevo.qualyzer.editors.IDialogTester;

/**
 * @author Barthelemy Dagenais (bart@cs.mcgill.ca)
 * 
 */
public class ReportIssueHandlerTest
{

	@Test
	public void testReportIssueDialog()
	{
		ReportIssueHandler handler = new ReportIssueHandler();
		handler.setTesting(true);
		handler.setTester(new IDialogTester()
		{

			@Override
			public void execute(Dialog dialog)
			{
				ReportIssueDialog rDialog = (ReportIssueDialog) dialog;
				String text = rDialog.getReportTextWidget().getText();
				assertTrue(text.length() > 0);
				rDialog.copyToClipboard();
				TextTransfer transfer = TextTransfer.getInstance();
				String clipboardText = (String) rDialog.getClipboard().getContents(transfer);
				assertEquals(text, clipboardText);
				rDialog.okPressed();
			}
		});
		try
		{
			handler.execute(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
