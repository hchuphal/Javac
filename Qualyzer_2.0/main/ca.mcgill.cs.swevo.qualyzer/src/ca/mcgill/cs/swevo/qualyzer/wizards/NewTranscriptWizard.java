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
package ca.mcgill.cs.swevo.qualyzer.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import ca.mcgill.cs.swevo.qualyzer.QualyzerException;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.TranscriptWizardPage;

/**
 * Wizard for creating a new transcript.
 */
public class NewTranscriptWizard extends Wizard
{
	private TranscriptWizardPage fPage;
	private Project fProject;
	private Transcript fTranscript;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public NewTranscriptWizard(Project project)
	{
		fProject = project;
	}
	
	@Override
	public void addPages()
	{
		fPage = new TranscriptWizardPage(fProject);
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
			FileUtil.setupTranscriptFiles(fPage.getTranscriptName(), fProject.getFolderName(), fPage.getAudioFile(), 
					""); //$NON-NLS-1$
			
			fTranscript = Facade.getInstance().createTranscript(fPage.getTranscriptName(), fPage.getDate(),
					fPage.getAudioFile(), fPage.getParticipants(), fProject);
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(), MessagesClient.getString(
					"wizards.NewTranscriptWizard.transcriptCreateError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClientClient"), e.getMessage()); //$NON-NLS-1$
			return false;
		}

		return true;
	}
	
	


	/**
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
	}

}
