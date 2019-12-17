/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *     Martin Robillard
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.ImportMemoPage;

/**
 * A wizard to import memos.
 */
public class ImportMemoWizard extends Wizard
{
	private ImportMemoPage fPage;
	private Project fProject;
	private Memo fMemo;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public ImportMemoWizard(Project project)
	{
		fProject = project;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		fPage = new ImportMemoPage(fProject);
		addPage(fPage);
	}
	
	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		try
		{
			FileUtil.setupMemoFiles(fPage.getMemoName(), fProject.getFolderName(), fPage.getMemoFile());
			
			fMemo = Facade.getInstance().createMemo(fPage.getMemoName(), fPage.getDate(),
					fPage.getAuthor(), fPage.getParticipants(), fProject, fPage.getCode(), fPage.getTranscript());
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(), MessagesClient.getString("wizards.ImportMemoWizard.memoError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient"), //$NON-NLS-1$
					e.getMessage()); 
			return false;
		}

		return true;
	}
	
	/**
	 * Get the memo that was created by this wizard.
	 * @return
	 */
	public Memo getMemo()
	{
		return fMemo;
	}
}
