package rtfeditor.editors;

import net.sf.colorer.eclipse.ColorerPlugin;
import net.sf.colorer.eclipse.editors.ColorerEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

public class RTFEditor extends ColorerEditor
{
	private Action fBoldAction;
	private Action fItalicAction;
	private Action fUnderlineAction;
	private Action fMarkTextAction;
	
	private boolean fIsDirty;
	
	public RTFEditor()
	{
		super();
		setDocumentProvider(new RTFDocumentProvider());
		
		fIsDirty = false;
		
		fBoldAction = new Action(){
			
			@Override
			public void run() {
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);
				
				viewer.toggleBold(position);
				setDirty();
			};
		};
		fBoldAction.setText("Bold");
		fBoldAction.setEnabled(false);
		
		fItalicAction = new Action(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);

				viewer.toggleItalic(position);
				setDirty();
			}
		};
		fItalicAction.setText("Italic");
		fItalicAction.setEnabled(false);
		
		fUnderlineAction = new Action(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);
				
				viewer.toggleUnderline(position);
				setDirty();
			}
		};
		fUnderlineAction.setText("Underline");
		fUnderlineAction.setEnabled(false);
		
		fMarkTextAction = new Action(){
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				RTFSourceViewer viewer = (RTFSourceViewer) getSourceViewer();
				Point selection = viewer.getSelectedRange();
				Position position = new Position(selection.x, selection.y);
				
				viewer.markFragment(position);
				setDirty();
			}
		};
		fMarkTextAction.setText("Mark Fragment (Graphical only)");
		fMarkTextAction.setEnabled(true);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles)
	{
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		
		final SourceViewer viewer = new RTFSourceViewer(parent, ruler, fOverviewRuler, isOverviewRulerVisible(), styles);
		
		getSourceViewerDecorationSupport(viewer);
		
		viewer.showAnnotations(true);
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				Point selection = viewer.getSelectedRange();
				fBoldAction.setEnabled(selection.y != 0);
				fItalicAction.setEnabled(selection.y != 0);
				fUnderlineAction.setEnabled(selection.y != 0);
			}
		});
		
		return viewer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#getSourceViewerDecorationSupport(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	protected SourceViewerDecorationSupport getSourceViewerDecorationSupport(ISourceViewer viewer)
	{
		if(fSourceViewerDecorationSupport == null)
		{
			fSourceViewerDecorationSupport = new RTFDecorationSupport(viewer, fOverviewRuler, fAnnotationAccess, getSharedColors());
			configureSourceViewerDecorationSupport(fSourceViewerDecorationSupport);
		}
		
		return fSourceViewerDecorationSupport;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(new RTFSourceViewerConfiguration());
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isLineNumberRulerVisible()
	 */
	@Override
	protected boolean isLineNumberRulerVisible()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	@Override
	protected void createActions()
	{
		super.createActions();
		setAction(RTFConstants.BOLD_ACTION_ID, fBoldAction);
		setAction(RTFConstants.UNDERLINE_ACTION_ID, fUnderlineAction);
		setAction(RTFConstants.ITALIC_ACTION_ID, fItalicAction);
		setAction(RTFConstants.FRAGMENT_TYPE, fMarkTextAction);
		
		//I have no idea why I have to use these numbers for the characters.
		//But they are the only things that work. - JF
		setActionActivationCode(RTFConstants.BOLD_ACTION_ID, (char) 2, 'b', SWT.CONTROL);
		setActionActivationCode(RTFConstants.ITALIC_ACTION_ID, (char) 9, 'i', SWT.CONTROL);
		setActionActivationCode(RTFConstants.UNDERLINE_ACTION_ID, (char) 21, 'u', SWT.CONTROL);
		
		//This doesnt work yet. Why?
		setActionActivationCode(RTFConstants.FRAGMENT_TYPE, (char) 11, 'k', SWT.CONTROL);
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#rulerContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu)
	{
		// super.rulerContextMenuAboutToShow(menu);
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
		
		ColorerPlugin.getDefault().setPropertyWordWrap(getTextColorer().getFileType(), 1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#overviewRulerContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
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
		// TODO Auto-generated method stub
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
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, RTFConstants.FRAGMENT_TYPE);
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
	private void setDirty()
	{
		fIsDirty = true;
		firePropertyChange(PROP_DIRTY);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return super.isDirty() || fIsDirty;
	}
	
}
