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
package ca.mcgill.cs.swevo.qualyzer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
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
import ca.mcgill.cs.swevo.qualyzer.model.validation.MemoNameValidator;

/**
 *
 */
public class RenameMemoDialog extends TitleAreaDialog
{
	private Project fProject;
	private String fName;
	private String fOldName;
	private Text fNewName;

	/**
	 * Constructor.
	 * @param shell
	 * @param project
	 */
	public RenameMemoDialog(Shell shell, Project project)
	{
		super(shell);
		fProject = project;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialog.RenameMemoDialog.renaming", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		setMessage(MessagesClient.getString("dialog.RenameMemoDialog.enterName", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
		
		Composite container = new Composite(parent, SWT.NULL);
		layout = new GridLayout(2, false);
		container.setLayout(layout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(container, SWT.NULL);
		label.setText(MessagesClient.getString("dialog.RenameMemoDialog.memoName", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		fNewName = new Text(container, SWT.BORDER);
		fNewName.setText(""); //$NON-NLS-1$
		fNewName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		fNewName.addModifyListener(new ModifyListener()
		{
			
			@Override
			public void modifyText(ModifyEvent e)
			{
				MemoNameValidator validator = new MemoNameValidator(fNewName.getText().trim(), fOldName, fProject);
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
		});
		
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		fName = fNewName.getText();
		super.okPressed();
	}
	
	/**
	 * Get the name that was entered.
	 * @return
	 */
	public String getName()
	{
		return fName.trim();
	}
	
	/**
	 * Set the current name of the memo.
	 * @param name
	 */
	public void setOldName(String name)
	{
		fOldName = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getNameText()
	{
		return fNewName;
	}
}
