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
package ca.mcgill.cs.swevo.qualyzer.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.validation.MemoValidator;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 *
 */
public class NewMemoPage extends WizardPage
{
	private static Logger gLogger = LoggerFactory.getLogger(NewMemoPage.class);
	private static final String SLASH = "/";  //$NON-NLS-1$
	
	protected Text fName;
	
	private Project fProject;
	private List<Participant> fParticipants;
	private Investigator fAuthor;
	private Composite fContainer;
	private Combo fAuthorName;
	private Table fTable;
	private DateTime fDate;

	private Combo fCodeCombo;

	private Combo fTranscriptCombo;

	/**
	 * Constructor.
	 * @param project
	 */
	public NewMemoPage(Project project)
	{
		super(MessagesClient.getString("wizards.pages.NewMemoPage.newMemo", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fProject = project;
		setTitle(MessagesClient.getString("wizards.pages.NewMemoPage.newMemoWizard", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.NewMemoPage.enterInfo", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fParticipants = new ArrayList<Participant>();
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.wizards.pages.TranscriptWizardPage#createControl(
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{	
		if(fContainer == null)
		{
			fContainer = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			fContainer.setLayout(layout);
		}
		
		createLabel(fContainer, MessagesClient.getString("wizards.pages.NewMemoPage.memoName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"));  //$NON-NLS-1$
		fName = new Text(fContainer, SWT.BORDER);
		fName.setText(""); //$NON-NLS-1$
		fName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		fName.addModifyListener(createModifyListener());
		
		createLabel(fContainer, MessagesClient.getString("wizards.pages.NewMemoPage.date", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fDate = new DateTime(fContainer, SWT.DATE);
		
		createLabel(fContainer, MessagesClient.getString("wizards.pages.NewMemoPage.author", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setupAuthorCombo();
		
		createLongLabel();
		createTable();
		
		createCodeCombo();
		
		createLabel(fContainer, MessagesClient.getString("wizards.pages.NewMemoPage.transcript", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fTranscriptCombo = new Combo(fContainer, SWT.READ_ONLY);
		fTranscriptCombo.setToolTipText(MessagesClient.getString("wizards.pages.NewMemoPage.chooseTranscript", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fTranscriptCombo.add(MessagesClient.getString("wizards.pages.NewMemoPage.noTranscript", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		for(Transcript transcript : fProject.getTranscripts())
		{
			fTranscriptCombo.add(transcript.getName());
		}
		fTranscriptCombo.select(0);
		fTranscriptCombo.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * 
	 */
	private void setupAuthorCombo()
	{
		fAuthorName = new Combo(fContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		fAuthorName.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName());
		String name = ""; //$NON-NLS-1$
		try
		{
			name = FileUtil.getProjectProperty(project, FileUtil.ACTIVE_INV);
		}
		catch (CoreException e)
		{
			gLogger.error("NewMemoPage: Could not open .project file", e); //$NON-NLS-1$
		}
		
		
		for(Investigator investigator : fProject.getInvestigators())
		{
			fAuthorName.add(investigator.getNickName());
			if(name.equals(investigator.getNickName()))
			{
				fAuthorName.select(fAuthorName.getItemCount() - 1);
			}
		}
		
		if(fAuthorName.getSelectionIndex() < 0)
		{
			fAuthorName.select(0);
		}
		
		fAuthorName.addModifyListener(createModifyListener());
	}

	/**
	 * 
	 */
	private void createCodeCombo()
	{
		createLabel(fContainer, MessagesClient.getString("wizards.pages.NewMemoPage.code", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fCodeCombo = new Combo(fContainer, SWT.READ_ONLY);
		fCodeCombo.setToolTipText(MessagesClient.getString("wizards.pages.NewMemoPage.chooseCode", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fCodeCombo.add(MessagesClient.getString("wizards.pages.NewMemoPage.noCode", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		for(Code code : fProject.getCodes())
		{
			fCodeCombo.add(code.getCodeName());
		}
		fCodeCombo.select(0);
		fCodeCombo.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
	}

	/**
	 * Set the internal container for the page.
	 * To be used by subclasses that need to create controls above the existing ones.
	 * @param container
	 */
	protected void setfContainer(Composite container)
	{
		fContainer = container;
	}
	
	/**
	 * @return
	 */
	private ModifyListener createModifyListener()
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
	 * 
	 */
	protected void validate()
	{
		MemoValidator validator = new MemoValidator(fName.getText().trim(), getAuthor(), fProject);
		
		if(validator.isValid())
		{
			setErrorMessage(null);
			setPageComplete(true);
		}
		else
		{
			setErrorMessage(validator.getErrorMessage());
			setPageComplete(false);
		}
	}
	
	/**
	 * 
	 */
	private void createTable()
	{
		GridData gd;
		fTable = new Table(fContainer, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		populateTable();		
	}

	/**
	 * 
	 */
	private void populateTable()
	{
		for(Participant participant : fProject.getParticipants())
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(participant.getParticipantId());
		}
	}

	/**
	 * 
	 */
	private void createLongLabel()
	{
		GridData gd;
		Label label = new Label(fContainer, SWT.NULL);
		label.setText(MessagesClient.getString("wizards.pages.NewMemoPage.participants", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		
	}

	/**
	 * @param container
	 * @param string
	 */
	private void createLabel(Composite container, String string)
	{
		Label label = new Label(container, SWT.NULL);
		label.setText(string);
	}

	/**
	 * Get the name of the Memo.
	 * @return
	 */
	public String getMemoName()
	{
		return fName.getText().trim();
	}
	
	/**
	 * Get the participants that are part of this memo.
	 * @return
	 */
	public List<Participant> getParticipants()
	{
		buildParticipants();
		return fParticipants;
	}
	
	private void buildParticipants()
	{
		fParticipants = new ArrayList<Participant>();
		
		TableItem[] items = fTable.getSelection();
		
		for(Participant participant : fProject.getParticipants())
		{
			for(TableItem item : items)
			{
				if(participant.getParticipantId().equals(item.getText()))
				{
					fParticipants.add(participant);
				}
			}
		}
	}
	
	/**
	 * Get the investigator that authored this memo.
	 * @return
	 */
	public Investigator getAuthor()
	{
		if(fAuthorName.getText().isEmpty())
		{
			fAuthor = null;
		}
		else
		{
			for(Investigator investigator : fProject.getInvestigators())
			{
				if(investigator.getNickName().equals(fAuthorName.getText()))
				{
					fAuthor = investigator;
				}
			}
		}
		
		return fAuthor;
	}
	
	/**
	 * Get the date the was assigned to the memo.
	 * In the format MM/DD/YYYY.
	 * @return
	 */
	public String getDate()
	{
		return (fDate.getMonth() + 1) + SLASH + fDate.getDay() + SLASH + fDate.getYear();
	}
	
	/**
	 * Return the code that was selected.
	 * @return
	 */
	public Code getCode()
	{
		int index = fCodeCombo.getSelectionIndex();
		if(index > 0)
		{
			return fProject.getCodes().get(index - 1);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public Transcript getTranscript()
	{
		int index = fTranscriptCombo.getSelectionIndex();
		if(index > 0)
		{
			return fProject.getTranscripts().get(index - 1);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public Text getNameText()
	{
		return fName;
	}
}
