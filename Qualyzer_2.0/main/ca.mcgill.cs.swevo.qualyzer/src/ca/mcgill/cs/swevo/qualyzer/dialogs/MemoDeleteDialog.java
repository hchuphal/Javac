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
 *
 */
public class MemoDeleteDialog extends TitleAreaDialog
{
	private boolean fCodes;
	private Button fButton;
	private boolean fIsPlural;
	/**
	 * Constructor.
	 * @param shell
	 */
	public MemoDeleteDialog(Shell shell, boolean isPlural)
	{
		super(shell);
		fCodes = false;
		fIsPlural = isPlural;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.MemoDeleteDialog.deleteMemo", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		if(fIsPlural)
		{
			setMessage(MessagesClient.getString("dialogs.MemoDeleteDialog.warning_plural", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"),  //$NON-NLS-1$
					IMessageProvider.WARNING);
		}
		else
		{
			setMessage(MessagesClient.getString("dialogs.MemoDeleteDialog.warning", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"),  //$NON-NLS-1$
					IMessageProvider.WARNING);
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(container, SWT.NULL);
		if(fIsPlural)
		{
			label.setText(MessagesClient.getString("dialogs.MemoDeleteDialog.confirm_plural", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
		else
		{
			label.setText(MessagesClient.getString("dialogs.MemoDeleteDialog.confirm", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		label.setLayoutData(gd);
		
		if(!fIsPlural)
		{
			fButton = new Button(container, SWT.CHECK);
			fButton.setSelection(false);
			fButton.setText(MessagesClient.getString("dialogs.MemoDeleteDialog.deleteCodes", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
		
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{
		fCodes = !fIsPlural && fButton.getSelection();
		super.okPressed();
	}
	
	/**
	 * Should the codes be deleted too?
	 * @return
	 */
	public boolean deleteCodes()
	{
		return fCodes;
	}
	
	/**
	 * 
	 * @return
	 */
	public Button getCheckBox()
	{
		return fButton;
	}
}
