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
package ca.mcgill.cs.swevo.qualyzer.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import net.sf.colorer.eclipse.ColorerPlugin;
import net.sf.colorer.eclipse.editors.ColorerEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import ca.mcgill.cs.swevo.qualyzer.IQualyzerPreferenceConstants;
import ca.mcgill.cs.swevo.qualyzer.QualyzerActivator;
import ca.mcgill.cs.swevo.qualyzer.editors.inputs.RTFEditorInput;
import ca.mcgill.cs.swevo.qualyzer.model.Code;
import ca.mcgill.cs.swevo.qualyzer.model.CodeListener;
import ca.mcgill.cs.swevo.qualyzer.model.Facade;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;
import ca.mcgill.cs.swevo.qualyzer.model.IAnnotatedDocument;
import ca.mcgill.cs.swevo.qualyzer.model.Investigator;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager;
import ca.mcgill.cs.swevo.qualyzer.model.Project;
import ca.mcgill.cs.swevo.qualyzer.model.ProjectListener;
import ca.mcgill.cs.swevo.qualyzer.model.Transcript;
import ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType;
import ca.mcgill.cs.swevo.qualyzer.ui.ResourcesUtil;
import ca.mcgill.cs.swevo.qualyzer.util.FileUtil;

/**
 * A Rich Text Editor.
 *
 */
public class RTFEditor extends ColorerEditor implements ProjectListener, CodeListener
{
	public static final String ID = "ca.mcgill.cs.swevo.qualyzer.editors.RTFEditor"; //$NON-NLS-1$

	private static final char UNDERLINE_CHAR = (char) 21;
	private static final char ITALIC_CHAR = (char) 9;
	private static final char BOLD_CHAR = (char) 2;
	private static final char FRAGMENT_CHAR = (char) 11;
	
	protected ImageRegistry fRegistry;
	
	private Action fBoldAction;
	private Action fItalicAction;
	private Action fUnderlineAction;
	private Action fMarkTextAction;
	private Action fRemoveCodeAction;
	private Action fRemoveAllCodesAction;
	private Action fViewFragmentsAction;
	
	private boolean fIsDirty;
	private IAnnotatedDocument fDocument;
	private Investigator fActiveInvestigator;
	
	/**
	 * Constructor.
	 * Initialises the actions.
	 */
	public RTFEditor()
	{
		super();
		setSourceViewerConfiguration(new RTFSourceViewerConfiguration(getTextColorer()));
		setDocumentProvider(new RTFDocumentProvider2());
		
		fIsDirty = false;
		fRegistry = QualyzerActivator.getDefault().getImageRegistry();
								
		getPreferenceStore().setValue(AbstractDecoratedTextEditorPreferenceConstants.QUICK_DIFF_ALWAYS_ON, false);
		QualyzerActivator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		FontData font = PreferenceConverter.getFontData(QualyzerActivator.getDefault().getPreferenceStore(),
				IQualyzerPreferenceConstants.FONT);
		PreferenceConverter.setValue(getPreferenceStore(), JFaceResources.TEXT_FONT, font);
	}
	
	/**
	 * Add an image to the registry.
	 * @param key
	 * @param pluginID
	 * @param path
	 */
	protected void addImage(String key, String pluginID, String path)
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
	protected Image getImage(String key, String pluginID)
	{
		return fRegistry.get(computeKey(key, pluginID));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, 
	 * org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		
		final SourceViewer viewer = new RTFSourceViewer(parent, ruler, fOverviewRuler,
				isOverviewRulerVisible(), styles);
		
		getSourceViewerDecorationSupport(viewer);
		
		viewer.showAnnotations(true);
		viewer.addSelectionChangedListener(createSelectionListener(viewer));
		
		return viewer;
	}

	/**
	 * Handles enablement of the various actions when the selection changes in the source viewer.
	 * @param viewer
	 * @return
	 */
	private ISelectionChangedListener createSelectionListener(final SourceViewer viewer)
	{
		return new ISelectionChangedListener()
		{
			
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				Point selection = viewer.getSelectedRange();
				IAnnotationModel model = getSourceViewer().getAnnotationModel();
				
				boolean enabled = selection.y != 0;
				
				boolean boldEnabled = isBoldEnabled(model, selection);
				boolean italicEnabled = isItalicEnabled(model, selection);
				boolean underlineEnabled = isUnderlineEnabled(model, selection);
				boolean markTextEnabled = isMarkEnabled(model, selection);
				
				fBoldAction.setEnabled(enabled && boldEnabled);
				fItalicAction.setEnabled(enabled && italicEnabled);
				fUnderlineAction.setEnabled(enabled && underlineEnabled);
				fMarkTextAction.setEnabled(enabled && markTextEnabled);
			}
		};
	}
	
	/**
	 * 
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isMarkEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(annotation instanceof FragmentAnnotation)
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		if(positions.size() == 0)
		{
			return true;
		}
		else if(positions.size() == 1)
		{
			Position position = positions.get(0);
			return position.offset == selection.x && position.length == selection.y;
		}
		else
		{
			return false;
		}
			
	}
	
	/**
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isUnderlineEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isUnderline(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
				
		return handleOverlaps(positions, selection);
	}

	/**
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isItalicEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isItalic(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
						
		return handleOverlaps(positions, selection);
	}

	/**
	 * @param model
	 * @param selection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isBoldEnabled(IAnnotationModel model, Point selection)
	{
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isBold(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
					
		return handleOverlaps(positions, selection);
	}

	/**
	 * Deal with the various cases of the selection overlapping any number of existing positions.
	 * True if there are no overlapping positions or if there is one that completely spans the selection,
	 * or if there are many that span the selection with no gaps.
	 * False otherwise.
	 * @param positions
	 * @param selection
	 * @return
	 */
	private boolean handleOverlaps(ArrayList<Position> positions, Point selection)
	{
		boolean toReturn = false;
		
		if(positions.size() == 0)
		{
			toReturn = true;
		}
		else if(positions.size() == 1)
		{
			Position position = positions.get(0);
			toReturn = position.offset <= selection.x && position.offset + position.length >= selection.x + selection.y;
		}
		else
		{
			sort(positions);
			Position startPos = positions.get(0);
			Position endPos = positions.get(positions.size() - 1);
			if(startPos.offset <= selection.x && endPos.offset + endPos.length >= selection.x + selection.y)
			{
				for(int i = 0; i < positions.size() - 1; i++)
				{
					Position p1 = positions.get(i);
					
					if(p1.offset + p1.length != positions.get(i+1).offset)
					{
						return false;
					}
				}
				toReturn = true;
			}
		}
		
		return toReturn;
	}

	/**
	 * Sort an arraylist of positions.
	 * @param overlap
	 * @param positions
	 */
	private void sort(ArrayList<Position> positions)
	{
		Collections.sort(positions, new Comparator<Position>()
		{

			@Override
			public int compare(Position o1, Position o2)
			{
				if(o1.offset < o2.offset)
				{
					return -1;
				}
				else if(o1.offset == o2.offset)
				{
					return 0;
				}
				else
				{
					return 1;
				}
			}
		});
		
	}

	/**
	 * Checks if an annotation is bold.
	 * @param annotation
	 * @return
	 */
	protected static boolean isBold(Annotation annotation)
	{
		String type = annotation.getType();
		return type.equals(RTFConstants.BOLD_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_TYPE) || 
		type.equals(RTFConstants.BOLD_UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE);
	}
	
	/**
	 * Checks if an annotation is italic.
	 * @param annotation
	 * @return
	 */
	protected static boolean isItalic(Annotation annotation)
	{
		String type = annotation.getType();
		return type.equals(RTFConstants.ITALIC_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_TYPE) || 
		type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE);
	}

	/**
	 * Checks if an annotation is underlined.
	 * @param annotation
	 * @return
	 */
	protected static boolean isUnderline(Annotation annotation)
	{
		String type = annotation.getType();
		return type.equals(RTFConstants.UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_UNDERLINE_TYPE) || 
		type.equals(RTFConstants.ITALIC_UNDERLINE_TYPE) || type.equals(RTFConstants.BOLD_ITALIC_UNDERLINE_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getSourceViewerDecorationSupport(
	 * org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer)
	{
		if(fSourceViewerDecorationSupport == null)
		{
			fSourceViewerDecorationSupport = new RTFDecorationSupport(viewer, fOverviewRuler, 
					fAnnotationAccess, getSharedColors());
			configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
		}
		
		return fSourceViewerDecorationSupport;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isLineNumberRulerVisible()
	 */
	@Override
	protected boolean isLineNumberRulerVisible()
	{
		return false;
	}
	
	/**
	 * Initialises all the actions and their key bindings.
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		
		RTFSourceViewer sourceViewer = (RTFSourceViewer) getSourceViewer();
		fBoldAction = new BoldAction(this, sourceViewer);
		fItalicAction = new ItalicAction(this, sourceViewer);
		fUnderlineAction = new UnderlineAction(this, sourceViewer);
		
		fMarkTextAction = new MarkTextAction(this, sourceViewer);
		fRemoveCodeAction = new RemoveCodeAction(this, sourceViewer);
		fRemoveAllCodesAction = new RemoveAllCodesAction(this, sourceViewer);
		fViewFragmentsAction = new ViewFragmentsAction(this, sourceViewer);
		
		setAction(RTFConstants.BOLD_ACTION_ID, fBoldAction);
		setAction(RTFConstants.UNDERLINE_ACTION_ID, fUnderlineAction);
		setAction(RTFConstants.ITALIC_ACTION_ID, fItalicAction);
		
		setAction(RTFConstants.FRAGMENT_ACTION_ID, fMarkTextAction);
		
		setAction(RTFConstants.REMOVE_ALL_CODES_ACTION_ID, fRemoveAllCodesAction);
		setAction(RTFConstants.REMOVE_CODE_ACTION_ID, fRemoveCodeAction);
		setAction(RTFConstants.VIEW_FRAGMENTS_ACTION_ID, fViewFragmentsAction);
		
		setActionActivationCode(RTFConstants.BOLD_ACTION_ID, BOLD_CHAR, 'b', SWT.CONTROL);
		setActionActivationCode(RTFConstants.ITALIC_ACTION_ID, ITALIC_CHAR, 'i', SWT.CONTROL);
		setActionActivationCode(RTFConstants.UNDERLINE_ACTION_ID, UNDERLINE_CHAR, 'u', SWT.CONTROL);
		setActionActivationCode(RTFConstants.FRAGMENT_ACTION_ID, FRAGMENT_CHAR, 'k', SWT.CONTROL);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#rulerContextMenuAboutToShow(
	 * org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu)
	{
		//super.rulerContextMenuAboutToShow(menu);
		//This removes the ruler context menu.
		//Do we want to add any of our own actions here?
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		setRulerContextMenuId("#RTFRulerContext"); //$NON-NLS-1$
		setOverviewRulerContextMenuId("#RTFOverviewRulerContext"); //$NON-NLS-1$
		setEditorContextMenuId("#RTFEditorContext"); //$NON-NLS-1$

		super.createPartControl(parent);
		
		fDocument = ((RTFEditorInput) getEditorInput()).getDocument();
		fActiveInvestigator = recoverActiveInvestigator();

		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.registerProjectListener(fDocument.getProject(), this);
		listenerManager.registerCodeListener(fDocument.getProject(), this);
		ColorerPlugin.getDefault().setPropertyWordWrap(getTextColorer().getFileType(), 1);
		getSourceViewer().getTextWidget().setWordWrap(true);
		
		setPartName(fDocument.getName());
	}
	
	/**
	 * Check the project description to find out who the active investigator is.
	 * @return
	 */
	private Investigator recoverActiveInvestigator()
	{
		Project project = fDocument.getProject();
		IProject wProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getFolderName());
		String name = ""; //$NON-NLS-1$
		
		try
		{
			name = FileUtil.getProjectProperty(wProject, FileUtil.ACTIVE_INV);
		}
		catch (CoreException e)
		{
			return project.getInvestigators().get(0);
		}
		
		for(Investigator investigator : project.getInvestigators())
		{
			if(investigator.getNickName().equals(name))
			{
				return investigator;
			}
		}
		
		return project.getInvestigators().get(0);
	}
	
	/**
	 * Returns the current Active Investigator. Mainly for use by MarkTextAction.
	 * @return
	 */
	protected Investigator getActiveInvestigator()
	{
		return fActiveInvestigator;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#overviewRulerContextMenuAboutToShow(
	 * org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void overviewRulerContextMenuAboutToShow(IMenuManager menu)
	{
		// super.overviewRulerContextMenuAboutToShow(menu);
		// This removes the overview ruler context menu.
		// Do we want to add any of our own actions?
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu)
	{
		super.editorContextMenuAboutToShow(menu);
		
		menu.remove(ITextEditorActionConstants.GROUP_OPEN);
		menu.remove(ITextEditorActionConstants.GROUP_PRINT);
		menu.remove(ITextEditorActionConstants.GROUP_ADD);
		menu.remove(ITextEditorActionConstants.GROUP_REST);
		menu.remove(ITextEditorActionConstants.SHIFT_RIGHT);
		menu.remove(ITextEditorActionConstants.SHIFT_LEFT);
		menu.remove(ITextEditorActionConstants.GROUP_FIND);
		menu.remove(IWorkbenchActionConstants.MB_ADDITIONS);
		menu.remove(ITextEditorActionConstants.GROUP_SETTINGS);
		menu.remove(ITextEditorActionConstants.CONTEXT_PREFERENCES);
		
		for(IContributionItem item : menu.getItems())
		{
			if(item.getId() == null)
			{
				menu.remove(item);
			}
		}
		
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.BOLD_ACTION_ID);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.ITALIC_ACTION_ID);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.UNDERLINE_ACTION_ID);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.FRAGMENT_ACTION_ID);
		
		if(isRemoveVisible())
		{
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.VIEW_FRAGMENTS_ACTION_ID);
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.REMOVE_CODE_ACTION_ID);
			addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.REMOVE_ALL_CODES_ACTION_ID);
		}
		
		//check according to selection
		fBoldAction.setChecked(isBoldChecked());
		fItalicAction.setChecked(isItalicChecked());
		fUnderlineAction.setChecked(isUnderlineChecked());
	}
	
	/**
	 * The Remove Code actions should only be visible when a fragment is selected.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isRemoveVisible()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		if(selection.y == 0)
		{
			return false;
		}
		else
		{
			Iterator<Annotation> iter = model.getAnnotationIterator();
			while(iter.hasNext())
			{
				Annotation annotation = iter.next();
				if(annotation instanceof FragmentAnnotation)
				{
					Position position = model.getPosition(annotation);
					if(position.offset == selection.x && position.length == selection.y)
					{
						return true;
					}
				}
			}
			return false;
		}
	}



	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isUnderlineChecked()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isUnderline(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		return positions.size() > 0;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isItalicChecked()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isItalic(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		return positions.size() > 0;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected boolean isBoldChecked()
	{
		IAnnotationModel model = getSourceViewer().getAnnotationModel();
		Point selection = getSourceViewer().getSelectedRange();
		
		ArrayList<Position> positions = new ArrayList<Position>();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		
		while(iter.hasNext())
		{
			Annotation annotation = iter.next();
			if(isBold(annotation))
			{
				Position position = model.getPosition(annotation);
				if(position.overlapsWith(selection.x, selection.y))
				{
					positions.add(position);
				}
			}
		}
		
		return positions.size() > 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor progressMonitor)
	{
		super.doSave(progressMonitor);
		fIsDirty = false;
	}

	/**
	 * 
	 */
	protected void setDirty()
	{
		if(!isDirty())
		{
			fIsDirty = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return super.isDirty() || fIsDirty;
	}

	

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.swevo.qualyzer.model.ProjectListener#projectChanged(
	 * ca.mcgill.cs.swevo.qualyzer.model.ListenerManager.ChangeType, ca.mcgill.cs.swevo.qualyzer.model.Project, 
	 * ca.mcgill.cs.swevo.qualyzer.model.Facade)
	 */
	@Override
	public void projectChanged(ChangeType cType, Project project, Facade facade)
	{
		if(ChangeType.DELETE == cType) //Close without saving.
		{
			getSite().getPage().closeEditor(this, false);
		}
		else if(ChangeType.RENAME == cType) //Close and save ask to save.
		{
			ResourcesUtil.closeEditor(getSite().getPage(), getEditorInput().getName());
		}
		else if(ChangeType.MODIFY == cType) //Active investigator has changed.
		{
			fDocument.setProject(project);
			refreshInput();
			fActiveInvestigator = recoverActiveInvestigator();
		}
		
	}
	
	/**
	 * Makes sure that all the objects are fresh from the database.
	 */
	private void refreshInput()
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(fDocument.getProject().getFolderName());
		IFile file;
		if(fDocument instanceof Transcript)
		{
			file = project.getFile("transcripts" + File.separator + fDocument.getFileName()); //$NON-NLS-1$
		}
		else
		{
			file = project.getFile("memos" + File.separator + fDocument.getFileName()); //$NON-NLS-1$
		}
		RTFEditorInput input = new RTFEditorInput(file, fDocument);
		
		setInput(input);
		setPartName(fDocument.getName());
		
	}

	/* (non-Javadoc)
	 * @see net.sf.colorer.eclipse.editors.ColorerEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		ListenerManager listenerManager = Facade.getInstance().getListenerManager();
		listenerManager.unregisterProjectListener(fDocument.getProject(), this);
		listenerManager.unregisterCodeListener(fDocument.getProject(), this);
		QualyzerActivator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}

	/**
	 * If a code has been modified or deleted, goes through all the fragments and updates them.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void codeChanged(ChangeType cType, Code[] codes, Facade facade)
	{
		RTFAnnotationModel model = (RTFAnnotationModel) getSourceViewer().getAnnotationModel();
		Iterator<Annotation> iter = model.getAnnotationIterator();
		fDocument.setProject(codes[0].getProject());

		if(cType == ChangeType.DELETE || cType == ChangeType.MODIFY)
		{
			IAnnotatedDocument document = Facade.getInstance().forceDocumentLoad(fDocument);
			Map<Integer, Fragment> newMap = document.getFragments();
			fDocument.setFragments(newMap);
			
			while(iter.hasNext())
			{
				Annotation annotation = iter.next();
				if(annotation instanceof FragmentAnnotation)
				{
					Fragment fragment = ((FragmentAnnotation) annotation).getFragment();
					if(!newMap.containsKey(fragment.getOffset()))
					{
						model.removeAnnotationOnly(annotation);
					}
					else
					{
						Fragment newFragment = newMap.get(fragment.getOffset());
						((FragmentAnnotation) annotation).setFragment(newFragment);
					}
				}
			}
		}
	}

	/**
	 * Get the Transcript or Memo.
	 * @return
	 */
	public IAnnotatedDocument getDocument()
	{
		return fDocument;
	}
	
	/**
	 * Get the BoldAction associated with this editor.
	 * @return
	 */
	public Action getBoldAction()
	{
		return fBoldAction;
	}
	
	/**
	 * Get the ItalicAction associated with this editor.
	 * @return
	 */
	public Action getItalicAction()
	{
		return fItalicAction;
	}
	
	/**
	 * Get the UnderlineAction associated with this editor.
	 * @return
	 */
	public Action getUnderlineAction()
	{
		return fUnderlineAction;
	}
	
	/**
	 * Get the MarkTextAction associated with this editor.
	 * @return
	 */
	public Action getMarkTextAction()
	{
		return fMarkTextAction;
	}
	
	/**
	 * Get the RemoveCodeAction associated with this editor.
	 * @return
	 */
	public Action getRemoveCodeAction()
	{
		return fRemoveCodeAction;
	}
	
	/**
	 * Get the RemoveAllCodesAction associated with this editor.
	 * @return
	 */
	public Action getRemoveAllCodesAction()
	{
		return fRemoveAllCodesAction;
	}
	
	/**
	 * Propagates a change in the font preference to the editor preference store.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		if(e == null || e.getProperty().equals(IQualyzerPreferenceConstants.FONT))
		{
			IPreferenceStore pref = getPreferenceStore();
			FontData oldFont = PreferenceConverter.getFontData(pref, JFaceResources.TEXT_FONT);
			FontData font = PreferenceConverter.getFontData(QualyzerActivator.getDefault().getPreferenceStore(), 
					IQualyzerPreferenceConstants.FONT);
			PreferenceConverter.setValue(pref, JFaceResources.TEXT_FONT, font);
			pref.firePropertyChangeEvent(JFaceResources.TEXT_FONT, oldFont, font);
		}
		else
		{
			super.propertyChange(e);
		}
	}
}
