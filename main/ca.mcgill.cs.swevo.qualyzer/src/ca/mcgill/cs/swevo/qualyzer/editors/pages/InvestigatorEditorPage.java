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
/**
 * 
 */
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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

import ca.mcgill.cs.swevo.qualyzer.editors.InvestigatorFormEditor;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.InvestigatorEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.InvestigatorListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.MemoListener;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.model.validation.InvestigatorValidator;
import ca.mcgill.cs.swevo.qualyzer.model.validation.StringLengthValidator;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The main page of the Investigator editor.
 *
 */
public class InvestigatorEditorPage extends FormPage implements ProjectListener, InvestigatorListener, MemoListener, 
	TranscriptListener
{

	private static final String DELIMITER = ":"; //$NON-NLS-1$
	private static final String INVESTIGATOR = MessagesClient.getString(
			"editors.pages.InvestigatorEditorPage.investigator", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"); //$NON-NLS-1$
	private Text fNickname;
	private Text fFullname;
	private Text fInstitution;
	private FormToolkit fToolkit;
	
	private Investigator fInvestigator;
	private boolean fIsDirty;
	private ScrolledForm fForm;
	private FormText fTranscriptText;
	private FormText fMemoText;
	/**
	 * @param editor
	 * @param investigator 
	 */
	public InvestigatorEditorPage(FormEditor editor, Investigator investigator)
	{
		super(editor, INVESTIGATOR, INVESTIGATOR);
		fInvestigator = investigator;
		fIsDirty = false;
		
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		Project project = fInvestigator.getProject();
		listenerManager.registerProjectListener(project, this);
		listenerManager.registerInvestigatorListener(project, this);
		listenerManager.registerMemoListener(project, this);
		listenerManager.registerTranscriptListener(project, this);
	}

	@Override
	public void createFormContent(IManagedForm managed)
	{
		fForm = managed.getForm();
		fToolkit = managed.getToolkit();
		fForm.setText(INVESTIGATOR);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		Composite body = fForm.getBody();
		body.setLayout(layout);
		
		@SuppressWarnings("unused")
		Label label = fToolkit.createLabel(body, 
				MessagesClient.getString("editors.pages.InvestigatorEditorPage.nickname", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		fNickname = createText(fInvestigator.getNickName(), body);
		
		label = fToolkit.createLabel(body, 
				MessagesClient.getString("editors.pages.InvestigatorEditorPage.fullName", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		fFullname = createText(fInvestigator.getFullName(), body);
		fFullname.addKeyListener(createStringLengthValidator(fForm, 
				MessagesClient.getString("editors.pages.InvestigatorEditorPage.fullName", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"), fFullname)); //$NON-NLS-1$
		
		fNickname.addKeyListener(createKeyAdapter(fForm));

		label = fToolkit.createLabel(body, 
				MessagesClient.getString("editors.pages.InvestigatorEditorPage.institution", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		fInstitution = createText(fInvestigator.getInstitution(), body);
		fInstitution.addKeyListener(createStringLengthValidator(fForm, 
				MessagesClient.getString("editors.pages.InvestigatorEditorPage.institution", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"), fInstitution)); //$NON-NLS-1$
		
		
		createCodedSection(body);
		
		createMemoSection(fForm, body);
	
		fToolkit.paintBordersFor(body);
	}
	
	/**
	 * @param form 
	 * @return
	 */
	private KeyAdapter createKeyAdapter(final ScrolledForm form)
	{
		return new KeyAdapter(){
			private ScrolledForm fForm = form;
			
			@Override
			public void keyReleased(KeyEvent event)
			{
				InvestigatorValidator lValidator = new InvestigatorValidator(fNickname.getText().trim(),
						fInvestigator.getNickName(), fInvestigator.getProject());
				
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
	
	private KeyAdapter createStringLengthValidator(final ScrolledForm form, final String pLabel, final Text pText)
	{
		return new KeyAdapter(){
						
			@Override
			public void keyReleased(KeyEvent event)
			{
				StringLengthValidator lValidator = new StringLengthValidator(pLabel, pText.getText().trim());
				
				if(!lValidator.isValid())
				{
					form.setMessage(lValidator.getErrorMessage(), IMessageProvider.ERROR);
					notDirty();
				}
				else
				{
					form.setMessage(null, IMessageProvider.NONE);
				}
			}
		};
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void createMemoSection(final ScrolledForm form, Composite body)
	{
		TableWrapData td;
		Section section;
		GridLayout grid;
		section = fToolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText(MessagesClient.getString("editors.pages.InvestigatorEditorPage.memos", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		section.addExpansionListener(createExpansionListener(form));
		Composite sectionClient = fToolkit.createComposite(section);
		grid = new GridLayout();
		grid.numColumns = 1;
		sectionClient.setLayout(grid);
		fMemoText = fToolkit.createFormText(sectionClient, true);
		fMemoText.addHyperlinkListener(openMemoListener());
		buildMemos();
		section.setClient(sectionClient);
	}

	
	/**
	 * Sets the text of the FormText widget that links to all the memos.
	 * @param sectionClient
	 * @param toolkit
	 */
	private void buildMemos()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(FormTextConstants.FORM_START);
		buf.append(FormTextConstants.PARAGRAPH_START);
		
		for(Memo memo : fInvestigator.getProject().getMemos())
		{
			if(fInvestigator.equals(memo.getAuthor()))
			{
				buf.append(FormTextConstants.LINK_START_HEAD + 
						MessagesClient.getString("editos.pages.InvestigatorEditorPage.memoKey", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages") + //$NON-NLS-1$
						memo.getName() + FormTextConstants.LINK_START_TAIL);
				buf.append(memo.getName());
				buf.append(FormTextConstants.LINK_END + FormTextConstants.LINE_BREAK);
			}
			else
			{
				Memo lMemo = Facade.getInstance().forceMemoLoad(memo);
				for(Fragment fragment : lMemo.getFragments().values())
				{
					boolean found = false;
					for(CodeEntry entry : fragment.getCodeEntries())
					{
						if(fInvestigator.equals(entry.getInvestigator()))
						{
							buf.append(FormTextConstants.LINK_START_HEAD + 
									MessagesClient.getString("editos.pages.InvestigatorEditorPage.memoKey", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages") + //$NON-NLS-1$
									memo.getName() + FormTextConstants.LINK_START_TAIL);
							buf.append(memo.getName());
							buf.append(FormTextConstants.LINK_END + FormTextConstants.LINE_BREAK);
							found = true;
							break;
						}
					}
					
					if(found)
					{
						break;
					}
				}
			}
		}
		
		buf.append(FormTextConstants.PARAGRAPH_END);
		buf.append(FormTextConstants.FORM_END);
		
		fMemoText.setText(buf.toString(), true, false);
		
		fForm.reflow(true);
	}

	/**
	 * Parses the href of the link to determine which memo to open and then opens it.
	 * @return
	 */
	private HyperlinkAdapter openMemoListener()
	{
		return new HyperlinkAdapter(){
			
			@Override
			public void linkActivated(HyperlinkEvent e)
			{
				String key = (String) e.getHref();
				String[] strings = key.split(DELIMITER);
				for(Memo memo : fInvestigator.getProject().getMemos())
				{
					if(memo.getName().equals(strings[1]))
					{
						ResourcesUtil.openEditor(getSite().getPage(), memo);
					}
				}	
			}
		};
	}

	/**
	 * @param form
	 * @param toolkit
	 * @param body
	 */
	private void createCodedSection(Composite body)
	{
		TableWrapData td;
		Section section;
		section = fToolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		section.setLayoutData(td);
		section.setText(MessagesClient.getString("editors.pages.InvestigatorEditorPage.codedTranscripts", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages")); //$NON-NLS-1$
		section.addExpansionListener(createExpansionListener(fForm));
		Composite sectionClient = fToolkit.createComposite(section);
		sectionClient.setLayout(new TableWrapLayout());
		fTranscriptText = fToolkit.createFormText(sectionClient, true);
		fTranscriptText.addHyperlinkListener(openTranscriptListener());
		buildTranscripts();
		section.setClient(sectionClient);
	}
	
	/**
	 * Sets the contents of the FormText widget that displays all the transcripts.
	 */
	private void buildTranscripts()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(FormTextConstants.FORM_START);
		buf.append(FormTextConstants.PARAGRAPH_START);
		
		for(Transcript transcript : fInvestigator.getProject().getTranscripts())
		{
			Transcript lTranscript = Facade.getInstance().forceTranscriptLoad(transcript);
			for(Fragment fragment : lTranscript.getFragments().values())
			{
				boolean found = false;
				for(CodeEntry entry : fragment.getCodeEntries())
				{
					if(fInvestigator.equals(entry.getInvestigator()))
					{
						buf.append(FormTextConstants.LINK_START_HEAD + 
								MessagesClient.getString("editos.pages.InvestigatorEditorPage.transcriptKey", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages") +  //$NON-NLS-1$
								transcript.getName() + FormTextConstants.LINK_START_TAIL);
						buf.append(transcript.getName());
						buf.append(FormTextConstants.LINK_END + FormTextConstants.LINE_BREAK);
						found = true;
						break;
					}
				}
				
				if(found)
				{
					break;
				}
			}
		}
		
		buf.append(FormTextConstants.PARAGRAPH_END);
		buf.append(FormTextConstants.FORM_END);
		
		fTranscriptText.setText(buf.toString(), true, false);
		
		fForm.reflow(true);
	}

	/**
	 * Parses the href to determine which transcript to open and then does so.
	 * @param transcript
	 * @return
	 */
	private HyperlinkAdapter openTranscriptListener()
	{
		return new HyperlinkAdapter(){
			
			@Override
			public void linkActivated(HyperlinkEvent e)
			{
				String key = (String) e.getHref();
				String[] strings = key.split(DELIMITER);
				for(Transcript transcript : fInvestigator.getProject().getTranscripts())
				{
					if(transcript.getName().equals(strings[1]))
					{
						ResourcesUtil.openEditor(getSite().getPage(), transcript);
						break;
					}
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
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(td);
		text.addKeyListener(createKeyListener());
		
		return text;
	}
	
	@Override
	public boolean isDirty()
	{
		return fIsDirty;
	}
	
	/**
	 * Get the Nickname that was entered for this Investigator.
	 * @return The Nickname field.
	 */
	public String getNickname()
	{
		return fNickname.getText().trim();
	}
	
	/**
	 * Get the full name that was entered for this Investigator.
	 * @return The full name field.
	 */
	public String getFullname()
	{
		return fFullname.getText().trim();
	}
	
	/**
	 * Get the Institution that was entered for this Investigator.
	 * @return The Institution field.
	 */
	public String getInstitution()
	{
		return fInstitution.getText().trim();
	}
	
	/**
	 * Handles updates to the dirty state as values change.
	 * @return
	 */
	private KeyListener createKeyListener()
	{
		return new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e){}

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
				boolean hasChanged = false;
				if(!fNickname.getText().trim().equals(fInvestigator.getNickName()))
				{
					hasChanged = true;
				}
				else if(!fFullname.getText().trim().equals(fInvestigator.getFullName()))
				{
					hasChanged = true;
				}
				else if(!fInstitution.getText().trim().equals(fInvestigator.getInstitution()))
				{
					hasChanged = true;
				}
				return hasChanged;
			}
			
		};
	}

	/**
	 * Set dirty to false.
	 */
	public void notDirty()
	{
		fIsDirty = false;
		getEditor().editorDirtyStateChanged();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectListener#projectChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Project, ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			getEditor().close(false);
		}
		else if(cType == ChangeType.RENAME)
		{
			ResourcesUtil.closeEditor(getSite().getPage(), getEditorInput().getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose()
	{
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		Project project = fInvestigator.getProject();
		listenerManager.unregisterProjectListener(project, this);
		listenerManager.unregisterInvestigatorListener(project, this);
		listenerManager.unregisterMemoListener(project, this);
		listenerManager.unregisterTranscriptListener(project, this);
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.InvestigatorListener#investigatorChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Investigator[], 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void investigatorChanged(ChangeType cType, Investigator[] investigators, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Investigator investigator : investigators)
			{
				if(fInvestigator.equals(investigator))
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
	 * ca.mcgill.cs.swevo.qualyzer.model.Memo[], ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		Project project;
		if(ChangeType.DELETE == cType)
		{
			project = PersistenceManager.getInstance().getProject(fInvestigator.getProject().getName());
		}
		else
		{
			project = memos[0].getProject();
		}
		
		for(Investigator investigator : project.getInvestigators())
		{
			if(fInvestigator.getPersistenceId().equals(investigator.getPersistenceId()))
			{
				setInput(new InvestigatorEditorInput(investigator));
				fInvestigator = investigator;
				((InvestigatorFormEditor) getEditor()).setInvestigator(fInvestigator);
				break;
			}
		}
		
		buildMemos();
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.TranscriptListener#transcriptChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Transcript[], 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void transcriptChanged(ChangeType cType, Transcript[] transcripts, Facade facade)
	{
		if(cType != ChangeType.ADD)
		{
			Project project;
			if(ChangeType.DELETE == cType)
			{
				project = PersistenceManager.getInstance().getProject(fInvestigator.getProject().getName());
			}
			else
			{
				project = transcripts[0].getProject();
			}
			
			for(Investigator investigator : project.getInvestigators())
			{
				if(fInvestigator.getPersistenceId().equals(investigator.getPersistenceId()))
				{
					setInput(new InvestigatorEditorInput(investigator));
					fInvestigator = investigator;
					((InvestigatorFormEditor) getEditor()).setInvestigator(fInvestigator);
					break;
				}
			}
			
			buildTranscripts();
		}
	}
}
