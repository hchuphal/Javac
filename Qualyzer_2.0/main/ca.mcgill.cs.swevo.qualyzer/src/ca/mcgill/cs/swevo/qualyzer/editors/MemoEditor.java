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
package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Memo;
import ca.mcgill.cs.swevo.qualyzer.model.MemoListener;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;

/**
 * The implementation of the RTFEditor that handles Memos. Adds a style bar.
 */
public class MemoEditor extends RTFEditor implements MemoListener
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.memoEditor"; //$NON-NLS-1$

	private static final int NUM_COLS = 5;
	
	private static final String BOLD_IMG = "BOLD_IMG"; //$NON-NLS-1$
	private static final String ITALIC_IMG = "ITALIC_IMG"; //$NON-NLS-1$
	private static final String UNDERLINE_IMG = "UNDERLINE_IMG"; //$NON-NLS-1$
	private static final String CODE_IMG = "CODE_IMG"; //$NON-NLS-1$

	private Button fBoldButton;
	private Button fUnderlineButton;
	private Button fItalicButton;
	private Button fCodeButton;
	
	/**
	 * 
	 */
	public MemoEditor()
	{
		addImage(BOLD_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_bold.png"); //$NON-NLS-1$
		addImage(ITALIC_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_italic.png"); //$NON-NLS-1$
		addImage(UNDERLINE_IMG, QualyzerActivator.PLUGIN_ID, "icons/text_underline.png"); //$NON-NLS-1$
		addImage(CODE_IMG, QualyzerActivator.PLUGIN_ID, "icons/code_obj.gif"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.MemoListener#memoChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Memo[],
	 *  ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void memoChanged(ChangeType cType, Memo[] memos, Facade facade)
	{
		if(cType == ChangeType.DELETE)
		{
			for(Memo memo : memos)
			{
				if(memo.equals(getDocument()))
				{
					ResourcesUtil.closeEditor(getSite().getPage(), getEditorInput().getName());
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a listener to the sourceviewer.
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		final SourceViewer viewer = (SourceViewer) super.createSourceViewer(parent, ruler, styles);
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				IAnnotationModel model = viewer.getAnnotationModel();
				Point selection = viewer.getSelectedRange();
				
				boolean enabled = selection.y != 0;
				
				fBoldButton.setEnabled(enabled && isBoldEnabled(model, selection));
				fItalicButton.setEnabled(enabled && isItalicEnabled(model, selection));
				fUnderlineButton.setEnabled(enabled && isUnderlineEnabled(model, selection));
				fCodeButton.setEnabled(enabled && isMarkEnabled(model, selection));
				
				fBoldButton.setSelection(isBoldChecked());
				fItalicButton.setSelection(isItalicChecked());
				fUnderlineButton.setSelection(isUnderlineChecked());
			}
			
		});
		return viewer;
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		//This controls displaying of the top button bar.
		parent.setLayout(new GridLayout(1, true));
		
		Composite topBar = new Composite(parent, SWT.BORDER);
		topBar.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		topBar.setLayout(new GridLayout(NUM_COLS, false));
		
		createFormatButtonBar(topBar);
		
		super.createPartControl(parent);
		
		parent.getChildren()[1].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		hookupButtonActions();
		
		Facade.getInstance().getListenerManager().registerMemoListener(getDocument().getProject(), this);
	}
	
	private Control createFormatButtonBar(Composite parent)
	{	
		fBoldButton = new Button(parent, SWT.TOGGLE);
		fBoldButton.setImage(getImage(BOLD_IMG, QualyzerActivator.PLUGIN_ID));
		fBoldButton.setEnabled(false);
		
		fUnderlineButton = new Button(parent, SWT.TOGGLE);
		fUnderlineButton.setImage(getImage(UNDERLINE_IMG, QualyzerActivator.PLUGIN_ID));
		fUnderlineButton.setEnabled(false);
		
		fItalicButton = new Button(parent, SWT.TOGGLE);
		fItalicButton.setImage(getImage(ITALIC_IMG, QualyzerActivator.PLUGIN_ID));
		fItalicButton.setEnabled(false);
		
		fCodeButton = new Button(parent, SWT.PUSH);
		fCodeButton.setImage(getImage(CODE_IMG, QualyzerActivator.PLUGIN_ID));
		fCodeButton.setEnabled(false);
		
		Label label = new Label(parent, SWT.NULL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
		
		return parent;
	}
	
	private void hookupButtonActions()
	{
		fBoldButton.addSelectionListener(createButtonSelectionListener(getBoldAction()));
		fUnderlineButton.addSelectionListener(createButtonSelectionListener(getUnderlineAction()));
		fItalicButton.addSelectionListener(createButtonSelectionListener(getItalicAction()));
		fCodeButton.addSelectionListener(createButtonSelectionListener(getMarkTextAction()));
	}
	
	/**
	 * @param fBoldAction2
	 * @return
	 */
	private SelectionAdapter createButtonSelectionListener(final Action action)
	{
		return new SelectionAdapter(){
			private Action fAction = action;
			
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(fAction.isEnabled())
				{
					fAction.run();
				}
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		Facade.getInstance().getListenerManager().unregisterMemoListener(getDocument().getProject(), this);
		super.dispose();
	}

}
