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

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.CodeChooserValidator;

/**
 *
 */
public class CodeChooserDialog extends TitleAreaDialog
{

	private Project fProject;
	private Combo fCodeName;
	private StyledText fDescription;
	private String[] fProposals;

	private Code fCode;
	private Fragment fFragment;
		
	/**
	 * Constructor.
	 * @param shell
	 */
	public CodeChooserDialog(Shell shell, Project project, Fragment fragment)
	{
		super(shell);
		fProject = project;
		fFragment = fragment;
		fCode = null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.CodeChooserDialog.add", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		setMessage(MessagesClient.getString("dialogs.CodeChooserDialog.enterName", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.dialogs.NewCodeDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout(2, false);
		Composite body = new Composite(parent, SWT.NULL);
		body.setLayout(layout);
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = new Label(body, SWT.NULL);
		label.setText(MessagesClient.getString("dialogs.CodeChooserDialog.code", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		fCodeName = new Combo(body, SWT.BORDER | SWT.DROP_DOWN);
		fCodeName.setText(""); //$NON-NLS-1$
		fCodeName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		label = new Label(body, SWT.NULL);
		label.setText(MessagesClient.getString("dialogs.CodeChooserDialog.description", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		fDescription = new StyledText(body, SWT.WRAP | SWT.BORDER);
		fDescription.setText(""); //$NON-NLS-1$
		fDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		fProposals = buildProposals();
		
		for(String prop : fProposals)
		{
			fCodeName.add(prop);
		}
		
		@SuppressWarnings("unused")
		AutoCompleteField field = new AutoCompleteField(fCodeName, new ComboContentAdapter(), fProposals);
		
		fCodeName.addModifyListener(createModifyListener());
		
		return parent;
	}

	/**
	 * Listens for changes in the text and validates the data entered. Displays a creation
	 *  warning if the code doesn't exist.
	 * @return
	 */
	private ModifyListener createModifyListener()
	{
		return new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e)
			{
				CodeChooserValidator validator = new CodeChooserValidator(fCodeName.getText().trim(), 
						fProject, fFragment);
				if(!validator.isValid())
				{
					if(validator.getErrorMessage().equals(MessagesClient.getString(
							"dialogs.CodeChooserDialog.nameTaken", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"))) //$NON-NLS-1$
					{
						setErrorMessage(null);
						setMessage(MessagesClient.getString("dialogs.CodeChooserDialog.enterName", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
						for(Code code : fProject.getCodes())
						{
							if(code.getCodeName().equals(fCodeName.getText().trim()))
							{
								fDescription.setText(code.getDescription());
							}
						}
						getButton(IDialogConstants.OK_ID).setEnabled(true);
					}
					else
					{
						setErrorMessage(validator.getErrorMessage());
						getButton(IDialogConstants.OK_ID).setEnabled(false);
					}
				}
				else
				{
					setErrorMessage(null);
					setMessage(MessagesClient.getString(
							"dialogs.CodeChooserDialog.doesNotExist", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"), IMessageProvider.WARNING); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				
			}
			
		};
	}


	/**
	 * @return
	 */
	private String[] buildProposals()
	{
		ArrayList<String> proposals = new ArrayList<String>();
		for(Code code : fProject.getCodes())
		{
			if(fFragment == null)
			{
				proposals.add(code.getCodeName());
			}
			else 
			{
				boolean found = false;
				for(CodeEntry entry : fFragment.getCodeEntries())
				{
					if(entry.getCode().equals(code))
					{
						found = true;
					}
				}
				if(!found)
				{
					proposals.add(code.getCodeName());
				}
			}
		}
		Collections.sort(proposals);
		
		return proposals.toArray(new String[0]);
	}
	
	/**
	 * Get the code choosen by this dialog.
	 * @return
	 */
	public Code getCode()
	{
		return fCode;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	public void okPressed()
	{	
		for(Code code : fProject.getCodes())
		{
			if(code.getCodeName().equals(fCodeName.getText().trim()))
			{
				fCode = code;
				break;
			}
		}
		
		CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
		
		if(fCode == null)
		{
			fCode = Facade.getInstance().createCode(fCodeName.getText().trim(), 
					fDescription.getText().trim(), fProject);
			view.getCommonViewer().refresh();
		}
		else
		{
			fCode.setDescription(fDescription.getText().trim());
			Facade.getInstance().saveCodes(new Code[]{fCode});
			view.getCommonViewer().refresh();
		}
		
		super.okPressed();
	}
	
	/**
	 *
	 * @return
	 */
	public Combo getCodeName()
	{
		return fCodeName;
	}
}
