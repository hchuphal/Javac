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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.validation.TranscriptValidator;
import ca.mcgill.cs.swevo.qualyzer.wizards.AddParticipantWizard;

/**
 * The only page in the new Transcript Wizard.
 */
public class TranscriptWizardPage extends WizardPage
{
	private static final int COMPOSITE_COLS = 4;

	private static final String AUDIO_PATH = File.separator+"audio"+File.separator; //$NON-NLS-1$
	
	protected Table fTable;
	protected Text fName;
	protected Label fAudioFile;
	protected boolean fAudioFileSelected;
	
	private Composite fContainer;
	private DateTime fDate;
	private Project fProject;
	private ArrayList<Participant> fParticipants;
	private final String fWorkspacePath;
	
	/**
	 * 
	 * @param project
	 */
	public TranscriptWizardPage(Project project)
	{
		super(MessagesClient.getString("wizards.pages.TranscriptWizardPage.newTranscript", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setTitle(MessagesClient.getString("wizards.pages.TranscriptWizardPage.newTranscript", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		setDescription(MessagesClient.getString("wizards.pages.TranscriptWizardPage.enterInfo", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fProject = project;
		fParticipants = new ArrayList<Participant>();
		fAudioFileSelected = false;
		fContainer = null;
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName());
		fWorkspacePath = wProject.getLocation().toString();
	}
	
	/**
	 * 
	 * @param project
	 * @param id
	 */
	public TranscriptWizardPage(Project project, String id)
	{
		super(id);
		setTitle(id);
		
		fProject = project;
		fParticipants = new ArrayList<Participant>();
		fAudioFileSelected = false;
		fContainer = null;
		
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName());
		fWorkspacePath = wProject.getLocation().toString();
	}

	/**
	 * To be used by children that want to place widgets above those create by this page.
	 * @return
	 */
	protected Composite getfContainer()
	{
		return fContainer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
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
		
		@SuppressWarnings("unused")
		Label label = createLabel(fContainer, 
				MessagesClient.getString("wizards.pages.TranscriptWizardPage.name", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fName = createText(fContainer);
		
		label = createLabel(fContainer, MessagesClient.getString("wizards.pages.TranscriptWizardPage.date", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		fDate = createDate(fContainer);
		
		createLongLabel();
		createTable();
		
		Composite composite = createComposite();
		label = createLabel(composite, 
				MessagesClient.getString("wizards.pages.TranscriptWizardPage.audioFile", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		
		fAudioFile = new Label(composite, SWT.BORDER);
		fAudioFile.setText(""); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		fAudioFile.setLayoutData(gd);
		fAudioFile.addKeyListener(createAudioKeyListener());
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText(MessagesClient.getString("wizards.pages.TranscriptWizardPage.browse", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		button.addSelectionListener(createButtonListener());
		
		button = new Button(composite, SWT.PUSH);
		button.setText(MessagesClient.getString("wizards.pages.TranscriptWizardPage.clear", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		button.addSelectionListener(createClearListener());
		
		setControl(fContainer);
		setPageComplete(false);
	}

	/**
	 * Clears the audio file selection.
	 * @return
	 */
	private SelectionListener createClearListener()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				fAudioFile.setText(""); //$NON-NLS-1$
				fAudioFileSelected = false;
			}
		};
	}

	/**
	 * @return
	 */
	private KeyAdapter createAudioKeyListener()
	{
		return new KeyAdapter(){
			
			@Override
			public void keyReleased(KeyEvent event)
			{
				validate();
			}
		};
	}
	
	/**
	 * @param fContainer2
	 * @return
	 */
	private DateTime createDate(Composite container)
	{
		DateTime date = new DateTime(container, SWT.DATE | SWT.BORDER);
		return date;
	}

	/**
	 * 
	 * @param parent
	 * @param container
	 */
	protected void setContainer(Composite container)
	{
		fContainer = container;
	}

	/**
	 * Creates the participants button bar.
	 */
	private void createLongLabel()
	{
		Composite composite = new Composite(fContainer, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label label = createLabel(composite, 
				MessagesClient.getString("wizards.pages.TranscriptWizardPage.selectParticipants", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText(MessagesClient.getString("wizards.pages.TranscriptWizardPage.add", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AddParticipantWizard wizard = new AddParticipantWizard(fProject);
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				if(dialog.open() == Window.OK)
				{
					CommonNavigator view = (CommonNavigator) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().findView(QualyzerActivator.PROJECT_EXPLORER_VIEW_ID);
					view.getCommonViewer().refresh();
					fProject = PersistenceManager.getInstance().getProject(fProject.getName());
					fTable.removeAll();
					populateTable();
				}
			}
		});
	}

	/**
	 * @return
	 */
	private Composite createComposite()
	{
		GridLayout layout;
		GridData gd;
		Composite composite = new Composite(fContainer, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = COMPOSITE_COLS;
		composite.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		return composite;
	}

	/**
	 * 
	 */
	private void createTable()
	{
		GridData gd;
		fTable = new Table(fContainer, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		populateTable();
		fTable.addSelectionListener(createSelectionListener());
	}

	/**
	 * Opens the file chooser for the audio file.
	 * @return
	 */
	private SelectionAdapter createButtonListener()
	{
		return new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
					FileDialog dialog = new FileDialog(fContainer.getShell());
					dialog.setFilterPath(fWorkspacePath+AUDIO_PATH);
					dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"}); //$NON-NLS-1$
					dialog.setFilterNames(new String[]{MessagesClient.getString(
							"wizards.pages.TranscriptWizardPage.audioExt", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages")}); //$NON-NLS-1$
					
					String file = dialog.open();
					fAudioFile.setText(file);
					if(!file.isEmpty())
					{
						fAudioFileSelected = true;
						String errorMessage = getErrorMessage();
						if(errorMessage != null && errorMessage.equals(
								MessagesClient.getString("wizards.pages.TranscriptWizardPage.enterAudioName", "ca.mcgill.cs.swevo.qualyzer.wizards.pages.messages"))) //$NON-NLS-1$
						{
							setError(null);
						}
					}
			}
			
		};
	}

	/**
	 * Validates all input.
	 * @return
	 */
	protected SelectionAdapter createSelectionListener()
	{
		return new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				validate();
			}
		};
	}

	/**
	 * 
	 * @param container
	 * @param string
	 * @return
	 */
	private Label createLabel(Composite container, String string)
	{
		Label label = new Label(container, SWT.NULL);
		label.setText(string);
		return label;
	}
	
	private Text createText(Composite parent)
	{
		Text text = new Text(parent, SWT.BORDER);
		text.setText(""); //$NON-NLS-1$
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		text.setLayoutData(gd);
		text.addModifyListener(createKeyListener());
		
		return text;
	}

	/**
	 * @return
	 */
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
	 * Sets the error message and the page complete status.
	 * @param message
	 */
	protected void setError(String message)
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
	 * @return The date field. In the format MM/DD/YYYY.
	 */
	public String getDate()
	{
		return (fDate.getMonth()+1)+"/"+ fDate.getDay() +"/"+ fDate.getYear(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * @return The name field.
	 */
	public String getTranscriptName()
	{
		return fName.getText().trim();
	}
	
	/**
	 * 
	 * @return The audio file's absolute path.
	 */
	public String getAudioFile()
	{
		return fAudioFile.getText();
	}
	
	/**
	 * 
	 * @return The list of participants that were selected.
	 */
	public List<Participant> getParticipants()
	{
		buildParticipants();
		return fParticipants;
	}

	/**
	 * Build the participant list based on the table.
	 */
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
	 * Checks if an audio file with a matching name already exists in the audio folder.
	 * @param filename
	 * @return The absolute path of the audio file represented by the filename.
	 */
	protected String findAudioFile(String filename)
	{	
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fProject.getFolderName());
		
		String path = project.getLocation() + AUDIO_PATH + filename +".mp3"; //$NON-NLS-1$
		File file = new File(path);
		
		if(file.exists())
		{
			return path;
		}
		
		path =  project.getLocation() + AUDIO_PATH + filename +".wav"; //$NON-NLS-1$
		file = new File(path);
		
		return file.exists() ? path : ""; //$NON-NLS-1$
	}

	/**
	 * 
	 */
	protected void validate()
	{
		TranscriptValidator lValidator = new TranscriptValidator(fName.getText().trim(), fProject, 
				fTable.getSelectionCount(), fAudioFile.getText());
		
		if(!lValidator.isValid())
		{
			setError(lValidator.getErrorMessage());
		}
		else
		{
			setError(null);
		}
	}

	/**
	 * 
	 * @return
	 */
	public Text getNameText()
	{
		return fName;
	}
	
	/**
	 * 
	 * @return
	 */
	public Table getTable()
	{
		return fTable;
	}
}
