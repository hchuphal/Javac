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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.ImportTranscriptValidator;

/**
 * The wizard page to import memos.
 */
public class ImportMemoPage extends NewMemoPage
{

	private static final int COLS = 3;
	private Text fMemoFile;

	/**
	 * Constructor.
	 * @param project
	 */
	public ImportMemoPage(Project project)
	{
		super(project);
		setTitle(MessagesClient.getString("wizards.pages.ImportMemoPage.importWizard", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.ImportMemoPage.chooseFile", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
	}
	
	/**
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewMemoPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
				
		Composite composite = new Composite(container, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = COLS;
		composite.setLayout(layout);
		
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		
		Label label = new Label(composite, SWT.NULL);
		label.setText("Filename:"); //$NON-NLS-1$
		fMemoFile = new Text(composite, SWT.BORDER);
		fMemoFile.setText(""); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		fMemoFile.setLayoutData(gd);
		fMemoFile.addModifyListener(createModifyTextListener());
		
		Button button = new Button(composite, SWT.PUSH);
		button.addSelectionListener(createNewSelectionListener());
		button.setText("Browse"); //$NON-NLS-1$
		
		setfContainer(container);
		super.createControl(parent);
	}

	/**
	 * Validates all text inputs.
	 * @return
	 */
	private ModifyListener createModifyTextListener()
	{
		return new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				validate();
			}
		};
	}
	
	/**
	 * Handles the browse button's behavior.
	 * @return
	 */
	private SelectionListener createNewSelectionListener()
	{
		return new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterExtensions(new String[]{"*.rtf", "*.txt"}); 
				dialog.setFilterNames(new String[]{
						MessagesClient.getString("wizards.pages.ImportTranscriptPage.textExt", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"),
						MessagesClient.getString("wizards.pages.ImportTranscriptPage.textTxt", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")}); 
								
				String file = dialog.open();
				if(file != null)
				{
					fMemoFile.setText(file);
				}
				
				if(!fileDoesNotExist())
				{
					fillOutForm();
				}
				
				validate();
			}
		};
	}
	
	/**
	 * Fills out the memo name based on the filename selected.
	 */
	private void fillOutForm()
	{
		int begin = fMemoFile.getText().lastIndexOf(File.separatorChar) + 1;
		int end = fMemoFile.getText().lastIndexOf('.');
		String name = fMemoFile.getText().substring(begin, end);
		
		fName.setText(name);
	}
	
	/**
	 * @return
	 */
	protected boolean fileDoesNotExist()
	{
		File file = new File(fMemoFile.getText());
		return !file.exists();
	}
	
	/**
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.NewMemoPage#validate()
	 */
	@Override
	protected void validate()
	{
		ImportTranscriptValidator validator = new ImportTranscriptValidator(fMemoFile.getText());
		if(!validator.isValid())
		{
			setErrorMessage(validator.getErrorMessage());
			setPageComplete(false);
		}
		else
		{
			super.validate();
		}
	}
	
	/**
	 * Get the file that was chosen to import.
	 * @return
	 */
	public String getMemoFile()
	{
		return fMemoFile.getText();
	}
	
	/**
	 * @return
	 */
	public Text getFileText()
	{
		return fMemoFile;
	}
}
