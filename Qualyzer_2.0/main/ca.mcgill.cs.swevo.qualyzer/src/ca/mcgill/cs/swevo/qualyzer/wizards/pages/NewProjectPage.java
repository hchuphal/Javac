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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.IQualyzerPreferenceConstants;
import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.validation.ProjectValidator;

/**
 * Wizard page for creating a new project.
 */
public class NewProjectPage extends WizardPage
{
	private final String fDefaultInvName;
	
	private Composite fContainer;
	private Text fProjectName;
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	private String fProjectNameString;
	private String fNicknameString;
	private String fFullNameString;
	private String fInstitutionString;
	
	/**
	 * Constructor.
	 */
	public NewProjectPage()
	{
		super(MessagesClient.getString("wizards.pages.NewProjectPage.newProject", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setTitle(MessagesClient.getString("wizards.pages.NewProjectPage.newProject", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.NewProjectPage.enterName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fDefaultInvName = QualyzerActivator.getDefault().getPreferenceStore().getString(
				IQualyzerPreferenceConstants.DEFAULT_INVESTIGATOR);
	}
	
	@Override
	public void createControl(Composite parent) 
	{
		fContainer = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fContainer.setLayout(layout);
		
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.NewProjectPage.projectName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fProjectName = new Text(fContainer, SWT.BORDER);
		fProjectName.setText(""); //$NON-NLS-1$
		fProjectName.addModifyListener(createKeyListener());
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectName.setLayoutData(gd);
	
		Group group = createGroup();
		
		createSectionHeader(group);
		
		label = new Label(group, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.NewProjectPage.nickname", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$

		fNickname = new Text(group, SWT.BORDER | SWT.SINGLE);
		fNickname.setText(fDefaultInvName); //$NON-NLS-1$
		
		fNickname.addModifyListener(createKeyListener());
		
		label = new Label(group, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.NewProjectPage.fullName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fFullname = new Text(group, SWT.BORDER | SWT.SINGLE);
		fFullname.setText(""); //$NON-NLS-1$
		
		label = new Label(group, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.NewProjectPage.insitution", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fInstitution = new Text(group, SWT.BORDER | SWT.SINGLE);
		fInstitution.setText(""); //$NON-NLS-1$
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fNickname.setLayoutData(gd);
		fFullname.setLayoutData(gd);
		fInstitution.setLayoutData(gd);
		
		// Required to avoid an error in the system
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * @param composite
	 */
	private void createSectionHeader(Composite composite)
	{
		Label label;
		GridData gd;
		label = new Label(composite, SWT.WRAP);
		label.setText(MessagesClient.getString("wizards.pages.NewProjectPage.info", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, false, false);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
	}

	/**
	 * @return
	 */
	private Group createGroup()
	{
		GridLayout layout;
		GridData gd;
		Group group = new Group(fContainer, SWT.NULL);
		group.setText(MessagesClient.getString("wizards.pages.NewProjectPage.investigator", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		layout = new GridLayout();
		layout.numColumns = 2;
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		group.setLayout(layout);
		group.setLayoutData(gd);
		return group;
	}

	/**
	 * Validates input text.
	 * @return
	 */
	private ModifyListener createKeyListener()
	{
		return new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e)
			{
				ProjectValidator lValidator = new ProjectValidator(fProjectName.getText().trim(), 
						fNickname.getText().trim(), ResourcesPlugin.getWorkspace().getRoot());
				if(lValidator.isValid())
				{
					setError(null);
				}
				else
				{
					setError(lValidator.getErrorMessage());
				}
			}
		};
	}
	
	private void setError(String message)
	{
		setErrorMessage(message);
		if(message == null)
		{
			setPageComplete(true);
		}
		else
		{
			setPageComplete(false);
		}
	}
	
	/**
	 * Get the project name field.
	 * @return
	 */
	public String getProjectName()
	{
		return fProjectNameString.trim();
	}
	
	/**
	 * Get the Nickname field.
	 * @return
	 */
	public String getInvestigatorNickname()
	{
		return fNicknameString.trim();
	}
	
	/**
	 * Get the fullname field.
	 * @return
	 */
	public String getInvestigatorFullname()
	{
		return fFullNameString.trim();
	}
	
	/**
	 * Get the Institution field.
	 * @return
	 */
	public String getInstitution()
	{
		return fInstitutionString.trim();
	}
	
	/**
	 * Copy the data from the text fields into strings so they can be retrieved.
	 */
	public void save()
	{
		fProjectNameString = fProjectName.getText();
		fNicknameString = fNickname.getText();
		fFullNameString = fFullname.getText();
		fInstitutionString = fInstitution.getText();
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getProjectNameText()
	{
		return fProjectName;
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getNickNameText()
	{
		return fNickname;
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getFullNameText()
	{
		return fFullname;
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getInstitutionText()
	{
		return fInstitution;
	}
	
}
