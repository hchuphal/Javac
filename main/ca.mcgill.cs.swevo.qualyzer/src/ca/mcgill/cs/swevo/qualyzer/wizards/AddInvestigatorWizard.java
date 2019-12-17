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
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.wizards.pages.AddInvestigatorPage;

/**
 * The wizard which controls the adding of a new Investigator to the project.
 */
public class AddInvestigatorWizard extends Wizard
{

	private AddInvestigatorPage fPage;
	private Project fProject;
	private Investigator fInvestigator;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddInvestigatorWizard(Project project)
	{
		fProject = project;
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages()
	{
		fPage = new AddInvestigatorPage(fProject);
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
			fInvestigator = Facade.getInstance().createInvestigator(fPage.getInvestigatorNickname(), 
					fPage.getInvestigatorFullname(), fPage.getInstitution(), fProject, true);
		}
		catch(QualyzerException e)
		{
			MessageDialog.openError(getShell(), MessagesClient.getString(
					"wizards.AddInvestigatorWizard.investigatorError", "ca.mcgill.cs.swevo.qualyzer.wizards.MessagesClient"), e.getMessage()); //$NON-NLS-1$
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the investigator that was created with the wizard.
	 * @return The Investigator that the user created.
	 */
	public Investigator getInvestigator()
	{
		return fInvestigator;
	}

}
