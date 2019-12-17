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
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.AddParticipantPage;

/**
 * The wizard the controls the addition of a new Participant to a project.
 */
public class AddParticipantWizard extends Wizard
{
	private AddParticipantPage fPage;
	private Project fProject;
	private Participant fParticipant;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddParticipantWizard(Project project)
	{
		fProject = project;
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages()
	{
		fPage = new AddParticipantPage(fProject);
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
			fParticipant = Facade.getInstance()
				.createParticipant(fPage.getParticipantId(), fPage.getFullname(), fProject);
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(), MessagesClient.getString(
					"wizards.AddParticipantWizard.participantError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClientClient"), e.getMessage()); //$NON-NLS-1$
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the participant that was built by the project.
	 * @return
	 */
	public Participant getParticipant()
	{
		return fParticipant;
	}

}
