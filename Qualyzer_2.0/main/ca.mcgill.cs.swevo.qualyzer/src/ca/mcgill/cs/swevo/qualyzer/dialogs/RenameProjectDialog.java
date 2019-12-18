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

package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.ProjectNameValidator;

/**
 *
 */
public class RenameProjectDialog extends TitleAreaDialog
{
	private Project fProject;
	private String fNewName;
	
	private Text fNameText;
	
	/**
	 * @param parentShell
	 */
	public RenameProjectDialog(Shell parentShell, Project project)
	{
		super(parentShell);
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.RenameProjectDialog.renameProject", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		setMessage(MessagesClient.getString("dialogs.RenameProjectDialog.warning", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"), IMessageProvider.WARNING); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		Composite body = new Composite(parent, SWT.NULL);
		body.setLayout(new GridLayout(2, false));
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(body, SWT.NULL);
		label.setText(MessagesClient.getString("dialogs.RenameProjectDialog.newName", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		fNameText = new Text(body, SWT.BORDER);
		fNameText.setText(""); //$NON-NLS-1$
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fNameText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		});
		
		return parent;
	}
	
	private void validate()
	{
		ProjectNameValidator validator = new ProjectNameValidator(fNameText.getText().trim(), 
				fProject.getName(), fProject);
		if(validator.isValid())
		{
			setErrorMessage(null);
			getButton(IDialogConstants.OK_ID).setEnabled(true);
		}
		else
		{
			setErrorMessage(validator.getErrorMessage());
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		fNewName = fNameText.getText().trim();
		super.okPressed();
	}
	
	/**
	 * @return the NewName
	 */
	public String getNewName()
	{
		return fNewName.trim();
	}
	
	/**
	 * Get the Text field to input a name, for use in testing.
	 * @return
	 */
	public Text getNameField()
	{
		return fNameText;
	}

}
