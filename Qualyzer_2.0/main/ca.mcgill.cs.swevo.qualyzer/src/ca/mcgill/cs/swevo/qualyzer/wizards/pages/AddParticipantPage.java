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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.ParticipantValidator;
import ca.mcgill.cs.swevo.qualyzer.model.validation.StringLengthValidator;

/**
 * The page of the Add Participant Wizard.
 */
public class AddParticipantPage extends WizardPage
{
	private Composite fContainer;
	private Project fProject;
	private Text fIdText;
	private Text fFullNameText;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddParticipantPage(Project project)
	{
		super(MessagesClient.getString("wizards.pages.AddParticipantPage.addParticipant", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setTitle(MessagesClient.getString("wizards.pages.AddParticipantPage.addParticipant", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.AddParticipantPage.enterInfo", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fProject = project;
	}
	
	@Override
	public void createControl(Composite parent)
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fContainer.setLayout(layout);
		
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.AddParticipantPage.participantId", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fIdText = new Text(fContainer, SWT.BORDER);
		fIdText.setText(""); //$NON-NLS-1$
		fIdText.addModifyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.AddParticipantPage.fullName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fFullNameText = new Text(fContainer, SWT.BORDER);
		fFullNameText.setText("");  //$NON-NLS-1$
		fFullNameText.addModifyListener(createStringLengthValidator(
				MessagesClient.getString("wizards.pages.AddParticipantPage.fullName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"), fFullNameText)); //$NON-NLS-1$
		
		setGridData();
		setControl(fContainer);
		setPageComplete(false);
	}
	
	/**
	 * @return
	 */
	private ModifyListener createKeyListener()
	{
		return new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e)
			{
				ParticipantValidator lValidator = new ParticipantValidator(fIdText.getText().trim(), fProject);
				if(lValidator.isValid())
				{
					setPageComplete(true);
					setErrorMessage(null);
				}
				else
				{
					setPageComplete(false);
					setErrorMessage(lValidator.getErrorMessage());
				}
			}

		};
	}
	
	private ModifyListener createStringLengthValidator(final String pLabel, final Text pText)
	{
		return new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e)
			{
				StringLengthValidator lValidator = new StringLengthValidator(pLabel, pText.getText());
				
				if(lValidator.isValid())
				{
					setPageComplete(true);
					setErrorMessage(null);
				}
				else
				{
					setPageComplete(false);
					setErrorMessage(lValidator.getErrorMessage());
				}	
			}
		};
	}
	
	private void setGridData()
	{
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fIdText.setLayoutData(gd);
		fFullNameText.setLayoutData(gd);
	}
	
	/**
	 * Get the Participant ID field.
	 * @return
	 */
	public String getParticipantId()
	{
		return fIdText.getText().trim();
	}

	/**
	 * Get the Fullname field.
	 * @return
	 */
	public String getFullname()
	{
		return fFullNameText.getText().trim();
	}
	
	/**
	 * Build the participant represented by the information entered in this page.
	 * @deprecated Use Facade.createParticipant(...)
	 * @return The participant that was built.
	 */
	public Participant getParticipant()
	{
		Participant participant = new Participant();
		participant.setParticipantId(getParticipantId());
		participant.setFullName(getFullname());
		return participant;
	}

	/**
	 * @return
	 */
	public Text getIDText()
	{
		return fIdText;
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getFullnameText()
	{
		return fFullNameText;
	}

}
