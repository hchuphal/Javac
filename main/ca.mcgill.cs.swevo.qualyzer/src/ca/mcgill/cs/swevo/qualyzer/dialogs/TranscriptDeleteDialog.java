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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog for deleting transcripts.
 */
public class TranscriptDeleteDialog extends TitleAreaDialog
{
	private boolean fDeleteParticipants;
	private boolean fDeleteCodes;
	
	private Button fParticipantButton;
	private Button fCodeButton;
	private boolean fIsPlural;
	
	/**
	 * Constructor.
	 * @param shell
	 */
	public TranscriptDeleteDialog(Shell shell, boolean isPlural)
	{
		super(shell);
		fDeleteParticipants = false;
		fDeleteCodes = false;
		fIsPlural = isPlural;
	}
	
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.TranscriptDeleteDialog.deleteTrancript", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		if(fIsPlural)
		{
			setMessage(MessagesClient.getString("dialogs.TranscriptDeleteDialog.warning_plural", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"),  //$NON-NLS-1$
					IMessageProvider.WARNING);
		}
		else
		{
			setMessage(MessagesClient.getString("dialogs.TranscriptDeleteDialog.warning", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"),  //$NON-NLS-1$
					IMessageProvider.WARNING);
		}
		
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		composite.setLayout(layout);
		parent.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(composite, SWT.NULL);
		if(fIsPlural)
		{
			label.setText(MessagesClient.getString("dialogs.TranscriptDeleteDialog.confirm_plural", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
		else
		{
			label.setText(MessagesClient.getString("dialogs.TranscriptDeleteDialog.confirm", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
		
		label.setLayoutData(gd);
		
		if(!fIsPlural)
		{
			fCodeButton = new Button(composite, SWT.CHECK);
			fCodeButton.setText(MessagesClient.getString("dialogs.TranscriptDeleteDialog.deleteCodes", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
			
			fParticipantButton = new Button(composite, SWT.CHECK);
			fParticipantButton.setText(MessagesClient.getString(
					"dialogs.TranscriptDeleteDialog.deleteParticipants", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
		
		return parent;
	}
	
	@Override
	public void okPressed()
	{
		fDeleteParticipants = !fIsPlural && fParticipantButton.getSelection();
		fDeleteCodes = !fIsPlural && fCodeButton.getSelection();
		
		super.okPressed();
	}
	
	/**
	 * Get if the delete participants button was selected.
	 * @return
	 */
	public boolean getDeleteParticipants()
	{
		return fDeleteParticipants;
	}
	
	/**
	 * Get if the delete codes button was selected.
	 * @return
	 */
	public boolean getDeleteCodes()
	{
		return fDeleteCodes;
	}
	
	/**
	 * 
	 * @return
	 */
	public Button getParticipantButton()
	{
		return fParticipantButton;
	}
	
	/**
	 * 
	 * @return
	 */
	public Button getCodeButton()
	{
		return fCodeButton;
	}
	
	
}
