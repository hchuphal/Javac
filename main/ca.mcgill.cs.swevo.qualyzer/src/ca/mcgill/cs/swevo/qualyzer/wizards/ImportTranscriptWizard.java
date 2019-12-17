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
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.ImportTranscriptPage;

/**
 * Wizard for importing transcripts.
 */
public class ImportTranscriptWizard extends Wizard
{

	private ImportTranscriptPage fPage;
	private Transcript fTranscript;
	private Project fProject;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public ImportTranscriptWizard(Project project)
	{
		fPage = new ImportTranscriptPage(project);
		fProject = project;
	}
	
	@Override
	public void addPages()
	{
		addPage(fPage);
	}
	
	/**
	 * Get the Transcript that was made by the wizard.
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
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
					fPage.getTranscriptFile());
			
			fTranscript = Facade.getInstance().createTranscript(fPage.getTranscriptName(), fPage.getDate(),
					fPage.getAudioFile(), fPage.getParticipants(), fProject);
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(), MessagesClient.getString(
					"wizards.ImportTranscriptWizard.transcriptError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient"), e.getMessage()); //$NON-NLS-1$
			return false;
		}

		return true;
	}

}
