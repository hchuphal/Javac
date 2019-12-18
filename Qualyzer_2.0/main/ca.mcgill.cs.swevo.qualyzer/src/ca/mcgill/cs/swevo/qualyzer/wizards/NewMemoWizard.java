/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonathan Faubert
 *******************************************************************************/
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewMemoPage;

/**
 *
 */
public class NewMemoWizard extends Wizard
{

	private NewMemoPage fPage;
	private Project fProject;
	private Memo fMemo;
	
	/**
	 * 
	 * @param project
	 */
	public NewMemoWizard(Project project)
	{
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		fPage = new NewMemoPage(fProject);
		addPage(fPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		try
		{
			FileUtil.setupMemoFiles(fPage.getMemoName(), fProject.getFolderName(), ""); //$NON-NLS-1$
			
			fMemo = Facade.getInstance().createMemo(fPage.getMemoName(), fPage.getDate(), fPage.getAuthor(), 
					fPage.getParticipants(), fProject, fPage.getCode(), fPage.getTranscript());
			
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(),
					MessagesClient.getString("wizards.NewMemoWizard.memoError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient"), e.getMessage()); //$NON-NLS-1$
			return false;
		}
		
		return fMemo != null;
	}

	/**
	 * Get the memo created by this wizard.
	 * @return
	 */
	public Memo getMemo()
	{
		return fMemo;
	}
	
}
