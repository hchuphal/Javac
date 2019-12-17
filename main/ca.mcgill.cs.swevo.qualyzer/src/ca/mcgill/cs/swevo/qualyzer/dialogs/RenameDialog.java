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
import ca.mcgill.cs.swevo.qualyzer.model.validation.TranscriptNameValidator;

/**
 * Dialog to rename transcripts. 
 */
public class RenameDialog extends TitleAreaDialog
{
	private Text fNewName;
	private String fName;
	private Project fProject;
	private String fOldName;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public RenameDialog(Shell shell, Project project)
	{
		super(shell);
		fName = ""; //$NON-NLS-1$
		fProject = project;
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.RenameDialog.renamingTranscript", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		setMessage(MessagesClient.getString("dialogs.RenameDialog.renameTranscript", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		this.getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
				
		Composite composite = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(MessagesClient.getString("dialogs.RenameDialog.newName", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		fNewName = new Text(composite, SWT.BORDER);
		fNewName.setText(""); //$NON-NLS-1$
		fNewName.addModifyListener(createKeyListener());
		fNewName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		return parent;
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
				TranscriptNameValidator lValidator = 
					new TranscriptNameValidator(fNewName.getText().trim(), fOldName, fProject);
				
				if(!lValidator.isValid())
				{
					setErrorMessage(lValidator.getErrorMessage());
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
				else
				{
					setErrorMessage(null);
					fName = fNewName.getText();
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}
		};
	}
	
	/**
	 * Get the new name.
	 * @return
	 */
	public String getName()
	{
		return fName.trim();
	}

	/**
	 * @param name
	 */
	public void setCurrentName(String name)
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		// TODO Auto-generated method stub
		super.okPressed();
	}
	
}
