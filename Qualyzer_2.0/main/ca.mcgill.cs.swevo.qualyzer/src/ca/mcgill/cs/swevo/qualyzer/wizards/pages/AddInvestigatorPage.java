/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Jonathan Faubert
 *      Martin Robillard
 *******************************************************************************/
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

import ca.mcgill.cs.swevo.qualyzer.IQualyzerPreferenceConstants;
import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.InvestigatorValidator;
import ca.mcgill.cs.swevo.qualyzer.model.validation.StringLengthValidator;

/**
 * Page to add an investigator.
 */
public class AddInvestigatorPage extends WizardPage
{
	private Project fProject;
	private Composite fContainer;
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	
	private final String fDefaultInvestigator;
	
	/**
	 * Constructor.
	 * @param project
	 */
	public AddInvestigatorPage(Project project)
	{
		super(MessagesClient.getString("wizards.pages.AddInvestigatorPage.addInvestigator", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setTitle(MessagesClient.getString("wizards.pages.AddInvestigatorPage.addInvestigator", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.AddInvestigatorPage.enterInfo", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fProject = project;
		fDefaultInvestigator = QualyzerActivator.getDefault().getPreferenceStore().getString(
				IQualyzerPreferenceConstants.DEFAULT_INVESTIGATOR);
	}
	
	@Override
	public void createControl(Composite parent)
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		fContainer.setLayout(layout);
		layout.numColumns = 2;
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.AddInvestigatorPage.nickname", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$

		fNickname = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fNickname.setText(fDefaultInvestigator); //$NON-NLS-1$
		if(idInUse())
		{
			fNickname.setText(""); //$NON-NLS-1$
		}
		
		//Only allows the user to proceed if a valid name is entered
		fNickname.addModifyListener(createKeyListener());
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.AddInvestigatorPage.fullName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fFullname = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fFullname.setText(""); //$NON-NLS-1$
		fFullname.addModifyListener(createStringLengthValidator(
				MessagesClient.getString("wizards.pages.AddInvestigatorPage.fullName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"), fFullname)); //$NON-NLS-1$
		
		label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.AddInvestigatorPage.insitution", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fInstitution = new Text(fContainer, SWT.BORDER | SWT.SINGLE);
		fInstitution.setText("");  //$NON-NLS-1$
		fInstitution.addModifyListener(createStringLengthValidator(
				MessagesClient.getString("wizards.pages.AddInvestigatorPage.insitution", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"), fInstitution)); //$NON-NLS-1$
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fNickname.setLayoutData(gd);
		fFullname.setLayoutData(gd);
		fInstitution.setLayoutData(gd);
		
		// Required to avoid an error in the system
		setControl(fContainer);
		setPageComplete(!fNickname.getText().isEmpty());
	}

	/**
	 * Get the Nickname field.
	 * @return
	 */
	public String getInvestigatorNickname()
	{
		return fNickname.getText().trim();
	}
	
	/**
	 * Get the fullname field.
	 * @return
	 */
	public String getInvestigatorFullname()
	{
		return fFullname.getText().trim();
	}
	
	/**
	 * Get the Institution field.
	 * @return
	 */
	public String getInstitution()
	{
		return fInstitution.getText().trim();
	}
	
	private ModifyListener createKeyListener()
	{
		return new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e)
			{
				InvestigatorValidator lValidator = new InvestigatorValidator(fNickname.getText().trim(), fProject);
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
				StringLengthValidator lValidator = new StringLengthValidator(pLabel, pText.getText().trim());
				
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
	
	private boolean idInUse()
	{
		for(Investigator inves : fProject.getInvestigators())
		{
			if(inves.getNickName().equals(fNickname.getText()))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Build the investigator represented by the information entered in the wizard.
	 * @deprecated use Facade.createInvestigator(...)
	 * @return The resulting investigator.
	 */
	public Investigator getInvestigator()
	{
		Investigator investigator = new Investigator();
		investigator.setFullName(fFullname.getText());
		investigator.setNickName(fNickname.getText());
		investigator.setInstitution(fInstitution.getText());
		return investigator;
	}
	
	/**
	 * For Testing.
	 * @return
	 */
	public Text getNicknameText()
	{
		return fNickname;
	}
	
	/**
	 * For Testing.
	 * @return
	 */
	public Text getFullNameText()
	{
		return fFullname;
	}
	
	/**
	 * For Testing.
	 * @return
	 */
	public Text getInstitutionText()
	{
		return fInstitution;
	}

}
