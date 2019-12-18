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
 * Wizard page to import a transcript.
 */
public class ImportTranscriptPage extends TranscriptWizardPage
{
	private static final int COLS = 3;
	private Text fTranscriptFile;
	
	/**
	 * Creates the new import transcript page.
	 * @param project The project in which to import a transcript.
	 */
	public ImportTranscriptPage(Project project)
	{
		super(project, MessagesClient.getString("wizards.pages.ImportTranscriptPage.importTranscript", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.ImportTranscriptPage.enterInfo", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
	}

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
		label.setText(MessagesClient.getString("wizards.pages.ImportTranscriptPage.filename", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fTranscriptFile = new Text(composite, SWT.BORDER);
		fTranscriptFile.setText(""); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		fTranscriptFile.setLayoutData(gd);
		fTranscriptFile.addModifyListener(createKeyListener());
		
		Button button = new Button(composite, SWT.PUSH);
		button.addSelectionListener(createNewSelectionListener());
		button.setText(MessagesClient.getString("wizards.pages.ImportTranscriptPage.browse", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		setContainer(container);
		super.createControl(parent);
	}

	/**
	 * Handles the browse button.
	 * @return
	 */
	private SelectionAdapter createNewSelectionListener()
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
				fTranscriptFile.setText(file);
				
				if(!fileDoesNotExist())
				{
					fillOutForm();
				}
				
				validate();
			}
		};
	}
	
	/**
	 * Fills in the transcript name and audio file based on the selection of the file to import.
	 */
	private void fillOutForm()
	{
		int begin = fTranscriptFile.getText().lastIndexOf(File.separatorChar) + 1;
		int end = fTranscriptFile.getText().lastIndexOf('.');
		String name = fTranscriptFile.getText().substring(begin, end);
		
		fName.setText(name);
		fAudioFile.setText(findAudioFile(name));
	}

	@Override
	protected ModifyListener createKeyListener()
	{
		return new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e)
			{
				if(!fAudioFileSelected && !fName.getText().isEmpty())
				{
					String fileName = findAudioFile(fName.getText());
					if(!fileName.isEmpty())
					{
						fAudioFile.setText(fileName);
					}
				}
				validate();
			}
		};
	}
	
	/**
	 * @return
	 */
	protected boolean fileDoesNotExist()
	{
		File file = new File(fTranscriptFile.getText());
		return !file.exists();
	}

	/**
	 * The file path for the transcript that is to be imported.
	 * @return
	 */
	public String getTranscriptFile()
	{		
		return fTranscriptFile.getText();
	}

	@Override
	protected void validate()
	{
		ImportTranscriptValidator lValidator = new ImportTranscriptValidator(fTranscriptFile.getText());
		if(!lValidator.isValid())
		{
			setError(lValidator.getErrorMessage());
		}
		else 
		{
			super.validate();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getFileText()
	{
		return fTranscriptFile;
	}

}
