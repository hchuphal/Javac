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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ca.mcgill.cs.swevo.qualyzer.editors.ParticipantFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.ParticipantEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.MemoListener;
import ca.mcgill.cs.swevo.qualyzer.model.Participant;
import ca.mcgill.cs.swevo.qualyzer.model.ParticipantListener;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.model.validation.ParticipantValidator;
import ca.mcgill.cs.swevo.qualyzer.model.validation.StringLengthValidator;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The form used to edit participant data.
 */
public class ParticipantEditorPage extends FormPage implements ProjectListener, TranscriptListener,
	ParticipantListener, MemoListener
{
	
	private static final String DELIMITER = ":"; //$NON-NLS-1$
	private static final String LABEL_PARTICIPANT_NAME = MessagesClient.getString(
			"editors.pages.ParticipantEditorPage.participantName", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"); //$NON-NLS-1$
	private static final String LABEL_PARTICIPANT_ID = MessagesClient.getString(
			"editors.pages.ParticipantEditorPage.participantId", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"); //$NON-NLS-1$
	private static final String LABEL_PARTICIPANT = MessagesClient.getString(
			"editors.pages.ParticipantEditorPage.participant", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"); //$NON-NLS-1$
	
	private Participant fParticipant;
	private Text fID;
	private Text fFullname;
	private boolean fIsDirty;
	private FormToolkit fToolkit;
	private FormText fTranscriptText;
	private FormText fMemoText;
	private ScrolledForm fForm;

	/**
	 * Construct a new participant page, the only one for the editor.
	 * @param editor The form editor.
	 * @param participant The participant to edit.
	 */
	public ParticipantEditorPage(FormEditor editor, Participant participant)
	{
		super(editor, LABEL_PARTICIPANT, LABEL_PARTICIPANT);
		fParticipant = participant;
		fIsDirty = false;
		
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerProjectListener(participant.getProject(), this);
		listenerManager.registerTranscriptListener(participant.getProject(), this);
		listenerManager.registerParticipantListener(participant.getProject(), this);
		listenerManager.registerMemoListener(participant.getProject(), this);
	}
	
	@Override
	public void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		fToolkit = managedForm.getToolkit();
		Composite body = fForm.getBody();
		fForm.setText(LABEL_PARTICIPANT);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		body.setLayout(layout);
		
		fToolkit.createLabel(body, LABEL_PARTICIPANT_ID);
		fID = createText(fParticipant.getParticipantId(), body);
		fID.addKeyListener(createKeyAdapter());
		
		fToolkit.createLabel(body, LABEL_PARTICIPANT_NAME);
		fFullname = createText(fParticipant.getFullName(), body);
		fFullname.addKeyListener(createStringLengthValidator(
				MessagesClient.getString("editors.pages.ParticipantEditorPage.participantName", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"), fFullname)); //$NON-NLS-1$
	
		buildTranscriptSection(body);
		buildMemoSection(body);
		
		fToolkit.paintBordersFor(body);
	}

	/**
	 * Validates input.
	 * @return
	 */
	private KeyAdapter createKeyAdapter()
	{
		return new KeyAdapter(){
			
			@Override
			public void keyReleased(KeyEvent event)
			{
				ParticipantValidator lValidator = new ParticipantValidator(fID.getText().trim(), 
						fParticipant.getParticipantId(), fParticipant.getProject());
				
				if(!lValidator.isValid())
				{
					fForm.setMessage(lValidator.getErrorMessage(), IMessageProvider.ERROR); 
					notDirty();
				}
				else
				{
					fForm.setMessage(null, IMessageProvider.NONE);
				}
			}
			
		};
	}
	
	private KeyAdapter createStringLengthValidator(final String pLabel, final Text pText)
	{
		return new KeyAdapter(){
						
			@Override
			public void keyReleased(KeyEvent event)
			{
				StringLengthValidator lValidator = new StringLengthValidator(pLabel, pText.getText().trim());
				
				if(!lValidator.isValid())
				{
					fForm.setMessage(lValidator.getErrorMessage(), IMessageProvider.ERROR);
					notDirty();
				}
				else
				{
					fForm.setMessage(null, IMessageProvider.NONE);
				}
			}
		};
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void buildTranscriptSection(Composite body)
	{
		TableWrapData td;
		Section section = fToolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.addExpansionListener(createExpansionListener(fForm));
		section.setText(MessagesClient.getString("editors.pages.ParticipantEditorPage.transcripts", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		Composite sectionClient = fToolkit.createComposite(section);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		sectionClient.setLayout(new TableWrapLayout());
		fTranscriptText = fToolkit.createFormText(sectionClient, true);
		fTranscriptText.addHyperlinkListener(createHyperlinkListener());
		fTranscriptText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		buildInterviews();
		sectionClient.setLayoutData(td);
		section.setClient(sectionClient);
	}
	
	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void buildMemoSection(Composite body)
	{
		TableWrapData td;
		Section section = fToolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.addExpansionListener(createExpansionListener(fForm));
		section.setText(MessagesClient.getString("editors.pages.ParticipantEditorPage.memos", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		Composite sectionClient = fToolkit.createComposite(section);
		sectionClient.setLayout(new TableWrapLayout());
		fMemoText = fToolkit.createFormText(sectionClient, true);
		fMemoText.addHyperlinkListener(createHyperlinkListener());
		fMemoText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		buildMemos();
		sectionClient.setLayoutData(td);
		section.setClient(sectionClient);
	}

	/**
	 * Sets the contents of the FormText Widget that displays the list of memos and links to them.
	 */
	private void buildMemos()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(FormTextConstants.FORM_START);
		buf.append(FormTextConstants.PARAGRAPH_START);
		
		for(Memo memo : fParticipant.getProject().getMemos())		
		{
			Memo loadedMemo = Facade.getInstance().forceMemoLoad(memo);
			if(loadedMemo.getParticipants().contains(fParticipant))
			{
				buf.append(FormTextConstants.LINK_START_HEAD + 
						MessagesClient.getString("editors.pages.ParticipantEditorPage.memoKey", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages") + //$NON-NLS-1$
						memo.getName() + FormTextConstants.LINK_START_TAIL); 
				buf.append(memo.getName());
				buf.append(FormTextConstants.LINK_END + FormTextConstants.LINE_BREAK);
			}
		}
		
		buf.append(FormTextConstants.PARAGRAPH_END);
		buf.append(FormTextConstants.FORM_END);
		
		fMemoText.setText(buf.toString(), true, false);
		
		fForm.reflow(true);
	}

	/**
	 * Sets the contents of the FormText widget that displays the list of transcripts and links to them.
	 */
	private void buildInterviews()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(FormTextConstants.FORM_START);
		buf.append(FormTextConstants.PARAGRAPH_START);
		
		for(Transcript transcript : fParticipant.getProject().getTranscripts())		
		{
			Transcript loadedTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			if(loadedTranscript.getParticipants().contains(fParticipant))
			{
				buf.append(FormTextConstants.LINK_START_HEAD + 
						MessagesClient.getString("editors.pages.ParticipantEditorPage.transcriptKey", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages") + //$NON-NLS-1$
						transcript.getName() + FormTextConstants.LINK_START_TAIL); 
				buf.append(transcript.getName());
				buf.append(FormTextConstants.LINK_END + FormTextConstants.LINE_BREAK);
			}
		}
		
		buf.append(FormTextConstants.PARAGRAPH_END);
		buf.append(FormTextConstants.FORM_END);
		
		fTranscriptText.setText(buf.toString(), true, false);
		
		fForm.reflow(true);
	}

	/**
	 * Extracts the type and name of the document to open. Then does so.
	 * @return
	 */
	private HyperlinkAdapter createHyperlinkListener()
	{
		return new HyperlinkAdapter(){

			@Override
			public void linkActivated(HyperlinkEvent e)
			{
				String key = (String) e.getHref();
				String[] strings = key.split(DELIMITER);
				IAnnotatedDocument document = null;
				if(strings[0].equals(MessagesClient.getString(
						"editors.pages.ParticipantEditorPage.transcript", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"))) //$NON-NLS-1$
				{
					for(Transcript transcript : fParticipant.getProject().getTranscripts())
					{
						if(transcript.getName().equals(strings[1]))
						{
							document = transcript;
							break;
						}
					}
				}
				else if(strings[0].equals(MessagesClient.getString("editors.pages.ParticipantEditorPage.memo", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"))) //$NON-NLS-1$
				{
					for(Memo memo : fParticipant.getProject().getMemos())
					{
						if(memo.getName().equals(strings[1]))
						{
							document = memo;
							break;
						}
					}
				}
				
				if(document != null)
				{
					IWorkbenchPage page = getSite().getPage();
					ResourcesUtil.openEditor(page, document);
				}
			}


		};
	}

	/**
	 * @param form
	 * @return
	 */
	private ExpansionAdapter createExpansionListener(final ScrolledForm form)
	{
		return new ExpansionAdapter(){
			public void expansionStateChanged(ExpansionEvent e)
			{
				form.reflow(true);
			}
		};
	}
	
	private Text createText(String data, Composite parent)
	{
		Text text = fToolkit.createText(parent, data);
		TableWrapData tableData = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(tableData);
		text.addKeyListener(createKeyListener());
		
		return text;
	}
	
	/**
	 * Get the Participant Id that was entered.
	 * @return The Participant Id
	 */
	public String getId()
	{
		return fID.getText().trim();
	}
	
	/**
	 * Get the Participant's full name.
	 * @return The full name field.
	 */
	public String getFullname()
	{
		return fFullname.getText().trim();
	}
	
	/**
	 * Updates the dirty state.
	 * @return
	 */
	private KeyListener createKeyListener()
	{
		return new KeyAdapter() 
		{	
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!fIsDirty && fieldHasChanged())
				{
					fIsDirty = true;
					getEditor().editorDirtyStateChanged();
				}
			}

			private boolean fieldHasChanged()
			{
				if(!fID.getText().trim().equals(fParticipant.getParticipantId()))
				{
					return true;
				}
				else if(!fFullname.getText().trim().equals(fParticipant.getFullName()))
				{
					return true;
				}
				return false;
			}
		};
	}
	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}
	
	/**
	 * Clears the dirty flag.
	 */
	public void notDirty()
	{
		fIsDirty = false;
		getEditor().editorDirtyStateChanged();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectListener#projectChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Project, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(ChangeType.DELETE == cType)
		{
			getEditor().close(false);
		}
		else if(ChangeType.RENAME == cType)
		{
			ResourcesUtil.closeEditor(getSite().getPage(), getEditorInput().getName());
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Transcript[], 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		Project project;
		if(ChangeType.DELETE == cType)
		{
			project = PersistenceManager.getInstance().getProject(fParticipant.getProject().getName());
		}
		else
		{
			project = transcripts[0].getProject();
		}
		
		for(Participant participant : project.getParticipants())
		{
			if(fParticipant.getPersistenceId().equals(participant.getPersistenceId()))
			{
				setInput(new ParticipantEditorInput(participant));
				fParticipant = participant;
				((ParticipantFormEditor) getEditor()).setParticipant(fParticipant);
				break;
			}
		}
		
		buildInterviews();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose()
	{
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.unregisterProjectListener(fParticipant.getProject(), this);
		listenerManager.unregisterTranscriptListener(fParticipant.getProject(), this);
		listenerManager.unregisterParticipantListener(fParticipant.getProject(), this);
		listenerManager.unregisterMemoListener(fParticipant.getProject(), this);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ParticipantListener#participantChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Participant[], 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void participantChanged(ChangeType cType, Participant[] participants, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Participant participant : participants)
			{
				if(fParticipant.equals(participant))
				{
					getEditor().close(false);
					break;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.MemoListener#memoChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType,
	 *  ca.mcgill.cs.swevo.qualyzer.model.Memo[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		Project project;
		if(ChangeType.DELETE == cType)
		{
			project = PersistenceManager.getInstance().getProject(fParticipant.getProject().getName());
		}
		else
		{
			project = memos[0].getProject();
		}
		
		for(Participant participant : project.getParticipants())
		{
			if(fParticipant.getPersistenceId().equals(participant.getPersistenceId()))
			{
				setInput(new ParticipantEditorInput(participant));
				fParticipant = participant;
				((ParticipantFormEditor) getEditor()).setParticipant(fParticipant);
				break;
			}
		}
		
		buildMemos();
		
	}
}
