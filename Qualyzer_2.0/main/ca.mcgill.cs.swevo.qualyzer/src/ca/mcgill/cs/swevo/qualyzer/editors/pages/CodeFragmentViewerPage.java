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
package ca.mcgill.cs.swevo.qualyzer.editors.pages;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
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

import ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.CodeListener;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.PersistenceManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.FragmentUtil;

/**
 * Displays all the Fragments that are associated with a given code.
 */
public class CodeFragmentViewerPage extends FormPage implements ProjectListener, CodeListener
{
	
	private static final String GREATER_CODE = "&gt;"; //$NON-NLS-1$
	private static final String GREATER = ">"; //$NON-NLS-1$
	private static final String LESS_CODE = "&lt;"; //$NON-NLS-1$
	private static final String LESS = "<"; //$NON-NLS-1$
	private static final String AMP = "&";
	private static final String AMP_CODE = "&amp;";
	private static final String VIEW_FRAGMENTS = MessagesClient.getString(
			"editors.pages.CodeFragmentViewerPage.viewFragments", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"); //$NON-NLS-1$
	private Code fCode;
	private ScrolledForm fForm;
	
	/**
	 * Constructor.
	 */
	public CodeFragmentViewerPage(FormEditor editor, Code code)
	{
		super(editor, VIEW_FRAGMENTS, VIEW_FRAGMENTS); 
		fCode = code;
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerProjectListener(code.getProject(), this);
		listenerManager.registerCodeListener(code.getProject(), this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm)
	{
		fForm = managedForm.getForm();
		fForm.setText(MessagesClient.getString("editors.pages.CodeFragmentViewerPage.viewAllFragments", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages") + //$NON-NLS-1$
				fCode.getCodeName()); 
		Composite body = fForm.getBody();
		FormToolkit toolkit = managedForm.getToolkit();
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		body.setLayout(layout);
		
		Button refresh = toolkit.createButton(body, MessagesClient.getString(
				"editors.pages.CodeFragmentViewerPage.refresh", "ca.mcgill.cs.swevo.qualyzer.editors.pages.messages"), SWT.PUSH); //$NON-NLS-1$
		refresh.addSelectionListener(refreshSelectedListener());
		
		Project project = fCode.getProject();
		Collections.sort(project.getTranscripts());
		for(Transcript transcript : project.getTranscripts())
		{
			ArrayList<Fragment> contents = findFragments(transcript);
			
			if(!contents.isEmpty())
			{
				buildSection(transcript, contents, toolkit, body);
			}
		}
		
		for(Memo memo : project.getMemos())
		{
			ArrayList<Fragment> contents = findFragments(memo);
			
			if(!contents.isEmpty())
			{
				buildSection(memo, contents, toolkit, body);
			}
		}
	}

	/**
	 * Opens a new editor with updated information and closes this one.
	 * @return
	 */
	private SelectionAdapter refreshSelectedListener()
	{
		return new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Project proj = PersistenceManager.getInstance().getProject(fCode.getProject().getName());
				Code code = null;
				for(Code c : proj.getCodes())
				{
					if(c.equals(fCode))
					{
						code = c;
						break;
					}
				}
				ResourcesUtil.openEditor(getSite().getPage(), code);
				getEditor().close(true);
			}
		};
	}

	/**
	 * Builds a Transcript or memo section. Lists all the fragments for one document.
	 * @param document
	 * @param contents
	 * @param toolkit
	 * @param body
	 */
	private void buildSection(IAnnotatedDocument document, ArrayList<Fragment> contents, FormToolkit toolkit,
			Composite body)
	{
		Section section = toolkit.createSection(body, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		section.setText(document.getClass().getSimpleName() + ": " + document.getName()); //$NON-NLS-1$
		section.addExpansionListener(new ExpansionAdapter(){

			@Override
			public void expansionStateChanged(ExpansionEvent e)
			{
				fForm.reflow(true);
			}
		});
		
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new TableWrapLayout());
		sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		String text = FragmentUtil.getDocumentText(document);
		FragmentUtil.sortFragments(contents);
		
		for(Fragment fragment : contents)
		{
			createTextBox(sectionClient, text, fragment, toolkit);
		}
		
		section.setClient(sectionClient);
		toolkit.paintBordersFor(sectionClient);
	}

	/**
	 * Creates the form text representing one fragment. Sets the fragment text itself as a hyperlink to the document.
	 * @param sectionClient
	 * @param text
	 * @param fragment
	 */
	private void createTextBox(Composite sectionClient, String text, Fragment fragment, FormToolkit toolkit)
	{
		int start = findStart(text, fragment);		
		int end = findEnd(text, fragment);
		
		String fragText = text.substring(start, end);
		
		FormText formText = toolkit.createFormText(sectionClient, true);
		
		StringBuilder builder = new StringBuilder();
		builder.append(FormTextConstants.FORM_START); 
		builder.append(FormTextConstants.PARAGRAPH_START);
		
		int fragStart = fragment.getOffset() - start;
		int fragEnd = fragStart + fragment.getLength();
		
		/* 
		 * Note that general escaping methods such as StringEscapeUtils.escapeHtml should
		 * note be used here because the FormText widget does not support all HTML escaped
		 * codes, only the ones used here. See the documentation for FormText.
		 */
		String temp = fragText.substring(0, fragStart).replace(LESS, LESS_CODE);
		temp = temp.replace(AMP, AMP_CODE);
		builder.append(temp.replace(GREATER, GREATER_CODE));
		
		builder.append(FormTextConstants.LINK_START_HEAD + FormTextConstants.LINK_START_TAIL);
		temp = fragText.substring(fragStart, fragEnd).replace(LESS, LESS_CODE);
		temp = temp.replace(AMP, AMP_CODE);
		builder.append(temp.replace(GREATER, GREATER_CODE));
		builder.append(FormTextConstants.LINK_END); 
		
		temp = fragText.substring(fragEnd, fragText.length()).replace(LESS, LESS_CODE);
		temp = temp.replace(AMP, AMP_CODE);
		builder.append(temp.replace(GREATER, GREATER_CODE));
		
		builder.append(FormTextConstants.PARAGRAPH_END); 
		builder.append(FormTextConstants.FORM_END); 
		
		formText.setText(builder.toString(), true, false);
		formText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		formText.addHyperlinkListener(createHyperlinkListener(fragment));
		
		formText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
	}

	/**
	 * Opens the editor represented by the fragment and selects the fragment text.
	 * @param fragment
	 * @return
	 */
	private HyperlinkAdapter createHyperlinkListener(final Fragment fragment)
	{
		return new HyperlinkAdapter(){
			private Fragment fFragment = fragment;
			@Override
			public void linkActivated(HyperlinkEvent e)
			{
				IEditorPart editor = ResourcesUtil.openEditor(getSite().getPage(), fFragment.getDocument());
				if(editor instanceof RTFEditor)
				{
					((RTFEditor) editor).selectAndReveal(fFragment.getOffset(), fFragment.getLength());
				}
			}
		};
	}

	/**
	 * Find the end of the text to display. Ends after the 2nd punctuation mark, or a paragraph.
	 * @param text
	 * @param fragment
	 * @return
	 */
	private int findEnd(String text, Fragment fragment)
	{
		int end = fragment.getOffset() + fragment.getLength();
		int numPunctuation = 0;
		if(end < text.length() && isPunctuation(text, end))
		{
			numPunctuation++;
		}
		
		while(end < text.length() && text.charAt(end) != '\n' && text.charAt(end) != '\t' && numPunctuation < 2)
		{
			end++;
			if(end < text.length() && isPunctuation(text, end))
			{
				numPunctuation++;
			}
		}
		
		if(end < text.length() && isPunctuation(text, end))
		{
			end++;
		}
		return end;
	}

	/**
	 * Find the start of the text to display. Starts either at the beginning of the previous sentence, 
	 * or the start of the paragraph.
	 * @param text
	 * @param fragment
	 * @return
	 */
	private int findStart(String text, Fragment fragment)
	{
		int start = fragment.getOffset();
		int numPunctuation = 0;
		
		if(start > 0 && isPunctuation(text, start -1))
		{
			numPunctuation++;
		}
		
		while(start > 0 && text.charAt(start) != '\n' && text.charAt(start) != '\t' && numPunctuation < 2)
		{
			start--;
			if(start > 0 && isPunctuation(text, start -1))
			{
				numPunctuation++;
			}
		}
		return start;
	}

	/**
	 * Checks if a the character at a certain index is punctuation.
	 * @param text
	 * @param index
	 * @return
	 */
	private boolean isPunctuation(String text, int index)
	{
		boolean isPunctuation = text.charAt(index) == '!' || text.charAt(index) == '?' || 
			text.charAt(index) == '.';
		return isPunctuation;
	}

	/**
	 * Gets all the fragments in the document that contain the code being viewed.
	 * @param document
	 * @return
	 */
	private ArrayList<Fragment> findFragments(IAnnotatedDocument document)
	{
		IAnnotatedDocument lDocument = Facade.getInstance().forceDocumentLoad(document);
		ArrayList<Fragment> toReturn = new ArrayList<Fragment>();
		
		for(Fragment fragment : lDocument.getFragments().values())
		{
			for(CodeEntry entry : fragment.getCodeEntries())
			{
				if(entry.getCode().equals(fCode))
				{
					toReturn.add(fragment);
				}
			}
		}
		return toReturn;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose()
	{
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.unregisterProjectListener(fCode.getProject(), this);
		listenerManager.unregisterCodeListener(fCode.getProject(), this);
		super.dispose();
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
		else if(cType == ChangeType.RENAME)
		{
			ResourcesUtil.closeEditor(getSite().getPage(), getEditorInput().getName());
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.CodeListener#codeChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Code[], 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void codeChanged(ChangeType cType, Code[] codes, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Code code : codes)
			{
				if(code.equals(fCode))
				{
					getEditor().close(false);
					break;
				}
			}
		}
		
	}
}
