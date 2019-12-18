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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;

/**
 * Dialog for editing the properties of a transcript.
 */
public class TranscriptPropertiesDialog extends TitleAreaDialog
{
	
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final String ADD_IMG = "ADD_IMG"; //$NON-NLS-1$
	private static final String REMOVE_IMG = "REMOVE_IMG"; //$NON-NLS-1$
	private static final int COLS = 4;
	private static final String TRANSCRIPT = File.separator+"transcripts"+File.separator; //$NON-NLS-1$
	
	private final String fProjectName;
	
	private ImageRegistry fRegistry;
	private Transcript fTranscript;
	private DateTime fDate;
	private String fAudioPath;
	private List<Participant> fParticipants;
	private Table fTable;
	private String fDateS;
	private Label fAudioLabel;
	
	/**
	 * Constructor.
	 * @param shell
	 * @param transcript
	 */
	public TranscriptPropertiesDialog(Shell shell, Transcript transcript)
	{
		super(shell);
		fTranscript = transcript;
		fParticipants = new ArrayList<Participant>();
		fProjectName = fTranscript.getProject().getName();
		fAudioPath = ""; //$NON-NLS-1$
		fRegistry = QualyzerActivator.getDefault().getImageRegistry();
		addImage(ADD_IMG, QualyzerActivator.PLUGIN_ID, "icons/add_obj.gif"); //$NON-NLS-1$
		addImage(REMOVE_IMG, QualyzerActivator.PLUGIN_ID, "icons/remove_obj.gif"); //$NON-NLS-1$
		
	}
	
	/**
	 * Add an image to the registry.
	 * @param key
	 * @param pluginID
	 * @param path
	 */
	private void addImage(String key, String pluginID, String path)
	{
		String fullKey = computeKey(key, pluginID);
		ImageDescriptor descriptor = fRegistry.getDescriptor(fullKey);
		if(descriptor == null)
		{
			fRegistry.put(fullKey, AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, path));
		}
	}
	
	private String computeKey(String key, String pluginID)
	{
		return pluginID + "_" + key; //$NON-NLS-1$
	}
	
	/**
	 * Get an image from the registry.
	 * @param key
	 * @param pluginID
	 * @return
	 */
	private Image getImage(String key, String pluginID)
	{
		return fRegistry.get(computeKey(key, pluginID));
	}

	@Override
	public void create()
	{
		super.create();
		setTitle(MessagesClient.getString("dialogs.TranscriptPropertiesDialog.properties", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
	}
	
	@Override
	public Control createDialogArea(Composite parent)
	{
		GridLayout layout = new GridLayout(2, false);
		Composite body =  new Composite(parent, SWT.NULL);
		body.setLayout(layout);
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label label = createLabel(body, MessagesClient.getString("dialogs.TranscriptPropertiesDialog.name", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		label = new Label(body, SWT.BORDER);
		label.setText(fTranscript.getName());
		label.setLayoutData(createTextGridData());
		
		label = createLabel(body, MessagesClient.getString("dialogs.TranscriptPropertiesDialog.path", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		label = new Label(body, SWT.BORDER);
		label.setText(fProjectName + TRANSCRIPT + fTranscript.getFileName());
		label.setLayoutData(createTextGridData());
		
		label = createLabel(body, MessagesClient.getString("dialogs.TranscriptPropertiesDialog.date", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		fDate = createDate(fTranscript.getDate(), body);
		
		Composite composite = createComposite(body);
		Button button;
		createParticipantButtonBar(composite);
		
		fTable = new Table(body, SWT.MULTI);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		fTable.setLayoutData(gd);
		buildParticipants();
		
		composite = createComposite(body);
		label = createLabel(composite, 
				MessagesClient.getString("dialogs.TranscriptPropertiesDialog.audioPath", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		
		if(fTranscript.getAudioFile() != null)
		{
			fAudioPath = fProjectName + fTranscript.getAudioFile().getRelativePath();
		}
		fAudioLabel = new Label(composite, SWT.BORDER);
		fAudioLabel.setText(fAudioPath);
		fAudioLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button = new Button(composite, SWT.PUSH);
		button.setText(MessagesClient.getString("dialogs.TranscriptPropertiesDialog.browse", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		button.addSelectionListener(createSelectionAdapter());
		
		button = new Button(composite, SWT.PUSH);
		button.setText(MessagesClient.getString("dialogs.TranscriptPropertiesDialog.clear", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		button.addSelectionListener(createClearListener());
				
		return parent;
	}

	/**
	 * Clears the audio selection.
	 * @return
	 */
	private SelectionListener createClearListener()
	{
		return new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				fAudioLabel.setText(""); //$NON-NLS-1$
				fAudioPath = ""; //$NON-NLS-1$
				setMessage(null);
			}
		};
	}

	/**
	 * @param date
	 * @param parent
	 * @return
	 */
	private DateTime createDate(String date, Composite parent)
	{
		DateTime datetime = new DateTime(parent, SWT.BORDER | SWT.DATE);
		String[] info = date.split(SLASH);
		datetime.setDate(Integer.parseInt(info[2]), Integer.parseInt(info[0])-1, Integer.parseInt(info[1]));
		datetime.setLayoutData(createTextGridData());
		
		return datetime;
	}

	/**
	 * @param composite
	 */
	private void createParticipantButtonBar(Composite composite)
	{
		Label label;
		label = createLabel(composite, 
				MessagesClient.getString("dialogs.TranscriptPropertiesDialog.participants", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		label.setLayoutData(createTextGridData());
		Button button = new Button(composite, SWT.PUSH);
		button.setImage(getImage(ADD_IMG, QualyzerActivator.PLUGIN_ID));
		button.addSelectionListener(createAddListener());
		button = new Button(composite, SWT.PUSH);
		button.setImage(getImage(REMOVE_IMG, QualyzerActivator.PLUGIN_ID));
		button.addSelectionListener(createRemoveListener());
	}

	/**
	 * Removes a Participant from the transcript.
	 * @return
	 */
	private SelectionAdapter createRemoveListener()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				while(fTable.getSelectionCount() > 0)
				{
					fTable.remove(fTable.getSelectionIndex());
				}
				
				if(fTable.getItemCount() <= 0)
				{
					setErrorMessage(MessagesClient.getString("dialogs.TranscriptPropertiesDialog.atLeastOne", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}
			}
		};
	}

	/**
	 * Adds a participant to the transcript.
	 * @return
	 */
	private SelectionAdapter createAddListener()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				List<Participant> list = fTranscript.getProject().getParticipants();
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider());
				String[] names = new String[list.size()];
				for(int i = 0; i < list.size(); i++)
				{
					names[i] = list.get(i).getParticipantId();
				}
				dialog.setElements(names);
				dialog.setTitle(MessagesClient.getString("dialogs.TranscriptPropertiesDialog.addWhich", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
				dialog.open();
				Object[] result = dialog.getResult();
				for(Object s : result)
				{
					if(notInTable(s))
					{
						TableItem item = new TableItem(fTable, SWT.NULL);
						item.setText((String)s);
					}
				}
				
				if(fTable.getItemCount() > 0)
				{
					setErrorMessage(null);
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}
		};
	}

	/**
	 * Checks to see if Object s is in the table.
	 * @param s
	 * @return
	 */
	protected boolean notInTable(Object s)
	{
		for(TableItem item : fTable.getItems())
		{
			if(item.getText().equals(s))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Build the list of participants from the table.
	 */
	private void buildParticipants()
	{
		Transcript transcript = Facade.getInstance().forceTranscriptLoad(fTranscript);
		
		for(Participant p : transcript.getParticipants())
		{
			TableItem item = new TableItem(fTable, SWT.NULL);
			item.setText(p.getParticipantId());
		}		
	}

	/**
	 * Opens the file chooser for the audio file.
	 * @return
	 */
	private SelectionAdapter createSelectionAdapter()
	{
		return new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterExtensions(new String[]{"*.mp3;*.wav"}); //$NON-NLS-1$
				dialog.setFilterNames(
						new String[]{MessagesClient.getString("dialogs.TranscriptPropertiesDialog.audioExt", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")}); //$NON-NLS-1$
				
				fAudioPath = dialog.open();
				if(fAudioPath != null)
				{
					setMessage(MessagesClient.getString("dialogs.TranscriptPropertiesDialog.warning1", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages") + //$NON-NLS-1$
							MessagesClient.getString("dialogs.TranscriptPropertiesDialog.warning2", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"), //$NON-NLS-1$
							IMessageProvider.WARNING); 
					fAudioLabel.setText(fAudioPath);
				}
			}
			
		};
	}

	/**
	 * @param parent
	 * @return
	 */
	private Composite createComposite(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = COLS;
		composite.setLayout(layout);
		GridData gd = createTextGridData();
		gd.horizontalSpan = 2;
		composite.setLayoutData(gd);
		return composite;
	}

	/**
	 * @param parent
	 */
	private Label createLabel(Composite parent, String text)
	{
		Label label = new Label(parent, SWT.NULL);
		label.setText(text);
		
		return label;
	}

	/**
	 * @return
	 */
	private GridData createTextGridData()
	{
		GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		return gd;
	}
	
	/**
	 * Get the Transcript being edited by this dialog.
	 * @return
	 */
	public Transcript getTranscript()
	{
		return fTranscript;
	}
	
	/**
	 * Get the date that was entered into the dialog.
	 * @return
	 */
	public String getDate()
	{
		return fDateS;
	}
	
	@Override
	public void okPressed()
	{
		save();
		
		if(fAudioPath.startsWith(fProjectName+File.separator+"audio"+File.separator)) //$NON-NLS-1$
		{
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectName);
			
			fAudioPath = project.getLocation() + fAudioPath.substring(fAudioPath.indexOf(File.separatorChar));
		}

		File file = new File(fAudioPath);
		
		if(fAudioPath.isEmpty() || file.exists())
		{
			super.okPressed();
		}
		else
		{
			MessageDialog.openError(getShell(), 
					MessagesClient.getString("dialogs.TranscriptPropertiesDialog.fileError", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages"),  //$NON-NLS-1$
					MessagesClient.getString("dialogs.TranscriptPropertiesDialog.doesNotExist", "ca.mcgill.cs.swevo.qualyzer.dialogs.messages")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Get the selected audio file path.
	 * @return
	 */
	public String getAudioFile()
	{
		return fAudioPath;
	}
	
	private void save()
	{
		fDateS = (fDate.getMonth()+1) +SLASH+ fDate.getDay() +SLASH+ fDate.getYear();
		
		fParticipants = new ArrayList<Participant>();
		
		for(TableItem item : fTable.getItems())
		{
			for(Participant participant : fTranscript.getProject().getParticipants())
			{
				if(item.getText().equals(participant.getParticipantId()))
				{
					fParticipants.add(participant);
				}
			}
		}
	}
	
	/**
	 * Get the list of participants in this transcript.
	 * @return
	 */
	public List<Participant> getParticipants()
	{
		return fParticipants;
	}
	
	/**
	 * 
	 * @return
	 */
	public DateTime getDateWidget()
	{
		return fDate;
	}
	
}
